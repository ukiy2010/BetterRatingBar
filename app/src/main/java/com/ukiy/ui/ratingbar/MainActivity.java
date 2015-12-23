package com.ukiy.ui.ratingbar;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    private BetterRatingBar rb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rb= (BetterRatingBar)findViewById(R.id.rb);

        rb.setEmptyDrawable(ContextCompat.getDrawable(this, R.mipmap.ic_launcher));
        rb.setRating(4.5f);
        rb.setStepSize(0.5f);
    }
}
