    package com.adrian.pidetucoche.view.map;


    import android.Manifest;
    import android.animation.Animator;
    import android.animation.AnimatorListenerAdapter;
    import android.annotation.SuppressLint;
    import android.content.pm.PackageManager;
    import android.content.res.ColorStateList;
    import android.graphics.Color;
    import android.location.Location;
    import android.os.AsyncTask;
    import android.os.Bundle;
    import android.os.Handler;
    import android.os.Looper;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.LinearLayout;
    import android.widget.TextView;
    import android.widget.Toast;

    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.constraintlayout.widget.ConstraintLayout;
    import androidx.constraintlayout.widget.ConstraintSet;
    import androidx.core.app.ActivityCompat;
    import androidx.core.content.ContextCompat;
    import androidx.core.widget.ImageViewCompat;
    import androidx.fragment.app.Fragment;
    import androidx.fragment.app.FragmentActivity;
    import androidx.lifecycle.MutableLiveData;
    import androidx.lifecycle.Observer;
    import androidx.lifecycle.ViewModelProviders;
    import androidx.navigation.NavController;
    import androidx.navigation.Navigation;
    import androidx.recyclerview.widget.DividerItemDecoration;
    import androidx.recyclerview.widget.RecyclerView;

    import com.adrian.pidetucoche.NavigationBackStackLogger;
    import com.adrian.pidetucoche.R;
    import com.adrian.pidetucoche.manage.permissions.LocatePermissions;
    import com.adrian.pidetucoche.view.AppViewModel;
    import com.adrian.pidetucoche.view.listalocalizaciones.ListaLocalizacionesAdapter;
    import com.google.android.gms.common.api.ApiException;
    import com.google.android.gms.location.FusedLocationProviderClient;
    import com.google.android.gms.location.LocationCallback;
    import com.google.android.gms.location.LocationRequest;
    import com.google.android.gms.location.LocationResult;
    import com.google.android.gms.location.LocationServices;
    import com.google.android.gms.maps.CameraUpdate;
    import com.google.android.gms.maps.CameraUpdateFactory;
    import com.google.android.gms.maps.GoogleMap;
    import com.google.android.gms.maps.OnMapReadyCallback;
    import com.google.android.gms.maps.SupportMapFragment;
    import com.google.android.gms.maps.model.LatLng;
    import com.google.android.gms.maps.model.LatLngBounds;
    import com.google.android.gms.maps.model.MapStyleOptions;
    import com.google.android.gms.maps.model.Marker;
    import com.google.android.gms.maps.model.MarkerOptions;
    import com.google.android.gms.maps.model.PointOfInterest;
    import com.google.android.gms.maps.model.Polygon;
    import com.google.android.gms.maps.model.PolygonOptions;
    import com.google.android.gms.maps.model.Polyline;
    import com.google.android.gms.maps.model.PolylineOptions;
    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.Task;
    import com.google.android.libraries.places.api.Places;
    import com.google.android.libraries.places.api.model.AutocompletePrediction;
    import com.google.android.libraries.places.api.model.Place;
    import com.google.android.libraries.places.api.model.RectangularBounds;
    import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
    import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
    import com.google.android.libraries.places.api.net.PlacesClient;
    import com.google.android.material.floatingactionbutton.FloatingActionButton;
    import com.google.maps.DirectionsApiRequest;
    import com.google.maps.GeoApiContext;
    import com.google.maps.GeolocationApi;
    import com.google.maps.GeolocationApiRequest;
    import com.google.maps.PendingResult;
    import com.google.maps.internal.GeolocationResponseAdapter;
    import com.google.maps.internal.PolylineEncoding;
    import com.google.maps.model.DirectionsResult;
    import com.google.maps.model.DirectionsRoute;
    import com.google.maps.model.TravelMode;

    import java.security.cert.CertPathValidatorException;
    import java.util.ArrayList;
    import java.util.Collections;
    import java.util.List;
    import java.util.Objects;

    import es.dmoral.toasty.Toasty;

    import static androidx.navigation.Navigation.findNavController;


    public abstract class MapFragment extends Fragment
            implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

        public static final String PLACESAUTOCOMPLETE = "AUTOCOMPLETE";
        public GoogleMap mMap;
        public FloatingActionButton locationActionButton;
        public boolean cameraLocationSeguiment;
        public LocatePermissions locatePermissions;
        public AppViewModel appViewModel;
        public GeoApiContext mGeoApiContext = null;

        public MapViewModel mapViewModel;
        public FusedLocationProviderClient mFusedLocationProviderClient;
        public LatLng lastUserLocation;
        public LocationCallback locationCallback;
        public LocationRequest mLocationRequest;
        public Polyline polyline;
        public TextView searchTextViewIcon;
        public PlacesClient placesClient;
        public NavController navController;
        public Marker destinationMarker;
        public FloatingActionButton goToDirectionFloatingButton;
        public boolean loading = true;
        public LinearLayout contratacionLayout;
        public Marker choferMarker;
        public TextView infoContratacionTitle;
        public TextView infoContratacionDescription;
        private ConstraintLayout constraintLayout;
        private ConstraintLayout constraintContratarServicio;

        public MapFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_map, container, false);
        }

        @SuppressLint("RestrictedApi")
        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            appViewModel = ViewModelProviders.of(requireActivity()).get(AppViewModel.class);
            mapViewModel = ViewModelProviders.of(requireActivity()).get(MapViewModel.class);
            navController = Navigation.findNavController(view);

            locationActionButton = view.findViewById(R.id.locationActionButton);
            infoContratacionTitle = view.findViewById(R.id.infoContratacionTitle);
            infoContratacionDescription = view.findViewById(R.id.infoContratacionDescription);
            constraintContratarServicio = view.findViewById(R.id.constraintContratarServicio);
            constraintLayout = view.findViewById(R.id.constraintMap);
            goToDirectionFloatingButton = view.findViewById(R.id.goToActionButton);
            searchTextViewIcon = view.findViewById(R.id.searchTextViewIcon);
            contratacionLayout = view.findViewById(R.id.infoContratacionLinear);
            contratacionLayout.setVisibility(View.GONE);
            goToDirectionFloatingButton.setVisibility(View.GONE);
            locationActionButton.setVisibility(View.GONE);



            SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

            Places.initialize(requireContext(), getString(R.string.google_maps_key));
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey("AIzaSyBJJhJxcJ-Eplgi7J8X1K-Pe6QIl2_R0C8")
                    .build();


            placesClient = Places.createClient(requireContext());

            mFusedLocationProviderClient = LocationServices
                    .getFusedLocationProviderClient(requireActivity());

            locatePermissions = new LocatePermissions();
            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(5000);
            mLocationRequest.setFastestInterval(2500);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

            List<Place.Field> placeFields = Collections.singletonList(Place.Field.NAME);

            FindCurrentPlaceRequest request =
                    FindCurrentPlaceRequest.newInstance(placeFields);

            if (lastUserLocation == null && appViewModel.lastUserLocation != null) {
                Log.i("UserGPS", "Cache location");
                lastUserLocation = new LatLng(appViewModel.lastUserLocation.latitude, appViewModel.lastUserLocation.longitude);
            }
            supportMapFragment.getMapAsync(this);
            ConstraintSet constraintSet = new ConstraintSet();

            switch (navController.getCurrentDestination().getId()){
                case R.id.seguimientoFragment:
                    constraintContratarServicio.setVisibility(View.GONE);
                    constraintSet.clone(constraintLayout);
                    constraintSet.setVerticalWeight(R.id.infoContratacionLinear, 28);
                    constraintSet.applyTo(constraintLayout);
                    contratacionLayout.setVisibility(View.VISIBLE);

                    break;

                case R.id.contratacionFragment:
                    constraintContratarServicio.setVisibility(View.VISIBLE);
                    constraintSet.clone(constraintLayout);
                    constraintSet.setVerticalWeight(R.id.infoContratacionLinear, 48);
                    constraintSet.applyTo(constraintLayout);
                    contratacionLayout.setVisibility(View.VISIBLE);

                    break;
            }



        }

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;
            mMap.setBuildingsEnabled(true);
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.getUiSettings().setAllGesturesEnabled(false);
            mMap.getUiSettings().setScrollGesturesEnabled(true);
            mMap.getUiSettings().setZoomGesturesEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.style_json));



            // Position the map's camera near Alice Springs in the center of Australia,
            // and set the zoom factor so most of Australia shows on the screen.
            if (appViewModel.userCameraPosition != null) googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(appViewModel.userCameraPosition));
            else googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(40, -3), 5));

            if (appViewModel.destinationMarkerOptions != null) {
                if (destinationMarker != null) destinationMarker.remove();
                destinationMarker = mMap.addMarker(appViewModel.destinationMarkerOptions);
                if (polyline != null) polyline.remove();
                calculateDirections(destinationMarker.getPosition());
            }

            if (checkPermissions()) {
                enableLocation();
                goToLocation(getDeviceLocation(), false);
                Log.e("GPS", "WORK");
            }
            locationActionButton.setOnClickListener(view -> {
                if (checkPermissions() == false) {
                    Log.i("Location", "Requesting permissions...");
                    requestPermissions(
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            0);


                }
                else if (mMap.isMyLocationEnabled() == false) {
                    enableLocation();
                }
                else if (cameraLocationSeguiment) setCameraLocationSeguiment(false);
                else setCameraLocationSeguiment(true);
            });

            loadOnClickListeners();
            loadObservers();
            personalizar(getView());

        }

        private boolean checkPermissions(){
            if (locatePermissions.isLocatePermissionEnabled(getContext())){
                Log.e("PERMISSIONS GPS", "TRUE");
                return true;
            }
            else {
                Log.e("PERMISSIONS GPS", "FALSE");
                return false;
            }
        }


        private LatLng getDeviceLocation() {
            try {
                    Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                    locationResult.addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful()) {
                                // Set the map's camera position to the current location of the device.
                                Location location = task.getResult();
                                if (location == null) return;
                                lastUserLocation = new LatLng(location.getLatitude(),
                                        location.getLongitude());
                            }
                        }
                    });

            } catch (SecurityException e) {
                Log.e("Exception: %s", e.getMessage());
            }

            return lastUserLocation;
        }



        @SuppressLint("MissingPermission")
        private void enableLocation(){
            Log.i("Permissions", "Permissions enabled!");
            locationActionButton.setImageResource(R.drawable.ic_my_location_black_24dp);
            searchTextViewIcon.setText("");
            mMap.setMyLocationEnabled(true);
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        lastUserLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        if (cameraLocationSeguiment) {
                            goToLocation(lastUserLocation, true);
                        } }
                };
            };


            mMap.setOnCameraMoveStartedListener(reasonCode -> {
                if (reasonCode == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
                    // I will no longer keep updating the camera location because
                    // the user interacted with it. This is my field I check before
                    // snapping the camera location to the latest value.
                    setCameraLocationSeguiment(false);
                }
            });
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,locationCallback,null /* Looper */);

        }

        public void setCameraLocationSeguiment(boolean cameraLocationSeguiment) {
            if (cameraLocationSeguiment) {
                ImageViewCompat.setImageTintList(locationActionButton, ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorPrimary)));
                goToLocation(getDeviceLocation(), true);
            }
            else ImageViewCompat.setImageTintList(locationActionButton, ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.colorGris)));
            this.cameraLocationSeguiment = cameraLocationSeguiment;
            Log.e("ABCD", "Location set to:" + this.cameraLocationSeguiment);

        }

        public void goToLocation(LatLng coords, boolean animated){
            if (coords == null) {
                Log.e("GPS", "Coords null!");
                return;
            }
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(coords, (mMap.getCameraPosition().zoom <= 14 ? 17 : mMap.getCameraPosition().zoom));

            if (animated) {
                mMap.animateCamera(cameraUpdate, 1250, null);
            } else {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(coords));
            }
        }

        public abstract List<MarkerOptions> marcadores();

        public abstract void personalizar(View view);

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            Log.i("PERMISSIONS", "CHECKING");
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableLocation();
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
            }
            return;

            // other 'case' lines to check for other
            // permissions this app might request.

        }



        public void calculateDirections(LatLng latLng) {
            String TAG = "DirectionsAPI";
            Log.d(TAG, "calculateDirections: calculating directions.");

            if (lastUserLocation == null) {
                Toasty.error(requireContext(), "Activa la ubicacion para poder calcular una ruta hacia ti", Toasty.LENGTH_LONG, true).show();
                return;
            }

            appViewModel.lastUserLocation = new LatLng(lastUserLocation.latitude, lastUserLocation.longitude);

            DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);
            directions.mode(TravelMode.DRIVING);

            directions.alternatives(false);
            directions.origin(
                    new com.google.maps.model.LatLng(
                            lastUserLocation.latitude,
                            lastUserLocation.longitude
                    )
            );
            com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                    latLng.latitude,
                    latLng.longitude
            );



            //Log.d(TAG, "calculateDirections: destination: " + placeId);
            //directions.destination("place_id:"+ placeId).setCallback(new PendingResult.Callback<DirectionsResult>() {
            directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
                @Override
                public void onResult(DirectionsResult result) {
                    Log.d(TAG, "onResult: routes: " + result.routes[0].toString());
                    Log.d(TAG, "onResult: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                    appViewModel.userRoute = result;
                    addPolylinesToMap(result);

                    Log.i("DEBUGAPI", "Add lines!.");


                }

                @Override
                public void onFailure(Throwable e) {
                    Log.e(TAG, "onFailure: " + e.getMessage() );

                }
            });
        }

        public void addPolylinesToMap(final DirectionsResult result){

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                    DirectionsRoute route =  result.routes[0];
                        List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                        List<LatLng> newDecodedPath = new ArrayList<>();
                        int width = getResources().getDisplayMetrics().widthPixels;
                        int height = getResources().getDisplayMetrics().heightPixels;
                        int padding = (int) (width * 0.29); // offset from edges of the map 10% of screen

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        // This loops through all the LatLng coordinates of ONE polyline.
                        for(com.google.maps.model.LatLng latLng: decodedPath){

//                        Log.d(TAG, "run: latlng: " + latLng.toString());

                            newDecodedPath.add(new LatLng(
                                    latLng.lat,
                                    latLng.lng
                            ));
                            builder.include(new LatLng(latLng.lat, latLng.lng));

                        }

                        if (polyline != null) polyline.remove();
                        polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                        polyline.setColor(Color.WHITE);
                        polyline.setClickable(true);

                        builder.include(lastUserLocation);

                        LatLngBounds bounds = builder.build();

                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                        mMap.animateCamera(cu);


                }
            });
        }



        public GoogleMap getmMap() {
            return mMap;
        }

        public LatLng getLastUserLocation() {
            return lastUserLocation;
        }

        public void loadObservers(){
            appViewModel.polylineOptionsMutableLiveData.observe(getViewLifecycleOwner(), new Observer<List<LatLng>>() {
                @Override
                public void onChanged(List<LatLng> latLngs) {
                    polyline = mMap.addPolyline(new PolylineOptions().addAll(latLngs).color(Color.WHITE).width(15));
                }
            });
        }

        public void loadOnClickListeners(){
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    Log.i("Test", latLng.toString());
                }
            });

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Log.i("MarkerClickListener", "null");
                    return false;
                }
            });
            goToDirectionFloatingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (polyline == null);
                }
            });

        }

        public void setDestinationMarker(Place place){
            appViewModel.userCameraPosition = mMap.getCameraPosition();
            if (lastUserLocation != null) appViewModel.lastUserLocation = new LatLng(lastUserLocation.latitude, lastUserLocation.longitude);

            if (polyline != null) polyline.remove();
            appViewModel.destinationMarkerOptions = new MarkerOptions()
                                                    .position(Objects.requireNonNull(place.getLatLng()))
                                                    .title(place.getName())
                                                    .snippet(place.getAddress());

            setCameraLocationSeguiment(false);
            if (destinationMarker != null) destinationMarker.remove();
            destinationMarker = mMap.addMarker(appViewModel.destinationMarkerOptions);
            goToLocation(destinationMarker.getPosition(), true);
        }

        public void setDestinationMarker(LatLng latLng){
            if (lastUserLocation != null) appViewModel.lastUserLocation = new LatLng(lastUserLocation.latitude, lastUserLocation.longitude);

            appViewModel.userCameraPosition = mMap.getCameraPosition();
            if (polyline != null) polyline.remove();
            appViewModel.destinationMarkerOptions = new MarkerOptions().position(Objects.requireNonNull(latLng));
            setCameraLocationSeguiment(false);
            if (destinationMarker != null) destinationMarker.remove();
            destinationMarker = mMap.addMarker(appViewModel.destinationMarkerOptions);
            goToLocation(destinationMarker.getPosition(), true);
        }
        public com.google.android.gms.maps.model.LatLng convertGoogleLatLngToNormalLatLng(com.google.maps.model.LatLng googleLatLng){
            return new com.google.android.gms.maps.model.LatLng(googleLatLng.lat, googleLatLng.lng);
        }
    }

