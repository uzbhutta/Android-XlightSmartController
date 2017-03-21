package com.umarbhutta.xlightcompanion.SDK.BLE;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.Set;

/**
 * Created by sunboss on 2017-03-21.
 */

public class BLEAdapterDelegate implements BLEAdapterWrapper {

    private final BluetoothAdapter adapter;

    public BLEAdapterDelegate(BluetoothAdapter adapter) {
        assert adapter != null;
        this.adapter = adapter;
    }

    @Override
    public Set<BluetoothDevice> getBondedDevices() {
        return adapter.getBondedDevices();
    }

    @Override
    public void cancelDiscovery() {
        adapter.cancelDiscovery();
    }

    @Override
    public boolean isDiscovering() {
        return adapter.isDiscovering();
    }

    @Override
    public void startDiscovery() {
        adapter.startDiscovery();
    }

    @Override
    public boolean isEnabled() {
        return adapter.isEnabled();
    }
}