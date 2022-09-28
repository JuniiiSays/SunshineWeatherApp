package com.example.android.sunshine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

// S03.01 (15) Add a class called ForecastAdapter
// S03.01 (22) Extend RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder>
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder>{

    // S03.01 (23) Create a private string array called mWeatherData
    private String[] mWeatherData;

    // S03.02 (3) Create a final private ForecastAdapterOnClickHandler called mClickHandler
    private final ForecastAdapterOnClickHandler mClickHandler;

    // S03.02 (1) Add an interface called ForecastAdapterOnClickHandler
    // S03.02 (2) Within that interface, define a void method that access a String as a parameter

    public interface ForecastAdapterOnClickHandler{
        void onClick(String weatherForDay);
    }

    // S03.01 (47) Create the default constructor (we will pass in parameters in a later lesson)
    // S03.02 (4) Add a ForecastAdapterOnClickHandler as a parameter to the constructor and store it in mClickHandler
    public ForecastAdapter(ForecastAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    // S03.01 (24) Override onCreateViewHolder
    // S03.01 (25) Within onCreateViewHolder, inflate the list item xml into a view
    // S03.01 (26) Within onCreateViewHolder, return a new ForecastAdapterViewHolder with the above view passed in as a parameter

    @NonNull
    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.forecast_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new ForecastAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {
        String weatherForThisDay = mWeatherData[position];
        forecastAdapterViewHolder.mWeatherTextView.setText(weatherForThisDay);
    }

    @Override
    public int getItemCount() {
        if (mWeatherData == null){
            return 0;
        }
        return mWeatherData.length;
    }

    // S03.01 (16) Create a class within ForecastAdapter called ForecastAdapterViewHolder
    // S03.01 (17) Extend RecyclerView.ViewHolder
    // S03.02 (5) Implement OnClickListener in the ForecastAdapterViewHolder class
    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // S03.01 (18) Create a public final TextView variable called mWeatherTextView
        public final TextView mWeatherTextView;

        // S03.01 (19) Create a constructor for this class that accepts a View as a parameter
        // S03.01 (20) Call super(view)
        // S03.01 (21) Using view.findViewById, get a reference to this layout's TextView and save it to mWeatherTextView

        public ForecastAdapterViewHolder(@NonNull View view) {
            super(view);
             mWeatherTextView = (TextView) view.findViewById(R.id.tv_weather_data);
            // S03.02 (7) Call setOnClickListener on the view passed into the constructor (use 'this' as the OnClickListener)
            view.setOnClickListener(this);
        }

        // S03.02 (6) Override onClick, passing the clicked day's data to mClickHandler via its onClick method
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String weatherForDay = mWeatherData[adapterPosition];
            mClickHandler.onClick(weatherForDay);
        }
    }

    // S03.01 (31) Create a setWeatherData method that saves the weatherData to mWeatherData
    // S03.01 (32) After you save mWeatherData, call notifyDataSetChanged
    public void setWeatherData(String[] weatherData){
        mWeatherData = weatherData;
        notifyDataSetChanged();
    }

}
