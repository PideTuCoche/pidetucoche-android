package com.adrian.pidetucoche.view;

import android.app.Application;
import android.drm.DrmStore;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.adrian.pidetucoche.db.AppDao;
import com.adrian.pidetucoche.db.AppDatabase;
import com.adrian.pidetucoche.db.Usuario;
import com.adrian.pidetucoche.view.listalocalizaciones.ListaLocalizacionesAdapter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.maps.model.DirectionsResult;

import java.util.List;

public class AppViewModel extends AndroidViewModel {
    public boolean tutorialpasado;
    public boolean userLogged;
    AppDao appDao;

    public enum EstadoContratacion{
        ESTADO_ESPERANDOCONDUCTOR,
        ESTADO_CONDUCTORESPERANDO,
        ESTADO_ENTRANSITO,
        ESTADO_FINALIZADO,
        ESTADO_FINGOTOREC
    }
    public MutableLiveData<String> terminoBusqueda = new MutableLiveData<>();
    public MutableLiveData<EstadoDelRegistro> estadoDelRegistro = new MutableLiveData<>();
    public MutableLiveData<EstadoDelLogin> estadoDelLogin = new MutableLiveData<>();
    public MutableLiveData<List<LatLng>> polylineOptionsMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<Place> placeMutableLiveData = new MutableLiveData<>();

    private AutocompleteSessionToken token;

    public MutableLiveData<List> resultadoBusquedas = new MutableLiveData<>();
    public MutableLiveData<String> localizacionSelecionada = new MutableLiveData<>();
    public MutableLiveData<EstadoContratacion> estadoContratacionMutableLiveData = new MutableLiveData<>();
    public DirectionsResult userRoute;


    public void resetearEstadoRegister() {estadoDelRegistro.postValue(EstadoDelRegistro.INITIAL);}
    public void resetearEstadoLogin() {estadoDelLogin.postValue(EstadoDelLogin.INITIAL);}
    public enum EstadoDelRegistro {REGISTRO_COMPLETADO, EMAIL_NO_DISPONBLE, EMAIL_NO_VALIDO, INITIAL}
    public enum EstadoDelLogin {LOGIN_COMPLETADO, EMAIL_VACIO, PASSWORD_VACIA, CUENTA_INVALIDA, INITIAL}

    public RecyclerView elementosRecyclerView;
    public ListaLocalizacionesAdapter placesAdapter;
    public CameraPosition userCameraPosition;
    public MarkerOptions destinationMarkerOptions;
    public LatLng lastUserLocation;
    public boolean servicioContratado;
    public MarkerOptions choferMarkerOptions;
    public MutableLiveData<Boolean> registerActived = new MutableLiveData<>();
    public boolean registerActivated;



    public float density;


    public AppViewModel(@NonNull Application application) {
        super(application);

        appDao = AppDatabase.getInstance(application).appDao();
    }

    public void registrarUsuarioYLogear(final String email, final String passw) {
        resetearEstadoRegister();
        if(email.isEmpty()){
            estadoDelRegistro.postValue(EstadoDelRegistro.EMAIL_NO_VALIDO);
        }

        AsyncTask.execute(() -> {
            Usuario usuario = appDao.comprobarEmailDisponible(email);
            if (usuario == null){
                appDao.InsertarUsuario(new Usuario(email, passw));
                estadoDelRegistro.postValue(EstadoDelRegistro.REGISTRO_COMPLETADO);

            } else {
                estadoDelRegistro.postValue(EstadoDelRegistro.EMAIL_NO_DISPONBLE );
            }
        });
    }

    public void logearUsuario(final String email, final String passw) {
        resetearEstadoLogin();
        if(email.isEmpty()){
            estadoDelLogin.setValue(EstadoDelLogin.EMAIL_VACIO);
            return;
        }
        if (passw.isEmpty()){
            estadoDelLogin.setValue(EstadoDelLogin.PASSWORD_VACIA);
            return;
        }

        AsyncTask.execute(() -> {
            Usuario usuario = appDao.getUsername(email, passw);
            if (usuario == null){
                estadoDelLogin.postValue(EstadoDelLogin.CUENTA_INVALIDA);

            } else {
                estadoDelLogin.postValue(EstadoDelLogin.LOGIN_COMPLETADO);
            }
        });
    }

    public void verTodos(){
        AsyncTask.execute(() -> {
            List<Usuario> usuarios = appDao.getAllUsers();

            for(Usuario usuario : usuarios){
                Log.e("ABCD", ""+ usuario);
            }
        });
    }

    public void establecerTerminoBusqueda(String t){
        terminoBusqueda.setValue(t);
    }

    public AutocompleteSessionToken getToken() {
        return token;
    }

    public void setToken(AutocompleteSessionToken token) {
        this.token = token;
    }
}
