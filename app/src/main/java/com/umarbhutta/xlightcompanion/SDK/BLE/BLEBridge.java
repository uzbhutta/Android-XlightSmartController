package com.umarbhutta.xlightcompanion.SDK.BLE;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.util.Log;

import com.umarbhutta.xlightcompanion.SDK.BaseBridge;


import java.lang.reflect.Method;

import static android.bluetooth.BluetoothDevice.BOND_BONDED;

/**
 * Created by sunboss on 2016-11-16.
 */
@SuppressWarnings({"UnusedDeclaration"})
public class BLEBridge extends BaseBridge {
    // misc
    private static final String TAG = BLEBridge.class.getSimpleName();

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_DISCONNECTING = 3;
    public static final int STATE_SCANNING = 5;
    public static final int STATE_SERVICES_DISCOVERED = 6;

    private boolean m_bPaired = false;
    private BluetoothDevice m_bleDevice;
    private BluetoothGatt m_bleGatt;
    private String m_bleAddress;
    private int connectionState = STATE_DISCONNECTED;

    public BLEBridge() {
        super();
        setName(TAG);
    }

    public boolean PairDevice(final String key) {
        // ToDo: pair with SmartController
        // createBond()
        //m_bPaired = true;
        return m_bPaired;
    }

    public boolean isPaired() {
        return m_bPaired;
    }

    public boolean connectController() {
        // Connect SmartController via BLE
        if( m_bleDevice != null ) {
            m_bleDevice.connectGatt(m_parentContext, false, coreGattCallback);
            return true;
        }
        return false;
    }

    public boolean refreshDeviceCache() {
        try {
            final Method refresh = BluetoothGatt.class.getMethod("refresh");
            if (refresh != null) {
                final boolean success = (Boolean) refresh.invoke(m_bleGatt);
                Log.i(TAG, "Refreshing result: " + success);
                return success;
            }
        } catch (Exception e) {
            Log.e(TAG, "An exception occured while refreshing device", e);
        }
        return false;
    }

    public void closeBluetoothGatt() {
        if (m_bleGatt != null) {
            m_bleGatt.disconnect();
        }

        if (m_bleGatt != null) {
            refreshDeviceCache();
        }

        if (m_bleGatt != null) {
            m_bleGatt.close();
            m_bleGatt = null;
        }
    }

    @Override
    public void setName(final String name) {
        super.setName(name);

        // Retrieve Bluetooth Device by device name
        m_bleDevice = BLEAdapter.SearchDeviceName(name);
        if( m_bleDevice != null ) {
            m_bPaired = (m_bleDevice.getBondState() == BOND_BONDED);
            m_bleAddress = m_bleDevice.getAddress();
        } else {
            m_bPaired = false;
            m_bleAddress = "";
        }
    }

    public String getAddress() {
        return m_bleAddress;
    }

    public boolean isInScanning() {
        return connectionState == STATE_SCANNING;
    }

    public boolean isConnectingOrConnected() {
        return connectionState >= STATE_CONNECTING;
    }

    @Override
    public boolean isConnected() {
        return connectionState >= STATE_CONNECTED;
    }

    public boolean isServiceDiscovered() {
        return connectionState == STATE_SERVICES_DISCOVERED;
    }

    /**
     * return
     * {@link #STATE_DISCONNECTED}
     * {@link #STATE_SCANNING}
     * {@link #STATE_CONNECTING}
     * {@link #STATE_CONNECTED}
     * {@link #STATE_SERVICES_DISCOVERED}
     */
    public int getConnectionState() {
        return connectionState;
    }

    private BleGattCallback coreGattCallback = new BleGattCallback() {

        @Override
        public void onConnectFailure(BleException exception) {
            Log.w(TAG, "coreGattCallback：onConnectFailure ");

            m_bleGatt = null;
            setConnect(false);
        }

        @Override
        public void onConnectSuccess(BluetoothGatt gatt, int status) {
            Log.i(TAG, "coreGattCallback：onConnectSuccess ");

            m_bleGatt = gatt;
            setConnect(true);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i(TAG, "coreGattCallback：onConnectionStateChange "
                    + '\n' + "status: " + status
                    + '\n' + "newState: " + newState
                    + '\n' + "thread: " + Thread.currentThread().getId());

            if (newState == BluetoothGatt.STATE_CONNECTED) {
                connectionState = STATE_CONNECTED;
                onConnectSuccess(gatt, status);

            } else if (newState == BluetoothGatt.STATE_DISCONNECTED) {
                connectionState = STATE_DISCONNECTED;
                onConnectFailure(new ConnectException(gatt, status));

            } else if (newState == BluetoothGatt.STATE_CONNECTING) {
                connectionState = STATE_CONNECTING;
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i(TAG, "coreGattCallback：onServicesDiscovered ");

            connectionState = STATE_SERVICES_DISCOVERED;
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.d(TAG, "coreGattCallback：onCharacteristicRead ");
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG, "coreGattCallback：onCharacteristicWrite ");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            Log.i(TAG, "coreGattCallback：onCharacteristicChanged ");
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.d(TAG, "coreGattCallback：onDescriptorRead ");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            Log.d(TAG, "coreGattCallback：onDescriptorWrite ");
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            Log.i(TAG, "coreGattCallback：onReliableWriteCompleted ");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            Log.d(TAG, "coreGattCallback：onReadRemoteRssi ");
        }
    };
}
