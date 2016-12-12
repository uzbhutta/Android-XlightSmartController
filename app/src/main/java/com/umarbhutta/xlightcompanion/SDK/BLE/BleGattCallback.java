package com.umarbhutta.xlightcompanion.SDK.BLE;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;

/**
 * Created by sunboss on 2016-12-11.
 */
public abstract class BleGattCallback extends BluetoothGattCallback {

    public abstract void onConnectSuccess(BluetoothGatt gatt, int status);

    @Override
    public abstract void onServicesDiscovered(BluetoothGatt gatt, int status);

    public abstract void onConnectFailure(BleException exception);
}