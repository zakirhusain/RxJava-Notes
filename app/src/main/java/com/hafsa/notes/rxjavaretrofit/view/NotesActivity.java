package com.hafsa.notes.rxjavaretrofit.view;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.hafsa.notes.R;
import com.hafsa.notes.rxjavaretrofit.network.ApiClient;
import com.hafsa.notes.rxjavaretrofit.network.ApiService;
import com.hafsa.notes.rxjavaretrofit.utils.MyDividerItemDecoration;
import com.hafsa.notes.rxjavaretrofit.utils.Note;
import com.hafsa.notes.rxjavaretrofit.utils.PrefUtils;
import com.hafsa.notes.rxjavaretrofit.utils.RecyclerTouchListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

public class NotesActivity extends AppCompatActivity {

    private static final String TAG = "NotesActivity";
    private ApiService apiService;
    private CompositeDisposable disposable = new CompositeDisposable();
    private NotesAdapter notesAdapter;
    private List<Note> noteList = new ArrayList<>();

    @BindView(R.id.coordinator_layout)
    CoordinatorLayout coordinatorLayout;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.txt_empty_notes_view)
    TextView noNotesView;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_notes);
        ButterKnife.bind(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.activity_title_home));
        setSupportActionBar(toolbar);


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNoteDialog(false, null, -1);
            }
        });


        // white background notification bar
        whiteNotificationBar(fab);

        apiService = ApiClient.getClient(this).create(ApiService.class);

        notesAdapter = new NotesAdapter(this, noteList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.VERTICAL, 16));
        recyclerView.setAdapter(notesAdapter);

        /**
         * On long press on RecyclerView item, open alert dialog
         * with options to choose
         * Edit and Delete
         * */

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView,
                new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                    }

                    @Override
                    public void onLongClick(View view, int position) {
                        showActionsDialog(position);
                    }
                }));


        /**
         * Check for stored Api Key in shared preferences
         * If not present, make api call to register the user
         * This will be executed when app is installed for the first time
         * or data is cleared from settings
         * */
        if (TextUtils.isEmpty(PrefUtils.getApiKey(this))) {
            registerUser();
        } else {
            // user is already registered, fetch all notes
            fetchAllNotes();
        }
    }

    /**
     * Registering new user
     * sending unique id as device identification
     * https://developer.android.com/training/articles/user-data-ids.html
     */
    private void registerUser() {
       // unique id to identify the device
        String uniqueId = UUID.randomUUID().toString();

        disposable.add(
                apiService.register(uniqueId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<com.hafsa.notes.rxjavaretrofit.utils.User>() {
                            @Override
                            public void onSuccess(com.hafsa.notes.rxjavaretrofit.utils.User user) {
                                // Storing user API Key in preferences
                                PrefUtils.storeApiKey(getApplicationContext(), user.getApiKey());

                                Toast.makeText(getApplicationContext(),
                                        "Device is registered successfully! ApiKey: " + PrefUtils.getApiKey(getApplicationContext()),
                                        Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage() );
                                showError(e);
                            }
                        })
        );
    }

    /**
     * Fetching all notes from api
     * The received items will be in random order
     * map() operator is used to sort the items in descending order by Id
     */
    private void fetchAllNotes() {
        disposable.add(
                apiService.fetchAllNotes()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new Function<List<Note>, List<Note>>() {
                            @Override
                            public List<Note> apply(List<Note> notes) throws Exception {
                                // sorting note
                                Collections.sort(notes, new Comparator<Note>() {
                                    @Override
                                    public int compare(Note note1, Note note2) {
                                        return note2.getId() - note1.getId();
                                    }
                                });
                                return notes;
                            }
                        })
                        .subscribeWith(new DisposableSingleObserver<List<Note>>() {
                            @Override
                            public void onSuccess(List<Note> notes) {
                                noteList.clear();
                                noteList.addAll(notes);
                                notesAdapter.notifyDataSetChanged();

                                toggleEmptyNotes();
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "onError: " + e.getMessage() );
                                showError(e);
                            }
                        })
        );

    }

    /**
     * Opens dialog with Edit - Delete options
     * Edit - 0
     * Delete - 0
     */
    private void showActionsDialog(final int position) {
        CharSequence colors[] = new CharSequence[]{"Edit", "Delete"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose option");
        builder.setItems(colors, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    showNoteDialog(true, noteList.get(position), position);
                } else {
                    deleteNote(noteList.get(position).getId(), position);
                }
            }
        });
        builder.show();
    }

    private void deleteNote(final int noteId, final int position) {
        Log.e(TAG, "deleteNote: " + noteId + ", " + position);
        disposable.add(
            apiService.deleteNote(noteId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Note deleted! " + noteId);

                        // Remove and notify adapter about item deletion
                        noteList.remove(position);
                        notesAdapter.notifyItemRemoved(position);

                        Toast.makeText(NotesActivity.this, "Note deleted!", Toast.LENGTH_SHORT).show();

                        toggleEmptyNotes();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage() );
                    }
                })
        );
    }

    private void whiteNotificationBar(FloatingActionButton fab) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = fab.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            fab.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }




    private void showNoteDialog(final boolean shouldUpdate, final Note note, final int position) {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(getApplicationContext());
        View view = layoutInflaterAndroid.inflate(R.layout.note_dialog, null);

        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(NotesActivity.this);
        alertDialogBuilderUserInput.setView(view);

        final EditText inputNote = view.findViewById(R.id.note);
        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        dialogTitle.setText(!shouldUpdate ? getString(R.string.lbl_new_note_title) : getString(R.string.lbl_edit_note_title));


        if (shouldUpdate && note != null) {
            inputNote.setText(note.getNote());
        }

        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton(shouldUpdate ? "update" : "save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alertDialog = alertDialogBuilderUserInput.create();
        alertDialog.show();

        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show toast message when no text is entered
                if (TextUtils.isEmpty(inputNote.getText().toString())) {
                    Toast.makeText(NotesActivity.this, "Enter note!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    alertDialog.dismiss();
                }

                // check if user updating note
                if (shouldUpdate && note != null) {
                    // update note by it's id
                    updateNote(note.getId(), inputNote.getText().toString(), position);
                } else {
                    // create new note
                    createNote(inputNote.getText().toString());
                }
            }
        });
    }

    private void createNote(String note) {
        disposable.add(
                apiService.createNote(note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<Note>() {
                    @Override
                    public void onSuccess(Note note) {
                        if (TextUtils.isEmpty(note.getNote())) {
                            Toast.makeText(getApplicationContext(), "Empty note", Toast.LENGTH_LONG).show();
                            return;
                        }

                        Log.d(TAG, "new note created: " + note.getId() + ", " + note.getNote() + ", " + note.getTimestamp());

                        // Add new item and notify adapter
                        noteList.add(0, note);
                        notesAdapter.notifyItemInserted(0);

                        toggleEmptyNotes();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage() );
                    }
                })
        );
    }

    private void updateNote(int id, final String note, final int position) {
        disposable.add(
            apiService.updateNote(id, note)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableCompletableObserver() {
                    @Override
                    public void onComplete() {
                        Log.d(TAG, "Note updated!");

                        Note n = noteList.get(position);
                        n.setNote(note);

                        // Update item and notify adapter
                        noteList.set(position, n);
                        notesAdapter.notifyItemChanged(position);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage() );
                    }
                })
        );
    }

    /**
     * Showing a Snackbar with error message
     * The error body will be in json format
     * {"error": "Error message!"}
     */
    private void showError(Throwable e) {
        String message = "";
        try {
            if (e instanceof IOException) {
                message = "No internet connection!";
            } else if (e instanceof HttpException) {
                HttpException error = (HttpException) e;
                String errorBody = error.response().errorBody().string();
                JSONObject jObj = new JSONObject(errorBody);

                message = jObj.getString("error");
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (JSONException e1) {
            e1.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (TextUtils.isEmpty(message)) {
            message = "Unknown error occurred! Check LogCat.";
        }

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, message, Snackbar.LENGTH_LONG);

        View sbView = snackbar.getView();
        TextView textView = sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    private void toggleEmptyNotes() {
        if (noteList.size() > 0) {
            noNotesView.setVisibility(View.GONE);
        } else {
            noNotesView.setVisibility(View.VISIBLE);
        }
    }

}
