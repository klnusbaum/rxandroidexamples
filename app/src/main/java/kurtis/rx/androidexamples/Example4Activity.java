package kurtis.rx.androidexamples;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import rx.Observer;
import rx.subjects.PublishSubject;

/**
 * Example 4: And Introduction to Subjects
 *
 * So far we've seen Observables (and their cousin the Single) and Observers. Now it's time to introduce another
 * Rx concept, the Subject. Subjects are special objects that are _both_ and Observable and an Observer. I like to
 * think of Subjects as a pipe. You can put things into one end of the Subject and it will come out the other
 * end of the Subject. Let's see this in action.
 */
public class Example4Activity extends AppCompatActivity {

    private TextView mCounterDisplay;
    private Button mIncrementButton;
    private PublishSubject<Integer> mCounterEmitter;

    private int mCounter = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureLayout();
        createCounterEmitter();
    }

    private void createCounterEmitter() {
        /**
         * For this example we're going to use a PublishSubject. There several different types of Subjects and they
         * primarily differ on their "in-out" behavior. With a PublishSubject, as soon as you put something in
         * one end of the pipe it immediately comes out the other end of the pipe.
         *
         * Speaking of the "out" end of the pipe, that's what we're going to hook up first. We said that
         * PublishSubjects were Observables which means we can Observe them like we would any other Observable. This
         * is how we "watch things come out of the other end of the pipe". We set up a fairly simple Observable that
         * just changes what our mCounterDisplay shows to the user.
         *
         * But how do we put things into the pipe? Let's go down to the onIncrementButtonClick method to see.
         */
        mCounterEmitter = PublishSubject.create();
        mCounterEmitter.subscribe(new Observer<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Integer integer) {
                  mCounterDisplay.setText(String.valueOf(integer));
            }
        });
    }

    private void configureLayout() {
        setContentView(R.layout.activity_example_4);
        configureCounterDisplay();
        configureIncrementButton();
    }

    private void configureCounterDisplay() {
        mCounterDisplay = (TextView) findViewById(R.id.counter_display);
        mCounterDisplay.setText(String.valueOf(mCounter));
    }

    private void configureIncrementButton() {
        mIncrementButton = (Button) findViewById(R.id.increment_button);
        mIncrementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onIncrementButtonClick();
            }
        });
    }

    private void onIncrementButtonClick() {
        /**
         * Ah! Here we are. Since Subjects are also Observers that means they have an onNext method (and an onCompleted
         * and an onError). This means we can put stuff into the pipe by simply calling onNext. Using this we can
         * increment our counter and put into our pipe using onNext. The value will come out up in the onNext of the
         * Observable we made up in createCounterEmitter. It's like on one end of the pipe we observe the increment
         * button being clicked and we communicate that to the Observer over on the other end of the pipe.
         *
         * That's all we're gonna say about Subjects for the time being. But we'll come back to them later. This may
         * seem like a bit of a contrived example. For instance, we could have easily accomplished the same behavior
         * here by simply updating the mCounterDisplay in the OnClickListener of our mIncrementButton. What we should
         * take away though is that Subjects allow observe changes or new values in one part of our app and then
         * communicate those changes over to another Observable elsewhere in our app.
         */
        mCounter++;
        mCounterEmitter.onNext(mCounter);
    }
}
