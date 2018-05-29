package com.hafsa.notes;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.MaybeObserver;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.SingleObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import rx.Observable;
import rx.Subscriber;
import rx.observables.MathObservable;

public class Math_Operator_RxJava_Activity extends AppCompatActivity {

    private static final String TAG = "Math_Operator_RxJava_Ac";

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math__operator__rx_java_);

        // Max() operator finds the maximum valued item in the Observable sequence and emits that value.
        //maxOperator();

        // The same operation can be done on other primitive datatype such as Float, Double, Long. Below examples emits the max value of float numbers.
        //floatObservable_Max();

        // Min() operator emits the minimum valued item in the Observable data set
        //minOperator();

        // Calculates the sum of all the items emitted by an Observable and emits only the Sum value.
        // In the below example, sumInteger() is used to calculate the sum of Integers. Likewise, we have sumFloat(), sumDouble() and sumLong() available to calculate sum of other primitive datatypes.
        //sumOperator();

        // Calculates the average of all the items emitted by an Observable and emits only the Average value.
        // To calculate average of other datatypes, averageFloat(), averageDouble() and averageLong() are available.
        //averageOperator();

        // Counts number of items emitted by an Observable and emits only the count value.
        //countOperator();

        // Reduce applies a function on each item and emits the final result.
        // First, it applies a function to first item, takes the result and feeds back to same function on second item. This process continuous until the last emission.
        // Once all the items are over, it emits the final result.
        reduceOperator();

       // customDataTypesOnMathOperator();
    }

   /* @RequiresApi(api = Build.VERSION_CODES.N)
    private void customDataTypesOnMathOperator() {
        List<Person> persons = new ArrayList<>();
        Observable<Person> observable = Observable.from(persons);

        MathObservable.from(observable)
                .max(Comparator.comparing(Person::getAge))
                .subscribe(new Observer<Person>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage() );
                    }

                    @Override
                    public void onNext(Person person) {
                        Log.i(TAG, "onNext: " + person.getName() + " : " + person.getAge());
                    }
                });
    }
*/
    private List<Person> getPersons() {
        List<Person> persons = new ArrayList<>();

        Person p1 = new Person("Hamdan Aathif", 4);
        persons.add(p1);
        Person p2 = new Person("Muhammad Raiyan", 4);
        persons.add(p2);
        Person p3 = new Person("Nida Fatima", 9);
        persons.add(p3);

        return persons;

    }

    private void reduceOperator() {
        io.reactivex.Observable
                .range(1, 10)
                .reduce(new BiFunction<Integer, Integer, Integer>() {
                    @Override
                    public Integer apply(Integer integer, Integer integer2) throws Exception {
                        Log.i(TAG, "apply:1 " + integer);
                        Log.i(TAG, "apply:2 " + integer2);
                        return integer + integer2;
                    }
                })
                .subscribe(new MaybeObserver<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        
                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        Log.i(TAG, "onSuccess: " + integer);
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

    private void countOperator() {
        getUsersObservable()
                .filter(new Predicate<User>() {
                    @Override
                    public boolean test(User user) throws Exception {
                        return user.getGender().equalsIgnoreCase("male");
                    }
                })
                .count()
                .subscribeWith(new SingleObserver<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onSuccess(Long aLong) {
                        Log.i(TAG, "onSuccess: " + aLong);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage() );
                    }
                });
    }

    private io.reactivex.Observable<User> getUsersObservable() {
        String[] maleUsers = new String[]{"Mark", "John", "Trump", "Obama"};
        String[] femaleUsers = new String[]{"Lucy", "Scarlett", "April"};

        final List<User> users = new ArrayList<>();

        for (String name : maleUsers) {
            User user = new User();
            user.setName(name);
            user.setGender("male");

            users.add(user);
        }

        for (String name : femaleUsers) {
            User user = new User();
            user.setName(name);
            user.setGender("female");

            users.add(user);
        }
        return io.reactivex.Observable
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

    private void averageOperator() {
        Integer[] numbers = {5, 101, 404, 22, 3, 1024, 65};
        Observable<Integer> observable = Observable.from(numbers);
        MathObservable
                .averageInteger(observable)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage() );
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.i(TAG, "onNext: " + integer);
                    }
                });
    }

    private void sumOperator() {
        Integer[] numbers = {5, 101, 404, 22, 3, 1024, 65};
        Observable<Integer> observable = Observable.from(numbers);
        MathObservable
                .sumInteger(observable)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage() );
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.i(TAG, "onNext: " + integer);
                    }
                });
    }

    private void minOperator() {
        Integer[] numbers = {5, 101, 404, 22, 3, 1024, 65};
        Observable<Integer> observable = Observable.from(numbers);
        MathObservable
                .min(observable)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage() );
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.i(TAG, "onNext: " + integer);
                    }
                });
    }

    private void floatObservable_Max() {
        Observable<Float> observable = Observable.just(10.5f, 14.5f, 11.5f, 5.6f);
        MathObservable
                .max(observable)
                .subscribe(new Subscriber<Float>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage() );
                    }

                    @Override
                    public void onNext(Float aFloat) {
                        Log.i(TAG, "onNext: " + aFloat);
                    }
                });
    }

    private void maxOperator() {
        Integer[] numbers = {5, 101, 404, 22, 3, 1024, 65};
        rx.Observable<Integer> observable = rx.Observable.from(numbers);
        MathObservable
                .max(observable)
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage() );
                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.i(TAG, "onNext: " + integer);
                    }
                });
    }
}
