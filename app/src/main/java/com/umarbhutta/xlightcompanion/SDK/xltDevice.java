package com.umarbhutta.xlightcompanion.SDK;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import java.util.ArrayList;

/**
 * Created by sunboss on 2016-11-15.
 *
 * Version: 0.1
 *
 * Please report bug at bs.sun@datatellit.com,
 * or send pull request to sunbaoshi1975/Android-XlightSmartController
 *
 */

@SuppressWarnings({"UnusedDeclaration"})
// Smart Device
public class xltDevice {

    //-------------------------------------------------------------------------
    // misc
    //-------------------------------------------------------------------------
    private static final String TAG = xltDevice.class.getSimpleName();
    public static final int DEFAULT_DEVICE_ID = 1;

    // on/off values
    public static final int STATE_OFF = 0;
    public static final int STATE_ON = 1;
    public static final int STATE_TOGGLE = 2;

    // default alarm/filter id
    public static final int DEFAULT_ALARM_ID = 255;
    public static final int DEFAULT_FILTER_ID = 0;

    // Event Names
    public static final String eventDeviceStatus = "xlc-status-device";
    public static final String eventSensorData = "xlc-data-sensor";

    // Broadcast Intent
    public static final String bciDeviceStatus = "ca.xlight.SDK." + eventDeviceStatus;
    public static final String bciSensorData = "ca.xlight.SDK." + eventSensorData;

    // Timeout constants
    private static final int TIMEOUT_CLOUD_LOGIN = 15;

    //-------------------------------------------------------------------------
    // Constants
    //-------------------------------------------------------------------------
    public static final int MAX_RING_NUM = 3;
    public static final int RING_ID_ALL = 0;
    public static final int RING_ID_1 = 1;
    public static final int RING_ID_2 = 2;
    public static final int RING_ID_3 = 3;

    public static final int BR_MIN_VALUE = 1;
    public static final int CT_MIN_VALUE = 2700;
    public static final int CT_MAX_VALUE = 6500;
    public static final int CT_SCOPE = 38;
    public static final int CT_STEP = ((CT_MAX_VALUE-CT_MIN_VALUE)/10);

    // Command values for JSONCommand Interface
    public static final int CMD_SERIAL = 0;
    public static final int CMD_POWER = 1;
    public static final int CMD_COLOR = 2;
    public static final int CMD_BRIGHTNESS = 3;
    public static final int CMD_SCENARIO = 4;
    public static final int CMD_CCT = 5;
    public static final int CMD_QUERY = 6;

    // Device (lamp) type
    public static final int devtypUnknown = 0;
    public static final int devtypCRing3 = 1;
    public static final int devtypCRing2 = 2;
    public static final int devtypCRing1 = 3;
    public static final int devtypWRing3 = 4;
    public static final int devtypWRing2 = 5;
    public static final int devtypWRing1 = 6;
    public static final int devtypMRing3 = 8;
    public static final int devtypMRing2 = 9;
    public static final int devtypMRing1 = 10;
    public static final int devtypDummy = 255;

    public enum BridgeType {
        NONE,
        Cloud,
        BLE,
        LAN
    }

    //-------------------------------------------------------------------------
    // Ring of Smart Fixture
    //-------------------------------------------------------------------------
    public class xltRing {
        // Lights Status
        public int m_State = 0;
        public int m_Brightness = 50;
        public int m_CCT = CT_MIN_VALUE;
        public int m_R = 128;
        public int m_G = 128;
        public int m_B = 0;

        public boolean isSameColor(final xltRing that) {
            if( this.m_State != that.m_State ) return false;
            if( this.m_CCT != that.m_CCT ) return false;
            if( this.m_R != that.m_R ) return false;
            if( this.m_G != that.m_G ) return false;
            if( this.m_B != that.m_B ) return false;
            return true;
        }

        public boolean isSameBright(final xltRing that) {
            if( this.m_State != that.m_State ) return false;
            if( this.m_CCT != that.m_CCT ) return false;
            if( this.m_Brightness != that.m_Brightness ) return false;
            return true;
        }
    }

    //-------------------------------------------------------------------------
    // Sensor Data
    //-------------------------------------------------------------------------
    public class SensorData {
        public float m_RoomTemp = 24;               // Room temperature
        public int m_RoomHumidity = 40;             // Room humidity

        public float m_OutsideTemp = 23;            // Local outside temperature
        public int m_OutsideHumidity = 30;          // Local outside humidity
    }

    //-------------------------------------------------------------------------
    // Variables
    //-------------------------------------------------------------------------
    // Profile
    private static boolean m_bInitialized = false;
    private String m_ControllerID;
    private int m_DevID = DEFAULT_DEVICE_ID;
    private String m_DevName = "Main xlight";
    private int m_DevType = devtypWRing3;

    // Bridge Objects
    private CloudBridge cldBridge;
    private BLEBridge bleBridge;
    private LANBridge lanBridge;

    // Bridge Selection
    private BridgeType m_currentBridge = BridgeType.Cloud;
    private boolean m_autoBridge = true;

    // Rings
    private xltRing[] m_Ring = new xltRing[MAX_RING_NUM];

    // Sensor Data
    public SensorData m_Data;

    // Event Handler List
    private ArrayList<Handler> m_lstEH_DevST = new ArrayList<>();
    private ArrayList<Handler> m_lstEH_SenDT = new ArrayList<>();

    //-------------------------------------------------------------------------
    // Regular Interfaces
    //-------------------------------------------------------------------------
    public xltDevice() {
        super();

        // Create member objects
        m_Data= new SensorData();
        for(int i = 0; i < MAX_RING_NUM; i++) {
            m_Ring[i] = new xltRing();
        }

        cldBridge = new CloudBridge();
        bleBridge = new BLEBridge();
        lanBridge = new LANBridge();
    }

    // Initialize objects
    public void Init(Context context) {
        // Clear event handler lists
        clearDeviceEventHandlerList();
        clearDataEventHandlerList();

        // Ensure we do it only once
        if( !m_bInitialized ) {
            ParticleBridge.init(context);
            // ToDo: get login credential or access token from DMI
            // make sure we logged onto IoT cloud
            ParticleBridge.authenticate();
            m_bInitialized = true;
        }

        // Update parent context
        setParentContext(context);

        // Set me as the parent device
        setParentDevice();
    }

    // Connect to message bridges
    public boolean Connect(final String controllerID) {
        // ToDo: get devID & devName by controllerID from DMI
        m_ControllerID = controllerID;
        setDeviceID(DEFAULT_DEVICE_ID);
        //setDeviceName();

        // Connect to Cloud
        ConnectCloud();

        // Connect to BLE
        ConnectBLE();

        // Connect to LAN
        ConnectLAN();

        return true;
    }

    public boolean ConnectCloud() {
        if( !m_bInitialized ) return false;

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Check ControllerID
                int timeout = TIMEOUT_CLOUD_LOGIN;
                while( !ParticleBridge.isAuthenticated() && timeout-- > 0 ) {
                    SystemClock.sleep(1000);
                }
                if( ParticleBridge.isAuthenticated() ) {
                    if (ParticleBridge.checkDeviceID(m_ControllerID)) {
                        // Connect Cloud Instance
                        cldBridge.connectCloud(m_ControllerID);
                    }
                }
            }
        }).start();
        return true;
    }

    public boolean ConnectBLE() {
        return(bleBridge.connectController("8888"));
    }

    public boolean ConnectLAN() {
        // ToDo: get IP & Port from Cloud or BLE (SmartController told it)
        return(lanBridge.connectController("192.168.0.114", 5555));
    }

    public boolean isSunny(final int DevType) {
        return(DevType >= devtypWRing3 && DevType <= devtypWRing1);
    }

    public boolean isRainbow(final int DevType) {
        return(DevType >= devtypCRing3 && DevType <= devtypCRing1);
    }

    public boolean isMirage(final int DevType) {
        return(DevType >= devtypMRing3 && DevType <= devtypMRing1);
    }

    private int getRingIndex(final int ringID) {
        return((ringID >= RING_ID_1 && ringID <= RING_ID_3) ? ringID - 1 : 0);
    }

    //-------------------------------------------------------------------------
    // Property Access Interfaces
    //-------------------------------------------------------------------------
    public String getControllerID() {
        return m_ControllerID;
    }

    public int getDeviceID() {
        return m_DevID;
    }

    public void setDeviceID(final int devID) {
        m_DevID = devID;
        cldBridge.setNodeID(devID);
        bleBridge.setNodeID(devID);
        lanBridge.setNodeID(devID);
    }

    private void setParentContext(Context context) {
        cldBridge.setParentContext(context);
        bleBridge.setParentContext(context);
        lanBridge.setParentContext(context);
    }

    private void setParentDevice() {
        cldBridge.setParentDevice(this);
        bleBridge.setParentDevice(this);
        lanBridge.setParentDevice(this);
    }

    public int getDeviceType() {
        return m_DevType;
    }

    public void setDeviceType(final int devType) {
        m_DevType = devType;
    }

    public String getDeviceName() {
        return m_DevName;
    }

    public void setDeviceName(final String devName) {
        m_DevName = devName;
    }

    public int getState() {
        return(getState(RING_ID_ALL));
    }

    public int getState(final int ringID) {
        int index = getRingIndex(ringID);
        return(m_Ring[index].m_State);
    }

    public void setState(final int state) {
        setState(RING_ID_ALL, state);
    }

    public void setState(final int ringID, final int state) {
        if( ringID == RING_ID_ALL ) {
            m_Ring[0].m_State = state;
            m_Ring[1].m_State = state;
            m_Ring[2].m_State = state;
        } else {
            int index = getRingIndex(ringID);
            m_Ring[index].m_State = state;
        }
    }

    public int getBrightness() {
        return(getBrightness(RING_ID_ALL));
    }

    public int getBrightness(final int ringID) {
        int index = getRingIndex(ringID);
        return(m_Ring[index].m_Brightness);
    }

    public void setBrightness(final int brightness) {
        setBrightness(RING_ID_ALL, brightness);
    }

    public void setBrightness(final int ringID, final int brightness) {
        if( ringID == RING_ID_ALL ) {
            m_Ring[0].m_Brightness = brightness;
            m_Ring[1].m_Brightness = brightness;
            m_Ring[2].m_Brightness = brightness;
        } else {
            int index = getRingIndex(ringID);
            m_Ring[index].m_Brightness = brightness;
        }
    }

    public int getCCT() {
        return(getCCT(RING_ID_ALL));
    }

    public int getCCT(final int ringID) {
        int index = getRingIndex(ringID);
        return(m_Ring[index].m_CCT);
    }

    public void setCCT(final int cct) {
        setCCT(RING_ID_ALL, cct);
    }

    public void setCCT(final int ringID, final int cct) {
        if( ringID == RING_ID_ALL ) {
            m_Ring[0].m_CCT = cct;
            m_Ring[1].m_CCT = cct;
            m_Ring[2].m_CCT = cct;
        } else {
            int index = getRingIndex(ringID);
            m_Ring[index].m_CCT = cct;
        }
    }

    public int getWhite() {
        return(getWhite(RING_ID_ALL));
    }

    public int getWhite(final int ringID) {
        int index = getRingIndex(ringID);
        return(m_Ring[index].m_CCT % 256);
    }

    public void setWhite(final int white) {
        setWhite(RING_ID_ALL, white);
    }

    public void setWhite(final int ringID, final int white) {
        if( ringID == RING_ID_ALL ) {
            m_Ring[0].m_CCT = white;
            m_Ring[1].m_CCT = white;
            m_Ring[2].m_CCT = white;
        } else {
            int index = getRingIndex(ringID);
            m_Ring[index].m_CCT = white;
        }
    }

    public int getRed() {
        return(getRed(RING_ID_ALL));
    }

    public int getRed(final int ringID) {
        int index = getRingIndex(ringID);
        return(m_Ring[index].m_R);
    }

    public void setRed(final int red) {
        setRed(RING_ID_ALL, red);
    }

    public void setRed(final int ringID, final int red) {
        if( ringID == RING_ID_ALL ) {
            m_Ring[0].m_R = red;
            m_Ring[1].m_R = red;
            m_Ring[2].m_R = red;
        } else {
            int index = getRingIndex(ringID);
            m_Ring[index].m_R = red;
        }
    }

    public int getGreen() {
        return(getGreen(RING_ID_ALL));
    }

    public int getGreen(final int ringID) {
        int index = getRingIndex(ringID);
        return(m_Ring[index].m_G);
    }

    public void setGreen(final int green) {
        setGreen(RING_ID_ALL, green);
    }

    public void setGreen(final int ringID, final int green) {
        if( ringID == RING_ID_ALL ) {
            m_Ring[0].m_G = green;
            m_Ring[1].m_G = green;
            m_Ring[2].m_G = green;
        } else {
            int index = getRingIndex(ringID);
            m_Ring[index].m_G = green;
        }
    }

    public int getBlue() {
        return(getBlue(RING_ID_ALL));
    }

    public int getBlue(final int ringID) {
        int index = getRingIndex(ringID);
        return(m_Ring[index].m_B);
    }

    public void setBlue(final int blue) {
        setBlue(RING_ID_ALL, blue);
    }

    public void setBlue(final int ringID, final int blue) {
        if( ringID == RING_ID_ALL ) {
            m_Ring[0].m_B = blue;
            m_Ring[1].m_B = blue;
            m_Ring[2].m_B = blue;
        } else {
            int index = getRingIndex(ringID);
            m_Ring[index].m_B = blue;
        }
    }

    //-------------------------------------------------------------------------
    // Bridge Selection Interfaces
    //-------------------------------------------------------------------------
    public String getBridgeInfo(final BridgeType bridge) {
        String desc;
        switch(bridge) {
            case Cloud:
                desc = "Cloud bridge " + (isCloudOK() ? "connected" : "not connected");
                break;
            case BLE:
                desc = bleBridge.getName() + " " + (isBLEOK() ? "connected" : "not connected");
                //desc += " Peer: ";
                break;
            case LAN:
                desc = lanBridge.getName() + " " + (isLANOK() ? "connected" : "not connected");
                //desc += " IP: " + " Port: ";
                break;
            default:
                desc = "No bridge available";
        }
        return desc;
    }

    public boolean isCloudOK() {
        return(cldBridge.isConnected());
    }

    public boolean isBLEOK() {
        return(bleBridge.isConnected());
    }

    public boolean isLANOK() {
        return(lanBridge.isConnected());
    }

    public boolean isBridgeOK(final BridgeType bridge) {
        switch(bridge) {
            case Cloud:
                return isCloudOK();
            case BLE:
                return isBLEOK();
            case LAN:
                return isLANOK();
        }
        return false;
    }

    public boolean getAutoBridge() {
        return m_autoBridge;
    }

    public void setAutoBridge(final boolean auto) {
        m_autoBridge = auto;
    }

    public void setBridgePriority(final BridgeType bridge, final int priority) {

    }

    private BridgeType selectBridge() {
        // ToDo: develop an algorithm to select proper bridge
        /// Use current bridge as long as available
        if( isBridgeOK(m_currentBridge) ) return m_currentBridge;

        if( getAutoBridge() ) {
            if (isCloudOK()) {
                m_currentBridge = BridgeType.Cloud;
            } else if (isBLEOK()) {
                m_currentBridge = BridgeType.BLE;
            } else if (isLANOK()) {
                m_currentBridge = BridgeType.LAN;
            } else {
                m_currentBridge = BridgeType.NONE;
            }
        }

        return m_currentBridge;
    }

    // Manually set bridge
    public boolean useBridge(final BridgeType bridge) {
        if( bridge == BridgeType.Cloud && !isCloudOK() ) {
            return false;
        }
        if( bridge == BridgeType.BLE && !isLANOK() ) {
            return false;
        }
        if( bridge == BridgeType.LAN && !isBLEOK() ) {
            return false;
        }

        m_currentBridge = bridge;
        return true;
    }

    public BridgeType getCurrentBridge() {
        return m_currentBridge;
    }

    //-------------------------------------------------------------------------
    // Device Control Interfaces (DCI)
    //-------------------------------------------------------------------------
    // Query Status
    public int QueryStatus() {
        int rc = -1;

        // Select Bridge
        selectBridge();
        if( isBridgeOK(m_currentBridge) ) {
            switch(m_currentBridge) {
                case Cloud:
                    rc = cldBridge.JSONCommandQueryDevice();
                    break;
                case BLE:
                    // ToDo: call BLE API
                    break;
                case LAN:
                    // ToDo: call LAN API
                    break;
            }
        }
        return rc;
    }

    // Turn On / Off
    public int PowerSwitch(final boolean state) {
        int rc = -1;

        // Select Bridge
        selectBridge();
        if( isBridgeOK(m_currentBridge) ) {
            switch(m_currentBridge) {
                case Cloud:
                    rc = cldBridge.FastCallPowerSwitch(state);
                    break;
                case BLE:
                    // ToDo: call BLE API
                    break;
                case LAN:
                    // ToDo: call LAN API
                    break;
            }
        }
        return rc;
    }

    // Change Brightness
    public int ChangeBrightness(final int value) {
        int rc = -1;

        // Select Bridge
        selectBridge();
        if( isBridgeOK(m_currentBridge) ) {
            switch(m_currentBridge) {
                case Cloud:
                    rc = cldBridge.JSONCommandBrightness(value);
                    break;
                case BLE:
                    // ToDo: call BLE API
                    break;
                case LAN:
                    // ToDo: call LAN API
                    break;
            }
        }
        return rc;
    }

    // Change CCT
    public int ChangeCCT(final int value) {
        int rc = -1;

        // Select Bridge
        selectBridge();
        if( isBridgeOK(m_currentBridge) ) {
            switch(m_currentBridge) {
                case Cloud:
                    rc = cldBridge.JSONCommandCCT(value);
                    break;
                case BLE:
                    // ToDo: call BLE API
                    break;
                case LAN:
                    // ToDo: call LAN API
                    break;
            }
        }
        return rc;
    }

    // Change Color (RGBW)
    public int ChangeColor(final int ring, final boolean state, final int br, final int ww, final int r, final int g, final int b) {
        int rc = -1;

        // Select Bridge
        selectBridge();
        if( isBridgeOK(m_currentBridge) ) {
            switch(m_currentBridge) {
                case Cloud:
                    rc = cldBridge.JSONCommandColor(ring, state, br, ww, r, g, b);
                    break;
                case BLE:
                    // ToDo: call BLE API
                    break;
                case LAN:
                    // ToDo: call LAN API
                    break;
            }
        }
        return rc;
    }

    // Change Scenario
    public int ChangeScenario(final int scenario) {
        int rc = -1;

        // Select Bridge
        selectBridge();
        if( isBridgeOK(m_currentBridge) ) {
            switch(m_currentBridge) {
                case Cloud:
                    rc = cldBridge.JSONCommandScenario(scenario);
                    break;
                case BLE:
                    // ToDo: call BLE API
                    break;
                case LAN:
                    // ToDo: call LAN API
                    break;
            }
        }
        return rc;
    }

    //-------------------------------------------------------------------------
    // Event Handler Interfaces
    //-------------------------------------------------------------------------
    public int addDeviceEventHandler(final Handler handler) {
        m_lstEH_DevST.add(handler);
        return m_lstEH_DevST.size();
    }

    public int addDataEventHandler(final Handler handler) {
        m_lstEH_SenDT.add(handler);
        return m_lstEH_SenDT.size();
    }

    public boolean removeDeviceEventHandler(final Handler handler) {
        return m_lstEH_DevST.remove(handler);
    }

    public boolean removeDataEventHandler(final Handler handler) {
        return m_lstEH_SenDT.remove(handler);
    }

    public void clearDeviceEventHandlerList() {
        m_lstEH_DevST.clear();
    }

    public void clearDataEventHandlerList() {
        m_lstEH_SenDT.clear();
    }

    // Send device status message to each handler
    public void sendDeviceStatusMessage(final Bundle data) {
        Handler handler;
        Message msg;
        for (int i = 0; i < m_lstEH_DevST.size(); i++) {
            handler = m_lstEH_DevST.get(i);
            if( handler != null ) {
                msg = handler.obtainMessage();
                if( msg != null ) {
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            }
        }
    }

    // Send sensor data message to each handler
    public void sendSensorDataMessage(final Bundle data) {
        Handler handler;
        Message msg;
        for (int i = 0; i < m_lstEH_SenDT.size(); i++) {
            handler = m_lstEH_SenDT.get(i);
            if( handler != null ) {
                msg = handler.obtainMessage();
                if( msg != null ) {
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            }
        }
    }

    //-------------------------------------------------------------------------
    // Device Manipulate Interfaces (DMI)
    //-------------------------------------------------------------------------
    public int sceAddScenario(final int scenarioId, final int br, final int cw, final int ww, final int r, final int g, final int b, final int filter) {
        int rc = -1;

        // Can only use Cloud Bridge
        if( isCloudOK() ) {
            rc = cldBridge.JSONConfigScenario(scenarioId, br, cw, ww, r, g, b, filter);
        }
        return rc;
    }

    public int sceAddSchedule(final int scheduleId, final boolean isRepeat, final String weekdays, final int hour, final int minute, final int alarmId) {
        int rc = -1;

        // Can only use Cloud Bridge
        if( isCloudOK() ) {
            rc = cldBridge.JSONConfigSchudle(scheduleId, isRepeat, weekdays, hour, minute, alarmId);
        }
        return rc;
    }

    public int sceAddRule(final int ruleId, final int scheduleId, final int scenarioId) {
        int rc = -1;

        // Can only use Cloud Bridge
        if( isCloudOK() ) {
            rc = cldBridge.JSONConfigRule(ruleId, scheduleId, scenarioId);
        }
        return rc;
    }
}
