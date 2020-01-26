package com.adrian.pidetucoche.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DividerItemDecoration;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.transition.Scene;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.adrian.pidetucoche.R;
import com.adrian.pidetucoche.view.listalocalizaciones.ListaLocalizacionesAdapter;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

import me.toptas.fancyshowcase.FancyShowCaseQueue;
import me.toptas.fancyshowcase.FancyShowCaseView;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private AppViewModel appViewModel;
    private ConstraintLayout.LayoutParams mAppBarParams;
    private DrawerLayout drawer;
    private AppBarLayout appBarLayout;
    private ConstraintLayout constraintLayout;
    private ConstraintSet constraintSet;
    private BottomNavigationView bottomNavView;
    private Toolbar searchToolbar;
    private NavController navController;
    private TextView fragmentNameView;
    private NavHostFragment navHostFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_main);

        appViewModel = ViewModelProviders.of(this).get(AppViewModel.class);

        appViewModel.setToken(AutocompleteSessionToken.newInstance());
//        appViewModel.verTodos();


        constraintLayout = findViewById(R.id.main_constraint);
        constraintSet = new ConstraintSet();

        appBarLayout = findViewById(R.id.app_bar_layout);

        appViewModel.density = getApplicationContext().getResources().getDisplayMetrics().density;

        searchToolbar = findViewById(R.id.toolbarSearch);
        fragmentNameView = findViewById(R.id.fragmentNameView);
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        updateActionBar(searchToolbar, drawer);
        mAppBarParams = (ConstraintLayout.LayoutParams) appBarLayout.getLayoutParams();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);


        bottomNavView = findViewById(R.id.bottom_nav_view);
        NavigationUI.setupWithNavController(bottomNavView, navController);

        // Initialize the AutocompleteSupportFragment.
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_maps_key), Locale.US);

        }

        bottomNavView.getMenu().getItem(1).setEnabled(false);






        SearchView searchView = findViewById(R.id.searchView);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                appViewModel.establecerTerminoBusqueda(newText);
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                appViewModel.terminoBusqueda.setValue("");
                searchToolbar.requestFocus();
                return false;
            }
        });

        appViewModel.elementosRecyclerView = findViewById(R.id.places_list);
        appViewModel.elementosRecyclerView.addItemDecoration(new DividerItemDecoration(appViewModel.elementosRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        appViewModel.placesAdapter = new ListaLocalizacionesAdapter(appViewModel, navController, searchView);
        appViewModel.elementosRecyclerView.setAdapter(appViewModel.placesAdapter);



        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller,
                                             @NonNull NavDestination destination, @Nullable Bundle arguments) {
                switch (destination.getId()){
                    case R.id.recogidaFragment:
                        bottomNavView.setVisibility(View.VISIBLE);
                        searchToolbar.setVisibility(View.VISIBLE);
                        fragmentNameView.setVisibility(View.GONE);
                        searchView.setVisibility(View.VISIBLE);
                        updateActionBar(searchToolbar, drawer);
                        updateActionBarMargins(16);
                        constraintSet.clone(constraintLayout);
                        constraintSet.connect(R.id.app_bar_layout, ConstraintSet.TOP, R.id.nav_host_fragment, ConstraintSet.TOP);
                        constraintSet.clear(R.id.app_bar_layout, ConstraintSet.BOTTOM);

                        constraintSet.applyTo(constraintLayout);
                        constraintSet.getParameters(R.id.app_bar_layout);

                        searchToolbar.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.white));
                        searchToolbar.getNavigationIcon().setColorFilter(ContextCompat.getColor(getBaseContext(), R.color.colorGris), PorterDuff.Mode.SRC_ATOP);

                        setConstraintDefaultLayout(R.id.nav_host_fragment);
                        // Habilitar item recogida y favoritos
                        bottomNavView.getMenu().getItem(0).setEnabled(true);
                        bottomNavView.getMenu().getItem(2).setEnabled(true);

                        // Deshabilitar item de seguimiento
                        bottomNavView.getMenu().getItem(1).setEnabled(false);

                        startTutorial();


                        break;

                    case R.id.seguimientoFragment:
                        setConstraintDefaultLayout(R.id.nav_host_fragment);
                        bottomNavView.setVisibility(View.VISIBLE);
                        searchToolbar.setVisibility(View.GONE);

                        /*constraintSet.clone(constraintLayout);
                        constraintSet.connect(R.id.app_bar_layout, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                        constraintSet.connect(R.id.app_bar_layout, ConstraintSet.BOTTOM, R.id.nav_host_fragment, ConstraintSet.TOP);
                        constraintSet.applyTo(constraintLayout);*/
                        // Habilitar item de seguimiento
                        bottomNavView.getMenu().getItem(1).setEnabled(true);

                        // Deshabilitar item de recogida
                        bottomNavView.getMenu().getItem(0).setEnabled(false);
                        bottomNavView.getMenu().getItem(2).setEnabled(false);

                        updateActionBarMargins(0);

                        break;

                    case R.id.contratacionFragment:
                        constraintSet.clone(constraintLayout);
                        constraintSet.connect(R.id.nav_host_fragment, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
                        constraintSet.applyTo(constraintLayout);

                        bottomNavView.setVisibility(View.GONE);
                        searchToolbar.setVisibility(View.VISIBLE);
                        fragmentNameView.setVisibility(View.VISIBLE);
                        searchView.setVisibility(View.GONE);
                        fragmentNameView.setText(R.string.title_contratacion);
                        navigationView.setVisibility(View.GONE);
                        appViewModel.placesAdapter.establecerListaElementos(null);

                        break;
                    case R.id.favoritosFragment:
                        bottomNavView.setVisibility(View.VISIBLE);
                        searchToolbar.setVisibility(View.GONE);
                        setConstraintDefaultLayout(R.id.nav_host_fragment);
                        appViewModel.placesAdapter.establecerListaElementos(null);

                        break;

                    case R.id.ultimosFragment:
                    case R.id.contactFragment:
                        constraintSet.clone(constraintLayout);

                        constraintSet.connect(R.id.app_bar_layout, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                        constraintSet.connect(R.id.app_bar_layout, ConstraintSet.BOTTOM, R.id.nav_view, ConstraintSet.TOP);
                        constraintSet.applyTo(constraintLayout);

                        searchView.setVisibility(View.GONE);
                        bottomNavView.setVisibility(View.GONE);
                        fragmentNameView.setVisibility(View.VISIBLE);
                        fragmentNameView.setText(destination.getId() == R.id.ultimosFragment ? "Ultimos viajes" : "Contáctanos");


                        searchToolbar.setBackgroundColor(ContextCompat.getColor(getBaseContext(), R.color.colorPrimary));
                        searchToolbar.getNavigationIcon().setColorFilter(ContextCompat.getColor(getBaseContext(), R.color.white), PorterDuff.Mode.SRC_ATOP);

                        fragmentNameView.setTextColor(Color.WHITE);
                        appViewModel.placesAdapter.establecerListaElementos(null);

                        /*
                        searchToolbar.setVisibility(View.VISIBLE);
                        fragmentNameView.setText(R.string.title_dashboard);
                        fragmentNameView.setVisibility(View.VISIBLE);

                        constraintSet.clone(constraintLayout);
                        constraintSet.connect(R.id.app_bar_layout, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                        constraintSet.connect(R.id.app_bar_layout, ConstraintSet.BOTTOM, R.id.nav_host_fragment, ConstraintSet.TOP);
                        updateActionBarMargins(0);
                        constraintSet.applyTo(constraintLayout);
                        navigationView.setVisibility(View.VISIBLE);

*/
                        updateActionBarMargins(0);
                        break;

                    default:
                        bottomNavView.setVisibility(View.GONE);
                        searchToolbar.setVisibility(View.GONE);
                        navigationView.setVisibility(View.GONE);
                        break;
                }
            }
        });
    }


    public void startTutorial(){


    }
    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }



    private void updateActionBar(Toolbar toolbar, DrawerLayout drawer){
        setSupportActionBar(toolbar);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.recogidaFragment, R.id.seguimientoFragment, R.id.favoritosFragment
        )
                .setDrawerLayout(drawer)
                .build();

    }

    private void updateActionBarMargins(int margin){
        int marginInDps = (int)(appViewModel.density*margin);
        mAppBarParams.setMargins(marginInDps, marginInDps, marginInDps, marginInDps);

    }

    private void setConstraintDefaultLayout(int fragmentId){
        constraintSet.clone(constraintLayout);
        switch (fragmentId){
            case R.id.nav_host_fragment:
                constraintSet.connect(fragmentId, ConstraintSet.TOP, R.id.app_bar_layout, ConstraintSet.BOTTOM);
                constraintSet.connect(fragmentId, ConstraintSet.BOTTOM, R.id.bottom_nav_view, ConstraintSet.TOP);
        }

        constraintSet.applyTo(constraintLayout);
    }

    @Override
    public void onBackPressed() {
        switch (navController.getCurrentDestination().getId()){
            case R.id.seguimientoFragment:
            case R.id.recogidaFragment:
            case R.id.loginFragment:
                finish();
                break;
            case R.id.registerFragment:
                navController.navigate(R.id.loginFragment);
                break;
                default:
                    navController.navigate(appViewModel.servicioContratado ? R.id.seguimientoFragment : R.id.recogidaFragment);
                    break;
        }
    }
}
