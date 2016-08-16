package com.umarbhutta.xlightcompanion;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
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
    private TextView addTextView, cancelTextView;
    private EditText nameEditText;

    private boolean scenarioPower = false;
    private int scenarioBrightness = 0;
    private int c, r, g, b;
    private String colorHex, scenarioName, scenarioInfo;

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
        addTextView = (TextView) findViewById(R.id.addTextView);
        cancelTextView = (TextView) findViewById(R.id.cancelTextView);
        nameEditText = (EditText) findViewById(R.id.nameEditText);

        powerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    scenarioPower = true;
                } else {
                    scenarioPower = false;
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
                                colorHex = String.format("%06X", (0xFFFFFF & color));
                                Log.e(TAG, "HEX: #" + colorHex);

                                c = (int)Long.parseLong(colorHex, 16);
                                r = (c >> 16) & 0xFF;
                                g = (c >> 8) & 0xFF;
                                b = (c >> 0) & 0xFF;
                                Log.e(TAG, "RGB: " + r + "," + g + "," + b);

                                colorHex = "#" + colorHex;
                                colorTextView.setText(colorHex);
                                colorTextView.setTextColor(Color.parseColor(colorHex));
                            }
                        })
                        .create()
                        .show(getSupportFragmentManager(), "dialog");
            }
        });

        addTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //send info back to ScenarioFragment
                if (nameEditText.getText().toString() != null) {
                    scenarioName = nameEditText.getText().toString();
                }

                if (scenarioPower) {
                    scenarioInfo = "A " + colorHex + " color with " + scenarioBrightness + "% brightness";
                } else {
                    scenarioInfo = "Turn all rings off";
                }

                //SEND TO PARTICLE CLOUD FOR ALL RINGS
                //TODO: send for multiple rings
                Common.CldJSONCommand(AddScenarioActivity.this, scenarioPower, r, g, b);

                //send data to update the list
                Intent returnIntent = getIntent();
                returnIntent.putExtra(ScenarioFragment.SCENARIO_NAME, scenarioName);
                returnIntent.putExtra(ScenarioFragment.SCENARIO_INFO, scenarioInfo);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go back to ScenarioFragment, do nothing
                finish();
            }
        });
    }
}
