/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;

import java.net.URL;

// S03.02 (8) Implement ForecastAdapterOnClickHandler from the MainActivity
// S05.01 COMPLETED (1) Implement the proper LoaderCallbacks interface and the methods of that interface
public class MainActivity extends AppCompatActivity implements ForecastAdapter.ForecastAdapterOnClickHandler, LoaderManager.LoaderCallbacks<String[]> {

    public static final String TAG = MainActivity.class.getSimpleName();

    // S03.01 (33) Delete mWeatherTextView
    // S03.01 (34) Add a private RecyclerView variable called mRecyclerView
    private RecyclerView mRecyclerView;

    // S03.01 (35) Add a private ForecastAdapter variable called mForecastAdapter
    private ForecastAdapter mForecastAdapter;

    // Create a variable to store a reference to the error message TextView
    private TextView mErrorMessageDisplay;
    // Create a ProgressBar variable to store a reference to the ProgressBar
    private ProgressBar mLoadingIndicator;

    private static final int FORECAST_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        // S03.01 (36) Delete the line where you get a reference to mWeatherTextView
        // S03.01 (37) Use findViewById to get a reference to the RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);
        // Find the TextView for the error message using findViewById
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        // S03.01 (38) Create layoutManager, a LinearLayoutManager with VERTICAL orientation and shouldReverseLayout == false
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        // S03.01 (41) Set the layoutManager on mRecyclerView
        mRecyclerView.setLayoutManager(layoutManager);
        // S03.01 (42) Use setHasFixedSize(true) on mRecyclerView to designate that all items in the list will have the same size
        mRecyclerView.setHasFixedSize(true);

        // S03.01 (43) set mForecastAdapter equal to a new ForecastAdapter
        /*
         * The ForecastAdapter is responsible for linking our weather data with the Views that
         * will end up displaying our weather data.
         */
        // S03.02 (11) Pass in 'this' as the ForecastAdapterOnClickHandler
        mForecastAdapter = new ForecastAdapter(this);
        // S03.01 (44) Use mRecyclerView.setAdapter and pass in mForecastAdapter
        /* Setting the adapter attaches it to the RecyclerView in our layout. */
        mRecyclerView.setAdapter(mForecastAdapter);

        // Find the ProgressBar using findViewById
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        // S05.01 COMPLETED (7) Remove the code for the AsyncTask and initialize the AsyncTaskLoader

        int loaderId = FORECAST_LOADER_ID;
        LoaderManager.LoaderCallbacks<String[]> callback = MainActivity.this;
        Bundle bundleForLoader = null;

        getSupportLoaderManager().initLoader(loaderId, bundleForLoader, callback);

    }

    // S03.02 (9) Override ForecastAdapterOnClickHandler's onClick method
    // S03.02 (10) Show a Toast when an item is clicked, displaying that item's weather data
    @Override
    public void onClick(String weatherForDay) {
        Context context = MainActivity.this;
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, weatherForDay);
        startActivity(intent);
    }

    // Create a method called showWeatherDataView that will hide the error message and show the weather data
    private void showWeatherDataView(){
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        // S03.01 (44) Show mRecyclerView, not mWeatherTextView
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    // Create a method called showErrorMessage that will hide the weather data and show the error message
    private void showErrorMessage(){
        // S03.01 (44) Show mRecyclerView, not mWeatherTextView
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    /**
     * This method is used when we are resetting data, so that at one point in time during a
     * refresh of our data, you can see that there is no data showing.
     */
    private void invalidateData() {
        mForecastAdapter.setWeatherData(null);
    }

    // S05.01 COMPLETED (2) Within onCreateLoader, return a new AsyncTaskLoader that looks a lot like the existing FetchWeatherTask.
    // S05.01 COMPLETED (3) Cache the weather data in a member variable and deliver it in onStartLoading.

    // S05.01 COMPLETED (4) When the load is finished, show either the data or an error message if there is no data

    @NonNull
    @Override
    public Loader<String[]> onCreateLoader(int id, @Nullable Bundle loaderArgs) {
        return new AsyncTaskLoader<String[]>(this) {

            String[] mWeatherData = null;

            @Override
            protected void onStartLoading() {

                if (mWeatherData != null){
                    deliverResult(mWeatherData);
                } else {
                    mLoadingIndicator.setVisibility(View.VISIBLE);
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public String[] loadInBackground() {

                String locationQuery = SunshinePreferences.getPreferredWeatherLocation(MainActivity.this);
                URL weatherRequestUrl = NetworkUtils.buildUrl(locationQuery);

                try {

                    String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl);
                    String[] simpleWeatherJsonData = OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(MainActivity.this,jsonWeatherResponse);
                    return simpleWeatherJsonData;

                } catch (Exception e){
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(@Nullable String[] data) {
                mWeatherData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String[]> loader, String[] data) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mForecastAdapter.setWeatherData(data);
        if (data == null){
            showErrorMessage();
        } else {
            showWeatherDataView();
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String[]> loader) {

    }

    // S05.01 COMPLETED (6) Remove any and all code from MainActivity that references FetchWeatherTask


    private void openLocationInMap(){
        String addressString = "1600 Ampitheatre Parkway, CA";
        Uri geoLocation = Uri.parse("geo:0,0?q=" + addressString);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);

        if (intent.resolveActivity(getPackageManager()) != null){
            startActivity(intent);
        } else {
            Log.d(TAG, "Couldn't call " + geoLocation.toString() + ", no receiving app is installed!");
        }
    }

    // Override onCreateOptionsMenu to inflate the menu for this Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // use getMenuInflater().inflate to inflate the menu
        getMenuInflater().inflate(R.menu.forecast, menu);
        // Return true to display the menu
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        // S05.01 COMPLETED (5) Refactor the refresh functionality to work with our AsyncTaskLoader
        if (id == R.id.action_refresh){
            // S03.01 (46) Instead of setting the text to "", set the adapter to null before refreshing
            invalidateData();
            getSupportLoaderManager().restartLoader(FORECAST_LOADER_ID, null, this);
            return true;
        }

        if (id == R.id.action_map){
            openLocationInMap();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}