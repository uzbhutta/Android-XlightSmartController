package com.umarbhutta.xlightcompanion.settings;

import android.content.SharedPreferences;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

/**
 * Created by Umar Bhutta.
 */
public class SettingsFragment extends PreferenceFragment {

    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

    //get the sharedPreferences fields
    public String getStoragePreference() {
        return sharedPreferences.getString("tempUnits", "celsius");
    }

    //set the sharedPreferences
    public void setSharedPreferences(String unit) {
        sharedPreferences
                .edit()
                .putString("tempUnits", unit)
                .apply();
    }


}
