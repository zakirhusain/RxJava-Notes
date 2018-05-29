package com.hafsa.notes;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.hafsa.notes.rxjava.Address;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class Map_RxJavaActivity extends AppCompatActivity {


    private static final String TAG = "Map_RxJavaActivity";

    Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map__rx_java);

        // Map operator transform each item emitted by an Observable and emits the modified item
        //mapOperator();

        //

        getUserObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Function<User, ObservableSource<User>>() {
                    @Override
                    public ObservableSource<User> apply(User user) throws Exception {
                        return getAddressObservable(user);
                    }
                })
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(User user) {
                        Log.i(TAG, "onNext: " + user);
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

    private void mapOperator() {
        getUserObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<User, User>() {
                    @Override
                    public User apply(User user) throws Exception {
                        user.setEmail(String.format("%s@rxjava.com", user.getName()));
                        user.setName(user.getName().toUpperCase());
                        return user;
                    }
                })
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(User user) {
                        Log.i(TAG, "onNext: " + user);
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

    /**
     * Assume this as a network call
     * returns Users with address filed added
     */
    private Observable<User> getAddressObservable(final User user) {

        final String[] addresses = new String[]{
                "1600 Amphitheatre Parkway, Mountain View, CA 94043",
                "2300 Traverwood Dr. Ann Arbor, MI 48105",
                "500 W 2nd St Suite 2900 Austin, TX 78701",
                "355 Main Street Cambridge, MA 02142"
        };

        return Observable
                .create(new ObservableOnSubscribe<User>() {
                    @Override
                    public void subscribe(ObservableEmitter<User> emitter) throws Exception {
                        Address address = new Address();
                        address.setAddress(addresses[new Random().nextInt(2) + 0]);
                        if (!emitter.isDisposed()) {
                            user.setAddress(address);


                            // Generate network latency of random duration
                            int sleepTime = new Random().nextInt(1000) + 500;

                            Thread.sleep(sleepTime);
                            emitter.onNext(user);
                            emitter.onComplete();
                        }
                    }
                }).subscribeOn(Schedulers.io());
    }

    /**
     * Assume this method is making a network call and fetching Users
     * an Observable that emits list of users
     * each User has name and email, but missing email id
     */

    private Observable<User> getUserObservable() {
        String[] names = {"Hamdanu", "Raiyyanu", "Zakir", "Hussain"};
        final List<User> users = new ArrayList<>();
        for (String name : names) {
            User user = new User();
            user.setName(name);
            user.setGender("Male");
            users.add(user);
        }

        return Observable
                .create(new ObservableOnSubscribe<User>() {
                    @Override
                    public void subscribe(ObservableEmitter<User> emitter) throws Exception {
                        for (User user : users) {
                            if (!emitter.isDisposed()) {
                                emitter.onNext(user);
                            }
                        }

                        if (!emitter.isDisposed()) {
                            emitter.onComplete();
                        }
                    }
                }).subscribeOn(Schedulers.io());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposable.dispose();
    }
}
