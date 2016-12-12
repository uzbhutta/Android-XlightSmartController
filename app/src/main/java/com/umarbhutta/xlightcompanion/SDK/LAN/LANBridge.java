package com.umarbhutta.xlightcompanion.SDK.LAN;

import com.umarbhutta.xlightcompanion.SDK.BaseBridge;

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
        setConnect(true);
        return isConnected();
    }
}
