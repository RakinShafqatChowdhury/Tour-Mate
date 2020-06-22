package com.example.tourmatebatch14;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.tourmatebatch14.Utils.EventUtils;
import com.example.tourmatebatch14.ViewModels.LocationViewModel;
import com.example.tourmatebatch14.ViewModels.LoginViewModel;
import com.example.tourmatebatch14.ViewModels.WeatherViewModel;
import com.example.tourmatebatch14.currentweatheinfo.CurrentWeatherResponseBody;
import com.example.tourmatebatch14.currentweatheinfo.WeatherServiceApi;

import com.example.tourmatebatch14.databinding.FragmentWeatherBinding;
import com.example.tourmatebatch14.forecastweather.ForecastList;
import com.example.tourmatebatch14.forecastweather.ForecastWeatherResponseBody;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link weatherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class weatherFragment extends Fragment {
    private static final String TAG = weatherFragment.class.getSimpleName();
    private FragmentWeatherBinding binding;
    private Location currentlocation = null;
    private LocationViewModel locationViewModel;
    private WeatherViewModel weatherViewModel;
    private String units = "metric";
    private PlacesClient placesClient;
    private int AUTOCOMPLETE_REQUEST_CODE = 1;
    public weatherFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.weather_menu,menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.weather_search).getActionView();
        searchView.setQueryHint("search any city");
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    getLatLngQuery(query);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Couldn't find city", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    private void getLatLngQuery(String query) throws IOException {
        Geocoder geocoder = new Geocoder(getActivity());
        List<Address> addresses= geocoder.getFromLocationName(query,1);
        if(addresses!=null && addresses.size()>0){
            double lat = addresses.get(0).getLatitude();
            double lng = addresses.get(0).getLongitude();
            Log.e("latlng", "getLatLngQuery: "+lat+" "+lng );
            currentlocation.setLatitude(lat);
            currentlocation.setLongitude(lng);
            getCurrentWeatherData(currentlocation);
        }else
        {
            Toast.makeText(getActivity(), "Please provide valid city name", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       switch (item.getItemId()){
           case R.id.weather_c:
               //showPlaceSearchDialog();
               units="metric";
               getCurrentWeatherData(currentlocation);
               break;

           case R.id.weather_f:
                units = "imperial";
               getCurrentWeatherData(currentlocation);
               break;
       }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if(units.equals("metric")){
            menu.findItem(R.id.weather_c).setVisible(false);
            menu.findItem(R.id.weather_f).setVisible(true);

        }
        else {
            menu.findItem(R.id.weather_f).setVisible(false);
            menu.findItem(R.id.weather_c).setVisible(true);

        }

    }

    private void showPlaceSearchDialog() {
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

// Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .build(getActivity());
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == AUTOCOMPLETE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            Place place = Autocomplete.getPlaceFromIntent(data);
            LatLng latLng = place.getLatLng();
            Location searchLocation = new Location(LocationManager.GPS_PROVIDER);
            searchLocation.setLatitude(latLng.latitude);
            searchLocation.setLongitude(latLng.longitude);
            currentlocation = searchLocation;
            getCurrentWeatherData(searchLocation);
            Toast.makeText(getActivity(), place.getName(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Places.initialize(getContext(),getString(R.string.place_api));
        placesClient = Places.createClient(getContext());

        binding = FragmentWeatherBinding.inflate(LayoutInflater.from(getActivity()));
        locationViewModel = ViewModelProviders.of(getActivity()).get(LocationViewModel.class);
        weatherViewModel = ViewModelProviders.of(getActivity()).get(WeatherViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        locationViewModel.locationLD.observe(getActivity(), new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                currentlocation = location;
                binding.latLngTV.setText(location.getLongitude() + "," + location.getLatitude());
                Log.e("location", "onChanged: " + location.getLatitude() + " " + location.getLongitude());
                getCurrentWeatherData(currentlocation);
                getForecastWeatherData(currentlocation);
            }
        });

        weatherViewModel.currentWeatherResponseLD.observe(getActivity(), new Observer<CurrentWeatherResponseBody>() {
            @Override
            public void onChanged(CurrentWeatherResponseBody currentWeatherResponseBody) {
                double temp = currentWeatherResponseBody.getMain().getTemp();
                String city = currentWeatherResponseBody.getName();
                String date = EventUtils.getFormatedDate(currentWeatherResponseBody.getDt());
                String icon = currentWeatherResponseBody.getWeather().get(0).getIcon();
                Log.e("icon", "onChanged: "+icon );
                Picasso.get().load(EventUtils.WEATHER_ICON_URL_PREFIX+icon+"@2x.png")
                        .into(binding.weatheIcon);
                binding.weatherdata.setText(city+"\n"+temp+"\n"+date);

            }
        });



    }

    private void getForecastWeatherData(Location currentlocation) {
        Log.e(TAG, "getForecastWeatherData: "+currentlocation );
        String apiKey = getString(R.string.weather_api_key);
        weatherViewModel.getForecastWeatherInfo(currentlocation,apiKey);

        weatherViewModel.forecastWeatherResponseLD.observe(getActivity(), new Observer<ForecastWeatherResponseBody>() {
            @Override
            public void onChanged(ForecastWeatherResponseBody forecastWeatherResponseBody) {
                List<ForecastList> forecastList = forecastWeatherResponseBody.getList();
                Toast.makeText(getActivity(), "size: "+forecastList.size(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onChanged: "+forecastList.size() );
                for(ForecastList fl : forecastList){
                    binding.latLngTV.setText(""+fl.getTemp().getMax()+"\n");
                }
            }
        });


    }

    public void getCurrentWeatherData(Location location) {

//        String base_url = "http://api.openweathermap.org/";
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(base_url)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
        String apiKey = getString(R.string.weather_api_key);
        weatherViewModel.getCurrentWeatherInfo(location,units,apiKey);
//        String endUrl = String.format("data/2.5/weather?lat=%f&lon=%f&units=%s&appid=%s",
//                location.getLatitude(),
//                location.getLongitude(),
//                units,
//                apiKey);
        // String endUrl = String.format("data/2.5/weather?lat=-122.0840&lon=37.4220&units=metric&appid=79945a8b31c41b9333fc9ba265975ec1");

//        WeatherServiceApi serviceApi = retrofit.create(WeatherServiceApi.class);

//        serviceApi.getCurrentWeatherInfo(endUrl)
//                .enqueue(new Callback<CurrentWeatherResponseBody>() {
//                    @Override
//                    public void onResponse(Call<CurrentWeatherResponseBody> call, Response<CurrentWeatherResponseBody> response) {
//                        if (response.isSuccessful()) {
//                            CurrentWeatherResponseBody responseBody = response.body();
//                            double temp = responseBody.getMain().getTemp();
//                            String city = responseBody.getName();
//                            int date = responseBody.getDt();
//
//                            binding.weatherdata.setText(temp + "\n" + date  + "\n" + city);
//                            Log.e(TAG, "onResponse: " + temp + date + city);
//                        } else {
//                            Log.e("Werror", "onResponse: Failed");
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<CurrentWeatherResponseBody> call, Throwable t) {
//                        Log.e(TAG, "onFailure: " + t.getLocalizedMessage());
//                    }
//                });
    }
}