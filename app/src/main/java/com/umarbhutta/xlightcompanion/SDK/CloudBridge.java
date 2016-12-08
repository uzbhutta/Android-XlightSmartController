package com.umarbhutta.xlightcompanion.SDK;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

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
                            JSONCommandQueryDevice();
                        }
                    }, 2000);

                } catch (ParticleCloudException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return true;
    }

    public int JSONCommandPower(final boolean state) {
        new Thread() {
            @Override
            public void run() {
                int power = state ? xltDevice.STATE_ON : xltDevice.STATE_OFF;

                // Make the Particle call here
                String json = "{\"cmd\":" + xltDevice.CMD_POWER + ",\"node_id\":" + getNodeID() + ",\"state\":" + power + "}";
                //String json = "{'cmd':" + VALUE_POWER + ",'node_id':" + nodeId + ",'state':" + power + "}";
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
                String json = "{\"cmd\":" + xltDevice.CMD_BRIGHTNESS + ",\"node_id\":" + getNodeID() + ",\"value\":" + value + "}";
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
                String json = "{\"cmd\":" + xltDevice.CMD_CCT + ",\"node_id\":" + getNodeID() + ",\"value\":" + value + "}";
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

                String json = "{\"cmd\":" + xltDevice.CMD_COLOR + ",\"node_id\":" + getNodeID() + ",\"ring\":[" + ring + "," + power + "," + br + "," + ww + "," + r + "," + g + "," + b + "]}";
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
                String json = "{\"cmd\":" + xltDevice.CMD_SCENARIO + ",\"node_id\":" + getNodeID() + ",\"SNT_id\":" + scenario + "}";
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

    public int JSONCommandQueryDevice() {
        new Thread() {
            @Override
            public void run() {
                // Make the Particle call here
                String json = "{\"cmd\":" + xltDevice.CMD_QUERY + ",\"node_id\":" + getNodeID() + "}";
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
                String json = "{'x0': '{\"op\":1,\"fl\":0,\"run\":0,\"uid\":\"r" + ruleId + "\",\"node_id\":" + getNodeID() + ", '}";
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

    public int FastCallPowerSwitch(final boolean state) {
        new Thread() {
            @Override
            public void run() {
                // Make the Particle call here
                String strParam = String.format("%d:%d", getNodeID(), state ? xltDevice.STATE_ON : xltDevice.STATE_OFF);
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
                            if( event.dataPayload.contains("DHTt") ) {
                                eventName = xltDevice.eventSensorData;
                            } else {
                                eventName = xltDevice.eventDeviceStatus;
                            }

                            if( m_parentDevice != null ) {
                                // Demo option: use handler & sendMessage to inform activities
                                if( m_parentDevice.getEnableEventSendMessage() ) {
                                    InformActivities(eventName, event.dataPayload);
                                }

                                // Demo Option: use broadcast & receivers to publish events
                                if( m_parentDevice.getEnableEventBroadcast() ) {
                                    BroadcastEvent(eventName, event.dataPayload);
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

    // Use handler & sendMessage to inform activities
    private void InformActivities(final String eventName, final String dataPayload) {
        if (m_parentDevice == null) return;
        try {
            JSONObject jObject = new JSONObject(dataPayload);
            if (eventName.equalsIgnoreCase(xltDevice.eventDeviceStatus)) {
                if (jObject.has("nd")) {
                    int nodeId = jObject.getInt("nd");
                    if (nodeId ==  m_parentDevice.getDeviceID()) {
                        Bundle bdlControl = new Bundle();
                        if (jObject.has("State")) {
                            m_parentDevice.setState(jObject.getInt("State"));
                            bdlControl.putInt("State", m_parentDevice.getState());
                        }
                        if (jObject.has("BR")) {
                            m_parentDevice.setBrightness(jObject.getInt("BR"));
                            bdlControl.putInt("BR", m_parentDevice.getBrightness());
                        }
                        if (jObject.has("CCT")) {
                            m_parentDevice.setCCT(jObject.getInt("CCT"));
                            bdlControl.putInt("CCT", m_parentDevice.getCCT());
                        }
                        m_parentDevice.sendDeviceStatusMessage(bdlControl);
                    }
                }
            } else if (eventName.equalsIgnoreCase(xltDevice.eventSensorData)) {
                Bundle bdlData = new Bundle();
                if (jObject.has("DHTt")) {
                    m_parentDevice.m_Data.m_RoomTemp = jObject.getInt("DHTt");
                    bdlData.putInt("DHTt", (int)m_parentDevice.m_Data.m_RoomTemp);
                }
                if (jObject.has("DHTh")) {
                    m_parentDevice.m_Data.m_RoomHumidity = jObject.getInt("DHTh");
                    bdlData.putInt("DHTh", m_parentDevice.m_Data.m_RoomHumidity);
                }
                m_parentDevice.sendSensorDataMessage(bdlData);
            }
        } catch (final JSONException e) {
            Log.e(TAG, "Json parsing error: " + e.getMessage());
        }
    }

    // Demo Option: use broadcast & receivers to publish events
    private void BroadcastEvent(final String eventName, String dataPayload) {
        try {
            JSONObject jObject = new JSONObject(dataPayload);
            if (jObject.has("nd")) {
                int nodeId = jObject.getInt("nd");
                // ToDO: search device
                if (nodeId ==  m_parentDevice.getDeviceID()) {
                    if (jObject.has("State")) {
                        m_parentDevice.setState(jObject.getInt("State"));
                    }
                    if (jObject.has("BR")) {
                        m_parentDevice.setBrightness(jObject.getInt("BR"));
                    }
                    if (jObject.has("CCT")) {
                        m_parentDevice.setCCT(jObject.getInt("CCT"));
                    }
                }
            }
            if (jObject.has("DHTt")) {
                m_parentDevice.m_Data.m_RoomTemp = jObject.getInt("DHTt");
            }
            if (jObject.has("DHTh")) {
                m_parentDevice.m_Data.m_RoomHumidity = jObject.getInt("DHTh");
            }
            //}
        } catch (final JSONException e) {
            Log.e(TAG, "Json parsing error: " + e.getMessage());
        }

        if (eventName.equalsIgnoreCase(xltDevice.eventDeviceStatus)) {
            m_parentContext.sendBroadcast(new Intent(xltDevice.bciDeviceStatus));
        } else if (eventName.equalsIgnoreCase(xltDevice.eventSensorData)) {
            m_parentContext.sendBroadcast(new Intent(xltDevice.bciSensorData));
        }
    }
}
