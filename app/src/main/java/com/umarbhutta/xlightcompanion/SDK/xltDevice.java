package com.umarbhutta.xlightcompanion.SDK;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.umarbhutta.xlightcompanion.SDK.BLE.BLEPairedDeviceList;
import com.umarbhutta.xlightcompanion.SDK.BLE.BLEBridge;
import com.umarbhutta.xlightcompanion.SDK.Cloud.CloudBridge;
import com.umarbhutta.xlightcompanion.SDK.Cloud.ParticleAdapter;
import com.umarbhutta.xlightcompanion.SDK.LAN.LANBridge;

import java.util.ArrayList;

import static com.umarbhutta.xlightcompanion.SDK.BLE.BLEPairedDeviceList.XLIGHT_BLE_NAME_PREFIX;

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
    public static final String DEFAULT_DEVICE_NAME = "";
    public static final String DEFAULT_DEVICE_BLENAME = XLIGHT_BLE_NAME_PREFIX + DEFAULT_DEVICE_NAME;


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

    public static final int DEFAULT_DEVICE_TYPE = devtypWRing3;

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
        public int m_R = 0;
        public int m_G = 0;
        public int m_B = 0;
        public int m_String1 = 50;
        public int m_String2 = 50;
        public int m_String3 = 50;

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
        public int m_RoomBrightness = 0;            // ALS value

        public float m_OutsideTemp = 23;            // Local outside temperature
        public int m_OutsideHumidity = 30;          // Local outside humidity
    }

    //-------------------------------------------------------------------------
    // Device / Node under the Controller
    //-------------------------------------------------------------------------
    public class xltNodeInfo {
        public int m_ID = 0;
        public int m_Type;
        public String m_Name;
        // Rings
        public xltRing[] m_Ring = new xltRing[MAX_RING_NUM];
    }

    //-------------------------------------------------------------------------
    // Variables
    //-------------------------------------------------------------------------
    // Profile
    private static boolean m_bInitialized = false;
    private String m_ControllerID;
    private int m_DevID = DEFAULT_DEVICE_ID;

    // Bridge Objects
    private CloudBridge cldBridge;
    private BLEBridge bleBridge;
    private LANBridge lanBridge;

    // Bridge Selection
    private BridgeType m_currentBridge = BridgeType.Cloud;
    private boolean m_autoBridge = true;

    // Device/Node List
    private ArrayList<xltNodeInfo> m_lstNodes = new ArrayList<>();
    xltNodeInfo m_currentNode = null;

    // Sensor Data
    public SensorData m_Data;

    // Event Notification
    private boolean m_enableEventBroadcast = false;
    private boolean m_enableEventSendMessage = true;

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
        cldBridge = new CloudBridge();
        bleBridge = new BLEBridge();
        lanBridge = new LANBridge();
    }

    // Initialize objects
    public void Init(Context context) {
        // Clear event handler lists
        clearDeviceEventHandlerList();
        clearDataEventHandlerList();
        clearDeviceList();

        // Ensure we do it only once
        if( !m_bInitialized ) {
            // Init BLE Adapter
            if( !BLEPairedDeviceList.initialized() ) {
                BLEPairedDeviceList.init(context);
            }

            // Init Particle Adapter
            if( !ParticleAdapter.initialized() ) {
                ParticleAdapter.init(context);
                // ToDo: get login credential or access token from DMI
                // make sure we logged onto IoT cloud
                ParticleAdapter.authenticate();
            }

            m_bInitialized = true;
        }

        // Update parent context
        setParentContext(context);

        // Set me as the parent device
        setParentDevice();

        // Set priority for each bridge - the bigger, the higher
        cldBridge.setPriority(6);
        bleBridge.setPriority(9);
        lanBridge.setPriority(3);
    }

    // Connect to message bridges
    public boolean Connect(final String controllerID) {
        // ToDo: get device (node) list: devID, devType, devName & devBLEName by controllerID from DMI
        // If DMI cannot communicate to the Cloud, return the most recent values in cookie,
        // If there is no cookie, return default values.
        m_ControllerID = controllerID;
        // ToDo: Add device/node list
        if( m_lstNodes.size() <= 0 ) {
            // Easy for testing
            addNodeToDeviceList(DEFAULT_DEVICE_ID, DEFAULT_DEVICE_TYPE, DEFAULT_DEVICE_NAME);
        }
        //...
        //bleBridge.setName(devBLEName);
        bleBridge.setName(DEFAULT_DEVICE_BLENAME);

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
                while( !ParticleAdapter.isAuthenticated() && timeout-- > 0 ) {
                    SystemClock.sleep(1000);
                }
                if( ParticleAdapter.isAuthenticated() ) {
                    if (ParticleAdapter.checkDeviceID(m_ControllerID)) {
                        // Connect Cloud Instance
                        cldBridge.connectCloud(m_ControllerID);
                    }
                }
            }
        }).start();
        return true;
    }

    public boolean ConnectBLE() {
        return(bleBridge.connectController());
    }

    public boolean ConnectLAN() {
        // ToDo: get IP & Port from Cloud or BLE (SmartController told it)
        return(lanBridge.connectController("192.168.0.114", 5555));
    }

    public boolean isSunny() {
        return(m_currentNode != null ? isSunny(m_currentNode.m_Type) : false);
    }

    public boolean isRainbow() {
        return(m_currentNode != null ? isRainbow(m_currentNode.m_Type) : false);
    }

    public boolean isMirage() {
        return(m_currentNode != null ? isMirage(m_currentNode.m_Type) : false);
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

    public int addNodeToDeviceList(final int devID, final int devType, final String devName) {
        xltNodeInfo lv_node = new xltNodeInfo();
        lv_node.m_ID = devID;
        lv_node.m_Type = devType;
        lv_node.m_Name = devName;
        for(int i = 0; i < MAX_RING_NUM; i++) {
            lv_node.m_Ring[i] = new xltRing();
        }
        m_lstNodes.add(lv_node);
        if( m_currentNode == null ) {
            m_currentNode = lv_node;
            m_DevID = devID;
        }
        return m_lstNodes.size();
    }

    public int findNodeFromDeviceList(final int devID) {
        for (xltNodeInfo lv_node : m_lstNodes) {
            if( lv_node.m_ID == devID ) {
                return m_lstNodes.indexOf(lv_node);
            }
        }
        return -1;
    }

    public boolean removeNodeFromDeviceList(final int devID) {
        int lv_index = findNodeFromDeviceList(devID);
        if( lv_index >= 0 ) {
            if( m_currentNode != null ) {
                if( m_currentNode.m_ID == devID ) m_currentNode = null;
            }
            m_lstNodes.remove(lv_index);
            return true;
        }
        return false;
    }

    public void clearDeviceList() {
        m_currentNode = null;
        m_lstNodes.clear();
    }

    public int getDeviceID() {
        return m_DevID;
    }

    // Change current device / node
    public void setDeviceID(final int devID) {
        m_DevID = devID;
        int lv_index = findNodeFromDeviceList(devID);
        if( lv_index >= 0 ) {
            m_currentNode = m_lstNodes.get(lv_index);
        }
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
        return(m_currentNode != null ? m_currentNode.m_Type : devtypDummy);
    }

    public String getDeviceName() {
        return(m_currentNode != null ? m_currentNode.m_Name : "");
    }

    public int getState() {
        return(getState(m_DevID));
    }

    public int getState(final int nodeID) {
        return(getState(nodeID, RING_ID_ALL));
    }

    public int getState(final int nodeID, final int ringID) {
        int index = getRingIndex(ringID);
        int lv_dev = findNodeFromDeviceList(nodeID);
        if( lv_dev >= 0 ) {
            return m_lstNodes.get(lv_dev).m_Ring[index].m_State;
        }
        return(-1);
    }

    public void setState(final int state) {
        setState(m_DevID, state);
    }

    public void setState(final int nodeID, final int state) {
        setState(nodeID, RING_ID_ALL, state);
    }

    public void setState(final int nodeID, final int ringID, final int state) {
        int lv_dev = findNodeFromDeviceList(nodeID);
        if( lv_dev >= 0 ) {
            if (ringID == RING_ID_ALL) {
                m_lstNodes.get(lv_dev).m_Ring[0].m_State = state;
                m_lstNodes.get(lv_dev).m_Ring[1].m_State = state;
                m_lstNodes.get(lv_dev).m_Ring[2].m_State = state;
            } else {
                int index = getRingIndex(ringID);
                m_lstNodes.get(lv_dev).m_Ring[index].m_State = state;
            }
        }
    }

    public int getBrightness() {
        return(getBrightness(m_DevID));
    }

    public int getBrightness(final int nodeID) {
        return(getBrightness(nodeID, RING_ID_ALL));
    }

    public int getBrightness(final int nodeID, final int ringID) {
        int index = getRingIndex(ringID);
        int lv_dev = findNodeFromDeviceList(nodeID);
        if( lv_dev >= 0 ) {
            return m_lstNodes.get(lv_dev).m_Ring[index].m_Brightness;
        }
        return(-1);
    }

    public void setBrightness(final int brightness) {
        setBrightness(m_DevID, brightness);
    }

    public void setBrightness(final int nodeID, final int brightness) {
        setBrightness(nodeID, RING_ID_ALL, brightness);
    }

    public void setBrightness(final int nodeID, final int ringID, final int brightness) {
        int lv_dev = findNodeFromDeviceList(nodeID);
        if( lv_dev >= 0 ) {
            if (ringID == RING_ID_ALL) {
                m_lstNodes.get(lv_dev).m_Ring[0].m_Brightness = brightness;
                m_lstNodes.get(lv_dev).m_Ring[1].m_Brightness = brightness;
                m_lstNodes.get(lv_dev).m_Ring[2].m_Brightness = brightness;
            } else {
                int index = getRingIndex(ringID);
                m_lstNodes.get(lv_dev).m_Ring[index].m_Brightness = brightness;
            }
        }
    }

    public int getCCT() {
        return(getCCT(m_DevID));
    }

    public int getCCT(final int nodeID) {
        return(getCCT(nodeID, RING_ID_ALL));
    }

    public int getCCT(final int nodeID, final int ringID) {
        int index = getRingIndex(ringID);
        int lv_dev = findNodeFromDeviceList(nodeID);
        if( lv_dev >= 0 ) {
            return m_lstNodes.get(lv_dev).m_Ring[index].m_CCT;
        }
        return(-1);
    }

    public void setCCT(final int nodeID, final int cct) {
        setCCT(nodeID, RING_ID_ALL, cct);
    }

    public void setCCT(final int cct) {
        setCCT(m_DevID, cct);
    }

    public void setCCT(final int nodeID, final int ringID, final int cct) {
        int lv_dev = findNodeFromDeviceList(nodeID);
        if( lv_dev >= 0 ) {
            if (ringID == RING_ID_ALL) {
                m_lstNodes.get(lv_dev).m_Ring[0].m_CCT = cct;
                m_lstNodes.get(lv_dev).m_Ring[1].m_CCT = cct;
                m_lstNodes.get(lv_dev).m_Ring[2].m_CCT = cct;
            } else {
                int index = getRingIndex(ringID);
                m_lstNodes.get(lv_dev).m_Ring[index].m_CCT = cct;
            }
        }
    }

    public int getWhite() {
        return(getWhite(m_DevID));
    }

    public int getWhite(final int nodeID) {
        return(getWhite(nodeID, RING_ID_ALL));
    }

    public int getWhite(final int nodeID, final int ringID) {
        int index = getRingIndex(ringID);
        int lv_dev = findNodeFromDeviceList(nodeID);
        if( lv_dev >= 0 ) {
            return(m_lstNodes.get(lv_dev).m_Ring[index].m_CCT % 256);
        }
        return(-1);
    }

    public void setWhite(final int white) {
        setWhite(m_DevID, white);
    }

    public void setWhite(final int nodeID, final int white) {
        setWhite(nodeID, RING_ID_ALL, white);
    }

    public void setWhite(final int nodeID, final int ringID, final int white) {
        int lv_dev = findNodeFromDeviceList(nodeID);
        if( lv_dev >= 0 ) {
            if (ringID == RING_ID_ALL) {
                m_lstNodes.get(lv_dev).m_Ring[0].m_CCT = white;
                m_lstNodes.get(lv_dev).m_Ring[1].m_CCT = white;
                m_lstNodes.get(lv_dev).m_Ring[2].m_CCT = white;
            } else {
                int index = getRingIndex(ringID);
                m_lstNodes.get(lv_dev).m_Ring[index].m_CCT = white;
            }
        }
    }

    public int getRed() {
        return(getRed(m_DevID));
    }

    public int getRed(final int nodeID) {
        return(getRed(nodeID, RING_ID_ALL));
    }

    public int getRed(final int nodeID, final int ringID) {
        int index = getRingIndex(ringID);
        int lv_dev = findNodeFromDeviceList(nodeID);
        if( lv_dev >= 0 ) {
            return(m_lstNodes.get(lv_dev).m_Ring[index].m_R);
        }
        return(-1);
    }

    public void setRed(final int red) {
        setRed(m_DevID, red);
    }

    public void setRed(final int nodeID, final int red) {
        setRed(nodeID, RING_ID_ALL, red);
    }

    public void setRed(final int nodeID, final int ringID, final int red) {
        int lv_dev = findNodeFromDeviceList(nodeID);
        if( lv_dev >= 0 ) {
            if (ringID == RING_ID_ALL) {
                m_lstNodes.get(lv_dev).m_Ring[0].m_R = red;
                m_lstNodes.get(lv_dev).m_Ring[1].m_R = red;
                m_lstNodes.get(lv_dev).m_Ring[2].m_R = red;
            } else {
                int index = getRingIndex(ringID);
                m_lstNodes.get(lv_dev).m_Ring[index].m_R = red;
            }
        }
    }

    public int getGreen() {
        return(getGreen(m_DevID));
    }

    public int getGreen(final int nodeID) {
        return(getGreen(nodeID, RING_ID_ALL));
    }

    public int getGreen(final int nodeID, final int ringID) {
        int index = getRingIndex(ringID);
        int lv_dev = findNodeFromDeviceList(nodeID);
        if( lv_dev >= 0 ) {
            return(m_lstNodes.get(lv_dev).m_Ring[index].m_G);
        }
        return(-1);
    }

    public void setGreen(final int green) {
        setGreen(m_DevID, green);
    }

    public void setGreen(final int nodeID, final int green) {
        setGreen(nodeID, RING_ID_ALL, green);
    }

    public void setGreen(final int nodeID, final int ringID, final int green) {
        int lv_dev = findNodeFromDeviceList(nodeID);
        if( lv_dev >= 0 ) {
            if (ringID == RING_ID_ALL) {
                m_lstNodes.get(lv_dev).m_Ring[0].m_G = green;
                m_lstNodes.get(lv_dev).m_Ring[1].m_G = green;
                m_lstNodes.get(lv_dev).m_Ring[2].m_G = green;
            } else {
                int index = getRingIndex(ringID);
                m_lstNodes.get(lv_dev).m_Ring[index].m_G = green;
            }
        }
    }

    public int getBlue() {
        return(getBlue(m_DevID));
    }

    public int getBlue(final int nodeID) {
        return(getBlue(nodeID, RING_ID_ALL));
    }

    public int getBlue(final int nodeID, final int ringID) {
        int index = getRingIndex(ringID);
        int lv_dev = findNodeFromDeviceList(nodeID);
        if( lv_dev >= 0 ) {
            return(m_lstNodes.get(lv_dev).m_Ring[index].m_B);
        }
        return(-1);
    }

    public void setBlue(final int blue) {
        setBlue(m_DevID, blue);
    }

    public void setBlue(final int nodeID, final int blue) {
        setBlue(nodeID, RING_ID_ALL, blue);
    }

    public void setBlue(final int nodeID, final int ringID, final int blue) {
        int lv_dev = findNodeFromDeviceList(nodeID);
        if( lv_dev >= 0 ) {
            if (ringID == RING_ID_ALL) {
                m_lstNodes.get(lv_dev).m_Ring[0].m_B = blue;
                m_lstNodes.get(lv_dev).m_Ring[1].m_B = blue;
                m_lstNodes.get(lv_dev).m_Ring[2].m_B = blue;
            } else {
                int index = getRingIndex(ringID);
                m_lstNodes.get(lv_dev).m_Ring[index].m_B = blue;
            }
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
        /// Use current bridge as long as available
        if( isBridgeOK(m_currentBridge) ) return m_currentBridge;

        if( getAutoBridge() ) {
            int maxPri = 0;
            if (isCloudOK() && cldBridge.getPriority() > maxPri) {
                m_currentBridge = BridgeType.Cloud;
                maxPri = cldBridge.getPriority();
            }

            if (isBLEOK() && bleBridge.getPriority() > maxPri) {
                m_currentBridge = BridgeType.BLE;
                maxPri = bleBridge.getPriority();
            }

            if (isLANOK() && lanBridge.getPriority() > maxPri) {
                m_currentBridge = BridgeType.LAN;
                maxPri = lanBridge.getPriority();
            }

            if (maxPri == 0) {
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
    public int PowerSwitch(final int state) {
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
    public boolean getEnableEventBroadcast() {
        return m_enableEventBroadcast;
    }

    public boolean getEnableEventSendMessage() {
        return m_enableEventSendMessage;
    }

    public void setEnableEventBroadcast(final boolean flag) {
        m_enableEventBroadcast = flag;
    }

    public void setEnableEventSendMessage(final boolean flag) {
        m_enableEventSendMessage = flag;
    }

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
