package com.umarbhutta.xlightcompanion.particle;

import android.content.Context;
import android.util.Log;

import com.umarbhutta.xlightcompanion.main.MainActivity;
import com.umarbhutta.xlightcompanion.scenario.ScenarioFragment;
import com.umarbhutta.xlightcompanion.schedule.ScheduleFragment;
import com.umarbhutta.xlightcompanion.particle.CloudAccount;

import java.io.IOException;
import java.util.ArrayList;

import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.devicesetup.ParticleDeviceSetupLibrary;

/**
 * Created by Umar Bhutta.
 */
public class ParticleBridge {
    //misc
    private static final String TAG = ParticleBridge.class.getSimpleName();

    //Max num constants
    public static final int MAX_SCHEDULES = 6;
    public static final int MAX_DEVICES = 6;

    //Particle vars
    public static ParticleDevice currDevice;
    private static int resultCode;

    //CLOUD FUNCTION CONSTS
    //cmd types
    public static final int VALUE_POWER = 1;
    public static final int VALUE_COLOR = 2;
    public static final int VALUE_BRIGHTNESS = 3;
    public static final int VALUE_SCENARIO = 4;
    public static final int VALUE_CCT = 5;
    //device id
    public static final int DEFAULT_DEVICE_ID = 1;
    //ring values
    public static final int RING_ALL = 0;
    public static final int RING_1 = 1;

    public static final int RING_2 = 2;
    public static final int RING_3 = 3;
    //ring text
    public static final String DEFAULT_LAMP_TEXT = "LIVING ROOM";
    public static final String RINGALL_TEXT = "ALL RINGS";
    public static final String RING1_TEXT = "RING 1";
    public static final String RING2_TEXT = "RING 2";
    public static final String RING3_TEXT = "RING 3";

    //on/off values
    public static final int STATE_OFF = 0;
    public static final int STATE_ON = 1;
    //default alarm/filter id
    public static final int DEFAULT_ALARM_ID = 255;
    public static final int DEFAULT_FILTER_ID = 0;

    //constants for testing lists
    public static final String[] deviceNames = {"Living Room", "Bedroom", "Basement Kitchen"};
    public static final String[] scheduleTimes = {"10:30 AM", "12:45 PM", "02:00 PM", "06:45 PM", "08:00 PM", "11:30 PM"};
    public static final String[] scheduleDays = {"Mo Tu We Th Fr", "Every day", "Mo We Th Sa Su", "Tomorrow", "We", "Mo Tu Fr Sa Su"};
    public static final String[] scenarioNames = {"Brunching", "Guests", "Naptime", "Dinner", "Sunset", "Bedtime"};
    public static final String[] scenarioDescriptions = {"A red color at 52% brightness", "A blue-green color at 100% brightness", "An amber color at 50% brightness", "Turn off", "A warm-white color at 100% brightness", "A green color at 52% brightness"};
    public static final String[] filterNames = {"Breathe", "Music Match", "Flash"};


    //Particle functions
    public static void authenticate(Context context) {
        ParticleDeviceSetupLibrary.init(context, MainActivity.class);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ParticleCloudSDK.getCloud().logIn(CloudAccount.EMAIL, CloudAccount.PASSWORD);
                    currDevice = ParticleCloudSDK.getCloud().getDevice(CloudAccount.DEVICE_ID);
                } catch (ParticleCloudException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static int JSONCommandPower(final int nodeId, final boolean state) {
        new Thread() {
            @Override
            public void run() {
                int power = state ? 1 : 0;

                // Make the Particle call here
                String json = "{\"cmd\":" + VALUE_POWER + ",\"node_id\":" + nodeId + ",\"state\":" + power + "}";
                //String json = "{'cmd':" + VALUE_POWER + ",'node_id':" + nodeId + ",'state':" + power + "}";
                ArrayList<String> message = new ArrayList<>();
                message.add(json);
                try {
                    Log.e(TAG, "JSONCommandPower" + message.get(0));
                    resultCode = currDevice.callFunction("JSONCommand", message);
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
                message.clear();
            }
        }.start();
        return resultCode;
    }

    public static int JSONCommandBrightness(final int nodeId, final int value) {
        new Thread() {
            @Override
            public void run() {
                // Make the Particle call here
                String json = "{\"cmd\":" + VALUE_BRIGHTNESS + ",\"node_id\":" + nodeId + ",\"value\":" + value + "}";
                ArrayList<String> message = new ArrayList<>();
                message.add(json);
                try {
                    Log.e(TAG, "JSONCommandBrightness" + message.get(0));
                    resultCode = currDevice.callFunction("JSONCommand", message);
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
                message.clear();
            }
        }.start();
        return resultCode;
    }

    public static int JSONCommandCCT(final int nodeId, final int value) {
        new Thread() {
            @Override
            public void run() {
                // Make the Particle call here
                String json = "{\"cmd\":" + VALUE_CCT + ",\"node_id\":" + nodeId + ",\"value\":" + value + "}";
                ArrayList<String> message = new ArrayList<>();
                message.add(json);
                try {
                    Log.d(TAG, "JSONCommandCCT" + message.get(0));
                    resultCode = currDevice.callFunction("JSONCommand", message);
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
                message.clear();
            }
        }.start();
        return resultCode;
    }

    public static int JSONCommandColor(final int nodeId, final int ring, final boolean state, final int cw, final int ww, final int r, final int g, final int b) {
        new Thread() {
            @Override
            public void run() {
                // Make the Particle call here
                int power = state ? 1 : 0;

                String json = "{\"cmd\":" + VALUE_COLOR + ",\"node_id\":" + nodeId + ",\"ring\":" + ring + ",\"color\":[" + power + "," + cw + "," + ww + "," + r + "," + g + "," + b + "]}";
                ArrayList<String> message = new ArrayList<>();
                message.add(json);
                try {
                    Log.e(TAG, "JSONCommandColor " + message.get(0));
                    resultCode = currDevice.callFunction("JSONCommand", message);
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
                message.clear();
            }
        }.start();
        return resultCode;
    }


    public static int JSONCommandScenario(final int nodeId, final int position) {
        new Thread() {
            @Override
            public void run() {
                //position corresponds to the spinner in Control. position of 1 corresponds to s1, 2 to s2. The 0th index in the spinner is the "None" item,
                //hence the parameter of position is good to go in this function as is - doesn't need to be incremented by 1 for the uid for scenario

                // Make the Particle call here
                String json = "{\"cmd\":" + VALUE_SCENARIO + ",\"node_id\":" + nodeId + ",\"SNT_id\":" + position + "}";
                ArrayList<String> message = new ArrayList<>();
                message.add(json);
                try {
                    Log.e(TAG, "JSONCommandScenario " + message.get(0));
                    resultCode = currDevice.callFunction("JSONCommand", message);
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
                message.clear();
            }
        }.start();
        return resultCode;
    }

    public static int JSONConfigScenario(final int brightness, final int cw, final int ww, final int r, final int g, final int b, final String filter) {
        new Thread() {
            @Override
            public void run() {
                int scenarioId = ScenarioFragment.name.size();
                boolean x[] = {false, false, false};

                //construct first part of string input, and store it in arraylist (of size 1)
                String json = "{'x0': '{\"op\":1,\"fl\":0,\"run\":0,\"uid\":\"s" + scenarioId + "\",\"ring1\":" + " '}";
                ArrayList<String> message = new ArrayList<>();
                message.add(json);
                //send in first part of string
                try {
                    Log.e(TAG, "JSONConfigScenario " + message.get(0));
                    resultCode = currDevice.callFunction("JSONConfig", message);
                    x[0] = true;
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
                message.clear();

                if (x[0]) {
                    //construct second part of string input, store in arraylist
                    json = "{'x1': '[" + STATE_ON + "," + cw + "," + ww + "," + r + "," + g + "," + b + "],\"ring2\":[" + STATE_ON + "," + cw + "," + ww + "," + r + "," + g + "," + b + "], '}";
                    message.add(json);
                    //send in second part of string
                    try {
                        Log.e(TAG, "JSONConfigScenario " + message.get(0));
                        resultCode = currDevice.callFunction("JSONConfig", message);
                        x[1] = true;
                    } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                        e.printStackTrace();
                    }
                    message.clear();
                }

                if (x[1]) {
                    //construct last part of string input, store in arraylist
                    //json = "\"ring3\":[" + STATE_ON + "," + cw + "," + ww + "," + r + "," + g + "," + b + "],\"brightness\":" + brightness + ",\"filter\":" + DEFAULT_FILTER_ID + "}";
                    json = "\"ring3\":[" + STATE_ON + "," + cw + "," + ww + "," + r + "," + g + "," + b + "],\"brightness\":" + brightness + ",\"filter\":" + DEFAULT_FILTER_ID + "}";
                    message.add(json);
                    //send in last part of string
                    try {
                        Log.e(TAG, "JSONConfigScenario " + message.get(0));
                        resultCode = currDevice.callFunction("JSONConfig", message);
                    } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                        e.printStackTrace();
                    }
                    message.clear();
                }
            }
        }.start();
        return resultCode;
    }

    public static int JSONConfigAlarm(final int nodeId, final boolean isRepeat, final String weekdays, final int hour, final int minute, final String scenarioName) {
        final int[] doneSending = {0};
        new Thread() {
            @Override
            public void run() {
                boolean x[] = {false, false, false, false};

                //SCHEDULE
                int scheduleId = ScheduleFragment.name.size();
                int repeat = isRepeat ? 1 : 0;

                //construct first part of string input, and store it in arraylist (of size 1)
                String json = "{'x0': '{\"op\":1,\"fl\":0,\"run\":0,\"uid\":\"a" + scheduleId + "\",\"isRepeat\":" + "1" + ", '}";
                ArrayList<String> message = new ArrayList<>();
                message.add(json);
                //send in first part of string
                try {
                    Log.e(TAG, "JSONConfigSchedule " + message.get(0));
                    resultCode = currDevice.callFunction("JSONConfig", message);
                    x[0] = true;
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
                message.clear();

                if (x[0]) {
                    //construct second part of string input, store in arraylist
                    json = "\"weekdays\":" + "0" + ",\"hour\":" + hour + ",\"min\":" + minute + ",\"alarm_id\":" + DEFAULT_ALARM_ID + "}";
                    message.add(json);
                    //send in second part of string
                    try {
                        Log.e(TAG, "JSONConfigSchedule " + message.get(0));
                        resultCode = currDevice.callFunction("JSONConfig", message);
                        x[1] = true;
                        doneSending[0] = 5;
                    } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                        e.printStackTrace();
                    }
                    message.clear();



                    //RULE
                    int rule_schedule_notif_Id = ScheduleFragment.name.size() - 1;
                    int scenarioId = 1;
                    for (int i = 0; i < ScenarioFragment.name.size(); i++) {
                        if (scenarioName == ScenarioFragment.name.get(i)) {
                            scenarioId = i;
                        }
                    }

                    //construct first part of string input, and store it in arraylist (of size 1)
                    String json2 = "{'x0': '{\"op\":1,\"fl\":0,\"run\":0,\"uid\":\"r" + rule_schedule_notif_Id + "\",\"node_id\":" + nodeId + ", '}";
                    ArrayList<String> message2 = new ArrayList<>();
                    message2.add(json2);
                    //send in first part of string
                    try {
                        Log.e(TAG, "JSONConfigRule " + message2.get(0));
                        resultCode = currDevice.callFunction("JSONConfig", message2);
                        x[2] = true;
                    } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                        e.printStackTrace();
                    }
                    message2.clear();

                    if (x[2]) {
                        //construct second part of string input, store in arraylist
                        json2 = "\"SCT_uid\":" + rule_schedule_notif_Id + ",\"SNT_uid\":" + scenarioId + ",\"notif_uid\":" + rule_schedule_notif_Id + "}";
                        message2.add(json2);
                        //send in second part of string
                        try {
                            Log.i(TAG, "JSONConfigRule " + message2.get(0));
                            resultCode = currDevice.callFunction("JSONConfig", message2);
                            x[3] = true;
                        } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                            e.printStackTrace();
                        }
                        message2.clear();
                    }
                }
            }
        }.start();
        return resultCode;
    }

//    public static int JSONConfigRule(final int nodeId, final String scenarioName) {
//        new Thread() {
//            @Override
//            public void run() {
//                int rule_schedule_notif_Id = ScheduleFragment.name.size() + 1;
//                int scenarioId = 1;
//                for (int i = 0; i < ScenarioFragment.name.size(); i++) {
//                    if (scenarioName == ScenarioFragment.name.get(i)) {
//                        scenarioId = i + 1;
//                    }
//                }
//                boolean x[] = {false, false};
//
//                //construct first part of string input, and store it in arraylist (of size 1)
//                String json = "{'x0': '{\"op\":1,\"fl\":0,\"run\":0,\"uid\":\"r" + rule_schedule_notif_Id + "\",\"node_id\":" + nodeId + ", '}";
//                ArrayList<String> message = new ArrayList<>();
//                message.add(json);
//                //send in first part of string
//                try {
//                    Log.e(TAG, "JSONConfigRule" + message.get(0));
//                    resultCode = currDevice.callFunction("JSONConfig", message);
//                    x[0] = true;
//                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
//                    e.printStackTrace();
//                }
//                message.clear();
//
//                if (x[0]) {
//                    //construct second part of string input, store in arraylist
//                    json = "\"SCT_uid\":\"a" + rule_schedule_notif_Id + "\",\"SNT_uid\":\"s" + scenarioId + "\",\"notif_uid\":\"n" + rule_schedule_notif_Id + "\"}";
//                    message.add(json);
//                    //send in second part of string
//                    try {
//                        Log.i(TAG, "JSONConfigRule" + message.get(0));
//                        resultCode = currDevice.callFunction("JSONConfig", message);
//                        x[1] = true;
//                    } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
//                        e.printStackTrace();
//                    }
//                    message.clear();
//                }
//            }
//        }.start();
//        return resultCode;
//    }

    public static int FastCallPowerSwitch(final int nodeId, final boolean state) {
        new Thread() {
            @Override
            public void run() {
                int power = state ? 1 : 0;

                // Make the Particle call here
                String strParam = String.format("%d:%d", nodeId, state ? 1 : 0);
                ArrayList<String> message = new ArrayList<>();
                message.add(strParam);
                try {
                    Log.d(TAG, "FastCallPowerSwitch: " + strParam);
                    resultCode = currDevice.callFunction("PowerSwitch", message);
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return resultCode;
    }
}
