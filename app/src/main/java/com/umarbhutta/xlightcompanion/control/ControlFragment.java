package com.umarbhutta.xlightcompanion.control;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.umarbhutta.xlightcompanion.SDK.xltDevice;
import com.umarbhutta.xlightcompanion.Tools.StatusReceiver;
import com.umarbhutta.xlightcompanion.main.MainActivity;
import com.umarbhutta.xlightcompanion.scenario.ScenarioFragment;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import me.priyesh.chroma.ChromaDialog;
import me.priyesh.chroma.ColorMode;
import me.priyesh.chroma.ColorSelectListener;

/**
 * Created by Umar Bhutta.
 */
public class ControlFragment extends Fragment {
    private static final String TAG = ControlFragment.class.getSimpleName();

    private static final String DEFAULT_LAMP_TEXT = "LIVING ROOM";
    private static final String RINGALL_TEXT = "ALL RINGS";
    private static final String RING1_TEXT = "RING 1";
    private static final String RING2_TEXT = "RING 2";
    private static final String RING3_TEXT = "RING 3";

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

    private Handler m_handlerControl;

    private Timer mTimer = null;
    private TimerTask mTimerTask = null;
    private static int count = 0;
    private boolean isPause = false;
    private boolean isStop = true;
    private int ran_r = 125, ran_g = 50, ran_b = 0;

    private class MyStatusReceiver extends StatusReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            powerSwitch.setChecked(MainActivity.m_mainDevice.getState() > 0);
            brightnessSeekBar.setProgress(MainActivity.m_mainDevice.getBrightness());
            cctSeekBar.setProgress(MainActivity.m_mainDevice.getCCT() - 2700);
        }
    }
    private final MyStatusReceiver m_StatusReceiver = new MyStatusReceiver();

    @Override
    public void onDestroyView() {
        if (!isStop) {
            stopTimer();
        }

        MainActivity.m_mainDevice.removeDeviceEventHandler(m_handlerControl);
        if( MainActivity.m_mainDevice.getEnableEventBroadcast() ) {
            getContext().unregisterReceiver(m_StatusReceiver);
        }
        super.onDestroyView();
    }

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

        powerSwitch.setChecked(MainActivity.m_mainDevice.getState() > 0);
        brightnessSeekBar.setProgress(MainActivity.m_mainDevice.getBrightness());
        cctSeekBar.setProgress(MainActivity.m_mainDevice.getCCT() - 2700);

        if( MainActivity.m_mainDevice.getEnableEventBroadcast() ) {
            IntentFilter intentFilter = new IntentFilter(xltDevice.bciDeviceStatus);
            intentFilter.setPriority(3);
            getContext().registerReceiver(m_StatusReceiver, intentFilter);
        }

        if( MainActivity.m_mainDevice.getEnableEventSendMessage() ) {
            m_handlerControl = new Handler() {
                public void handleMessage(Message msg) {
                    int intValue = msg.getData().getInt("State", -255);
                    if (intValue != -255) {
                        powerSwitch.setChecked(intValue > 0);
                    }

                    intValue = msg.getData().getInt("BR", -255);
                    if (intValue != -255) {
                        brightnessSeekBar.setProgress(intValue);
                    }

                    intValue = msg.getData().getInt("CCT", -255);
                    if (intValue != -255) {
                        cctSeekBar.setProgress(intValue - 2700);
                    }
                }
            };
            MainActivity.m_mainDevice.addDeviceEventHandler(m_handlerControl);
            updateDeviceRingLabel();
        }

        powerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //check if on or off
                state = isChecked;
                //ParticleAdapter.JSONCommandPower(ParticleAdapter.DEFAULT_DEVICE_ID, state);
                //ParticleAdapter.FastCallPowerSwitch(ParticleAdapter.DEFAULT_DEVICE_ID, state);
                MainActivity.m_mainDevice.PowerSwitch(state ? xltDevice.STATE_ON : xltDevice.STATE_OFF);
            }
        });

        colorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int initColor;
                int ringID = xltDevice.RING_ID_ALL;
                if( ring1 && !ring2 && !ring3 ) ringID = xltDevice.RING_ID_1;
                if( ring2 && !ring1 && !ring3 ) ringID = xltDevice.RING_ID_2;
                if( ring3 && !ring1 && !ring2 ) ringID = xltDevice.RING_ID_3;
                if( MainActivity.m_mainDevice.getRed(ringID) == 0 && MainActivity.m_mainDevice.getGreen(ringID) == 0 && MainActivity.m_mainDevice.getBlue(ringID) == 0) {
                    initColor = ContextCompat.getColor(getActivity(), R.color.colorAccent);
                } else {
                    initColor = Color.argb(0xff, MainActivity.m_mainDevice.getRed(ringID), MainActivity.m_mainDevice.getGreen(ringID), MainActivity.m_mainDevice.getBlue(ringID));
                }
                Log.e(TAG, "int: " + initColor + " HEX: #" + String.format("%06X", (0xFFFFFF & initColor)));
                new ChromaDialog.Builder()
                        .initialColor(initColor)
                        .colorMode(ColorMode.RGB) // There's also ARGB and HSV
                        .onColorSelected(new ColorSelectListener() {
                            @Override
                            public void onColorSelected(int color) {
                                colorHex = String.format("%06X", (0xFFFFFF & color));
                                Log.e(TAG, "int: " + color + " HEX: #" + colorHex);

                                state = powerSwitch.isChecked();
                                int br = brightnessSeekBar.getProgress();
                                //int ww = (cctSeekBar.getProgress() / ((6500 - 2700) * 255));
                                int ww = 0;
                                int r = (color >> 16) & 0xFF;
                                int g = (color >> 8) & 0xFF;
                                int b = (color >> 0) & 0xFF;
                                Log.e(TAG, "RGB: " + r + "," + g + "," + b);

                                colorHex = "#" + colorHex;
                                colorTextView.setText(colorHex);
                                colorTextView.setTextColor(Color.parseColor(colorHex));

                                //send message to Particle based on which rings have been selected
                                if ((ring1 && ring2 && ring3) || (!ring1 && !ring2 && !ring3)) {
                                    //ParticleAdapter.JSONCommandColor(ParticleAdapter.DEFAULT_DEVICE_ID, ParticleAdapter.RING_ALL, state, br, ww, r, g, b);
                                    MainActivity.m_mainDevice.ChangeColor(xltDevice.RING_ID_ALL, state, br, ww, r, g, b);
                                    MainActivity.m_mainDevice.setWhite(xltDevice.RING_ID_ALL, ww);
                                    MainActivity.m_mainDevice.setRed(xltDevice.RING_ID_ALL, r);
                                    MainActivity.m_mainDevice.setGreen(xltDevice.RING_ID_ALL, g);
                                    MainActivity.m_mainDevice.setBlue(xltDevice.RING_ID_ALL, b);
                                } else if (ring1 && ring2) {
                                    //ParticleAdapter.JSONCommandColor(ParticleAdapter.DEFAULT_DEVICE_ID, ParticleAdapter.RING_1, state, br, ww, r, g, b);
                                    //ParticleAdapter.JSONCommandColor(ParticleAdapter.DEFAULT_DEVICE_ID, ParticleAdapter.RING_2, state, br, ww, r, g, b);
                                    MainActivity.m_mainDevice.ChangeColor(xltDevice.RING_ID_1, state, br, ww, r, g, b);
                                    MainActivity.m_mainDevice.ChangeColor(xltDevice.RING_ID_2, state, br, ww, r, g, b);
                                    MainActivity.m_mainDevice.setWhite(xltDevice.RING_ID_1, ww);
                                    MainActivity.m_mainDevice.setRed(xltDevice.RING_ID_1, r);
                                    MainActivity.m_mainDevice.setGreen(xltDevice.RING_ID_1, g);
                                    MainActivity.m_mainDevice.setBlue(xltDevice.RING_ID_1, b);
                                    MainActivity.m_mainDevice.setWhite(xltDevice.RING_ID_2, ww);
                                    MainActivity.m_mainDevice.setRed(xltDevice.RING_ID_2, r);
                                    MainActivity.m_mainDevice.setGreen(xltDevice.RING_ID_2, g);
                                    MainActivity.m_mainDevice.setBlue(xltDevice.RING_ID_2, b);
                                } else if (ring2 && ring3) {
                                    //ParticleAdapter.JSONCommandColor(ParticleAdapter.DEFAULT_DEVICE_ID, ParticleAdapter.RING_2, state, br, ww, r, g, b);
                                    //ParticleAdapter.JSONCommandColor(ParticleAdapter.DEFAULT_DEVICE_ID, ParticleAdapter.RING_3, state, br, ww, r, g, b);
                                    MainActivity.m_mainDevice.ChangeColor(xltDevice.RING_ID_2, state, br, ww, r, g, b);
                                    MainActivity.m_mainDevice.ChangeColor(xltDevice.RING_ID_3, state, br, ww, r, g, b);
                                    MainActivity.m_mainDevice.setWhite(xltDevice.RING_ID_2, ww);
                                    MainActivity.m_mainDevice.setRed(xltDevice.RING_ID_2, r);
                                    MainActivity.m_mainDevice.setGreen(xltDevice.RING_ID_2, g);
                                    MainActivity.m_mainDevice.setBlue(xltDevice.RING_ID_2, b);
                                    MainActivity.m_mainDevice.setWhite(xltDevice.RING_ID_3, ww);
                                    MainActivity.m_mainDevice.setRed(xltDevice.RING_ID_3, r);
                                    MainActivity.m_mainDevice.setGreen(xltDevice.RING_ID_3, g);
                                    MainActivity.m_mainDevice.setBlue(xltDevice.RING_ID_3, b);

                                } else if (ring1 && ring3) {
                                    //ParticleAdapter.JSONCommandColor(ParticleAdapter.DEFAULT_DEVICE_ID, ParticleAdapter.RING_1, state, br, ww, r, g, b);
                                    //ParticleAdapter.JSONCommandColor(ParticleAdapter.DEFAULT_DEVICE_ID, ParticleAdapter.RING_3, state, br, ww, r, g, b);
                                    MainActivity.m_mainDevice.ChangeColor(xltDevice.RING_ID_1, state, br, ww, r, g, b);
                                    MainActivity.m_mainDevice.ChangeColor(xltDevice.RING_ID_3, state, br, ww, r, g, b);
                                    MainActivity.m_mainDevice.setWhite(xltDevice.RING_ID_1, ww);
                                    MainActivity.m_mainDevice.setRed(xltDevice.RING_ID_1, r);
                                    MainActivity.m_mainDevice.setGreen(xltDevice.RING_ID_1, g);
                                    MainActivity.m_mainDevice.setBlue(xltDevice.RING_ID_1, b);
                                    MainActivity.m_mainDevice.setWhite(xltDevice.RING_ID_3, ww);
                                    MainActivity.m_mainDevice.setRed(xltDevice.RING_ID_3, r);
                                    MainActivity.m_mainDevice.setGreen(xltDevice.RING_ID_3, g);
                                    MainActivity.m_mainDevice.setBlue(xltDevice.RING_ID_3, b);
                                } else if (ring1) {
                                   //ParticleAdapter.JSONCommandColor(ParticleAdapter.DEFAULT_DEVICE_ID, ParticleAdapter.RING_1, state, br, ww, r, g, b);
                                    MainActivity.m_mainDevice.ChangeColor(xltDevice.RING_ID_1, state, br, ww, r, g, b);
                                    MainActivity.m_mainDevice.setWhite(xltDevice.RING_ID_1, ww);
                                    MainActivity.m_mainDevice.setRed(xltDevice.RING_ID_1, r);
                                    MainActivity.m_mainDevice.setGreen(xltDevice.RING_ID_1, g);
                                    MainActivity.m_mainDevice.setBlue(xltDevice.RING_ID_1, b);
                                } else if (ring2) {
                                    //ParticleAdapter.JSONCommandColor(ParticleAdapter.DEFAULT_DEVICE_ID, ParticleAdapter.RING_2, state, br, ww, r, g, b);
                                    MainActivity.m_mainDevice.ChangeColor(xltDevice.RING_ID_2, state, br, ww, r, g, b);
                                    MainActivity.m_mainDevice.setWhite(xltDevice.RING_ID_2, ww);
                                    MainActivity.m_mainDevice.setRed(xltDevice.RING_ID_2, r);
                                    MainActivity.m_mainDevice.setGreen(xltDevice.RING_ID_2, g);
                                    MainActivity.m_mainDevice.setBlue(xltDevice.RING_ID_2, b);
                                } else if (ring3) {
                                    //ParticleAdapter.JSONCommandColor(ParticleAdapter.DEFAULT_DEVICE_ID, ParticleAdapter.RING_3, state, br, ww, r, g, b);
                                    MainActivity.m_mainDevice.ChangeColor(xltDevice.RING_ID_3, state, br, ww, r, g, b);
                                    MainActivity.m_mainDevice.setWhite(xltDevice.RING_ID_3, ww);
                                    MainActivity.m_mainDevice.setRed(xltDevice.RING_ID_3, r);
                                    MainActivity.m_mainDevice.setGreen(xltDevice.RING_ID_3, g);
                                    MainActivity.m_mainDevice.setBlue(xltDevice.RING_ID_3, b);
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
                //ParticleAdapter.JSONCommandBrightness(ParticleAdapter.DEFAULT_DEVICE_ID, seekBar.getProgress());
                MainActivity.m_mainDevice.ChangeBrightness(seekBar.getProgress());
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
                //ParticleAdapter.JSONCommandCCT(ParticleAdapter.DEFAULT_DEVICE_ID, seekBar.getProgress()+2700);
                MainActivity.m_mainDevice.ChangeCCT(seekBar.getProgress() + 2700);
            }
        });

        scenarioSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (!isStop) {
                    stopTimer();
                }

                if (parent.getItemAtPosition(position).toString() == "None") {
                    //scenarioNoneLL.animate().alpha(1).setDuration(600).start();

                    //enable all views below spinner
                    disableEnableControls(true);
                } else {
                    //if anything but "None" is selected, fade scenarioNoneLL out
                    //scenarioNoneLL.animate().alpha(0).setDuration(500).start();

                    //disable all views below spinner
                    disableEnableControls(false);

                    //ParticleAdapter.JSONCommandScenario(ParticleAdapter.DEFAULT_DEVICE_ID, position);
                    //position passed into above function corresponds to the scenarioId i.e. s1, s2, s3 to trigger
                    //MainActivity.m_mainDevice.ChangeScenario(position);

                    // For demonstration
                    if (parent.getItemAtPosition(position).toString() == "Dinner") {
                        MainActivity.m_mainDevice.ChangeColor(xltDevice.RING_ID_ALL, true, 70, 197, 136, 33, 0);
                    } else if (parent.getItemAtPosition(position).toString() == "Sleep") {
                        MainActivity.m_mainDevice.ChangeColor(xltDevice.RING_ID_ALL, true, 10, 26, 254, 52, 0);
                    } else if (parent.getItemAtPosition(position).toString() == "Dance") {
                        if (isStop) {
                            startTimer();
                        }
                    }
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
        String label = MainActivity.m_mainDevice.getDeviceName();

        if (ring1 && ring2 && ring3) {
            label += ": " + RINGALL_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring123);
        } else if (!ring1 && !ring2 && !ring3) {
            label += ": " + RINGALL_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_noring);
        } else if (ring1 && ring2) {
            label += ": " + RING1_TEXT + " & " + RING2_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring12);
        } else if (ring2 && ring3) {
            label += ": " + RING2_TEXT + " & " + RING3_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring23);
        } else if (ring1 && ring3) {
            label += ": " + RING1_TEXT + " & " + RING3_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring13);
        } else if (ring1) {
            label += ": " + RING1_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring1);
        } else if (ring2) {
            label += ": " + RING2_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring2);
        } else if (ring3) {
            label += ": " + RING3_TEXT;
            lightImageView.setImageResource(R.drawable.aquabg_ring3);
        } else {
            label += "";
            lightImageView.setImageResource(R.drawable.aquabg_noring);
        }

        deviceRingLabel.setText(label);
    }

    private void startTimer(){
        isStop = false;
        if (mTimer == null) {
            mTimer = new Timer();
        }

        if (mTimerTask == null) {
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    Log.i(TAG, "count: "+String.valueOf(count));

                    int which;
                    Random random = new Random();
                    do {
                        try {
                            which = random.nextInt(3);
                            if( which == 0 ) {
                                //r = random.nextInt(256);
                                ran_r += random.nextInt(60);
                                ran_r %= 255;
                            } else if( which == 1 ) {
                                //g = random.nextInt(256);
                                ran_g += random.nextInt(45);
                                ran_g %= 255;
                            } else {
                                //b = random.nextInt(256);
                                ran_b += random.nextInt(36);
                                ran_b %= 255;
                            }
                            MainActivity.m_mainDevice.ChangeColor(xltDevice.RING_ID_ALL, true, 10, 0, ran_r, ran_g, ran_b);
                            Thread.sleep(2500);
                        } catch (InterruptedException e) {
                        }
                    } while (isPause);

                    count ++;
                }
            };
        }

        if(mTimer != null && mTimerTask != null )
            mTimer.schedule(mTimerTask, 1000, 1000);

    }

    private void stopTimer(){
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
        count = 0;
        isStop = true;
    }
}
