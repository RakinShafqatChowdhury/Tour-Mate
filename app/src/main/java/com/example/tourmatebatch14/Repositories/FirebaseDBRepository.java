package com.example.tourmatebatch14.Repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.tourmatebatch14.POJOS.Moments;
import com.example.tourmatebatch14.POJOS.TourmateEvent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseDBRepository {

    private static final String TAG = FirebaseDBRepository.class.getSimpleName();
    private FirebaseUser firebaseUser;
    private DatabaseReference rootRef;
    private DatabaseReference userRef;
    private DatabaseReference eventRef;
    private DatabaseReference momentsRef;
    public MutableLiveData<List<TourmateEvent>> eventListLiveData;
    public MutableLiveData<TourmateEvent> eventDetailsLD;

    public FirebaseDBRepository(){
        eventListLiveData = new MutableLiveData<>();
        eventDetailsLD = new MutableLiveData<>();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child(firebaseUser.getUid());
        momentsRef = userRef.child("Moments");
        eventRef = userRef.child("My Events");
        eventRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<TourmateEvent> events = new ArrayList<>();
                for(DataSnapshot data : dataSnapshot.getChildren()){
                    events.add(data.getValue(TourmateEvent.class));
                }
                Log.e(TAG, "onDataChange: "+events.size());
                eventListLiveData.postValue(events);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void saveNewEventToFirebase(TourmateEvent event){
        String eventID = eventRef.push().getKey();
        event.setEventID(eventID);
        eventRef.child(eventID).setValue(event).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

    public  MutableLiveData<TourmateEvent> getEventDetailsById(String e){
        eventRef.child(e).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TourmateEvent tourmateEvent = dataSnapshot.getValue(TourmateEvent.class);
                eventDetailsLD.postValue(tourmateEvent);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return  eventDetailsLD;
    }

    public void saveImagetoFirebaseRTDB(Moments moments){
        String momentId = momentsRef.push().getKey();
        moments.setMomentId(momentId);
        momentsRef.child(moments.getEventId())
                .child(momentId)
                .setValue(moments);
    }

}
