package com.example.tourmatebatch14.ViewModels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tourmatebatch14.Repositories.FirebaseRepository;

public class LoginViewModel extends ViewModel implements FirebaseRepository.UserAuthenticationListener {

    public enum AuthenticationState{
        AUTHENTICATED,
        UNAUTHENTICATED
    }
    public MutableLiveData<AuthenticationState> authenticationStateMutableLiveData;
    private FirebaseRepository firebaseRepository;
    public MutableLiveData<String> errMsgLiveData;

    public LoginViewModel(){
        authenticationStateMutableLiveData = new MutableLiveData<>();
        errMsgLiveData = new MutableLiveData<>();
        firebaseRepository = new FirebaseRepository(this);
        if (firebaseRepository.getFirebaseUser() != null){
            authenticationStateMutableLiveData.setValue(AuthenticationState.AUTHENTICATED);
        }else{
            authenticationStateMutableLiveData.setValue(AuthenticationState.UNAUTHENTICATED);
        }
    }

    public void loginUser(String email,String password){
        firebaseRepository.loginFirebaseUser(email,password);
    }

    public void registerUser(String email,String password){
        firebaseRepository.registerFirebaseUser(email,password);
    }

    @Override
    public void onAuthSuccess() {
        authenticationStateMutableLiveData.setValue(AuthenticationState.AUTHENTICATED);
    }

    @Override
    public void onAuthFailure(String msg) {
        authenticationStateMutableLiveData.setValue(AuthenticationState.UNAUTHENTICATED);
        errMsgLiveData.postValue(msg);
    }

}
