package com.example.tourmatebatch14;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.tourmatebatch14.Adapters.EventAdapter;
import com.example.tourmatebatch14.POJOS.TourmateEvent;
import com.example.tourmatebatch14.Repositories.FirebaseRepository;
import com.example.tourmatebatch14.ViewModels.EventViewModel;
import com.example.tourmatebatch14.ViewModels.LoginViewModel;
import com.example.tourmatebatch14.databinding.FragmentEventListfragmentBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventListfragment extends Fragment {
    private static final String TAG = EventListfragment.class.getSimpleName();
    private EventViewModel eventViewModel;
    private FirebaseRepository firebaseRepository;
    private FragmentEventListfragmentBinding binding;
    private EventAdapter adapter;
    private LoginViewModel loginViewModel;

    public EventListfragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu,menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.item_add_event:
                Navigation.findNavController(getActivity(),R.id.nav_host_fragment).navigate(R.id.action_eventListfragment_to_addEventFragment);
                break;
            case R.id.action_settings:
               FirebaseAuth.getInstance().signOut();

               Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.loginFragment);


                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentEventListfragmentBinding.inflate(LayoutInflater.from(getActivity()));
        firebaseRepository = new FirebaseRepository(loginViewModel);
        //eventViewModel = ViewModelProviders.of(this).get(EventViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        loginViewModel.authenticationStateMutableLiveData.observe(getActivity(), new Observer<LoginViewModel.AuthenticationState>() {
            @Override
            public void onChanged(LoginViewModel.AuthenticationState authenticationState) {
                switch(authenticationState){
                    case AUTHENTICATED:

                        eventViewModel = ViewModelProviders.of(getActivity()).get(EventViewModel.class);
                        getAllEvents();
                        break;
                    case UNAUTHENTICATED:
                        //binding.statusTV.setText("Something Went Wrong bro!");
                        Navigation.findNavController(view).navigate(R.id.loginFragment);
                        break;
                }
            }


        });

    }

    private void getAllEvents() {
        eventViewModel.eventListLiveData.observe(getActivity(), new Observer<List<TourmateEvent>>() {
            @Override
            public void onChanged(List<TourmateEvent> tourmateEvents) {
                Log.e(TAG, "onChanged: "+tourmateEvents.size());
                adapter = new EventAdapter(getActivity(), tourmateEvents);
                LinearLayoutManager llm = new LinearLayoutManager(getActivity());
                binding.eventListRV.setLayoutManager(llm);
                binding.eventListRV.setAdapter(adapter);
            }
        });
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        if(user!=null){
//           loginViewModel.onAuthSuccess();
//        }
//    }


}


