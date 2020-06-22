package com.example.tourmatebatch14.Adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;


import com.example.tourmatebatch14.POJOS.TourmateEvent;
import com.example.tourmatebatch14.R;
import com.example.tourmatebatch14.databinding.EventRowBinding;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private Context context;
    private List<TourmateEvent> eventList;
    private EventRowBinding binding;

    public EventAdapter(Context context, List<TourmateEvent> eventList) {
        this.context = context;
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context),
                R.layout.event_row, parent, false);
        return new EventViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull final EventViewHolder holder, final int position) {
        binding.rowEventName.setText(eventList.get(position).getEventName());
        binding.rowDepartureDate.setText(eventList.get(position).getDepartureDate());
        binding.rowEventPlace.setText(String.format("%s-%s", eventList.get(position).getDeparture(), eventList.get(position).getDestination()));
        binding.rowMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context,v);
                popupMenu.getMenuInflater().inflate(R.menu.event_row_menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String eventId = eventList.get(position).getEventID();
                        switch (item.getItemId()){
                            case R.id.eventRowDetails:
                                Bundle bundle = new Bundle();
                                bundle.putString("id",eventId);
                                Navigation.findNavController(holder.itemView).navigate(R.id.action_eventListfragment_to_eventDetailsFragment, bundle);
                                break;
                            case  R.id.eventRowEdit:
                                break;
                            case R.id.eventRowDelete:
                                break;
                        }
                        return false;

                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    class EventViewHolder extends RecyclerView.ViewHolder {
        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }


}

