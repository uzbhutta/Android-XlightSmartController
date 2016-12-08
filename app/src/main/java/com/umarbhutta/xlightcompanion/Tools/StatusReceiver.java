package com.umarbhutta.xlightcompanion.Tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class StatusReceiver extends BroadcastReceiver {
    private final String TAG = DataReceiver.class.getSimpleName();

    public StatusReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.i(TAG, "INTENT RECEIVED by " + TAG);
    }
}
