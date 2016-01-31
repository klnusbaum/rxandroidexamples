package kurtis.rx.androidexamples;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import rx.Single;
import rx.SingleSubscriber;
import rx.functions.Func1;

/**
 * Example 5: Introducing Map
 *
 * This is going to be a relatively short example. We just want to introduce the concept of a map for those who
 * haven't seen it before. If you've ever worked with functional programming, you're probably familiar with the
 * map operator. You can think of map as a function that takes in one value and outputs another value. Usually there
 * is some relationship between value put in to the map and the value that is output. Let's take a look at a simple
 * example.
 */
public class Example5Activity extends AppCompatActivity {

    private TextView mValueDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureLayout();

        /**
         * We're going to create a single that emits one value, 4. We want to eventually display this
         * value so we need to convert it from an Integer to a String. One way we can do this is using map. Like
         * we said above, maps can take in one value and output another. This suites our purpose quite well. Since
         * our Single will emit one Integer of value 4, we'll use map to convert it to a String which can then be easily
         * displayed.
         *
         * This is a fairly trivial use of the map function. But maps are actually quite powerful! As we'll see in the
         * next example, maps can be used to execute arbitrary code and help us transform data in very useful ways.
         */
        Single.just(4).map(new Func1<Integer, String>() {
            @Override
            public String call(Integer integer) {
                return String.valueOf(integer);
            }
        }).subscribe(new SingleSubscriber<String>() {
            @Override
            public void onSuccess(String value) {
                if (mValueDisplay != null) {
                    mValueDisplay.setText(value);
                }
            }

            @Override
            public void onError(Throwable error) {

            }
        });
    }

    private void configureLayout() {
        setContentView(R.layout.activity_example_5);
        mValueDisplay = (TextView) findViewById(R.id.value_display);
    }
}
