package com.umarbhutta.xlightcompanion.SDK;

/**
 * Created by sunboss on 2016-11-16.
 */

@SuppressWarnings({"UnusedDeclaration"})
public class BLEBridge extends BaseBridge {
    // misc
    private static final String TAG = BLEBridge.class.getSimpleName();
    private boolean m_bPaired = false;

    public BLEBridge() {
        super();
        setName(TAG);
    }

    public boolean PairDevice(final String key) {
        // ToDo: pair with SmartController
        m_bPaired = true;
        return m_bPaired;
    }

    public boolean isPaired() {
        return m_bPaired;
    }

    public boolean connectController(final String key) {
        // ToDo: connect SmartController via BLE
        setConnect(true);
        return isConnected();
    }
}
