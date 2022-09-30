package com.example.android.sunshine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {

    private String mForecast;
    private TextView mDisplayWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mDisplayWeather = (TextView) findViewById(R.id.tv_display_weather);

        Intent intent = getIntent();

        if (intent != null){
            if (intent.hasExtra(Intent.EXTRA_TEXT)){
                mForecast = intent.getStringExtra(Intent.EXTRA_TEXT);
                mDisplayWeather.setText(mForecast);
            }
        }

    }
}