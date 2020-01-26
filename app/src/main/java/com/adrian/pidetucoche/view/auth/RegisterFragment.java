package com.adrian.pidetucoche.view.auth;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.adrian.pidetucoche.R;
import com.adrian.pidetucoche.view.AppViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {


    NavController navController;
    AppViewModel appViewModel;
    EditText emailEditText, passwordEditText;
    TextView irAlLogin;
    public RegisterFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        appViewModel = ViewModelProviders.of(requireActivity()).get(AppViewModel.class);

        emailEditText = view.findViewById(R.id.edittext_email);
        passwordEditText = view.findViewById(R.id.edittext_password);

        view.findViewById(R.id.edittext_irAlLogin).setOnClickListener(
                v -> {
                    navController.navigate(R.id.loginFragment);
                    Log.e("REGISTER", "Not work");
                }

        );



        Button button = view.findViewById(R.id.button_registrar);
        button.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String passw = passwordEditText.getText().toString();

            appViewModel.registrarUsuarioYLogear(email, passw);

            appViewModel.estadoDelRegistro.observe(getViewLifecycleOwner(), estadoDelRegistro -> {
                switch (estadoDelRegistro) {
                    case EMAIL_NO_DISPONBLE:
                        navController.navigate(R.id.tutorialFragment);
                        Toast.makeText(getContext(), "User exist!!!.", Toast.LENGTH_LONG).show();
                        break;
                    case EMAIL_NO_VALIDO:
                        break;
                    case REGISTRO_COMPLETADO:
                        appViewModel.userLogged = true;
                        navController.navigate(R.id.recogidaFragment);
                        break;
                }
            });
        });

    }
}