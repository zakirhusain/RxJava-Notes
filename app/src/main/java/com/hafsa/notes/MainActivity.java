package com.hafsa.notes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    TextView textView;
    private Disposable mDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Observable
        Observable<String> animalsObservable = getObservable();


        textView = findViewById(R.id.text_view);

        // Observer
        Observer<String> animalsObserver = getAnimalsObserver();


        // observer subscribing to observable
        animalsObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(animalsObserver);
    }

    private Observer<String> getAnimalsObserver() {
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
                Log.i(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete: ");
            }
        };
    }

    private Observable<String> getObservable() {
        return Observable.just("Ant", "Cat", "Bee",
                "Dog", "Fox");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // don't send events once the activity is destroyed
        mDisposable.dispose();
    }
}
