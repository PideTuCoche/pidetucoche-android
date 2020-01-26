package com.adrian.pidetucoche.view.listalocalizaciones;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.adrian.pidetucoche.R;
import com.adrian.pidetucoche.view.AppViewModel;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.maps.GeoApiContext;
import com.google.maps.model.LatLng;

import java.util.List;

public class ListaLocalizacionesAdapter extends RecyclerView.Adapter<ListaLocalizacionesAdapter.ListaLocalizacionesHolder>{

    List<AutocompletePrediction> predictions;
    AppViewModel appViewModel;
    NavController navController;
    SearchView searchView;

    public ListaLocalizacionesAdapter(AppViewModel appViewModel, NavController navController, SearchView searchView) {
        this.appViewModel = appViewModel;
        this.navController = navController;
        this.searchView = searchView;
    }


    @NonNull
    @Override
    public ListaLocalizacionesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ListaLocalizacionesHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_listalocalizaciones, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListaLocalizacionesHolder holder, final int position) {
        final AutocompletePrediction prediction = predictions.get(position);

        holder.placesName.setText(prediction.getPrimaryText(null).toString());
        holder.placesIcon.setImageResource(R.drawable.ic_location_on_black_24dp);


        holder.placesName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("RECYCLERVIEW", holder.placesName + "CLICKED!");
                appViewModel.localizacionSelecionada.setValue(prediction.getPlaceId());
                searchView.clearFocus();
            }
        });

    }

    @Override
    public int getItemCount() {
        return predictions == null ? 0 : predictions.size();
    }

    public void establecerListaElementos(List<AutocompletePrediction> elementos){
        this.predictions = elementos;
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                appViewModel.localizacionSelecionada.setValue(predictions.get(0).getPlaceId());
                searchView.clearFocus();
            }
        });
        notifyDataSetChanged();
    }

    class ListaLocalizacionesHolder extends RecyclerView.ViewHolder {
        TextView placesName;
        ImageView placesIcon;

        public ListaLocalizacionesHolder(@NonNull View itemView) {
            super(itemView);
            placesName = itemView.findViewById(R.id.places_name);
            placesIcon = itemView.findViewById(R.id.places_icon);
        }
    }
}