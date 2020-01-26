package com.adrian.pidetucoche.view.recogida;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import androidx.lifecycle.Observer;

import com.adrian.pidetucoche.R;
import com.adrian.pidetucoche.view.map.MapFragment;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;

public class RecogidaFragment extends MapFragment
    implements GoogleMap.OnPoiClickListener, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLongClickListener {
    @Override
    public List<MarkerOptions> marcadores() {

        List<MarkerOptions> favs = new ArrayList<>();

        favs.add(new MarkerOptions().position(new LatLng(-34, 151)).title("Marker in Recogida"));

        return favs;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void personalizar(View view) {
        goToDirectionFloatingButton.setVisibility(View.VISIBLE);
        locationActionButton.setVisibility(View.VISIBLE);



        if (appViewModel.servicioContratado) navController.navigate(R.id.seguimientoFragment);
        if (!appViewModel.tutorialpasado) {
            startTutorial(view);
            appViewModel.tutorialpasado = true;
        }


    }

    private void startTutorial(View view) {
        Animation animation = new AlphaAnimation(0.0f,1.0f);
        animation.setDuration(250);
        final FancyShowCaseView fancyShowCaseWelcome = new FancyShowCaseView.Builder(requireActivity())
                .title("Con esta guía tendrás una explicación rápida de como utilizar la aplicación.")
                .titleStyle(R.style.showcaseText, Gravity.CENTER)
                .backgroundColor(R.color.colorAccent)
                .build();

        final FancyShowCaseView fancyPhase1 = new FancyShowCaseView.Builder(requireActivity())
                .title("Pulsando el boton de Localizacion, podremos ubicarnos en el mapa..")
                .titleStyle(R.style.showcaseText, Gravity.CENTER)
                .backgroundColor(R.color.colorAccent)
                .focusOn(view.findViewById(R.id.locationActionButton))
                .build();

        final FancyShowCaseView fancyPhase2 = new FancyShowCaseView.Builder(requireActivity())
                .title("Despues en la barra de busqueda, puedes introducir una direccion para generar una ruta.")
                .titleStyle(R.style.showcaseText, Gravity.CENTER)
                .backgroundColor(R.color.colorAccent)
                .build();

        final FancyShowCaseView fancyPhase3 = new FancyShowCaseView.Builder(requireActivity())
                .title("Tambien puedes pulsar en los marcadores que hay distribuidos por el mapa.")
                .titleStyle(R.style.showcaseText, Gravity.CENTER)
                .focusCircleRadiusFactor(0.5)
                .focusOn(view)
                .backgroundColor(R.color.colorAccent)
                .build();

        final FancyShowCaseView fancyPhase4 = new FancyShowCaseView.Builder(requireActivity())
                .title("Una vez que te aparezca la ruta creada en el Mapa, pulsa el boton Ir para comprobar la disponibilidad.")
                .titleStyle(R.style.showcaseText, Gravity.CENTER)
                .backgroundColor(R.color.colorAccent)
                .focusOn(view.findViewById(R.id.goToActionButton))
                .build();

        FancyShowCaseQueue mQueue = new FancyShowCaseQueue()
                .add(fancyShowCaseWelcome)
                .add(fancyPhase1)
                .add(fancyPhase2)
                .add(fancyPhase3)
                .add(fancyPhase4);
        mQueue.show();

    }

    @Override
    public void loadObservers() {
        super.loadObservers();

        appViewModel.terminoBusqueda.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Log.e("PlacesAPI", "Calling PlacesAPI...");
                FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                        // Call either setLocationBias() OR setLocationRestriction().
                        //.setLocationRestriction(bounds)
                        .setCountry("es")
                        .setSessionToken(appViewModel.getToken())
                        .setQuery(s)
                        .build();

                placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                        /*for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                            Log.i(PLACESAUTOCOMPLETE, prediction.getPlaceId());
                            Log.i(PLACESAUTOCOMPLETE, prediction.getPrimaryText(null).toString());
                        }*/
                    appViewModel.resultadoBusquedas.setValue(response.getAutocompletePredictions());
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        Log.e(PLACESAUTOCOMPLETE, "Place not found: " + apiException.getStatusCode());
                    }
                });
            }
        });

        appViewModel.resultadoBusquedas.observe(getViewLifecycleOwner(), new Observer<List>() {
            @Override
            public void onChanged(List list) {
                if (loading) return;
                appViewModel.placesAdapter.establecerListaElementos(list);
            }
        });

        appViewModel.localizacionSelecionada.observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String placeId) {
                if (loading) return;
                getLatLngFromPlaceId(placeId);
                appViewModel.placesAdapter.establecerListaElementos(null);
                getActivity().getWindow().setSoftInputMode(
                        WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
            }
        });

        appViewModel.placeMutableLiveData.observe(getViewLifecycleOwner(), new Observer<Place>() {
            @Override
            public void onChanged(Place place) {
                if (place == null) return;
                setDestinationMarker(place);
                calculateDirections(destinationMarker.getPosition());
            }
        });

        loading = false;
    }

    @Override
    public void loadOnClickListeners() {
        super.loadOnClickListeners();
        mMap.setOnPoiClickListener(this);

        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnMarkerClickListener(this);

        goToDirectionFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (polyline == null  || destinationMarker ==null) {
                    Toasty.info(requireContext(), "Introduce un resultado en la busqueda o pulsa un marcador.", Toast.LENGTH_SHORT, true).show();
                    return;
                }
                else {
                    // Toast.makeText(requireContext(), "PRUEBA", Toast.LENGTH_LONG).show();

                    navController.navigate(R.id.contratacionFragment);
                }
            }
        });
    }

    public void getLatLngFromPlaceId(String placeId){
        if (loading) return;
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);
        FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            Log.i("PlacesDetails", "Place found: " + place.getName());
            appViewModel.placeMutableLiveData.postValue(place);
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                Log.e("PlacesDetails", "Place not found: " + exception.getMessage());
            }
        });

    }

    @Override
    public void onPoiClick(PointOfInterest pointOfInterest) {
        setDestinationMarker(pointOfInterest.latLng);
        destinationMarker.setTitle(pointOfInterest.name);
        calculateDirections(destinationMarker.getPosition());

    }




    @Override
    public void onInfoWindowClick(Marker marker) {
        calculateDirections(marker.getPosition());
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        calculateDirections(marker.getPosition());
        return false;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        setDestinationMarker(latLng);
        calculateDirections(latLng);
    }

}
