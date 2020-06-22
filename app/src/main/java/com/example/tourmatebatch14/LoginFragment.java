package com.example.tourmatebatch14;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.tourmatebatch14.Repositories.FirebaseDBRepository;
import com.example.tourmatebatch14.Repositories.FirebaseRepository;
import com.example.tourmatebatch14.ViewModels.LoginViewModel;
import com.example.tourmatebatch14.databinding.FragmentLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private LoginViewModel loginViewModel;
    private FirebaseRepository firebaseRepository;

    public LoginFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        firebaseRepository = new FirebaseRepository(loginViewModel);
        binding = FragmentLoginBinding.inflate(LayoutInflater.from(getActivity()));
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        //binding.emailET.setText(firebaseRepository.getEmail());

        loginViewModel.authenticationStateMutableLiveData.observe(getActivity(), new Observer<LoginViewModel.AuthenticationState>() {
            @Override
            public void onChanged(LoginViewModel.AuthenticationState authenticationState) {
                switch(authenticationState){
                    case AUTHENTICATED:

                        Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_eventListfragment);

                        break;
                    case UNAUTHENTICATED:
                        binding.statusTV.setText("Something Went Wrong!");


                        break;
                }
            }
        });

        loginViewModel.errMsgLiveData.observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.statusTV.setText(s);
            }
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.emailET.getText().toString();
                String password = binding.passwordET.getText().toString();

                loginViewModel.loginUser(email,password);

            }
        });

        binding.regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.emailET.getText().toString();
                String password = binding.passwordET.getText().toString();
                loginViewModel.registerUser(email,password);
            }
        });



    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
//        FirebaseUser user = firebaseAuth.getCurrentUser();
//        if(user!=null){
//            loginViewModel.onAuthSuccess();
//        }
//    }
}
