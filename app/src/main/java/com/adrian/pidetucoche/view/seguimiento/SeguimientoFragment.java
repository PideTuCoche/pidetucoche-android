package com.adrian.pidetucoche.view.seguimiento;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.adrian.pidetucoche.R;
import com.adrian.pidetucoche.view.AppViewModel;
import com.adrian.pidetucoche.view.map.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class SeguimientoFragment extends MapFragment {


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public List<MarkerOptions> marcadores() {

        List<MarkerOptions> favs = new ArrayList<>();
        return favs;
    }

    @Override
    public void personalizar(View view) {
        view.findViewById(R.id.infoContratacionPrecio).setVisibility(View.GONE);
        appViewModel.estadoContratacionMutableLiveData.observe(getViewLifecycleOwner(), new Observer<AppViewModel.EstadoContratacion>() {
            @Override
            public void onChanged(AppViewModel.EstadoContratacion estadoContratacion) {
                switch (estadoContratacion){
                    case ESTADO_ESPERANDOCONDUCTOR:
                        AsyncTask.execute(() -> {
                            try {
                                Thread.sleep(5000);
                                appViewModel.estadoContratacionMutableLiveData.postValue(AppViewModel.EstadoContratacion.ESTADO_CONDUCTORESPERANDO);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                        break;
                    case ESTADO_CONDUCTORESPERANDO:
                        infoContratacionTitle.setText("Recogida disponible");
                        infoContratacionDescription.setText("Dirigase hacia el vehiculo");
                        goToLocation(appViewModel.lastUserLocation, true);
                        if (choferMarker != null) choferMarker.remove();
                        choferMarker = mMap.addMarker(appViewModel.choferMarkerOptions.position(lastUserLocation));

                        AsyncTask.execute(() -> {
                            try {
                                Thread.sleep(10000);
                                appViewModel.estadoContratacionMutableLiveData.postValue(AppViewModel.EstadoContratacion.ESTADO_ENTRANSITO);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                        break;
                    case ESTADO_ENTRANSITO:
                        infoContratacionTitle.setText("En transito");
                        infoContratacionDescription.setText("Tiempo restante: " + appViewModel.userRoute.routes[0].legs[0].duration);
                        mMap.setMyLocationEnabled(false);
                        addPolylinesToMap(appViewModel.userRoute);
                        AsyncTask.execute(() -> {
                            try {
                                Thread.sleep(20000);
                                appViewModel.estadoContratacionMutableLiveData.postValue(AppViewModel.EstadoContratacion.ESTADO_FINALIZADO);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                        break;
                    case ESTADO_FINALIZADO:
                        infoContratacionTitle.setText("Viaje finalizado");
                        infoContratacionDescription.setText("Visita el apartado de ultimos viajes\n si desea dejar una valoracion");
                        if (choferMarker != null) choferMarker.remove();
                        choferMarker = mMap.addMarker(appViewModel.choferMarkerOptions.position(convertGoogleLatLngToNormalLatLng(appViewModel.userRoute.routes[0].legs[0].endLocation)));
                        AsyncTask.execute(() -> {
                            try {
                                Thread.sleep(10000);
                                appViewModel.servicioContratado = false;
                                appViewModel.estadoContratacionMutableLiveData.postValue(AppViewModel.EstadoContratacion.ESTADO_FINGOTOREC);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        });
                        break;
                    case ESTADO_FINGOTOREC:
                        appViewModel.placesAdapter.establecerListaElementos(null);
                        mMap.setMyLocationEnabled(true);
                        navController.navigate(R.id.recogidaFragment);
                        break;

                }
            }
        });
    }

    @Override
    public void loadObservers() {

    }

    @Override
    public void loadOnClickListeners() {
        super.loadOnClickListeners();
    }


}
