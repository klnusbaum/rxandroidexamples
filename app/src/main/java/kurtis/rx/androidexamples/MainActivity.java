package kurtis.rx.androidexamples;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;



public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button firstButton;
    private Button button2;
    private Button firstButton3;
    private Button firstButton4;
    private Button firstButton5;
    private Button firstButton6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureLayout();
    }

    private void configureLayout() {
        setContentView(R.layout.main_activity2);
        firstButton = (Button) findViewById(R.id.button1);
        firstButton.setOnClickListener(this);
        button2 = (Button) findViewById(R.id.button2);
        button2.setOnClickListener(this);
        firstButton3 = (Button) findViewById(R.id.button3);
        firstButton3.setOnClickListener(this);
        firstButton4 = (Button) findViewById(R.id.button4);
        firstButton4.setOnClickListener(this);
        firstButton5 = (Button) findViewById(R.id.button5);
        firstButton5.setOnClickListener(this);
        firstButton6 = (Button) findViewById(R.id.button6);
        firstButton6.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        if (view == firstButton){
            startActivity(new Intent(this,Example1Activity.class));
        }
        if (view == button2){
            startActivity(new Intent(this,Example2Activity.class));
        }
        if (view == firstButton3){
            startActivity(new Intent(this,Example3Activity.class));
        }
        if (view == firstButton4){
            startActivity(new Intent(this,Example4Activity.class));
        }
        if (view == firstButton5){
            startActivity(new Intent(this,Example5Activity.class));
        }
        if (view == firstButton6){
            startActivity(new Intent(this,Example6Activity.class));
        }
    }


}
