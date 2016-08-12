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
    public static final int MAX_SCHEDULES = 6;
    public static final int MAX_DEVICES = 6;

    public static final String EMAIL = "umar.bhutta@hotmail.com";
    public static final String PASSWORD = "ballislife2016";
    public static final String DEVICE_ID = "30003e001547343339383037";
    public static ParticleDevice currDevice;
    private static int resultCode;

    public static final String[] deviceNames = {"Living Room", "Bedroom", "Basement Kitchen"};

    public static final String[] scheduleTimes = {"10:30 AM", "12:45 PM", "02:00 PM", "06:45 PM", "08:00 PM", "11:30 PM"};
    public static final String[] scheduleDays = {"Mo Tu We Th Fr", "Every day", "Mo We Th Sa Su", "Tomorrow", "We", "Mo Tu Fr Sa Su"};
    public static final String[] scheduleNames = {"Brunching", "Guests", "Naptime", "Dinner", "Sunset", "Bedtime"};

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

    public static int devSoftSwitch(final Context context, int deviceId, int ring, final String message) {
        new Thread() {
            @Override
            public void run() {
                // Make the Particle call here

                ArrayList<String> devSoftSwitchInput = new ArrayList<>();
                devSoftSwitchInput.add(message);

                try {
                    resultCode = currDevice.callFunction("DevSoftSwitch", devSoftSwitchInput);
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
                devSoftSwitchInput.clear();
            }
        }.start();
        return resultCode;
    }
}
