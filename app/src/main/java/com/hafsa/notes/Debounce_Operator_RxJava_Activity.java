package com.hafsa.notes;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewTextChangeEvent;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class Debounce_Operator_RxJava_Activity extends AppCompatActivity {

    private static final String TAG = "Debounce_Operator_RxJav";

    private CompositeDisposable disposable = new CompositeDisposable();
    private Unbinder unbinder;

    @BindView(R.id.edit_text)
    EditText inputSearch;

    @BindView(R.id.rx_javaTV)
    TextView txtSearchString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debounce__operator__rx_java_);

        unbinder = ButterKnife.bind(this);

        disposable.add(
                RxTextView.textChangeEvents(inputSearch)
                .skipInitialValue()
                .debounce(300, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(searchQuery())
        );


    }

    private DisposableObserver<TextViewTextChangeEvent> searchQuery() {
        return new DisposableObserver<TextViewTextChangeEvent>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onNext(TextViewTextChangeEvent textViewTextChangeEvent) {
                Log.d(TAG, "search string: " + textViewTextChangeEvent.text().toString());

                txtSearchString.setText("Query: " + textViewTextChangeEvent.text().toString());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage() );
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete: ");
            }
        };
    }
}
