package com.hafsa.notes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class Disposable_RxJavaActivity extends AppCompatActivity {

    private Disposable disposable;

    private static final String TAG = "Disposable_RxJavaActivi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disposable__rx_java);

        // Observable
        Observable<String> animalsObservable = getAnimalsObservable();

        // Observer
        Observer<String> animalsObserver = getAnimalsObserver();


        animalsObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(animalsObserver);
    }

    private Observer<String> getAnimalsObserver() {
        return new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG, "onSubscribe: ");
                disposable = d;
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
                Log.i(TAG, "onComplete: " + " All items are emitted successfully");
            }
        };
    }

    private Observable<String> getAnimalsObservable() {
        return Observable.just("Ant", "Bee", "Cat", "Dog", "Fox");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // don't send event's once activity is destroyed
        disposable.dispose();
    }
}
