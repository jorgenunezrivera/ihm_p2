package ihm.ihm_p2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;


public class Activity_infopueblo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_infopueblo);
        Intent intent = getIntent();
        String texto = intent.getStringExtra("npueblo");
        // Create the text view
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(texto);

        // Set the text view as the activity layout
        setContentView(textView);


    }
}
