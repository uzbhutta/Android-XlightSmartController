package com.umarbhutta.xlightcompanion.SDK.LAN;

import com.umarbhutta.xlightcompanion.SDK.BaseBridge;
import com.umarbhutta.xlightcompanion.SDK.xltDevice;

/**
 * Created by sunboss on 2016-11-16.
 */

@SuppressWarnings({"UnusedDeclaration"})
public class LANBridge extends BaseBridge {
    // misc
    private static final String TAG = LANBridge.class.getSimpleName();

    public LANBridge() {
        super();
        setName(TAG);
    }

    public boolean connectController(final String address, final int port) {
        // ToDo: connect to SmartController HTTP
        //setConnect(true);
        //m_parentDevice.onBridgeStatusChanged(xltDevice.BridgeType.LAN, xltDevice.BCS_CONNECTED);
        //if( m_parentDevice.m_onConnected != null ) {
        //    m_parentDevice.m_onConnected.onConnected(xltDevice.BridgeType.LAN, true);
        //}
        return isConnected();
    }

    public boolean disconnectController() {
        // ToDo: disconnect from SmartController
        setConnect(false);
        m_parentDevice.onBridgeStatusChanged(xltDevice.BridgeType.LAN, xltDevice.BCS_NOT_CONNECTED);
        if( m_parentDevice.m_onConnected != null ) {
            m_parentDevice.m_onConnected.onConnected(xltDevice.BridgeType.LAN, false);
        }
        return isConnected();
    }
}
