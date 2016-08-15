package com.umarbhutta.xlightcompanion;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import me.priyesh.chroma.ChromaDialog;
import me.priyesh.chroma.ColorMode;
import me.priyesh.chroma.ColorSelectListener;

public class AddScenarioActivity extends AppCompatActivity {

    private static final String TAG = AddScenarioActivity.class.getSimpleName();
    private Switch powerSwitch;
    private SeekBar brightnessSeekBar;
    private TextView colorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_scenario);

        //hide nav bar
        getSupportActionBar().hide();

        //change status bar color to accent
        Window window = this.getWindow();
        //clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        //finally change the color
        window.setStatusBarColor(this.getResources().getColor(R.color.colorAccent));

        powerSwitch = (Switch) findViewById(R.id.powerSwitch);
        brightnessSeekBar = (SeekBar) findViewById(R.id.brightnessSeekBar);
        colorTextView = (TextView) findViewById(R.id.colorTextView);

        powerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    //store "on" value
                } else {
                    //store "off" value
                }
            }
        });

        colorTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ChromaDialog.Builder()
                        .initialColor(ContextCompat.getColor(AddScenarioActivity.this, R.color.colorPrimary))
                        .colorMode(ColorMode.RGB) // There's also ARGB and HSV
                        .onColorSelected(new ColorSelectListener() {
                            @Override
                            public void onColorSelected(int color) {
                                Log.e(TAG, "int: " + color);
                                String colorHex = String.format("%06X", (0xFFFFFF & color));
                                Log.e(TAG, "HEX: #" + colorHex);

                                int c = (int)Long.parseLong(colorHex, 16);
                                int r = (c >> 16) & 0xFF;
                                int g = (c >> 8) & 0xFF;
                                int b = (c >> 0) & 0xFF;
                                Log.e(TAG, "RGB: " + r + "," + g + "," + b);
                            }
                        })
                        .create()
                        .show(getSupportFragmentManager(), "dialog");
            }
        });
    }
}
