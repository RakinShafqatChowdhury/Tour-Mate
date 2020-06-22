package com.example.tourmatebatch14;

import android.Manifest;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.service.voice.AlwaysOnHotwordDetector;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tourmatebatch14.ViewModels.LocationViewModel;
import com.example.tourmatebatch14.databinding.FragmentNearbyBinding;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.internal.ICameraUpdateFactoryDelegate;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link nearbyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class nearbyFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener {
    private GoogleMap googleMap;
    private LocationViewModel locationViewModel;
    private FragmentNearbyBinding binding;
    private List<Geofence> geofenceList = new ArrayList<>();
    private GeofencingClient geofencingClient;

    public nearbyFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        locationViewModel = ViewModelProviders.of(getActivity()).get(LocationViewModel.class);
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_nearby, container, false);
        geofencingClient = LocationServices.getGeofencingClient(getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.nearbyMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnMapLongClickListener(this);
        locationViewModel.locationLD.observe(this, new Observer<Location>() {
            @Override
            public void onChanged(Location location) {
                LatLng myPosition = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.addMarker(new MarkerOptions().title("Me").position(myPosition));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 15f));
            }
        });
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        //Toast.makeText(getActivity(), "data: "+latLng.longitude+latLng.latitude, Toast.LENGTH_SHORT).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Geofence");
        builder.setMessage("Enter a name for this place");
        EditText editText = new EditText(getActivity());
        editText.setHint("Place name");
        builder.setView(editText);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = editText.getText().toString().trim();
                if (!name.isEmpty()) {
                    createGeofenceObj(latLng, name);
                    Log.e("SET", "onClick: " + latLng.latitude + latLng.longitude + name);
                }

            }
        });

        builder.setNegativeButton("CANCEL", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createGeofenceObj(LatLng latLng, String name) {
        Geofence geofence = new Geofence.Builder().setRequestId(name)
                .setCircularRegion(latLng.latitude, latLng.longitude, 200)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(6 * 3600 * 1000)
                .build();
        geofenceList.add(geofence);
        Log.e("fenceobj", "createGeofenceObj: "+geofence.toString() );
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(getGeofencingrequest(), getPendingIntent())
              .addOnSuccessListener(new OnSuccessListener<Void>() {
                  @Override
                  public void onSuccess(Void aVoid) {
                      Toast.makeText(getActivity(), "Added and monitoring", Toast.LENGTH_SHORT).show();
                  }
              });
    }

    private PendingIntent getPendingIntent() {
        //Services
        Intent intent = new Intent(getContext(), GeofenceTriggeringService.class);
        PendingIntent pendingIntent = PendingIntent.getService(getContext(),69,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        //Intent intent =  new Intent(getContext(),GeofenceReceiver.class);
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),69,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private GeofencingRequest getGeofencingrequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.addGeofences(geofenceList);
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        return builder.build();
    }
}