package com.umarbhutta.xlightcompanion.SDK.BLE;

import android.bluetooth.BluetoothDevice;

import java.util.Collections;
import java.util.Set;

/**
 * Created by sunboss on 2017-03-21.
 */

public class NullBLEWrapper implements BLEAdapterWrapper {
    @Override
    public Set<BluetoothDevice> getBondedDevices() {
        return Collections.emptySet();
    }

    @Override
    public void cancelDiscovery() {
        // nothing to cancel
    }

    @Override
    public boolean isDiscovering() {
        return false;
    }

    @Override
    public void startDiscovery() {
        // nothing to discover
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
