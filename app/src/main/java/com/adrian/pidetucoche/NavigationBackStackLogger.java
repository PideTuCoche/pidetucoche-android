package com.adrian.pidetucoche;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.navigation.Navigation;

public class NavigationBackStackLogger {
    public static void log(View view){
        Log.e("NBS", " ");
        Bundle b = Navigation.findNavController(view).saveState();
        for(int is : b.getIntArray("android-support-nav:controller:backStackIds")){
            Log.e("NBS", "> " +view.getContext().getResources().getResourceEntryName(is));
        }
    }
}