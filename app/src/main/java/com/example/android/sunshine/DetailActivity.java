package com.example.android.sunshine;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class DetailActivity extends AppCompatActivity {

    String mForecast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();

        if (intent != null){
            if (intent.hasExtra(Intent.EXTRA_TEXT)){
                mForecast = intent.getStringExtra(Intent.EXTRA_TEXT);
            }
        }

    }
}