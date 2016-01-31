package kurtis.rx.androidexamples;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;

/**
 * Example 1: Staring Off
 *
 * Welcome friend! So glad to see you! So you're a little curious about this RxJava stuff and how it applies to
 * Android? Maybe you've seen it used a few places but you're still a little confused and would like some
 * clarifications. Well look no further!
 *
 * The goal here is to teach you a little bit about RxJava and specifically, how it can be used
 * in android to do some really slick stuff. When I was first introduced to RxJava I was mostly
 * cargo coding with it. Some of it made sense, but I had a deep misunderstanding of some of the fundamentals.
 * I was also really frustrated by a lack of good examples that actually explained what the heck was going on.
 *
 * In an effort to save you some leg work (and save you from some of my mistakes) I decided to throw together a few
 * examples. Hopefully we can help you get your feet wet with Rx and understand some of the core principles at
 * play. In the coming examples, we'll show you _exactly_ how to use Rx for asynchronous loading, one of the more
 * common uses of Rx in Android. And we'll follow up with a few more examples showing some of the advanced features
 * that Rx provides. I'd also encourage you to peek over at the RxJava source code at any point where you feel like
 * doing so would be beneficial. For the most part (with some exceptions) the source code is very well written and
 * documented. It was super helpful for me when I was first learning.
 *
 * But before we jump into the nitty gritty, let's start off with something basic. Rx, at it's core, is about streams.
 * Steams of data. These streams are produced by a wonderful set of objects known as Observables. Observable are said
 * to "emit" values. They're kind of the opposite of iterators. With iterators you "pull" each new value as you loop
 * through the iterator in. With Observables, you get pushed values and you have to respond to them with
 *
 * *drum roll please*
 *
 * Observers! Observers watch Observables and do certain actions when the Observable emits a value. It's worth
 * noting that Observables can also do two other things: they can inform an Observer that an error has occurred,
 * and they can inform an observer that it has finished emitting values.
 *
 * All three of these actions, emitting a value, informing an error has occurred, and informing that there are no
 * more values that will be emitted, are encapsulated in the Observer interface. The corresponding functions are
 * onNext, onError, and onCompleted. Let's see an example of a simple Observable and Observer in action.
 */
public class Example1Activity extends AppCompatActivity {

    RecyclerView mColorListView;
    SimpleStringAdapter mSimpleStringAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureLayout();
        createObservable();
    }

    private void createObservable() {

        /**
         * To kick things off, let's start with a relatively contrived example. Let's make an Observable that emits a
         * single value, a list of strings. We'll do this by using the Observable.just method. This method
         * creates an Observable such that when an Observer subscribes (more on that in a minute), the onNext of the
         * Observer is immediately called with the given value. If there are no errors (which in this case there will
         * be none) then onCompleted is called.
         */
        Observable<List<String>> listObservable = Observable.just(getColorList());

        /**
         * So here we are. This is where the magic happens. We're going to create an anonymous Observer that
         * _reacts_ (see what I did there?) to our Observable. We do this by defining the Observer's behavior for
         * onCompleted, onError, and onNext. We really only care about onNext here since we don't have any special
         * behavior we want to do when the stream is finished or if there is an error.
         *
         * As mentioned above, upon subscribing to the Observable, our onNext (and subsequently onCompleted) callback
         * will be called immediately. This is how Observables created with Observable.just work. I find it useful to
         * think about an Observable in terms of what it does when it is subscribed to. In fact, as of this
         * writing, if you look at the constructor for the Observable class, you'll see it takes a single argument: an
         * OnSubscribe object. Observables are in many ways defined by their behavior upon being subscribed to. Let's
         * take care to remember this, as it will come in handy later. In fact, I'm going to state it again since it's
         * so important:
         *
         *   Observables are in many ways defined by their behavior upon being subscribed to.
         */
        listObservable.subscribe(new Observer<List<String>>() {

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(List<String> colors) {
                /**
                 * As soon as this Observer subscribes we'll get our callback here with the list of colors we gave
                 * to the Observable.just method above. We then display the list of colors. Pretty simple, right?
                 *
                 * Well that's it for part 1. I wanna leave you with one last bit of information regarding some of
                 * the semantics around observables. I'm just gonna straight up copy the Rx docs here:
                 *     " By the terms of the Observable contract, it may call onNext zero or more times, and then may
                 *       follow those calls with a call to either onCompleted or onError but not both, which will be
                 *       its last call. "
                 * Basically, once an observer has completed or errored out, it's done.
                 */
                mSimpleStringAdapter.setStrings(colors);
            }
        });

    }

    private void configureLayout() {
        setContentView(R.layout.activity_example_1);
        mColorListView = (RecyclerView) findViewById(R.id.color_list);
        mColorListView.setLayoutManager(new LinearLayoutManager(this));
        mSimpleStringAdapter = new SimpleStringAdapter(this);
        mColorListView.setAdapter(mSimpleStringAdapter);
    }

    private static List<String> getColorList() {
        ArrayList<String> colors = new ArrayList<>();
        colors.add("blue");
        colors.add("green");
        colors.add("red");
        colors.add("chartreuse");
        colors.add("Van Dyke Brown");
        return colors;
    }
}
