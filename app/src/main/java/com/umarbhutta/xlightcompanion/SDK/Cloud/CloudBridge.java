package com.umarbhutta.xlightcompanion.SDK.Cloud;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.umarbhutta.xlightcompanion.SDK.BaseBridge;
import com.umarbhutta.xlightcompanion.SDK.xltDevice;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import io.particle.android.sdk.cloud.ParticleCloudException;
import io.particle.android.sdk.cloud.ParticleCloudSDK;
import io.particle.android.sdk.cloud.ParticleDevice;
import io.particle.android.sdk.cloud.ParticleEvent;
import io.particle.android.sdk.cloud.ParticleEventHandler;

/**
 * Created by sunboss on 2016-11-23.
 */
@SuppressWarnings({"UnusedDeclaration"})
public class CloudBridge extends BaseBridge {
    // misc
    private static final String TAG = CloudBridge.class.getSimpleName();

    private ParticleDevice currDevice;
    private static int resultCode;
    private static long subscriptionId = 0;

    public CloudBridge() {
        super();
        setName(TAG);
    }

    public boolean connectCloud(final String devID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    currDevice = ParticleCloudSDK.getCloud().getDevice(devID);
                    SubscribeDeviceEvents();
                    setConnect(true);

                    // Delay 2 seconds, then Query Main Device
                    Handler myHandler = new Handler(Looper.getMainLooper());
                    myHandler.postDelayed(new Runnable() {
                        @Override
                        public void run()
                        {
                            JSONCommandQueryDevice(0);
                        }
                    }, 2000);

                } catch (ParticleCloudException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return true;
    }

    public boolean disconnectCloud() {
        UnsubscribeDeviceEvents();
        setConnect(false);
        return true;
    }

    public int JSONCommandPower(final boolean state) {
        new Thread() {
            @Override
            public void run() {
                int power = state ? xltDevice.STATE_ON : xltDevice.STATE_OFF;

                // Make the Particle call here
                String json = "{\"cmd\":" + xltDevice.CMD_POWER + ",\"nd\":" + getNodeID() + ",\"state\":" + power + "}";
                //String json = "{'cmd':" + VALUE_POWER + ",'nd':" + nodeId + ",'state':" + power + "}";
                ArrayList<String> message = new ArrayList<>();
                message.add(json);
                try {
                    Log.e(TAG, "JSONCommandPower" + message.get(0));
                    resultCode = currDevice.callFunction("JSONCommand", message);
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
                message.clear();
            }
        }.start();
        return resultCode;
    }

    public int JSONCommandBrightness(final int value) {
        new Thread() {
            @Override
            public void run() {
                // Make the Particle call here
                String json = "{\"cmd\":" + xltDevice.CMD_BRIGHTNESS + ",\"nd\":" + getNodeID() + ",\"value\":" + value + "}";
                ArrayList<String> message = new ArrayList<>();
                message.add(json);
                try {
                    Log.e(TAG, "JSONCommandBrightness" + message.get(0));
                    resultCode = currDevice.callFunction("JSONCommand", message);
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
                message.clear();
            }
        }.start();
        return resultCode;
    }

    public int JSONCommandCCT(final int value) {
        new Thread() {
            @Override
            public void run() {
                // Make the Particle call here
                String json = "{\"cmd\":" + xltDevice.CMD_CCT + ",\"nd\":" + getNodeID() + ",\"value\":" + value + "}";
                ArrayList<String> message = new ArrayList<>();
                message.add(json);
                try {
                    Log.d(TAG, "JSONCommandCCT" + message.get(0));
                    resultCode = currDevice.callFunction("JSONCommand", message);
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
                message.clear();
            }
        }.start();
        return resultCode;
    }

    public int JSONCommandColor(final int ring, final boolean state, final int br, final int ww, final int r, final int g, final int b) {
        new Thread() {
            @Override
            public void run() {
                // Make the Particle call here
                int power = state ? 1 : 0;

                String json = "{\"cmd\":" + xltDevice.CMD_COLOR + ",\"nd\":" + getNodeID() + ",\"ring\":[" + ring + "," + power + "," + br + "," + ww + "," + r + "," + g + "," + b + "]}";
                ArrayList<String> message = new ArrayList<>();
                message.add(json);
                try {
                    Log.e(TAG, "JSONCommandColor " + message.get(0));
                    resultCode = currDevice.callFunction("JSONCommand", message);
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
                message.clear();
            }
        }.start();
        return resultCode;
    }


    public int JSONCommandScenario(final int scenario) {
        new Thread() {
            @Override
            public void run() {
                //position corresponds to the spinner in Control. position of 1 corresponds to s1, 2 to s2. The 0th index in the spinner is the "None" item,
                //hence the parameter of position is good to go in this function as is - doesn't need to be incremented by 1 for the uid for scenario

                // Make the Particle call here
                String json = "{\"cmd\":" + xltDevice.CMD_SCENARIO + ",\"nd\":" + getNodeID() + ",\"SNT_id\":" + scenario + "}";
                ArrayList<String> message = new ArrayList<>();
                message.add(json);
                try {
                    Log.e(TAG, "JSONCommandScenario " + message.get(0));
                    resultCode = currDevice.callFunction("JSONCommand", message);
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
                message.clear();
            }
        }.start();
        return resultCode;
    }

    public int JSONCommandSpecialEffect(final int filter) {
        new Thread() {
            @Override
            public void run() {
                // Make the Particle call here
                String json = "{\"cmd\":" + xltDevice.CMD_EFFECT + ",\"nd\":" + getNodeID() + ",\"filter\":" + filter + "}";
                ArrayList<String> message = new ArrayList<>();
                message.add(json);
                try {
                    Log.e(TAG, "JSONCommandSpecialEffect " + message.get(0));
                    resultCode = currDevice.callFunction("JSONCommand", message);
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
                message.clear();
            }
        }.start();
        return resultCode;
    }

    public int JSONCommandQueryDevice(final int nodeID) {
        new Thread() {
            @Override
            public void run() {
                // Make the Particle call here
                String json = "{\"cmd\":" + xltDevice.CMD_QUERY + ",\"nd\":" + (nodeID == 0 ? getNodeID() : nodeID) + "}";
                ArrayList<String> message = new ArrayList<>();
                message.add(json);
                try {
                    Log.i(TAG, "JSONCommandQueryDevice" + message.get(0));
                    resultCode = currDevice.callFunction("JSONCommand", message);
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
                message.clear();
            }
        }.start();
        return resultCode;
    }

    public int JSONConfigScenario(final int scenarioId, final int brightness, final int cw, final int ww, final int r, final int g, final int b, final int filter) {
        new Thread() {
            @Override
            public void run() {
                boolean x[] = {false, false, false};

                //construct first part of string input, and store it in arraylist (of size 1)
                String json = "{'x0': '{\"op\":1,\"fl\":0,\"run\":0,\"uid\":\"s" + scenarioId + "\",\"ring1\":" + " '}";
                ArrayList<String> message = new ArrayList<>();
                message.add(json);
                //send in first part of string
                try {
                    Log.e(TAG, "JSONConfigScenario " + message.get(0));
                    resultCode = currDevice.callFunction("JSONConfig", message);
                    x[0] = true;
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
                message.clear();

                if (x[0]) {
                    //construct second part of string input, store in arraylist
                    json = "{'x1': '[" + xltDevice.STATE_ON + "," + cw + "," + ww + "," + r + "," + g + "," + b + "],\"ring2\":[" + xltDevice.STATE_ON + "," + cw + "," + ww + "," + r + "," + g + "," + b + "], '}";
                    message.add(json);
                    //send in second part of string
                    try {
                        Log.e(TAG, "JSONConfigScenario " + message.get(0));
                        resultCode = currDevice.callFunction("JSONConfig", message);
                        x[1] = true;
                    } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                        e.printStackTrace();
                    }
                    message.clear();
                }

                if (x[1]) {
                    //construct last part of string input, store in arraylist
                    //json = "\"ring3\":[" + xltDevice.STATE_ON + "," + cw + "," + ww + "," + r + "," + g + "," + b + "],\"brightness\":" + brightness + ",\"filter\":" + DEFAULT_FILTER_ID + "}";
                    json = "\"ring3\":[" + xltDevice.STATE_ON + "," + cw + "," + ww + "," + r + "," + g + "," + b + "],\"brightness\":" + brightness + ",\"filter\":" + filter + "}";
                    message.add(json);
                    //send in last part of string
                    try {
                        Log.e(TAG, "JSONConfigScenario " + message.get(0));
                        resultCode = currDevice.callFunction("JSONConfig", message);
                    } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                        e.printStackTrace();
                    }
                    message.clear();
                }
            }
        }.start();
        return resultCode;
    }

    public int JSONConfigSchudle(final int scheduleId, final boolean isRepeat, final String weekdays, final int hour, final int minute, final int alarmId) {
        final int[] doneSending = {0};
        new Thread() {
            @Override
            public void run() {
                boolean x[] = {false, false};

                //SCHEDULE
                int repeat = isRepeat ? 1 : 0;

                //construct first part of string input, and store it in arraylist (of size 1)
                String json = "{'x0': '{\"op\":1,\"fl\":0,\"run\":0,\"uid\":\"a" + scheduleId + "\",\"isRepeat\":" + "1" + ", '}";
                ArrayList<String> message = new ArrayList<>();
                message.add(json);
                //send in first part of string
                try {
                    Log.e(TAG, "JSONConfigSchedule " + message.get(0));
                    resultCode = currDevice.callFunction("JSONConfig", message);
                    x[0] = true;
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
                message.clear();

                if (x[0]) {
                    //construct second part of string input, store in arraylist
                    json = "\"weekdays\":" + "0" + ",\"hour\":" + hour + ",\"min\":" + minute + ",\"alarm_id\":" + alarmId + "}";
                    message.add(json);
                    //send in second part of string
                    try {
                        Log.e(TAG, "JSONConfigSchedule " + message.get(0));
                        resultCode = currDevice.callFunction("JSONConfig", message);
                        x[1] = true;
                        doneSending[0] = 5;
                    } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                        e.printStackTrace();
                    }
                    message.clear();
                }
            }
        }.start();
        return resultCode;
    }

    public int JSONConfigRule(final int ruleId, final int scheduleId, final int scenarioId) {
        new Thread() {
            @Override
            public void run() {
                boolean x[] = {false, false};

                //construct first part of string input, and store it in arraylist (of size 1)
                String json = "{'x0': '{\"op\":1,\"fl\":0,\"run\":0,\"uid\":\"r" + ruleId + "\",\"nd\":" + getNodeID() + ", '}";
                ArrayList<String> message = new ArrayList<>();
                message.add(json);
                //send in first part of string
                try {
                    Log.e(TAG, "JSONConfigRule" + message.get(0));
                    resultCode = currDevice.callFunction("JSONConfig", message);
                    x[0] = true;
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
                message.clear();

                if (x[0]) {
                    //construct second part of string input, store in arraylist
                    json = "\"SCT_uid\":\"a" + scheduleId + "\",\"SNT_uid\":\"s" + scenarioId + "\",\"notif_uid\":\"n" + ruleId + "\"}";
                    message.add(json);
                    //send in second part of string
                    try {
                        Log.i(TAG, "JSONConfigRule" + message.get(0));
                        resultCode = currDevice.callFunction("JSONConfig", message);
                        x[1] = true;
                    } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                        e.printStackTrace();
                    }
                    message.clear();
                }
            }
        }.start();
        return resultCode;
    }

    public int JSONGetDeviceStatus() {
        new Thread() {
            @Override
            public void run() {
                //construct first part of string input, and store it in arraylist (of size 1)
                String json = "{\"op\":0,\"fl\":1,\"run\":0,\"uid\":\"h" + getNodeID() + "}";
                ArrayList<String> message = new ArrayList<>();
                message.add(json);
                //send in first part of string
                try {
                    Log.d(TAG, "JSONGetDeviceStatus " + message.get(0));
                    resultCode = currDevice.callFunction("JSONConfig", message);
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
                message.clear();
            }
        }.start();
        return resultCode;
    }

    public int FastCallPowerSwitch(final int state) {
        new Thread() {
            @Override
            public void run() {
                // Make the Particle call here
                String strParam = String.format("%d:%d", getNodeID(), state);
                ArrayList<String> message = new ArrayList<>();
                message.add(strParam);
                try {
                    Log.d(TAG, "FastCallPowerSwitch: " + strParam);
                    resultCode = currDevice.callFunction("PowerSwitch", message);
                } catch (ParticleCloudException | ParticleDevice.FunctionDoesNotExistException | IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return resultCode;
    }

    // Particle events publishing & subscribing
    public long SubscribeDeviceEvents() {
        new Thread() {
            @Override
            public void run() {
                try {
                    subscriptionId = currDevice.subscribeToEvents(null, new ParticleEventHandler() {
                        public void onEvent(String eventName, ParticleEvent event) {
                            Log.i(TAG, "Received event: " + eventName + " with payload: " + event.dataPayload);
                            // Notes: due to bug of SDK 0.3.4, the eventName is not correct
                            /// We work around by specifying eventName
                            /*
                            if( event.dataPayload.contains("DHTt") || event.dataPayload.contains("ALS") || event.dataPayload.contains("PIR") ) {
                                eventName = xltDevice.eventSensorData;
                            } else {
                                eventName = xltDevice.eventDeviceStatus;
                            }*/

                            if( m_parentDevice != null ) {
                                // Demo option: use handler & sendMessage to inform activities
                                // Parsing Event
                                Bundle bdlEventData = new Bundle();
                                if (eventName.equalsIgnoreCase(xltDevice.eventDeviceStatus)) {
                                    int nodeId = ParseDeviceStatusEvent(event.dataPayload, bdlEventData);
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
                                } else if (eventName.equalsIgnoreCase(xltDevice.eventSensorData)) {
                                    if( ParseSensorDataEvent(event.dataPayload, bdlEventData) > 0 ) {
                                        if( m_parentDevice.getEnableEventSendMessage() ) {
                                            m_parentDevice.sendSensorDataMessage(bdlEventData);
                                        }
                                        if( m_parentDevice.getEnableEventBroadcast() ) {
                                            m_parentContext.sendBroadcast(new Intent(xltDevice.bciSensorData));
                                        }
                                    }
                                } else if (eventName.equalsIgnoreCase(xltDevice.eventAlarm)) {
                                    // ToDo: parse alarm message and Send alarm message
                                    //...
                                    if( m_parentDevice.getEnableEventBroadcast() ) {
                                        m_parentContext.sendBroadcast(new Intent(xltDevice.bciAlarm));
                                    }
                                } else if (eventName.equalsIgnoreCase(xltDevice.eventDeviceConfig)) {
                                    // ToDo: parse 3 formats and send device config message
                                    //...
                                    if (m_parentDevice.getEnableEventBroadcast()) {
                                        m_parentContext.sendBroadcast(new Intent(xltDevice.bciDeviceConfig));
                                    }
                                }
                            }
                        }

                        public void onEventError(Exception e) {
                            Log.e(TAG, "Event error: ", e);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return subscriptionId;
    }

    public void UnsubscribeDeviceEvents() {
        new Thread() {
            @Override
            public void run() {
                if( subscriptionId > 0 ) {
                    try {
                        currDevice.unsubscribeFromEvents(subscriptionId);
                    } catch (ParticleCloudException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    private int ParseDeviceStatusEvent(final String dataPayload, Bundle bdlControl) {
        int nodeId = -1;
        try {
            JSONObject jObject = new JSONObject(dataPayload);
            if (jObject.has("nd")) {
                nodeId = jObject.getInt("nd");
                int ringId = xltDevice.RING_ID_ALL;
                if (nodeId == m_parentDevice.getDeviceID() || m_parentDevice.findNodeFromDeviceList(nodeId) >= 0) {
                    bdlControl.putInt("nd", nodeId);
                    if (jObject.has("up")) {
                        m_parentDevice.setNodeAlive(nodeId, jObject.getInt("up") > 0);
                        bdlControl.putInt("up", jObject.getInt("up"));
                    } else {
                        m_parentDevice.setNodeAlive(nodeId, true);
                    }
                    if (jObject.has("tp")) {
                        m_parentDevice.setDeviceType(nodeId, jObject.getInt("tp"));
                        bdlControl.putInt("type", jObject.getInt("tp"));
                    }
                    if (jObject.has("filter")) {
                        m_parentDevice.setFilter(nodeId, jObject.getInt("filter"));
                        bdlControl.putInt("filter", jObject.getInt("filter"));
                    }
                    if (jObject.has("Ring")) {
                        ringId = jObject.getInt("Ring");
                    }
                    bdlControl.putInt("Ring", ringId);
                    if (jObject.has("State")) {
                        m_parentDevice.setState(nodeId, jObject.getInt("State"));
                        bdlControl.putInt("State", jObject.getInt("State"));
                    }
                    if (jObject.has("BR")) {
                        m_parentDevice.setBrightness(nodeId, jObject.getInt("BR"));
                        bdlControl.putInt("BR", jObject.getInt("BR"));
                    }
                    if (jObject.has("CCT")) {
                        m_parentDevice.setCCT(nodeId, jObject.getInt("CCT"));
                        bdlControl.putInt("CCT", jObject.getInt("CCT"));
                    }
                    if (jObject.has("W")) {
                        m_parentDevice.setWhite(nodeId, ringId, jObject.getInt("W"));
                        bdlControl.putInt("W", jObject.getInt("W"));
                    }
                    if (jObject.has("R")) {
                        m_parentDevice.setRed(nodeId, ringId, jObject.getInt("R"));
                        bdlControl.putInt("R", jObject.getInt("R"));
                    }
                    if (jObject.has("G")) {
                        m_parentDevice.setGreen(nodeId, ringId, jObject.getInt("G"));
                        bdlControl.putInt("G", jObject.getInt("G"));
                    }
                    if (jObject.has("B")) {
                        m_parentDevice.setBlue(nodeId, ringId, jObject.getInt("B"));
                        bdlControl.putInt("B", jObject.getInt("B"));
                    }
                }
            }
        } catch (final JSONException e) {
            Log.e(TAG, "Json ParseDeviceStatusEvent error: " + e.getMessage());
            return -1;
        }
        return nodeId;
    }

    private int ParseSensorDataEvent(final String dataPayload, Bundle bdlData) {
        try {
            JSONObject jObject = new JSONObject(dataPayload);
            if (jObject.has("DHTt")) {
                m_parentDevice.m_Data.m_RoomTemp = jObject.getInt("DHTt");
                bdlData.putInt("DHTt", (int)m_parentDevice.m_Data.m_RoomTemp);
            }
            if (jObject.has("DHTh")) {
                m_parentDevice.m_Data.m_RoomHumidity = jObject.getInt("DHTh");
                bdlData.putInt("DHTh", m_parentDevice.m_Data.m_RoomHumidity);
            }
            if (jObject.has("ALS")) {
                m_parentDevice.m_Data.m_RoomBrightness = jObject.getInt("ALS");
                bdlData.putInt("ALS", m_parentDevice.m_Data.m_RoomBrightness);
            }
            if (jObject.has("MIC")) {
                m_parentDevice.m_Data.m_Mic = jObject.getInt("MIC");
                bdlData.putInt("MIC", m_parentDevice.m_Data.m_Mic);
            }
            if (jObject.has("PIR")) {
                m_parentDevice.m_Data.m_PIR = jObject.getInt("PIR");
                bdlData.putInt("PIR", m_parentDevice.m_Data.m_PIR);
            }
            if (jObject.has("GAS")) {
                m_parentDevice.m_Data.m_GAS = jObject.getInt("GAS");
                bdlData.putInt("GAS", m_parentDevice.m_Data.m_GAS);
            }
            if (jObject.has("SMK")) {
                m_parentDevice.m_Data.m_Smoke = jObject.getInt("SMK");
                bdlData.putInt("SMK", m_parentDevice.m_Data.m_Smoke);
            }
            if (jObject.has("PM25")) {
                m_parentDevice.m_Data.m_PM25 = jObject.getInt("PM25");
                bdlData.putInt("PM25", m_parentDevice.m_Data.m_PM25);
            }
            if (jObject.has("NOS")) {
                m_parentDevice.m_Data.m_Noise = jObject.getInt("NOS");
                bdlData.putInt("NOS", m_parentDevice.m_Data.m_Noise);
            }
        } catch (final JSONException e) {
            Log.e(TAG, "Json ParseSensorDataEvent error: " + e.getMessage());
            return -1;
        }
        return 1;
    }
}
