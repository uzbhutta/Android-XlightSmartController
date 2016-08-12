package com.umarbhutta.xlightcompanion;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SVBar;

/**
 * Created by Umar Bhutta.
 */
public class ControlFragment extends Fragment {
    private static final String TAG = ControlFragment.class.getSimpleName();
    private Switch powerSwitch;
    private SeekBar brightnessSeekBar;
    private TextView colorTextView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_control, container, false);

        powerSwitch = (Switch) view.findViewById(R.id.powerSwitch);
        brightnessSeekBar = (SeekBar) view.findViewById(R.id.brightnessSeekBar);
        colorTextView = (TextView) view.findViewById(R.id.colorTextView);

        powerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //send "on" message to DevPowerSwitch
                    Common.devSoftSwitch(getContext(), 0, 0, "on");
                } else {
                    //send "off" message to DevPowerSwitch
                    Common.devSoftSwitch(getContext(), 0, 0, "off");
                }
            }
        });

        colorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext());
                //setting custom layout to dialog
                dialog.setContentView(R.layout.colorpicker_dialog);
                dialog.setTitle("Set Color");

                ColorPicker picker = (ColorPicker) dialog.findViewById(R.id.picker);
                SVBar svBar = (SVBar) dialog.findViewById(R.id.svbar);
                picker.addSVBar(svBar);

                //To get the color
                picker.getColor();
                //To set the old selected color
                picker.setOldCenterColor(picker.getColor());
                //to turn of showing the old color
                picker.setShowOldCenterColor(false);

                //adds listener to the color picker which is implemented
                picker.setOnColorSelectedListener(new ColorPicker.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                        Log.e(TAG, "The color selected is: " + color);
                    }
                });

                dialog.show();
            }
        });

        return view;
    }
}
