package com.umarbhutta.xlightcompanion.SDK.BLE;

import android.bluetooth.BluetoothDevice;

import java.util.Set;

/**
 * Created by sunboss on 2017-03-21.
 */

public interface BLEAdapterWrapper {

    Set<BluetoothDevice> getBondedDevices();

    void cancelDiscovery();

    boolean isDiscovering();

    void startDiscovery();

    boolean isEnabled();
}