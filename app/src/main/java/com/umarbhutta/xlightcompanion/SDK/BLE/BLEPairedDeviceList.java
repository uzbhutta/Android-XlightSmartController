package com.umarbhutta.xlightcompanion.SDK.BLE;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by sunboss on 2016-12-10.
 */

@SuppressWarnings({"UnusedDeclaration"})
public class BLEPairedDeviceList {
    // misc
    private static final String TAG = BLEPairedDeviceList.class.getSimpleName();

    public static final int REQUEST_ENABLE_BT = 1010;
    public static final String XLIGHT_BLE_NAME_PREFIX = "Xlight";
    //private static final int XLIGHT_BLE_CLASS = 0x9A050C;   // default value for HC-06 is 0x1F00
    private static final int XLIGHT_BLE_CLASS = 0x1F00;   // default value for HC-06 is 0x1F00

    private static BLEAdapterWrapper mBtAdapter = BLEAdapterFactory.getBluetoothAdapterWrapper();
    private static ArrayList<BluetoothDevice> mPairedDevices = new ArrayList<>();

    private static boolean m_bInitialized = false;
    private static Context m_Context;
    private static boolean m_bSupported = false;

    public static void init(Context context) {
        m_Context = context;
        m_bSupported = m_Context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
        if  (!m_bSupported) {
            Log.e(TAG, "Bluetooth NOT supported!");
            return;
        }

        CheckBluetoothState();
        m_bInitialized = true;
    }

    public static boolean initialized() {
        return m_bInitialized;
    }

    public static boolean IsSupported() {
        return m_bSupported;
    }

    public static boolean IsEnabled() {
        return mBtAdapter.isEnabled();
    }

    public static void CheckBluetoothState() {
        if (IsEnabled()) {
            Log.d(TAG, "Bluetooth is enabled...");
            Set<BluetoothDevice> devices = mBtAdapter.getBondedDevices();
            if (devices != null && !devices.isEmpty()) {
                for (BluetoothDevice device : devices) {
                    if (device.getBluetoothClass().hashCode() == XLIGHT_BLE_CLASS && device.getName().startsWith(XLIGHT_BLE_NAME_PREFIX)) {
                        mPairedDevices.add(device);
                    }
                }
            }
        }
    }

    public static BluetoothDevice SearchDeviceName(final String devName) {
        for (BluetoothDevice device : mPairedDevices) {
            if (device.getName().equalsIgnoreCase(devName)) {
                return device;
            }
        }
        return null;
    }
}
