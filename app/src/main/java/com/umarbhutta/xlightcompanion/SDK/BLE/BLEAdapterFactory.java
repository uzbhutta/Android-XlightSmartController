package com.umarbhutta.xlightcompanion.SDK.BLE;

import android.bluetooth.BluetoothAdapter;

/**
 * Created by sunboss on 2017-03-21.
 */

public class BLEAdapterFactory {
    private BLEAdapterFactory() {
        // utility class
    }

    public static BLEAdapterWrapper getBluetoothAdapterWrapper() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        return defaultAdapter != null ? new BLEAdapterDelegate(defaultAdapter) : new NullBLEWrapper();
    }
}
