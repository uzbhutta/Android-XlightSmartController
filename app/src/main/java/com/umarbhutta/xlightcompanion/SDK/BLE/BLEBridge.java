package com.umarbhutta.xlightcompanion.SDK.BLE;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.os.Handler;
import android.os.Message;
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
    private static final boolean D = true;

    private DeviceConnector mDeviceConnector = new NullDeviceConnector();
    private boolean m_bPaired = false;
    private boolean m_bLoggedIn = false;
    private BluetoothDevice m_bleDevice;
    private String m_bleAddress;

    public BLEBridge() {
        super();
        setName(TAG);
    }

    public boolean isPaired() {
        return m_bPaired;
    }

    public boolean connectController() {
        // Connect SmartController via BLE
        if( m_bleDevice != null && m_bleAddress.length() > 0 ) {
            MessageHandler messageHandler = new MessageHandlerImpl(mHandler);
            mDeviceConnector = new BLEDeviceConnector(messageHandler, m_bleAddress);
            mDeviceConnector.connect();
            return true;
        }
        return false;
    }

    public boolean Login(final String key) {
        // ToDo: send login message
        //m_bLoggedIn = true;
        return m_bLoggedIn;
    }

    @Override
    public void setName(final String name) {
        super.setName(name);

        // Retrieve Bluetooth Device by device name
        m_bleDevice = BLEPairedDeviceList.SearchDeviceName(name);
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

    // The Handler that gets information back from the BluetoothService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessageHandler.MSG_CONNECTED:
                    // Device connected
                    Log.i(TAG, "onConnectSuccess");
                    setConnect(true);
                    //onBluetoothStateChanged();
                    break;
                case MessageHandler.MSG_CONNECTING:
                    Log.i(TAG, "onConnecting");
                    setConnect(false);
                    //onBluetoothStateChanged();
                    break;
                case MessageHandler.MSG_NOT_CONNECTED:
                    Log.i(TAG, "onDisconnected");
                    setConnect(false);
                    //onBluetoothStateChanged();
                    break;
                case MessageHandler.MSG_CONNECTION_FAILED:
                    Log.w(TAG, "onConnectFailed");
                    setConnect(false);
                    //onBluetoothStateChanged();
                    break;
                case MessageHandler.MSG_CONNECTION_LOST:
                    Log.w(TAG, "onConnectionLost");
                    setConnect(false);
                    //onBluetoothStateChanged();
                    break;
                case MessageHandler.MSG_BYTES_WRITTEN:
                    String written = new String((byte[]) msg.obj);
                    Log.i(TAG, "written = '" + written + "'");
                    break;
                case MessageHandler.MSG_LINE_READ:
                    String line = (String) msg.obj;
                    if (D) Log.d(TAG, line);
                    break;
            }
        }
    };
}
