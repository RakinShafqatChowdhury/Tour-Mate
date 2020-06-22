package com.example.tourmatebatch14;


import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.Toast;

import com.example.tourmatebatch14.POJOS.TourmateEvent;
import com.example.tourmatebatch14.ViewModels.EventViewModel;
import com.example.tourmatebatch14.databinding.FragmentAddEventBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class addEventFragment extends Fragment {
    private FragmentAddEventBinding binding;
    private String departureDate = null;
    private EventViewModel eventViewModel;

    public addEventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentAddEventBinding.inflate(LayoutInflater.from(getActivity()));
        eventViewModel = ViewModelProviders.of(this).get(EventViewModel.class);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.createNewEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eventName = binding.eventNameInput.getText().toString();
                String departureName = binding.departureInput.getText().toString();
                String destinationName = binding.destinationInput.getText().toString();
                String initialBudget = binding.initialBudgetInput.getText().toString();

                if(eventName.isEmpty() && departureName.isEmpty() && destinationName.isEmpty() && initialBudget.isEmpty()){
                    Toast.makeText(getActivity(), "Please fill up all the sections", Toast.LENGTH_SHORT).show();
                    return;
                }

                TourmateEvent tourmateEvent = new TourmateEvent(null,eventName,departureName,destinationName,Integer.parseInt(initialBudget),departureDate);
                eventViewModel.saveEvent(tourmateEvent);
                Navigation.findNavController(view).navigate(R.id.eventListfragment);

            }
        });

        binding.dateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });



    }

    private void showDatePickerDialog() {
        Calendar calender = Calendar.getInstance();
        int year = calender.get(Calendar.YEAR);
        int month = calender.get(Calendar.MONTH);
        int day = calender.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog dpd = new DatePickerDialog(getActivity(),dateSetListener,year,month,day);
        dpd.show();
    }

    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year,month,dayOfMonth);
            departureDate = new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime());
            binding.dateBtn.setText(departureDate);
        }
    };
}
