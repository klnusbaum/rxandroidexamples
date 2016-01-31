package kurtis.rx.androidexamples;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Example 2: Using Rx for Async Loading
 *
 * When I think of RxJava in the context of Android, I primarily think of it's use for loading data asynchronously.
 * That said, Rx can do lots of things. But loading data asynchronously is where I've seen it used the most. Let's
 * dive right in and see a simple example of using Rx to load some data from a REST endpoint.
 */
public class Example2Activity extends AppCompatActivity {

    private Subscription mTvShowSubscription;
    private RecyclerView mTvShowListView;
    private ProgressBar mProgressBar;
    private SimpleStringAdapter mSimpleStringAdapter;
    private RestClient mRestClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRestClient = new RestClient(this);
        configureLayout();
        createObservable();
    }

    private void createObservable() {

        /**
         * In our last example, we used Observable.just to create our Observable. We'd like to do the same thing here,
         * but we have a bit of an issue. Whatever we pass to the Observable.just method is evaluated immediately, just
         * like any normal method. This is a problem for us because RestClient.getFavoriteTvShows is a blocking network
         * call. If we only use Observable.just, our call to RestClient.getFavoriteTvShows will be evaluated immediately
         * and block our UI thread. We need to find some way to have getFavoriteTvShows run off of the UI thread.
         *
         * Enter the Observable.fromCallable method. You can think of Observable.fromCallable as a wrapper around some
         * other arbitrary Observable creation code. But it gives us two important things:
         *   1. The creation code is not run until someone subscribes to the Observer.
         *   2. The creation code can be run on a different thread.
         *
         * This is key.
         *
         * We use fromCallable by passing it a Callable object. In this object, we do the actual creation of the thing
         * we want to return in the call method. In this case, we want to return a list of favorite tv shows that
         * we get by querying a REST endpoint.
         */
        Observable<List<String>> tvShowObservable = Observable.fromCallable(new Callable<List<String>>() {
            @Override
            public List<String> call() {
                return mRestClient.getFavoriteTvShows();
            }
        });

        /**
         * Whoa nelly. There's a lot going on here. Stay with me. Let's break it down.
         *
         * Alright, what's this subscribeOn? subscribeOn essentially alters the Observable we created above.
         * All of the code that this Observable would normally run (including
         * the code the gets run when the Observable is subscribed to) will now run on a different Thread. Perfect!
         * This means the logic in our Callable object (including the call to getFavoriteTvShows) will run on a
         * different thread since it's run when we subscribe to the Observable. But which thread will it run on?
         *
         * In this case we specify that the code is run on the "IO Scheduler". What's a Scheduler? For now we can
         * just think of a Scheduler as a separate thread for doing working. There's actually a little more going on
         * here but this description will suffice for our purposes.
         *
         * We've hit a bit of a snag now though. Our Observable is now running on our io Scheduler. This
         * means it's going to interact with our Observer on the io Scheduler as well. This is a problem. This
         * means out onNext method is going to get called on the io Scheduler. But our onNext code calls methods on
         * some of our views and view methods can only be called on the UI thread.
         *
         * There's a simple way to address this. We can tell Rx that we want to observe this Observable on our UI
         * thread, i.e. we want our onNext callback to be called on our UI thread. To do this, we'll call observeOn
         * and get back yet a further altered Observable. This one will now call all of the Observer callbacks on
         * whatever Scheduler we give it. In this case we want to observe on the android main thread (UI thread),
         * so we pass in AndroidSchedules.mainThread().
         *
         * That was a lot, so let's recap. We've got an Observable whose creation logic was encapsulated in a
         * Callable object. We did this so that
         * we could have it's creation code run on a different thread, the io Scheduler. We then have it emit
         * it's results back to the Observer on the main UI thread.
         *
         * There's one last thing. What's this mTvShowSubscription thing? When we subscribe an Observer to an
         * Observable a Subscription is created. A Subscription represents a connection between an Observer and an
         * Observable. Sometimes we need to sever this connection. Let's take a look at our onDestroy method below
         * to see why.
         */
        mTvShowSubscription = tvShowObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Observer<List<String>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(List<String> tvShows) {
                                displayTvShows(tvShows);
                            }
                        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        /**
         * If you've ever done threading work on Android before you know there's typically one huge problem:
         * what happens if your thread finishes it's execution _after_ an Activity has been torn down. This can
         * cause a whole host of problems including memory leaks (if the background work never completes) and
         * NullPointerExceptions if you try to mess around with your views after the Activity has been destroyed.
         *
         * Subscriptions allow us to deal with this problem. We can say "Hey, Observable, this Observer doesn't want
         * to receive your emissions anymore. Please disconnect from the Observer." We do this by calling unsubscribe.
         *
         * After calling unsubscribe, the anonymous Observer we created above will no longer receive emissions.
         * This ensures we'll never try to call any view related methods after the Activity has been
         * destroyed because our onNext callback will no longer get called.
         *
         * Also, after calling unsubscribe there are no longer any references to the anonymous Observer we created
         * above. This means it can be safely garbage collected. This is good because that Observer had an implicit
         * reference to this Activity. If the Observer doesn't go away, our Activity can never get garbage collected.
         *
         * Phew. Well there we go. If you've made it this far, you're past the hard part. Now that you've groked this
         * stuff, the rest should kind of fall into place. Congrats! Let's do a quick recap:
         *
         *   - Observable.defer allows us to defer the creation of an Observable which can be handy when the result
         *   you want to emit from your Observable needs to be calculated off of the UI thread.
         *   - subscribeOn allows us to run our value calculation code on a specific thread, namely one that is not
         *   the UI thread.
         *   - observeOn allows us to then observe the emitted values of an Observable on an appropriate thread, namely
         *   the main UI thread.
         *   - We should always unsubscribe our Observers in order to prevent nasty things from happening when
         *   we're using Observables to load thing asynchronously.
         */
        if (mTvShowSubscription != null && !mTvShowSubscription.isUnsubscribed()) {
            mTvShowSubscription.unsubscribe();
        }
    }

    private void displayTvShows(List<String> tvShows) {
        mSimpleStringAdapter.setStrings(tvShows);
        mProgressBar.setVisibility(View.GONE);
        mTvShowListView.setVisibility(View.VISIBLE);
    }

    private void configureLayout() {
        setContentView(R.layout.activity_example_2);
        mProgressBar = (ProgressBar) findViewById(R.id.loader);
        mTvShowListView = (RecyclerView) findViewById(R.id.tv_show_list);
        mTvShowListView.setLayoutManager(new LinearLayoutManager(this));
        mSimpleStringAdapter = new SimpleStringAdapter(this);
        mTvShowListView.setAdapter(mSimpleStringAdapter);
    }
}
