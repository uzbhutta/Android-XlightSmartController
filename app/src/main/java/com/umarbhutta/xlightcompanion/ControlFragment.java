package com.umarbhutta.xlightcompanion;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;

import me.priyesh.chroma.ChromaDialog;
import me.priyesh.chroma.ColorMode;
import me.priyesh.chroma.ColorSelectListener;

/**
 * Created by Umar Bhutta.
 */
public class ControlFragment extends Fragment {
    private static final String TAG = ControlFragment.class.getSimpleName();
    private Switch powerSwitch;
    private SeekBar brightnessSeekBar;
    private TextView colorTextView;
    private Spinner scenarioSpinner;

    private String colorHex;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_control, container, false);

        powerSwitch = (Switch) view.findViewById(R.id.powerSwitch);
        brightnessSeekBar = (SeekBar) view.findViewById(R.id.brightnessSeekBar);
        colorTextView = (TextView) view.findViewById(R.id.colorTextView);

        scenarioSpinner = (Spinner) view.findViewById(R.id.scenarioSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> scenarioAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, ScenarioFragment.name);
        // Specify the layout to use when the list of choices appears
        scenarioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the scenarioAdapter to the spinner
        scenarioSpinner.setAdapter(scenarioAdapter);

        powerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //send "on" message to DevPowerSwitch
                    Common.CldPowerSwitch(getContext(), Common.DEFAULT_NODE_ID, Common.RING_ALL, Common.KEY_POWER, Common.VSTATUS_ON);
                } else {
                    //send "off" message to DevPowerSwitch
                    Common.CldPowerSwitch(getContext(), Common.DEFAULT_NODE_ID, Common.RING_ALL, Common.KEY_POWER, Common.VSTATUS_OFF);
                }
            }
        });

        colorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChromaDialog.Builder()
                        .initialColor(ContextCompat.getColor(getActivity(), R.color.colorAccent))
                        .colorMode(ColorMode.RGB) // There's also ARGB and HSV
                        .onColorSelected(new ColorSelectListener() {
                            @Override
                            public void onColorSelected(int color) {
                                Log.e(TAG, "int: " + color);
                                colorHex = String.format("%06X", (0xFFFFFF & color));
                                Log.e(TAG, "HEX: #" + colorHex);

                                int c = (int)Long.parseLong(colorHex, 16);
                                int r = (c >> 16) & 0xFF;
                                int g = (c >> 8) & 0xFF;
                                int b = (c >> 0) & 0xFF;
                                Log.e(TAG, "RGB: " + r + "," + g + "," + b);

                                colorHex = "#" + colorHex;
                                colorTextView.setText(colorHex);
                                colorTextView.setTextColor(Color.parseColor(colorHex));
                            }
                        })
                        .create()
                        .show(getFragmentManager(), "dialog");
            }
        });

        return view;
    }
}
