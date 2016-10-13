package com.umarbhutta.xlightcompanion.control;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.main.MainActivity;
import com.umarbhutta.xlightcompanion.particle.ParticleBridge;
import com.umarbhutta.xlightcompanion.scenario.ScenarioFragment;

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
    private SeekBar cctSeekBar;
    private TextView colorTextView;
    private Spinner scenarioSpinner;
    private LinearLayout scenarioNoneLL;
    private ToggleButton ring1Button, ring2Button, ring3Button;
    private TextView deviceRingLabel, powerLabel, brightnessLabel, cctLabel, colorLabel;
    private ImageView lightImageView;

    private ArrayList<String> scenarioDropdown;

    private String colorHex;
    private boolean state = false;
    boolean ring1 = false, ring2 = false, ring3 = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_control, container, false);

        scenarioDropdown = new ArrayList<>(ScenarioFragment.name);
        scenarioDropdown.add(0, "None");

        powerSwitch = (Switch) view.findViewById(R.id.powerSwitch);
        brightnessSeekBar = (SeekBar) view.findViewById(R.id.brightnessSeekBar);
        cctSeekBar = (SeekBar) view.findViewById(R.id.cctSeekBar);
        cctSeekBar.setMax(6500-2700);
        colorTextView = (TextView) view.findViewById(R.id.colorTextView);
        scenarioNoneLL = (LinearLayout) view.findViewById(R.id.scenarioNoneLL);
        scenarioNoneLL.setAlpha(1);
        ring1Button = (ToggleButton) view.findViewById(R.id.ring1Button);
        ring2Button = (ToggleButton) view.findViewById(R.id.ring2Button);
        ring3Button = (ToggleButton) view.findViewById(R.id.ring3Button);
        deviceRingLabel = (TextView) view.findViewById(R.id.deviceRingLabel);
        brightnessLabel = (TextView) view.findViewById(R.id.brightnessLabel);
        cctLabel = (TextView) view.findViewById(R.id.cctLabel);
        powerLabel = (TextView) view.findViewById(R.id.powerLabel);
        colorLabel = (TextView) view.findViewById(R.id.colorLabel);
        lightImageView = (ImageView) view.findViewById(R.id.lightImageView);

        scenarioSpinner = (Spinner) view.findViewById(R.id.scenarioSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> scenarioAdapter = new ArrayAdapter<>(getActivity(), R.layout.control_scenario_spinner_item, scenarioDropdown);
        // Specify the layout to use when the list of choices appears
        scenarioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the scenarioAdapter to the spinner
        scenarioSpinner.setAdapter(scenarioAdapter);

        powerSwitch.setChecked(MainActivity.mainDevice_st > 0);
        brightnessSeekBar.setProgress(MainActivity.mainDevice_br);
        cctSeekBar.setProgress(MainActivity.mainDevice_cct - 2700);

        MainActivity.handlerControl = new Handler() {
            public void handleMessage(Message msg) {
                int intValue =  msg.getData().getInt("State", -255);
                if( intValue != -255 ) {
                    powerSwitch.setChecked(intValue > 0);
                }

                intValue =  msg.getData().getInt("BR", -255);
                if( intValue != -255 ) {
                    brightnessSeekBar.setProgress(intValue);
                }

                intValue =  msg.getData().getInt("CCT", -255);
                if( intValue != -255 ) {
                    cctSeekBar.setProgress(intValue - 2700);
                }
            }
        };

        powerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //check if on or off
                state = isChecked;
                //ParticleBridge.JSONCommandPower(ParticleBridge.DEFAULT_DEVICE_ID, state);
                ParticleBridge.FastCallPowerSwitch(ParticleBridge.DEFAULT_DEVICE_ID, state);
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

                                int cw = 0;
                                int ww = 0;
                                int c = (int) Long.parseLong(colorHex, 16);
                                int r = (c >> 16) & 0xFF;
                                int g = (c >> 8) & 0xFF;
                                int b = (c >> 0) & 0xFF;
                                Log.e(TAG, "RGB: " + r + "," + g + "," + b);

                                colorHex = "#" + colorHex;
                                colorTextView.setText(colorHex);
                                colorTextView.setTextColor(Color.parseColor(colorHex));

                                //send message to Particle based on which rings have been selected
                                if ((ring1 && ring2 && ring3) || (!ring1 && !ring2 && !ring3)) {
                                    ParticleBridge.JSONCommandColor(ParticleBridge.DEFAULT_DEVICE_ID, ParticleBridge.RING_ALL, state, cw, ww, r, g, b);
                                } else if (ring1 && ring2) {
                                    ParticleBridge.JSONCommandColor(ParticleBridge.DEFAULT_DEVICE_ID, ParticleBridge.RING_1, state, cw, ww, r, g, b);
                                    ParticleBridge.JSONCommandColor(ParticleBridge.DEFAULT_DEVICE_ID, ParticleBridge.RING_2, state, cw, ww, r, g, b);
                                } else if (ring2 && ring3) {
                                    ParticleBridge.JSONCommandColor(ParticleBridge.DEFAULT_DEVICE_ID, ParticleBridge.RING_2, state, cw, ww, r, g, b);
                                    ParticleBridge.JSONCommandColor(ParticleBridge.DEFAULT_DEVICE_ID, ParticleBridge.RING_3, state, cw, ww, r, g, b);
                                } else if (ring1 && ring3) {
                                    ParticleBridge.JSONCommandColor(ParticleBridge.DEFAULT_DEVICE_ID, ParticleBridge.RING_1, state, cw, ww, r, g, b);
                                    ParticleBridge.JSONCommandColor(ParticleBridge.DEFAULT_DEVICE_ID, ParticleBridge.RING_3, state, cw, ww, r, g, b);
                                } else if (ring1) {
                                    ParticleBridge.JSONCommandColor(ParticleBridge.DEFAULT_DEVICE_ID, ParticleBridge.RING_1, state, cw, ww, r, g, b);
                                } else if (ring2) {
                                    ParticleBridge.JSONCommandColor(ParticleBridge.DEFAULT_DEVICE_ID, ParticleBridge.RING_2, state, cw, ww, r, g, b);
                                } else if (ring3) {
                                    ParticleBridge.JSONCommandColor(ParticleBridge.DEFAULT_DEVICE_ID, ParticleBridge.RING_3, state, cw, ww, r, g, b);
                                } else {
                                    //do nothing
                                }
                            }
                        })
                        .create()
                        .show(getFragmentManager(), "dialog");
            }
        });

        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e(TAG, "The brightness value is " + seekBar.getProgress());
                ParticleBridge.JSONCommandBrightness(ParticleBridge.DEFAULT_DEVICE_ID, seekBar.getProgress());
            }
        });

        cctSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.d(TAG, "The CCT value is " + seekBar.getProgress()+2700);
                ParticleBridge.JSONCommandCCT(ParticleBridge.DEFAULT_DEVICE_ID, seekBar.getProgress()+2700);
            }
        });

        scenarioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).toString() == "None") {
                    //scenarioNoneLL.animate().alpha(1).setDuration(600).start();

                    //enable all views below spinner
                    disableEnableControls(true);
                } else {
                    //if anything but "None" is selected, fade scenarioNoneLL out
                    //scenarioNoneLL.animate().alpha(0).setDuration(500).start();

                    //disable all views below spinner
                    disableEnableControls(false);

                    ParticleBridge.JSONCommandScenario(ParticleBridge.DEFAULT_DEVICE_ID, position);
                    //position passed into above function corresponds to the scenarioId i.e. s1, s2, s3 to trigger
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ring1Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ring1 = isChecked;
                updateDeviceRingLabel();
            }
        });
        ring2Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ring2 = isChecked;
                updateDeviceRingLabel();
            }
        });
        ring3Button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ring3 = isChecked;
                updateDeviceRingLabel();
            }
        });

        return view;
    }

    private void disableEnableControls(boolean isEnabled) {
        powerSwitch.setEnabled(isEnabled);
        colorTextView.setEnabled(isEnabled);
        brightnessSeekBar.setEnabled(isEnabled);
        cctSeekBar.setEnabled(isEnabled);

        int selectColor = R.color.colorAccent, allLabels = R.color.textColorPrimary;
        if (isEnabled) {
            selectColor = R.color.colorAccent;
            allLabels = R.color.textColorPrimary;
        } else {
            selectColor = R.color.colorDisabled;
            allLabels = R.color.colorDisabled;
        }
        colorTextView.setTextColor(ContextCompat.getColor(getActivity(), selectColor));
        powerLabel.setTextColor(ContextCompat.getColor(getActivity(), allLabels));
        brightnessLabel.setTextColor(ContextCompat.getColor(getActivity(), allLabels));
        cctLabel.setTextColor(ContextCompat.getColor(getActivity(), allLabels));
        colorLabel.setTextColor(ContextCompat.getColor(getActivity(), allLabels));
    }

    private void updateDeviceRingLabel() {
        String label = ParticleBridge.DEFAULT_LAMP_TEXT;

        if (ring1 && ring2 && ring3) {
            label += ": " + ParticleBridge.RINGALL_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring123);
        } else if (!ring1 && !ring2 && !ring3) {
            label += ": " + ParticleBridge.RINGALL_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_noring);
        } else if (ring1 && ring2) {
            label += ": " + ParticleBridge.RING1_TEXT + " & " + ParticleBridge.RING2_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring12);
        } else if (ring2 && ring3) {
            label += ": " + ParticleBridge.RING2_TEXT + " & " + ParticleBridge.RING3_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring23);
        } else if (ring1 && ring3) {
            label += ": " + ParticleBridge.RING1_TEXT + " & " + ParticleBridge.RING3_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring13);
        } else if (ring1) {
            label += ": " + ParticleBridge.RING1_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring1);
        } else if (ring2) {
            label += ": " + ParticleBridge.RING2_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring2);
        } else if (ring3) {
            label += ": " + ParticleBridge.RING3_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring3);
        } else {
            label += "";
            lightImageView.setImageResource(R.drawable.aquabg_noring);
        }

        deviceRingLabel.setText(label);
    }
}
