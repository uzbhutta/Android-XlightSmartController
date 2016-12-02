package com.umarbhutta.xlightcompanion.SDK;

/**
 * Created by sunboss on 2016-11-17.
 */

import android.content.Context;

@SuppressWarnings({"UnusedDeclaration"})
// Base Class for Bridges
public class BaseBridge {
    private boolean m_bConnected = false;
    private int m_nodeID;
    private String m_Name = "Unknown bridge";
    private int m_priority = 5;
    protected Context m_parentContext = null;
    protected xltDevice m_parentDevice = null;

    public boolean isConnected() {
        return m_bConnected;
    }

    public void setConnect(final boolean connected) {
        m_bConnected = connected;
    }

    public void setNodeID(final int nodeID) {
        m_nodeID = nodeID;
    }

    public int getNodeID() {
        return m_nodeID;
    }

    public String getName() {
        return m_Name;
    }

    public void setName(final String name) {
        m_Name = name;
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
    }
}
