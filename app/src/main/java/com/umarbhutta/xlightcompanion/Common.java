package com.umarbhutta.xlightcompanion;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;

import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.devicesetup.ParticleDeviceSetupLibrary;

/**
 * Created by Umar Bhutta.
 */
public class Common {
    //Max num constants
    public static final int MAX_SCHEDULES = 6;
    public static final int MAX_DEVICES = 6;

    //Login details
    public static final String EMAIL = "umar.bhutta@hotmail.com";
    public static final String PASSWORD = "ballislife2016";
    public static final String DEVICE_ID = "30003e001547343339383037";

    //Particle vars
    public static ParticleDevice currDevice;
    private static int resultCode;

    //CLOUD FUNCTION CONSTS
    //cmd types
    public static final int VALUE_POWER = 0;
    public static final int VALUE_COLOR = 1;
    public static final int VALUE_BRIGHTNESS = 2;
    //device id
    public static final int DEFAULT_DEVICE_ID = 1;
    //ring values
    public static final int RING_ALL = 0;
    public static final int RING_1 = 1;
    public static final int RING_2 = 2;
    public static final int RING_3 = 3;
    //SUBTYPE_DIMMER_STATUS values
    public static final int STATE_OFF = 0;
    public static final int STATE_ON = 1;


    //constants for testing lists
    public static final String[] deviceNames = {"Living Room", "Bedroom", "Basement Kitchen"};
    public static final String[] scheduleTimes = {"10:30 AM", "12:45 PM", "02:00 PM", "06:45 PM", "08:00 PM", "11:30 PM"};
    public static final String[] scheduleDays = {"Mo Tu We Th Fr", "Every day", "Mo We Th Sa Su", "Tomorrow", "We", "Mo Tu Fr Sa Su"};
    public static final String[] scenarioNames = {"Brunching", "Guests", "Naptime", "Dinner", "Sunset", "Bedtime"};
    public static final String[] scenarioDescriptions = {"A red color at 52% brightness", "A blue-green color at 100% brightness", "An amber color at 50% brightness", "Turn off", "A warm-white color at 100% brightness", "A green color at 52% brightness"};


    //Particle functions
    public static void authenticate(Context context) {
        ParticleDeviceSetupLibrary.init(context, MainActivity.class);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ParticleCloudSDK.getCloud().logIn(EMAIL, PASSWORD);
                    currDevice = ParticleCloudSDK.getCloud().getDevice(DEVICE_ID);
                } catch (ParticleCloudException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public static int CldJsonCommandPower(final int deviceId, final int ring, final int instruction) {
        new Thread() {
            @Override
            public void run() {
                // Make the Particle call here
                String json = "{\"cmd\":" + VALUE_POWER + ",\"device_id\":" + deviceId + ",\"ring\":" + ring + ",\"state\":" + instruction + "}";
                ArrayList<String> message = new ArrayList<>();
                message.add(json);
                try {
                    resultCode = currDevice.callFunction("CldJsonCommand", message);
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
                message.clear();
            }
        }.start();
        return resultCode;
    }

    public static int CldJsonCommandColor(final int deviceId, final int ring, final int cw, final int ww, final int r, final int g, final int b) {
        new Thread() {
            @Override
            public void run() {
                // Make the Particle call here
                String json = "{\"cmd\":" + VALUE_COLOR + ",\"device_id\":" + deviceId + ",\"ring\":" + ring + ",\"color\":[" + cw + "," + ww + "," + r + "," + g + "," + b + "]}";
                ArrayList<String> message = new ArrayList<>();
                message.add(json);
                try {
                    resultCode = currDevice.callFunction("CldJsonCommand", message);
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
                message.clear();
            }
        }.start();
        return resultCode;
    }

    public static int CldJsonCommandBrightness(final int deviceId, final int ring, final int value) {
        new Thread() {
            @Override
            public void run() {
                // Make the Particle call here
                String json = "{\"cmd\":" + VALUE_POWER + ",\"device_id\":" + deviceId + ",\"ring\":" + ring + ",\"state\":" + value + "}";
                ArrayList<String> message = new ArrayList<>();
                message.add(json);
                try {
                    resultCode = currDevice.callFunction("CldJsonCommand", message);
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
                message.clear();
            }
        }.start();
        return resultCode;
    }

    public static int CldJSONConfigScenario(final boolean power, final int brightness, final int cw, final int ww, final int r, final int g, final int b) {
        new Thread() {
            @Override
            public void run() {
                int scenarioNum = ScenarioFragment.name.size() + 1;

                //construct first part of string input, and store it in arraylist (of size 1)
                String json = "{'x0':'{\"op\":1, \"fl\":0, \"run\":0, \"uid\":\"s" + scenarioNum + "\",\"ring1\":[" + power + "," + "0, 0," + r + "," + g + "," + b + "], '}";
                ArrayList<String> cldJSONCommandInput = new ArrayList<>();
                cldJSONCommandInput.add(json);
                //send in first part of string
                try {
                    resultCode = currDevice.callFunction("CldJSONConfig", cldJSONCommandInput);
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
                cldJSONCommandInput.clear();

                //construct second part of string input, store in arraylist
                json = "{'x1': '\"ring2\":[\" + power + \",\" + \"0, 0,\" + r + \",\" + g + \",\" + b + \"], \"ring3\":[\" + power + \",\" + \"0, 0,\" + r + \",\" + g + \",\" + b + \"], '}";
                cldJSONCommandInput.add(json);
                //send in second part of string
                try {
                    resultCode = currDevice.callFunction("CldJSONConfig", cldJSONCommandInput);
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
                cldJSONCommandInput.clear();

                //construct last part of string input, store in arraylist
                json = "\"filter\":0}";
                cldJSONCommandInput.add(json);
                //send in last part of string
                try {
                    resultCode = currDevice.callFunction("CldJSONConfig", cldJSONCommandInput);
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
                cldJSONCommandInput.clear();
            }
        }.start();
        return resultCode;
    }
}
