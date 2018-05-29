package com.hafsa.notes.rxjava;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.hafsa.notes.R;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BasicRxJavaActivity extends AppCompatActivity {

    private static final String TAG = "BasicRxJavaActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_rx_java);

        // observable
        Observable<String> animalObservables = getAnimalsObservable();


        // Observer
        Observer<String> animalsObserver = getObserver();


        // Observer subscribing to observable
        animalObservables
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(animalsObserver);

    }

    private Observer<String> getObserver() {
        return new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.i(TAG, "onSubscribe: " + d.toString());
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
                Log.i(TAG, "onComplete: " + "All items are emitted successfully");
            }
        };
    }

    private Observable<String> getAnimalsObservable() {
        return Observable.just("Cheetah","Deer","Goat","Fox", "Lion", "Zebra");
    }
}
