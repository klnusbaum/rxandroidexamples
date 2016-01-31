package kurtis.rx.androidexamples;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

/**
 * Example 6: Putting it all Together
 *
 * Phew. We've come a long way since the beginning. Time to put everything we've learned together into one last
 * mega-example. Here we're going to show you how to use Rx to create a simple suggestions search and we're going to
 * use every concept we've encountered so far. Let's dive in.
 */
public class Example6Activity extends AppCompatActivity {

    private RestClient mRestClient;
    private EditText mSearchInput;
    private TextView mNoResultsIndicator;
    private RecyclerView mSearchResults;
    private SimpleStringAdapter mSearchResultsAdapter;

    private PublishSubject<String> mSearchResultsSubject;
    private Subscription mTextWatchSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRestClient = new RestClient(this);
        configureLayout();
        createObservables();
        listenToSearchInput();
    }

    private void createObservables() {

        /**
         * Let's start off creating a PublishSubject. We're going to use this Subject to pipe user search
         * requests to our RestClient. We're essentially going to construct the following pipeline
         *
         * Search Request -> PublishSubject -> RestClient Request -> Server Response -> Display Response
         *
         * Let's look at the details of how we're going to setup this pipeline.
         */
        mSearchResultsSubject = PublishSubject.create();

        /**
         * Once again, we've got a lot going on here. Let's break things down one-by-one to understand the whole
         * chain we've set up.
         *
         * The first things we've got is a debounce. What the heck is this thing? Well, if you take a peak below,
         * you'll see how things are going to come into our pipeline from an EditText. Using the text watcher, we'll
         * see every, single time the user adds or removes a character from their search. This is neat, but
         * we don't want to send out a request to the server on every single keystroke. We'd like to wait a little
         * bit for the user to stop typing and _then_ send our search request to the server. This is what debounce
         * allows us to do. It tells mSearchResultsSubject to _only_ emit the last value that came into it _after_
         * nothing new has come into the mSearchResultsSubject for 400 milliseconds. Essentially, this means our subject
         * won't emit the search string until the user hasn't changed the string for 400 milliseconds, and at the
         * end of the 400 milliseconds it will only emit the latest search string the user entered. Perfect! This
         * should help us avoid unnecessary searches and a UI that is constantly changing as a result of every single
         * keystroke.
         *
         * We want to use what the debounce emits to query our server via our RestClient. Since querying our RestClient
         * is an IO operation we need to observe the emissions of debounce on the io thread. So boom,
         * observeOn(Schedulers.io()).
         *
         * Cool, so now we're emitting the our search results onto the IO thread. This is where the magic of map
         * comes in. We're going to use map to "map" our search queries to a list of search results. Because
         * map can run any arbitrary function, we'll use our RestClient to "transform" our search query into the list
         * of actual results we want to display.
         *
         * Since our map was run on the io thread, and we want to use the results it emits to populate our views,
         * we then need to switch back to the UI thread. So we add an observeOn(AndroidSchedulers.mainThread()). Now
         * we've got the search results being emitted on the UI thread. Note the ordering of all our observerOns here.
         * They're critical. We've essentially setup the following "chain" of emissions:
         *
         *                                mSearchResultsSubject
         *                                         |
         *                                         |
         *                                         V
         *                                      debounce
         *                                         ||
         *                                         ||
         *                                         V
         *                                        map
         *                                         |
         *                                         |
         *                                         V
         *                                     subscribe
         *
         * The | represents emissions happening on the UI thread and the || represents emissions happening on the
         * io thread.
         *
         * Finally, we use the results from the search to display (or not display) the search results to the user.
         * Pretty neat, right?!
         *
         * Well that's it for me. I hope these examples have been helpful. There are of course many more aspects of
         * Rx that you should totally explore if you're into that. But hopefully by now you've got a good
         * understanding of the basics, and that exploration should be much easier. If you have any feedback for
         * us, or any other examples you'd like to see, please drop us a line. And if you have any
         * examples _you'd_ like to add, please feel free to submit a pull request. Happy coding!
         */
        mTextWatchSubscription = mSearchResultsSubject
                .debounce(400, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .map(new Func1<String, List<String>>() {
                    @Override
                    public List<String> call(String s) {
                        return mRestClient.searchForCity(s);
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(List<String> cities) {
                        handleSearchResults(cities);
                    }
                });
    }

    private void handleSearchResults(List<String> cities) {
        if (cities.isEmpty()) {
            showNoSearchResults();
        } else {
            showSearchResults(cities);
        }
    }

    private void showNoSearchResults() {
        mNoResultsIndicator.setVisibility(View.VISIBLE);
        mSearchResults.setVisibility(View.GONE);
    }

    private void showSearchResults(List<String> cities) {
        mNoResultsIndicator.setVisibility(View.GONE);
        mSearchResults.setVisibility(View.VISIBLE);
        mSearchResultsAdapter.setStrings(cities);
    }

    private void listenToSearchInput() {
        mSearchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSearchResultsSubject.onNext(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void configureLayout() {
        setContentView(R.layout.activity_example_6);
        mSearchInput = (EditText) findViewById(R.id.search_input);
        mNoResultsIndicator = (TextView) findViewById(R.id.no_results_indicator);
        mSearchResults = (RecyclerView) findViewById(R.id.search_results);
        mSearchResults.setLayoutManager(new LinearLayoutManager(this));
        mSearchResultsAdapter = new SimpleStringAdapter(this);
        mSearchResults.setAdapter(mSearchResultsAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTextWatchSubscription != null && !mTextWatchSubscription.isUnsubscribed()) {
            mTextWatchSubscription.unsubscribe();
        }
    }
}
