package com.adrian.pidetucoche.view.map;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.google.android.gms.maps.GoogleMap;

public class MapViewModel extends AndroidViewModel {
    private GoogleMap mMap;

    public MapViewModel(@NonNull Application application) {
        super(application);
    }

    public GoogleMap getmMap() {return mMap;}
    public void setmMap(GoogleMap mMap) {this.mMap = mMap;}
}
