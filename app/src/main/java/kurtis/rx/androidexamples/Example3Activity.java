package kurtis.rx.androidexamples;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.Callable;

import rx.Single;
import rx.SingleSubscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Example 3: Cleaning Things Up A Bit
 *
 * Our previous example was pretty good. We dove into a lot of details about how Observables work and learned
 * how to load data asynchronously. Let's take a brief look at how we could streamline the previous example a little
 * bit.
 */
public class Example3Activity extends AppCompatActivity {

    private Subscription mTvShowSubscription;
    private RecyclerView mTvShowListView;
    private ProgressBar mProgressBar;
    private TextView mErrorMessage;
    private SimpleStringAdapter mSimpleStringAdapter;
    private RestClient mRestClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRestClient = new RestClient(this);
        configureLayout();
        createSingle();
    }

    private void createSingle() {
        /**
         * Observables are great, but in many cases they're kind of overkill. Many times when using an Observable,
         * we're only going to have one result. So all of the semantics around onNext and onCompleted are
         * a little much. As it turns out, there's a simpler version of an Observable called a Single.
         *
         * Singles work almost exactly the same as Observables. But instead of their Observers having three callbacks
         * (onCompleted, onNext, and onError), they Observers of a Single only have two callbacks: onSuccess
         * and onError. Cool. Less code for us to write.
         *
         * Just like Observable, we can create asynchronous loading Singles by using the fromCallable method.
         */
        Single<List<String>> tvShowSingle = Single.fromCallable(new Callable<List<String>>() {
            @Override
            public List<String> call() throws Exception {
                /**
                 * Uncomment me (and comment out the line below) to see what happens when an error occurs.
                 *
                 * return RestClient.getFavoriteTvShowsWithException();
                 */
                return mRestClient.getFavoriteTvShows();
            }
        });

        /**
         * This should look pretty familiar. We're doing a lot of the same stuff we did in Example 2. We're calling
         * subscribeOn to make sure that our call to the RestClient is run off of the UI thread and we're calling
         * observeOn to make sure that the results of the Single are emitted on the UI thread.
         *
         * Instead of using an Observer, we're just using a class called SingleSubscriber. It's very similar to
         * an Observer except that it just has the two methods we mentioned above: onSuccess and onError. A
         * SingleSubscriber is to a Single what an Observer is to an Observable.
         *
         * Subscribing to a Single also results in the creation of a Subscription object. This Subscription behaves the
         * same way as in Example 2 and we make sure to unsubscribe in our onDestroy method.
         *
         * That's all for Example 3. I added a bit of error handling logic in this Example just to give you a feel
         * for what it looks like. You can uncomment the line up in our call method to see what happens when we
         * make a REST call that generates an error.
         */
        mTvShowSubscription = tvShowSingle
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleSubscriber<List<String>>() {
                    @Override
                    public void onSuccess(List<String> tvShows) {
                        displayTvShows(tvShows);
                    }

                    @Override
                    public void onError(Throwable error) {
                        displayErrorMessage();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mTvShowSubscription != null && !mTvShowSubscription.isUnsubscribed()) {
            mTvShowSubscription.unsubscribe();
        }
    }

    private void displayTvShows(List<String> tvShows) {
        mSimpleStringAdapter.setStrings(tvShows);
        mProgressBar.setVisibility(View.GONE);
        mTvShowListView.setVisibility(View.VISIBLE);
    }

    private void displayErrorMessage() {
        mProgressBar.setVisibility(View.GONE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }

    private void configureLayout() {
        setContentView(R.layout.activity_example_3);
        mErrorMessage = (TextView) findViewById(R.id.error_message);
        mProgressBar = (ProgressBar) findViewById(R.id.loader);
        mTvShowListView = (RecyclerView) findViewById(R.id.tv_show_list);
        mTvShowListView.setLayoutManager(new LinearLayoutManager(this));
        mSimpleStringAdapter = new SimpleStringAdapter(this);
        mTvShowListView.setAdapter(mSimpleStringAdapter);
    }
}
