package ca.xlight.demoapp.SDK;

import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sunboss on 2017-05-29.
 */
@SuppressWarnings({"UnusedDeclaration"})
public class EventParser {
    private xltDevice m_parentDevice = null;
    private String m_Tag = EventParser.class.getSimpleName();

    public void SetParentDevice(final xltDevice parent) {
        m_parentDevice = parent;
    }

    public void SetTag(final String tag) {
        m_Tag = tag;
    }

    public int ParseDeviceStatusEvent(final String dataPayload, Bundle bdlControl) {
        if( m_parentDevice == null) return -1;
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
            Log.e(m_Tag, "Json ParseDeviceStatusEvent error: " + e.getMessage());
            return -1;
        }
        return nodeId;
    }

    public int ParseSensorDataEvent(final String dataPayload, Bundle bdlData) {
        if( m_parentDevice == null) return -1;
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
            Log.e(m_Tag, "Json ParseSensorDataEvent error: " + e.getMessage());
            return -1;
        }
        return 1;
    }
}
