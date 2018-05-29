package com.hafsa.notes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

public class OperatorsActivity extends AppCompatActivity {

    private static final String TAG = "OperatorsActivity";

    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operators);
        //filterOperator();

        // Concat operator combines output of two or more Observables into a single Observable. Concat operator always maintains the sequential execution without interleaving the emissions.
        // So the first Observables completes its emission before the second starts and so forth if there are more observables.
        concatOperator();
    }

    private void concatOperator() {
        Observable
                .concat(getMaleObservable(), getFemaleObservable())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(User user) {
                        Log.i(TAG, "onNext: " + user.getName() + " " + user.getGender());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage() );
                    }

                    @Override
                    public void onComplete() {
                        Log.i(TAG, "onComplete: ");
                    }
                });
    }

    private Observable<User> getFemaleObservable() {
        String[] names = new String[]{"Lucy", "Scarlett", "April"};

        final List<User> users = new ArrayList<>();
        for (String name : names) {
            User user = new User();
            user.setName(name);
            user.setGender("female");

            users.add(user);
        }
        return Observable
                .create(new ObservableOnSubscribe<User>() {
                    @Override
                    public void subscribe(ObservableEmitter<User> emitter) throws Exception {
                        for (User user : users) {
                            if (!emitter.isDisposed()) {
                                Thread.sleep(1000);
                                emitter.onNext(user);
                            }
                        }

                        if (!emitter.isDisposed()) {
                            emitter.onComplete();
                        }
                    }
                }).subscribeOn(Schedulers.io());
    }

    private Observable<User> getMaleObservable() {
        String[] names = new String[]{"Mark", "John", "Trump", "Obama"};

        final List<User> users = new ArrayList<>();

        for (String name : names) {
            User user = new User();
            user.setName(name);
            user.setGender("male");

            users.add(user);
        }
        return Observable
                .create(new ObservableOnSubscribe<User>() {
                    @Override
                    public void subscribe(ObservableEmitter<User> emitter) throws Exception {
                        for (User user : users) {
                            if (!emitter.isDisposed()) {
                                Thread.sleep(500);
                                emitter.onNext(user);
                            }
                        }

                        if (!emitter.isDisposed()) {
                            emitter.onComplete();
                        }
                    }
                }).subscribeOn(Schedulers.io());
    }

    private void filterOperator() {
        // Observable
        Observable<String> animalsObservable = getObservable();


        // Observer
        Observer<String> animalsObserver = getObserver();


        animalsObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        Log.i(TAG, "test: " + s);
                         return s.toLowerCase().startsWith("b");
                    }
                })
                .subscribe(animalsObserver);
    }


    private Observer<String> getObserver() {
        return new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG, "onSubscribe: ");      
            }

            @Override
            public void onNext(String s) {
                Log.i(TAG, "onNext: " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage() );
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete: " + "All items are emitted");
            }
        };
    }
    
    private Observable<String> getObservable() {
        return Observable.just("Lion", "Cheetah", "Bear", "Buffalo",
                "Deer", "Goat");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.dispose();
    }
}
