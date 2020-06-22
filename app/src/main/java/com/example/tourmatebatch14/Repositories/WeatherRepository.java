package com.example.tourmatebatch14.Repositories;

import android.location.Location;
import android.util.Log;
import android.widget.SearchView;

import androidx.lifecycle.MutableLiveData;

import com.example.tourmatebatch14.Utils.RetrofitClient;
import com.example.tourmatebatch14.currentweatheinfo.CurrentWeatherResponseBody;
import com.example.tourmatebatch14.currentweatheinfo.WeatherServiceApi;
import com.example.tourmatebatch14.forecastweather.ForecastWeatherResponseBody;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WeatherRepository {

    private static final String TAG = WeatherRepository.class.getSimpleName();
    public MutableLiveData<CurrentWeatherResponseBody> currentWeatherResponseLD
            =new MutableLiveData<>();
    public MutableLiveData<ForecastWeatherResponseBody> forecastWeatherResponseLD = new MutableLiveData<>();

    public MutableLiveData<CurrentWeatherResponseBody> getCurrentWeatherInfo(Location location, String unit, String apiKey){
        WeatherServiceApi serviceApi = RetrofitClient.getClient().create(WeatherServiceApi.class);
        String endUrl = String.format("data/2.5/weather?lat=%f&lon=%f&units=%s&appid=%s",
                location.getLatitude(),
                location.getLongitude(),
                unit,
                apiKey);

        serviceApi.getCurrentWeatherInfo(endUrl)
                .enqueue(new Callback<CurrentWeatherResponseBody>() {
                    @Override
                    public void onResponse(Call<CurrentWeatherResponseBody> call, Response<CurrentWeatherResponseBody> response) {
                        if (response.isSuccessful()) {
                            CurrentWeatherResponseBody responseBody = response.body();
                            currentWeatherResponseLD.postValue(responseBody);

                        } else {
                            Log.e("Werror", "onResponse: Failed");
                        }

                    }

                    @Override
                    public void onFailure(Call<CurrentWeatherResponseBody> call, Throwable t) {
                        Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
                    }
                });
        return currentWeatherResponseLD;
    }

    public MutableLiveData<ForecastWeatherResponseBody> getForecastWeatherInfo(Location location,  String apiKey){
        WeatherServiceApi serviceApi = RetrofitClient.getForecastClient().create(WeatherServiceApi.class);
        String endUrl = String.format("data/2.5/forecast/daily?lat=%f&lon=%f&cnt=7&appid=%s",
                location.getLatitude(),
                location.getLongitude(),
                apiKey);

        serviceApi.getForecastWeatherInfo(endUrl)
                .enqueue(new Callback<ForecastWeatherResponseBody>() {
                    @Override
                    public void onResponse(Call<ForecastWeatherResponseBody> call, Response<ForecastWeatherResponseBody> response) {
                        if(response.isSuccessful()){
                            forecastWeatherResponseLD.postValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<ForecastWeatherResponseBody> call, Throwable t) {

                    }
                });
        return forecastWeatherResponseLD;
    }
}
