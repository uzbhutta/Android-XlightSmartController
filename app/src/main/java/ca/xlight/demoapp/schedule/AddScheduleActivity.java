package ca.xlight.demoapp.schedule;

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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;

import ca.xlight.demoapp.R;
import ca.xlight.demoapp.SDK.xltDevice;
import ca.xlight.demoapp.main.MainActivity;
import ca.xlight.demoapp.scenario.ScenarioFragment;

import java.util.Calendar;

public class AddScheduleActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private CheckBox isRepeatCheckbox;
    private CheckBox checkboxMonday, checkboxTuesday, checkboxWednesday, checkboxThursday, checkboxFriday, checkboxSaturday, checkboxSunday;
    private Spinner scenarioSpinner;
    private Spinner deviceSpinner;
    private Button addButton;
    private ImageView backImageView;

    private boolean isRepeat = false;
    private int hour, minute;
    private String  am_pm, weekdays, outgoingWeekdays, scenarioName;
    //a boolean of which day of week has been selected in active (0-6, 0 =  Monday)
    private boolean[] weekdaySelected = {false, false, false, false, false, false, false};

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
        deviceSpinner = (Spinner) findViewById(R.id.deviceSpinner);
        addButton = (Button) findViewById(R.id.addButton);
        backImageView = (ImageView) findViewById(R.id.backImageView);

        checkboxSunday = (CheckBox) findViewById(R.id.checkboxSunday);
        checkboxMonday = (CheckBox) findViewById(R.id.checkboxMonday);
        checkboxTuesday = (CheckBox) findViewById(R.id.checkboxTuesday);
        checkboxWednesday = (CheckBox) findViewById(R.id.checkboxWednesday);
        checkboxThursday = (CheckBox) findViewById(R.id.checkboxThursday);
        checkboxFriday = (CheckBox) findViewById(R.id.checkboxFriday);
        checkboxSaturday = (CheckBox) findViewById(R.id.checkboxSaturday);


        //initialize scenario spinner
        scenarioSpinner = (Spinner) findViewById(R.id.scenarioSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        final ArrayAdapter<String> scenarioAdapter = new ArrayAdapter<>(this, R.layout.add_schedule_spinner_item, ScenarioFragment.name);
        // Specify the layout to use when the list of choices appears
        scenarioAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the scenarioAdapter to the spinner
        scenarioSpinner.setAdapter(scenarioAdapter);

        //initialize device spinner
        deviceSpinner = (Spinner) findViewById(R.id.deviceSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> deviceAdapter = new ArrayAdapter<>(this, R.layout.add_schedule_spinner_item, MainActivity.deviceNames);
        // Specify the layout to use when the list of choices appears
        deviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the scenarioAdapter to the spinner
        deviceSpinner.setAdapter(deviceAdapter);

        //repeat checkbox
        isRepeatCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isRepeat = isChecked;
            }
        });

        //weekdays checkbox

        checkboxMonday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                weekdaySelected[0] = isChecked;
            }
        });
        checkboxTuesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                weekdaySelected[1] = isChecked;
            }
        });
        checkboxWednesday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                weekdaySelected[2] = isChecked;
            }
        });
        checkboxThursday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                weekdaySelected[3] = isChecked;
            }
        });
        checkboxFriday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                weekdaySelected[4] = isChecked;
            }
        });
        checkboxSaturday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                weekdaySelected[5] = isChecked;
            }
        });
        checkboxSunday.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                weekdaySelected[6] = isChecked;
            }
        });


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

                //get value of device spinner
                //nodeId = (int) scenarioSpinner.getSelectedItemId();

                //get value of scenario spinner
                scenarioName = scenarioSpinner.getSelectedItem().toString();

                //get value of days and value of display
                weekdays = "";
                outgoingWeekdays = "";

                if (weekdaySelected[0]) { //Monday
                    weekdays += "1";
                    outgoingWeekdays += "Mo ";
                } else {
                    weekdays += "0";
                }

                if (weekdaySelected[1]) {
                    weekdays += "1";
                    outgoingWeekdays += "Tu ";
                } else {
                    weekdays += "0";
                }

                if (weekdaySelected[2]) {
                    weekdays += "1";
                    outgoingWeekdays += "We ";
                } else {
                    weekdays += "0";
                }

                if (weekdaySelected[3]) {
                    weekdays += "1";
                    outgoingWeekdays += "Th ";
                } else {
                    weekdays += "0";
                }

                if (weekdaySelected[4]) {
                    weekdays += "1";
                    outgoingWeekdays += "Fr ";
                } else {
                    weekdays += "0";
                }

                if (weekdaySelected[5]) {
                    weekdays += "1";
                    outgoingWeekdays += "Sa ";
                } else {
                    weekdays += "0";
                }

                if (weekdaySelected[6]) { //Sunday
                    weekdays += "1";
                    outgoingWeekdays += "Su ";
                } else {
                    weekdays += "0";
                }

                if (outgoingWeekdays == "Mo Tu We Th Fr Sa Su ") {
                    outgoingWeekdays = "Every day";
                } else if (outgoingWeekdays == "") {
                    outgoingWeekdays = "Today";
                }

                //call JSONConfigAlarm to send a schedule row
                // DMI
                //ParticleAdapter.JSONConfigAlarm(defeaultNodeId, isRepeat, weekdays, hour, minute, scenarioName);
                int scheduleId = ScheduleFragment.name.size();
                MainActivity.m_mainDevice.sceAddSchedule(scheduleId, isRepeat, weekdays, hour, minute, xltDevice.DEFAULT_ALARM_ID);
                // Get scenarioId from name
                int scenarioId = 1;
                for (int i = 0; i < ScenarioFragment.name.size(); i++) {
                    if (scenarioName == ScenarioFragment.name.get(i)) {
                        scenarioId = i;
                    }
                }
                MainActivity.m_mainDevice.sceAddRule(scheduleId, scheduleId, scenarioId);

                //send data to update the list
                Intent returnIntent = getIntent();
                returnIntent.putExtra(ScheduleFragment.SCENARIO_NAME, scenarioName);
                returnIntent.putExtra(ScheduleFragment.SCHEDULE_HOUR, hourString);
                returnIntent.putExtra(ScheduleFragment.SCHEDULE_MINUTE, minuteString);
                returnIntent.putExtra(ScheduleFragment.SCHEDULE_AMPM, am_pm);
                returnIntent.putExtra(ScheduleFragment.SCHEDULE_ISREPEAT, isRepeat);
                returnIntent.putExtra(ScheduleFragment.SCHEDULE_WEEKDAYS, outgoingWeekdays);
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
