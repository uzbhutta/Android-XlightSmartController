package com.umarbhutta.xlightcompanion.schedule;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.umarbhutta.xlightcompanion.particle.ParticleBridge;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.scenario.ScenarioFragment;

import java.util.Calendar;

public class AddScheduleActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private CheckBox isRepeatCheckbox;
    private Spinner scenarioSpinner;
    private Button addButton;
    private ImageView backImageView;

    private int nodeId = ParticleBridge.DEFAULT_DEVICE_ID;
    private boolean isRepeat;
    private int hour, minute, daysInt = 0;
    private String  am_pm, days, scenarioName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_schedule);

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

        //initialize all views
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        isRepeatCheckbox = (CheckBox) findViewById(R.id.isRepeatCheckbox);
        scenarioSpinner = (Spinner) findViewById(R.id.scenarioSpinner);
        addButton = (Button) findViewById(R.id.addButton);
        backImageView = (ImageView) findViewById(R.id.backImageView);

        //initialize spinner
        scenarioSpinner = (Spinner) findViewById(R.id.scenarioSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> scenarioAdapter = new ArrayAdapter<>(this, R.layout.add_schedule_scenario_spinner_item, ScenarioFragment.name);
        // Specify the layout to use when the list of choices appears
        scenarioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the scenarioAdapter to the spinner
        scenarioSpinner.setAdapter(scenarioAdapter);

        //on click for add button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get values from timePicker
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    hour = timePicker.getHour();
                    minute = timePicker.getMinute();
                } else {
                    hour = timePicker.getCurrentHour();
                    minute = timePicker.getCurrentMinute();
                }

                Calendar datetime = Calendar.getInstance();
                datetime.set(Calendar.HOUR_OF_DAY, hour);
                datetime.set(Calendar.MINUTE, minute);

                if (datetime.get(Calendar.AM_PM) == Calendar.AM) {
                    am_pm = "AM";
                } else if (datetime.get(Calendar.AM_PM) == Calendar.PM) {
                    am_pm = "PM";
                }

                //store hour and minute in String and handle edge cases
                String hourString = String.valueOf(hour);
                String minuteString = String.valueOf(minute);
                if (hour > 12) {
                    hourString = String.valueOf(hour - 12);
                }
                if (hourString.length() == 1) {
                    hourString = "0" + hourString;
                }
                if (minute < 12) {
                    minuteString = "0" + String.valueOf(minute);
                }

                //get value of isRepeat
                isRepeat = isRepeatCheckbox.isChecked();

                //get value of spinner
                scenarioName = scenarioSpinner.getSelectedItem().toString();

                //TODO: get value of days

                //TODO: send to Particle
                ParticleBridge.CldJSONConfigSchedule(isRepeat, daysInt, hour, minute);
                ParticleBridge.CldJSONConfigRule(nodeId, scenarioName);

                //send data to update the list
                Intent returnIntent = getIntent();
                returnIntent.putExtra(ScheduleFragment.SCENARIO_NAME, scenarioName);
                returnIntent.putExtra(ScheduleFragment.SCHEDULE_HOUR, hourString);
                returnIntent.putExtra(ScheduleFragment.SCHEDULE_MINUTE, minuteString);
                returnIntent.putExtra(ScheduleFragment.SCHEDULE_AMPM, am_pm);
                returnIntent.putExtra(ScheduleFragment.SCHEDULE_ISREPEAT, isRepeat);
                returnIntent.putExtra(ScheduleFragment.SCHEDULE_DAYS, days);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
