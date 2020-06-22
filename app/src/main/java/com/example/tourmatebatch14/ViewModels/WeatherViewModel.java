package com.example.tourmatebatch14.ViewModels;

import android.location.Location;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tourmatebatch14.Repositories.WeatherRepository;
import com.example.tourmatebatch14.currentweatheinfo.CurrentWeatherResponseBody;
import com.example.tourmatebatch14.forecastweather.ForecastWeatherResponseBody;

public class WeatherViewModel extends ViewModel {
    private WeatherRepository repository;
    public MutableLiveData<CurrentWeatherResponseBody>currentWeatherResponseLD = new MutableLiveData<>();
    public MutableLiveData<ForecastWeatherResponseBody> forecastWeatherResponseLD = new MutableLiveData<>();

    public WeatherViewModel(){
        repository = new WeatherRepository();
    }
    public void getCurrentWeatherInfo(Location location, String unit, String apiKey){
        currentWeatherResponseLD = repository.getCurrentWeatherInfo(location,unit,apiKey);
    }

    public void getForecastWeatherInfo(Location location, String apiKey){
        forecastWeatherResponseLD = repository.getForecastWeatherInfo(location,apiKey);
    }
}
