package com.example.tourmatebatch14.Utils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    public static final String  BASE_URL = "https://api.openweathermap.org/";
    public static final String forecast_url = "https://samples.openweathermap.org/";
    public static Retrofit getClient(){

       return new Retrofit.Builder()
               .baseUrl(BASE_URL)
               .addConverterFactory(GsonConverterFactory.create())
               .build();
    }

    public static Retrofit getForecastClient(){

        return new Retrofit.Builder()
                .baseUrl(forecast_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
}
