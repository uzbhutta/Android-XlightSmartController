package com.umarbhutta.xlightcompanion.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.umarbhutta.xlightcompanion.particle.ParticleBridge;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.scenario.ScenarioFragment;

import java.util.ArrayList;

import me.priyesh.chroma.ChromaDialog;
import me.priyesh.chroma.ColorMode;
import me.priyesh.chroma.ColorSelectListener;

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
