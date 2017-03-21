package com.umarbhutta.xlightcompanion.SDK.BLE;

/**
 * Created by sunboss on 2017-03-21.
 */

public class NullDeviceConnector implements DeviceConnector {
    @Override
    public void connect() {
        // do nothing
    }

    @Override
    public void disconnect() {
        // do nothing
    }

    @Override
    public void sendAsciiMessage(CharSequence chars) {
        // do nothing
    }

    @Override
    public int getState() {
        return STATE_NONE;
    }
}