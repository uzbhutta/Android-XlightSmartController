package com.umarbhutta.xlightcompanion.SDK.BLE;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.umarbhutta.xlightcompanion.SDK.BaseBridge;
import com.umarbhutta.xlightcompanion.SDK.SerialMessage;
import com.umarbhutta.xlightcompanion.SDK.xltDevice;


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
    private SerialMessage mMsg = new SerialMessage();
    private boolean m_bPaired = false;
    private boolean m_bLoggedIn = false;
    private BluetoothDevice m_bleDevice = null;
    private String m_bleAddress;
    private static int resultCode;

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

    public boolean disconnectController() {
        // Disconnect SmartController BLE
        if( mDeviceConnector != null ) {
            mDeviceConnector.disconnect();
            return true;
        }
        return false;
    }

    public boolean Login(final String key) {
        // ToDo: send msg and wait result
        // 0;139;3;1;3;AccessCode\n
        //CharSequence chars;
        //mDeviceConnector.sendAsciiMessage();
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

    public int QueryStatus(final int nodeID) {
        //1;139;1;1;12\n
        String strParam = String.format("%d;%d;1;1;12", nodeID, xltDevice.NODEID_SMARTPHONE);
        return AsynSendMessage(strParam);
    }

    public int PowerSwitch(final int nodeID, final int state) {
        //1;139;1;1;7;0\n
        //1;139;1;1;7;1\n
        //1;139;1;1;7;2\n
        String strParam = String.format("%d;%d;1;1;7;%d", nodeID, xltDevice.NODEID_SMARTPHONE, state);
        return AsynSendMessage(strParam);
    }

    public int ChangeBrightness(final int nodeID, final int value) {
        //1;139;1;1;9;55\n
        String strParam = String.format("%d;%d;1;1;9;%d", nodeID, xltDevice.NODEID_SMARTPHONE, value);
        return AsynSendMessage(strParam);
    }

    public int ChangeCCT(final int nodeID, final int value) {
        //1;139;1;1;11;3800\n
        String strParam = String.format("%d;%d;1;1;11;%d", nodeID, xltDevice.NODEID_SMARTPHONE, value);
        return AsynSendMessage(strParam);
    }

    public int ChangeColor(final int nodeID, final int ring, final boolean state, final int br, final int ww, final int r, final int g, final int b) {
        //1;139;1;1;13;70:0:255\n
        String strParam = String.format("%d;%d;1;1;13;%d;%d;%d;%d;%d", nodeID, xltDevice.NODEID_SMARTPHONE, br, ww, r, g, b);
        return AsynSendMessage(strParam);
    }

    public int ChangeScenario(final int nodeID, final int scenario) {
        //1;139;1;1;15;1\n
        String strParam = String.format("%d;%d;1;1;15;%d", nodeID, xltDevice.NODEID_SMARTPHONE, scenario);
        return AsynSendMessage(strParam);
    }

    public int SetSpecialEffect(final int nodeID, final int filter) {
        //1;139;1;1;17;1\n
        String strParam = String.format("%d;%d;1;1;17;%d", nodeID, xltDevice.NODEID_SMARTPHONE, filter);
        return AsynSendMessage(strParam);
    }

    public int SysSetupWiFi(final String sSSID, final String sPassword, final int nAuth, final int nCipher) {
        String strParam = String.format("0;%d;3;1;6;0:%s", xltDevice.NODEID_SMARTPHONE, sSSID);
        if( sPassword.length() > 0 ) {
            strParam += String.format(":%s", sPassword);
        }
        if( nAuth > 0 ) {
            strParam += String.format(":%d", nAuth);
        }
        if( nCipher > 0 ) {
            strParam += String.format(":%d", nCipher);
        }
        return AsynSendMessage(strParam);
    }

    public int SysQueryCoreID() {
        String strParam = String.format("0;%d;3;1;6;1", xltDevice.NODEID_SMARTPHONE);
        return AsynSendMessage(strParam);
    }

    public int SysConfig(final String sCmd) {
        String strParam = String.format("0;%d;3;1;6;%s", xltDevice.NODEID_SMARTPHONE, sCmd);
        return AsynSendMessage(strParam);
    }

    public int SysControl(final String sCmd) {
        String strParam = String.format("0;%d;3;1;13;%s", xltDevice.NODEID_SMARTPHONE, sCmd);
        return AsynSendMessage(strParam);
    }

    private int AsynSendMessage(final String strMsg) {
        if( !isConnected() ) return -1;
        new Thread() {
            @Override
            public void run() {
                mDeviceConnector.sendAsciiMessage(strMsg);
                resultCode = 1;
            }
        }.start();
        return resultCode;
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
                    m_parentDevice.onBridgeStatusChanged(xltDevice.BridgeType.BLE, xltDevice.BCS_CONNECTED);
                    if( m_parentDevice.m_onConnected != null ) {
                        m_parentDevice.m_onConnected.onConnected(xltDevice.BridgeType.BLE, true);
                    }
                    break;
                case MessageHandler.MSG_CONNECTING:
                    Log.i(TAG, "onConnecting");
                    setConnect(false);
                    //onBluetoothStateChanged();
                    m_parentDevice.onBridgeStatusChanged(xltDevice.BridgeType.BLE, xltDevice.BCS_CONNECTING);
                    break;
                case MessageHandler.MSG_NOT_CONNECTED:
                    Log.i(TAG, "onDisconnected");
                    setConnect(false);
                    //onBluetoothStateChanged();
                    m_parentDevice.onBridgeStatusChanged(xltDevice.BridgeType.BLE, xltDevice.BCS_NOT_CONNECTED);
                    if( m_parentDevice.m_onConnected != null ) {
                        m_parentDevice.m_onConnected.onConnected(xltDevice.BridgeType.BLE, false);
                    }
                    break;
                case MessageHandler.MSG_CONNECTION_FAILED:
                    Log.w(TAG, "onConnectFailed");
                    setConnect(false);
                    //onBluetoothStateChanged();
                    m_parentDevice.onBridgeStatusChanged(xltDevice.BridgeType.BLE, xltDevice.BCS_CONNECTION_FAILED);
                    if( m_parentDevice.m_onConnected != null ) {
                        m_parentDevice.m_onConnected.onConnected(xltDevice.BridgeType.BLE, false);
                    }
                    break;
                case MessageHandler.MSG_CONNECTION_LOST:
                    Log.w(TAG, "onConnectionLost");
                    setConnect(false);
                    //onBluetoothStateChanged();
                    m_parentDevice.onBridgeStatusChanged(xltDevice.BridgeType.BLE, xltDevice.BCS_CONNECTION_LOST);
                    if( m_parentDevice.m_onConnected != null ) {
                        m_parentDevice.m_onConnected.onConnected(xltDevice.BridgeType.BLE, false);
                    }
                    break;
                case MessageHandler.MSG_BYTES_WRITTEN:
                    String written = new String((byte[]) msg.obj);
                    Log.i(TAG, "written = '" + written + "'");
                    break;
                case MessageHandler.MSG_LINE_READ:
                    String line = (String) msg.obj;
                    if (D) Log.d(TAG, line);
                    // Parse message
                    if( mMsg.parseString(line) ) {
                        if( mMsg.m_dest == xltDevice.NODEID_SMARTPHONE && mMsg.m_orig == xltDevice.NODEID_GATEWAY ) {
                            // Notification
                            if( mMsg.m_cmd == SerialMessage.C_PRESENTATION ) {
                                Bundle bdlEventData = new Bundle();
                                switch( mMsg.m_type ) {
                                    case 1:     // Alert
                                        // ToDo: parse alarm message and Send alarm message
                                        //...
                                        if( m_parentDevice.getEnableEventBroadcast() ) {
                                            m_parentContext.sendBroadcast(new Intent(xltDevice.bciAlarm));
                                        }
                                        break;
                                    case 2:     // Sensor Data
                                        if( m_eventParser.ParseSensorDataEvent(mMsg.m_payload, bdlEventData) > 0 ) {
                                            if( m_parentDevice.getEnableEventSendMessage() ) {
                                                m_parentDevice.sendSensorDataMessage(bdlEventData);
                                            }
                                            if( m_parentDevice.getEnableEventBroadcast() ) {
                                                m_parentContext.sendBroadcast(new Intent(xltDevice.bciSensorData));
                                            }
                                        }
                                        break;
                                    case 3:     // Log
                                        // ToDo:
                                        break;
                                    case 4:     // Device Status
                                        int nodeId = m_eventParser.ParseDeviceStatusEvent(mMsg.m_payload, bdlEventData);
                                        if( nodeId > 0 ) {
                                            if( m_parentDevice.getEnableEventSendMessage() ) {
                                                m_parentDevice.sendDeviceStatusMessage(bdlEventData);
                                            }
                                            if( m_parentDevice.getEnableEventBroadcast() ) {
                                                Intent devStatus = new Intent(xltDevice.bciDeviceStatus);
                                                devStatus.putExtra("nd", nodeId);
                                                m_parentContext.sendBroadcast(devStatus);
                                            }
                                        }
                                        break;
                                    case 5:     // Device Config
                                        // ToDo: parse 3 formats and send device config message
                                        //...
                                        if (m_parentDevice.getEnableEventBroadcast()) {
                                            m_parentContext.sendBroadcast(new Intent(xltDevice.bciDeviceConfig));
                                        }
                                        break;
                                }
                            } else if( mMsg.m_cmd == SerialMessage.C_INTERNAL && mMsg.m_ack == 2 ) {
                                // System command ack
                                int result = 1;
                                if( mMsg.m_payload.length() > 0 ) {
                                    if (mMsg.m_payload.charAt(0) == '0') result = 0;
                                }
                                m_parentDevice.onBridgeFunctionAck(result, mMsg.m_type, mMsg.m_payload);
                                // Core ID
                                if( mMsg.m_type == SerialMessage.I_CONFIG && result == 1 ) {
                                    if( mMsg.m_payload.length() > 10 ) {
                                        String[] attributes = mMsg.m_payload.split(":");
                                        if( attributes.length == 3 ) {
                                            if (attributes[1] == "0" || attributes[1] == "1") {
                                                m_parentDevice.onBridgeCoreID(attributes[2]);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;
            }
        }
    };
}
