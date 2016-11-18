package com.umarbhutta.xlightcompanion.SDK;

/**
 * Created by sunboss on 2016-11-17.
 */

@SuppressWarnings({"UnusedDeclaration"})
// Base Class for Bridges
public class BaseBridge {
    private boolean m_bConnected = false;
    private String m_Name = "Unknown bridge";
    private int m_priority = 5;

    public boolean isConnected() {
        return m_bConnected;
    }

    public void setConnect(final boolean connected) {
        m_bConnected = connected;
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
}
