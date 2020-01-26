package com.adrian.pidetucoche.view.auth;


import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.adrian.pidetucoche.R;
import com.adrian.pidetucoche.view.AppViewModel;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import es.dmoral.toasty.Toasty;


/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment {


    NavController navController;
    AppViewModel appViewModel;
    EditText emailEditText, passwordEditText;
    View includeRegister;
    TextView irAlLogin;


    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        appViewModel = ViewModelProviders.of(requireActivity()).get(AppViewModel.class);
        irAlLogin = view.findViewById(R.id.edittext_irAlLogin);

        emailEditText = view.findViewById(R.id.edittext_email);
        passwordEditText = view.findViewById(R.id.edittext_password);

        view.findViewById(R.id.button_iralregistro).setOnClickListener(
                v -> {
                    navController.navigate(R.id.registerFragment);
                    //includeRegister.setVisibility(View.VISIBLE);
                }
        );



        appViewModel.registerActived.observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean registerActived) {
                if (!registerActived) {
                    includeRegister.setVisibility(View.GONE);
                    appViewModel.registerActivated = false;
                }
            }
        });

        view.findViewById(R.id.button_acceder).setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            String passw = passwordEditText.getText().toString();

            appViewModel.logearUsuario(email, passw);

            appViewModel.estadoDelLogin.observe(getViewLifecycleOwner(), estadoDelLogin -> {
                switch (estadoDelLogin){
                    case EMAIL_VACIO:
                        Toasty.error(getContext(), "El campo del Email esta vacio", Toast.LENGTH_LONG, true).show();
                        YoYo.with(Techniques.Shake)
                                .duration(700)
                                .playOn(view.findViewById(R.id.edittext_email));
                        break;
                    case PASSWORD_VACIA:
                        Toasty.error(getContext(), "El campo de la contraseña esta vacio", Toast.LENGTH_LONG, true).show();
                        YoYo.with(Techniques.Shake)
                                .duration(700)
                                .playOn(view.findViewById(R.id.edittext_password));
                        break;
                    case CUENTA_INVALIDA:
                        Toasty.error(getContext(), "Usuario o contraseña incorrectos.", Toast.LENGTH_LONG, true).show();
                        YoYo.with(Techniques.Shake)
                                .duration(700)
                                .repeat(1)
                                .playOn(view.findViewById(R.id.edittext_email));
                        YoYo.with(Techniques.Shake)
                                .duration(700)
                                .repeat(1)
                                .playOn(view.findViewById(R.id.edittext_password));
                        break;
                    case LOGIN_COMPLETADO:
                        Toasty.success(getContext(), "Has iniciado session correctamente", Toast.LENGTH_LONG, true).show();
                        appViewModel.userLogged = true;
                        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        navController.navigate(R.id.recogidaFragment);
                        break;


                }
            });

            }
        );
    }
}
