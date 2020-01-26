package com.adrian.pidetucoche.view.tutorial;


import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adrian.pidetucoche.R;
import com.adrian.pidetucoche.view.AppViewModel;
import com.adrian.pidetucoche.view.map.MapFragment;
import com.adrian.pidetucoche.view.map.MapViewModel;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import me.toptas.fancyshowcase.FancyShowCaseView;


/**
 * A simple {@link Fragment} subclass.
 */
public class TutorialFragment extends Fragment{

    public AppViewModel appViewModel;
    public NavController navController;

    public TutorialFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tutorial, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appViewModel = ViewModelProviders.of(requireActivity()).get(AppViewModel.class);
        navController = Navigation.findNavController(view);

        appViewModel.tutorialpasado = false;
        navController.navigate(R.id.recogidaFragment);
        /*
        final FancyShowCaseView fancyShowCaseWelcome = new FancyShowCaseView.Builder(this)
                .title("Con esta guía tendrás una explicación rápida de los diferentes botones de la aplicación.")
                .titleStyle(R.style.showcaseText, Gravity.CENTER)
                .backgroundColor(R.color.showCaseBackground)
                .build();
*

*/
    }
}
