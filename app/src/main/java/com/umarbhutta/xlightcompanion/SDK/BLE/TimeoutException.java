package com.umarbhutta.xlightcompanion.SDK.BLE;

/**
 * Created by sunboss on 2016-12-11.
 */

public class TimeoutException extends BleException {
    public TimeoutException() {
        super(ERROR_CODE_TIMEOUT, "Timeout Exception Occurred! ");
    }
}