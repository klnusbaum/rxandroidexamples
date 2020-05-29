package kurtis.rx.androidexamples;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import rx.Observer;
import rx.subjects.PublishSubject;

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
        mCounterEmitter = PublishSubject.create();
        mCounterEmitter.subscribe(new Observer<Integer>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Integer integer) {
                if(integer == 3) { //error produce
                    int t = 5 / 0;
                }
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
        mCounter++;
        mCounterEmitter.onNext(mCounter);
    }
}
