package com.umarbhutta.xlightcompanion.SDK.BLE;

/**
 * Created by sunboss on 2017-03-21.
 */

public interface DeviceConnector {

    int STATE_NONE = 0;       // we're doing nothing
    int STATE_CONNECTING = 2; // now initiating an outgoing connection
    int STATE_CONNECTED = 3;  // now connected to a remote device

    void connect();

    void disconnect();

    void sendAsciiMessage(CharSequence chars);

    int getState();
}