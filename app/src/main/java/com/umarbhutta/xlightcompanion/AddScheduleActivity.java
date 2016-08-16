package com.umarbhutta.xlightcompanion;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class AddScheduleActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private CheckBox isRepeatCheckbox;
    private Spinner scenarioSpinner;
    private TextView addTextView, cancelTextView;
    private ImageView backImageView;

    private int hour, minute;
    private boolean isRepeat;
    private String days, scenarioName;

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
        addTextView = (TextView) findViewById(R.id.addTextView);
        cancelTextView = (TextView) findViewById(R.id.cancelTextView);
        backImageView = (ImageView) findViewById(R.id.backButton);

        //initialize spinner
        scenarioSpinner = (Spinner) findViewById(R.id.scenarioSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> scenarioAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, ScenarioFragment.name);
        // Specify the layout to use when the list of choices appears
        scenarioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the scenarioAdapter to the spinner
        scenarioSpinner.setAdapter(scenarioAdapter);

        //on click for add button
        addTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get values from timePicker
                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();
                //store hour and minute in String and handle edge cases
                String hourString = String.valueOf(hour);
                String minuteString = String.valueOf(minute);
                if (hour > 12) {
                    hourString = String.valueOf(hour - 12);
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

                //send data to update the list
                Intent returnIntent = getIntent();
                returnIntent.putExtra(ScheduleFragment.SCENARIO_NAME, scenarioName);
                returnIntent.putExtra(ScheduleFragment.SCHEDULE_HOUR, hour);
                returnIntent.putExtra(ScheduleFragment.SCHEDULE_MINUTE, minute);
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
