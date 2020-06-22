package com.example.tourmatebatch14.currentweatheinfo;

import com.example.tourmatebatch14.forecastweather.ForecastWeatherResponseBody;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface WeatherServiceApi {
    @GET
    Call<CurrentWeatherResponseBody> getCurrentWeatherInfo(@Url String endUrl);

    @GET
    Call<ForecastWeatherResponseBody> getForecastWeatherInfo(@Url String endUrl);
}
