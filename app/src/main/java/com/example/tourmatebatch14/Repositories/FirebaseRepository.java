package com.example.tourmatebatch14.Repositories;

import androidx.annotation.NonNull;

import com.example.tourmatebatch14.ViewModels.LoginViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseRepository {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private UserAuthenticationListener listener;

    public FirebaseRepository(LoginViewModel loginViewModel){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        listener = loginViewModel;
    }

    public void loginFirebaseUser(String email,String password){
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    firebaseUser = firebaseAuth.getCurrentUser();
                    listener.onAuthSuccess();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onAuthFailure(e.getLocalizedMessage());
            }
        });
    }

    public void registerFirebaseUser(String email, String password){
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    firebaseUser = firebaseAuth.getCurrentUser();
                    listener.onAuthSuccess();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onAuthFailure(e.getLocalizedMessage());
            }
        });
    }

    public FirebaseUser getFirebaseUser() {
        return firebaseUser;
    }

    public interface UserAuthenticationListener{
        void onAuthSuccess();
        void onAuthFailure(String msg);
    }

    public String getEmail(){
        return firebaseUser.getEmail();
    }

}
