package com.umarbhutta.xlightcompanion;

import android.content.Context;
import android.widget.Toast;

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

    public static final String KEY_POWER = "power";
    public static final String KEY_COLOR = "color";
    public static final String KEY_BRIGHTNESS = "brightness";

    //MySensors Xlight protocol for message
    //--------------------------------------------------------------------------------------------------------
    //NOTES: when turning light on or off, function from correct Fragment will send function here a 0 or 1 for SUBTYPE_DIMMER_VSTATUS. For on/off, functions here
    //will use the VDIMMER message-type. For changing color, functions here will use CUSTOM message-type and will require corresponding payload of VAR1, 8 bytes as follows

    //    V_VAR1 Structure (little endian): ie 00 00 01 (no spaces) means 8=00, 7=00, 6==01, and so on.
    //    --------------------------
    //    Byte #     data
    //    --------------------------
    //    8       always 00
    //    7       ring # (0 = all rings, 1 = ring1, 2 = ring2... etc)
    //    6       Status (1/on or 0/off)
    //    5       Cold white value
    //    4       Warm white value
    //    3       Red value
    //    2       Green value
    //    1       Blue value

    //node-id, 1 for now since we are dealing with only 1 device for MVP of app
    public static final int DEFAULT_NODE_ID = 1;
    //child-sensor-id (either Custom=23 or Dimmer=4)
    public static final int CHILD_SENSOR_ID_CUSTOM = 23;
    public static final int CHILD_SENSOR_ID_DIMMER = 4;
    //message-type (set=1, req=2, internal=3, stream=4), we only need set.
    //message-type determines type of sub-types we have available to us (i.e. if message-type = 1, subtypes all begin with V (vars) ie V_STATUS or V_DIMMER)
    public static final int MESSAGE_TYPE_SET = 1;
    //auto-ack (yes=1, no=0), we only need yes
    public static final int ACK_YES = 1;
    //sub-type
    //VAR1=24 is only subtype available if child-sensor-id=CUSTOM based on Xlight MySensor protocol.
    //V_STATUS=2 and V_DIMMER are the subtypes available if child-sensor-id=DIMMER
    public static final int SUBTYPE_CUSTOM_VAR1 = 24;
    public static final int SUBTYPE_DIMMER_VSTATUS = 2;
    public static final int SUBTYPE_DIMMER_VDIMMER = 3;
    //ring values
    public static final int RING_ALL = 0;
    public static final int RING_1 = 1;
    public static final int RING_2 = 2;
    public static final int RING_3 = 3;
    //SUBTYPE_DIMMER_STATUS values
    public static final int VSTATUS_OFF = 0;
    public static final int VSTATUS_ON = 1;

    //Login details
    public static final String EMAIL = "umar.bhutta@hotmail.com";
    public static final String PASSWORD = "ballislife2016";
    public static final String DEVICE_ID = "30003e001547343339383037";

    //Particle vars
    public static ParticleDevice currDevice;
    private static int resultCode;

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

    public static int CldPowerSwitch(final Context context, final int deviceId, int ring, final String type, final int command) {
        new Thread() {
            @Override
            public void run() {
                // Make the Particle call here

                if (type == KEY_POWER)
                {
                    String mySensorsMessage = deviceId + ";" + CHILD_SENSOR_ID_DIMMER + ";" + MESSAGE_TYPE_SET + ";" + ACK_YES + ";" + SUBTYPE_DIMMER_VSTATUS + ";" + command;

                    ArrayList<String> cldPowerSwitchInput = new ArrayList<>();
                    cldPowerSwitchInput.add(mySensorsMessage);

                    try {
                        resultCode = currDevice.callFunction("CldPowerSwitch", cldPowerSwitchInput);
                    } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                        e.printStackTrace();
                    }
                    cldPowerSwitchInput.clear();
                }
                else if (type == KEY_COLOR)
                {

                }
                else if (type == KEY_BRIGHTNESS)
                {

                }
                else
                {

                }
            }
        }.start();
        return resultCode;
    }
}
