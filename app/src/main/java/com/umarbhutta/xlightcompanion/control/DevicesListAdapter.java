package com.umarbhutta.xlightcompanion.control;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.SDK.xltDevice;
import com.umarbhutta.xlightcompanion.Tools.StatusReceiver;
import com.umarbhutta.xlightcompanion.main.MainActivity;
import com.umarbhutta.xlightcompanion.R;

/**
 * Created by Umar Bhutta.
 */
public class DevicesListAdapter extends RecyclerView.Adapter {

    private Handler m_handlerDeviceList;
    public Switch[] m_Switch = new Switch[MainActivity.deviceNodeIDs.length];

    public int findPositionByNodeID(final int _nodeID) {
        for (int iSw = 0; iSw < m_Switch.length; iSw++) {
            if((Integer)m_Switch[iSw].getTag() == _nodeID) {
                return iSw;
            }
        }
        return -1;
    }

    public void setSwitchState(final int _nodeID, final int _state) {
        int nPos = findPositionByNodeID(_nodeID);
        if( nPos >= 0 ) {
            m_Switch[nPos].setChecked(_state > 0);
        }
    }

    private class MyStatusReceiver extends StatusReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int nNodeID = intent.getIntExtra("nd", -1);
            if( nNodeID >= 0 ) {
                int nState = MainActivity.m_mainDevice.getState(nNodeID);
                setSwitchState(nNodeID, nState);
            }
        }
    }
    private final MyStatusReceiver m_StatusReceiver = new MyStatusReceiver();

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        if( MainActivity.m_mainDevice.getEnableEventSendMessage() ) {
            m_handlerDeviceList = new Handler() {
                public void handleMessage(Message msg) {
                    int nNodeID = msg.getData().getInt("nd", -1);
                    if( nNodeID >= 0 ) {
                        int nState = msg.getData().getInt("State", -255);
                        setSwitchState(nNodeID, nState);
                    }
                }
            };
            MainActivity.m_mainDevice.addDeviceEventHandler(m_handlerDeviceList);
        }

        if( MainActivity.m_mainDevice.getEnableEventBroadcast() ) {
            IntentFilter intentFilter = new IntentFilter(xltDevice.bciDeviceStatus);
            intentFilter.setPriority(3);
            recyclerView.getContext().registerReceiver(m_StatusReceiver, intentFilter);
        }
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.devices_list_item, parent, false);
        return new DevicesListViewHolder(view);
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        if( m_handlerDeviceList != null ) {
            MainActivity.m_mainDevice.removeDeviceEventHandler(m_handlerDeviceList);
        }
        if( MainActivity.m_mainDevice.getEnableEventBroadcast() ) {
            recyclerView.getContext().unregisterReceiver(m_StatusReceiver);
        }
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((DevicesListViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return MainActivity.deviceNodeIDs.length;
    }

    private class DevicesListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mDeviceName;
        private Switch mDeviceSwitch;
        private int mDeviceID;

        public DevicesListViewHolder(View itemView) {
            super(itemView);
            mDeviceName = (TextView) itemView.findViewById(R.id.deviceName);
            mDeviceSwitch = (Switch) itemView.findViewById(R.id.deviceSwitch);

            //itemView.setOnClickListener(this);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Change Current Device/Node
                    MainActivity.m_mainDevice.setDeviceID(mDeviceID);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    // Change Current Device/Node
                    MainActivity.m_mainDevice.setDeviceID(mDeviceID);
                    // Bring to Control Activity
                    if( MainActivity.m_eventHandler != null ) {
                        Message msg = MainActivity.m_eventHandler.obtainMessage();
                        if( msg != null ) {
                            Bundle bdlData = new Bundle();
                            bdlData.putInt("cmd", 1); // Menu
                            bdlData.putInt("item", R.id.nav_control); // Item
                            msg.setData(bdlData);
                            MainActivity.m_eventHandler.sendMessage(msg);
                        }
                    }
                    return false;
                }
            });

            mDeviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //ParticleAdapter.FastCallPowerSwitch(ParticleAdapter.DEFAULT_DEVICE_ID, isChecked);
                    // Change Current Device/Node
                    MainActivity.m_mainDevice.setDeviceID(mDeviceID);
                    MainActivity.m_mainDevice.PowerSwitch(isChecked ? xltDevice.STATE_ON : xltDevice.STATE_OFF);
                }
            });
        }

        public void bindView (int position) {
            mDeviceID = MainActivity.deviceNodeIDs[position];
            mDeviceName.setText(MainActivity.deviceNames[position] + ": " + mDeviceID);
            mDeviceSwitch.setChecked(MainActivity.m_mainDevice.getState(mDeviceID) > 0);
            mDeviceSwitch.setTag(mDeviceID);
            m_Switch[position] = mDeviceSwitch;
        }

        @Override
        public void onClick(View v) {
        }
    }
}
