package ca.xlight.demoapp.SDK;

/**
 * Created by sunboss on 2016-11-17.
 */

import android.content.Context;

@SuppressWarnings({"UnusedDeclaration"})
// Base Class for Bridges
public class BaseBridge {
    private boolean m_bConnected = false;
    private String m_Name = "Unknown bridge";
    private int m_priority = 5; // the bigger, the higher
    protected Context m_parentContext = null;
    protected xltDevice m_parentDevice = null;
    protected EventParser m_eventParser = new EventParser();


    public boolean isConnected() {
        return m_bConnected;
    }

    public void setConnect(final boolean connected) {
        m_bConnected = connected;
    }

    public int getNodeID() {
        if( m_parentDevice != null ) {
            return m_parentDevice.getDeviceID();
        } else {
            return xltDevice.DEFAULT_DEVICE_ID;
        }
    }

    public String getName() {
        return m_Name;
    }

    public void setName(final String name) {
        m_Name = name;
        m_eventParser.SetTag(name);
    }

    public int getPriority() {
        return m_priority;
    }

    public void setPriority(final int priority) {
        m_priority = priority;
    }

    public void setParentContext(Context context) {
        m_parentContext = context;
    }

    public void setParentDevice(xltDevice device) {
        m_parentDevice = device;
        m_eventParser.SetParentDevice(device);
    }
}
