package com.example.tourmatebatch14.ViewModels;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tourmatebatch14.POJOS.Moments;
import com.example.tourmatebatch14.POJOS.TourmateEvent;
import com.example.tourmatebatch14.Repositories.FirebaseDBRepository;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnPausedListener;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.List;

public class EventViewModel extends ViewModel {
    private FirebaseDBRepository dbRepository;
    public MutableLiveData<List<TourmateEvent>> eventListLiveData;
    public MutableLiveData<TourmateEvent> eventDetailsLD;
    public MutableLiveData<Integer> progressLD = new MutableLiveData<>();

    public EventViewModel(){
        dbRepository = new FirebaseDBRepository();
        eventListLiveData = dbRepository.eventListLiveData;
    }

    public void saveEvent(TourmateEvent event){
        dbRepository.saveNewEventToFirebase(event);
    }

    public void getEventDetails(String eventId){
       eventDetailsLD = dbRepository.getEventDetailsById(eventId);
    }

    public void saveImagetoFirebaseStorage(File file, final String eventId){
        StorageReference rootRef = FirebaseStorage.getInstance().getReference();
        Uri fileUri = Uri.fromFile(file);
        final StorageReference imageRef = rootRef.child("Moments/"+fileUri.getLastPathSegment());
        UploadTask uploadTask = imageRef.putFile(fileUri);

// Observe state change events such as progress, pause, and resume
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                int progressInt = (int) progress;
                progressLD.postValue(progressInt);
                //System.out.println("Upload is " + progress + "% done");
            }
        }).addOnPausedListener(new OnPausedListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onPaused(UploadTask.TaskSnapshot taskSnapshot) {
                //System.out.println("Upload is paused");
            }
        });

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return imageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    Moments moments = new Moments(null,eventId,downloadUri.toString());
                    Log.e("moments", "onComplete: "+moments.getMomentDownloadUrl()+moments.getMomentId()+moments.getEventId() );
                    dbRepository.saveImagetoFirebaseRTDB(moments);
                } else {
                    // Handle failures
                    // ...
                    Log.e("error", "onComplete: Error " );
                }
            }
        });
    }
}
