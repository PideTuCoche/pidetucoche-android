package com.adrian.pidetucoche.view.contratacion;


import android.annotation.SuppressLint;

import androidx.fragment.app.Fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.adrian.pidetucoche.R;
import com.adrian.pidetucoche.view.AppViewModel;
import com.adrian.pidetucoche.view.map.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.model.LatLng;

import java.util.List;



/**
 * A simple {@link Fragment} subclass.
 */
public class ContratacionFragment extends MapFragment {

    Button pedirVehiculo;

    public ContratacionFragment() {

    }

    @Override
    public List<MarkerOptions> marcadores() {
        return null;
    }

    @SuppressLint({"RestrictedApi", "SetTextI18n"})
    @Override
    public void personalizar(View view) {
        view.findViewById(R.id.infoContratacionPrecio).setVisibility(View.VISIBLE);
        pedirVehiculo = view.findViewById(R.id.pedirVehiculoButton);
        destinationMarker.remove();
        int nSteps = appViewModel.userRoute.routes[0].legs[0].steps.length;
        Log.i("Duration", appViewModel.userRoute.routes[0].legs[0].duration.toString());

        BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.car_icon);
        MarkerOptions markerOptions = new MarkerOptions()
                                        .icon(icon)
                                        .position(convertGoogleLatLngToNormalLatLng(appViewModel.userRoute.routes[0].legs[0].steps[nSteps/4].startLocation));


        appViewModel.choferMarkerOptions = markerOptions;
        if (choferMarker != null) choferMarker.remove();
        if (polyline != null) polyline.remove();
        choferMarker = mMap.addMarker(markerOptions);
        infoContratacionTitle.setText("Conductor disponible");
        infoContratacionDescription.setText("Te recogera en " + appViewModel.userRoute.routes[0].legs[0].steps[nSteps/4].duration.humanReadable
                                                                                                        .replace("min", "minuto")
                                                                                                        .replace("mints", "minutos")
                                                                                                        .replace("hours", "horas")
                                                                                                        .replace("hour", "hora"));
        calculateDirections(convertGoogleLatLngToNormalLatLng(appViewModel.userRoute.routes[0].legs[0].steps[nSteps/4].startLocation));


        pedirVehiculo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appViewModel.servicioContratado = true;
                appViewModel.estadoContratacionMutableLiveData.setValue(AppViewModel.EstadoContratacion.ESTADO_ESPERANDOCONDUCTOR);
                navController.navigate(R.id.seguimientoFragment);
            }
        });



    }


    @Override
    public void loadObservers() {
        super.loadObservers();

    }

    @Override
    public void loadOnClickListeners() {
        super.loadOnClickListeners();
    }



    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
