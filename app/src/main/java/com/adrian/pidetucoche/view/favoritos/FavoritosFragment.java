package com.adrian.pidetucoche.view.favoritos;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.adrian.pidetucoche.view.map.MapFragment;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class FavoritosFragment extends MapFragment {

    @Override
    public List<MarkerOptions> marcadores() {

        List<MarkerOptions> favs = new ArrayList<>();

//
//            final MarkerOptions markerOptions = new MarkerOptions().position(sydney).title("Marker in Sydney");


        return favs;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void personalizar(View view) {
        locationActionButton.setVisibility(View.VISIBLE);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(41.4039, 2.1743), 13));

        mMap.addMarker(new MarkerOptions().position(new LatLng(41.4039, 2.1743)).title("Sagrada Familia"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(41.3911, 2.1806)).title("Arco de Triunfo"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(41.3781, 2.1480)).title("Joan Miró park"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(41.3986, 2.1852)).title("L'Auditori"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(41.3879, 2.1986)).title("Port Olímpic"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(41.4555, 2.2015)).title("Institut Puig Castellar"));


    }

    @Override
    public void loadObservers() {

    }

    @Override
    public void loadOnClickListeners() {

    }


}
