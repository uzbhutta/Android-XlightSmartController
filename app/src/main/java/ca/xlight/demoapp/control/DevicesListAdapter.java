package ca.xlight.demoapp.control;

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
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ca.xlight.demoapp.SDK.xltDevice;
import ca.xlight.demoapp.Tools.StatusReceiver;
import ca.xlight.demoapp.glance.AddNewDevice;
import ca.xlight.demoapp.glance.GlanceFragment;
import ca.xlight.demoapp.main.MainActivity;
import ca.xlight.demoapp.R;

/**
 * Created by Umar Bhutta.
 */
public class DevicesListAdapter extends RecyclerView.Adapter {

    private Handler m_handlerDeviceList;
    private ArrayList<Switch> m_Switch = new ArrayList<>();
    private ArrayList<ImageView> m_Icon = new ArrayList<>();

    public int findPositionByNodeID(final int _nodeID) {
        for (int iSw = 0; iSw < m_Switch.size(); iSw++) {
            if((Integer)m_Switch.get(iSw).getTag() == _nodeID) {
                return iSw;
            }
        }
        return -1;
    }

    public void setSwitchState(final int _nodeID, final int _state, final boolean _alive, final boolean _isSwitch) {
        int nPos = findPositionByNodeID(_nodeID);
        if( nPos >= 0 ) {
            m_Switch.get(nPos).setChecked(_state > 0);
            if (_isSwitch) {
                if (_alive) {
                    m_Icon.get(nPos).setImageResource(_state > 0 ? R.drawable.ic_radio_button_checked_green_24dp : R.drawable.ic_radio_button_checked_red_24dp);
                } else {
                    m_Icon.get(nPos).setImageResource(R.drawable.ic_radio_button_checked_grey_24dp);
                }
            } else {
                if (_alive) {
                    m_Icon.get(nPos).setImageResource(_state > 0 ? R.drawable.ic_lightbulb_outline_green_24dp : R.drawable.ic_lightbulb_outline_black_24dp);
                } else {
                    m_Icon.get(nPos).setImageResource(R.drawable.ic_lightbulb_outline_grey_24dp);
                }
            }
        }
    }

    private class MyStatusReceiver extends StatusReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int nNodeID = intent.getIntExtra("nd", -1);
            if( nNodeID >= 0 ) {
                int nState;
                int nType = MainActivity.m_mainDevice.getDeviceType(nNodeID);
                if ( MainActivity.m_mainDevice.isSwitch(nType) ) {
                    nState = MainActivity.m_mainDevice.getKMState(nNodeID);
                } else {
                    nState = MainActivity.m_mainDevice.getState(nNodeID);
                }
                boolean bAlive = MainActivity.m_mainDevice.getNodeAlive(nNodeID);
                setSwitchState(nNodeID, nState, bAlive, MainActivity.m_mainDevice.isSwitch(nType));
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
                        int nState;
                        int nType = MainActivity.m_mainDevice.getDeviceType(nNodeID);
                        if ( MainActivity.m_mainDevice.isSwitch(nType) ) {
                            nState = MainActivity.m_mainDevice.getKMState(nNodeID);
                        } else {
                            nState = msg.getData().getInt("State", -255);
                        }
                        boolean bAlive = msg.getData().getBoolean("up", true);
                        setSwitchState(nNodeID, nState, bAlive, MainActivity.m_mainDevice.isSwitch(nType));
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
        return MainActivity.deviceNodeIDs.size();
    }

    private class DevicesListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        private TextView mDeviceName;
        private Switch mDeviceSwitch;
        private ImageView mStatusIcon;
        private int mDeviceID;

        public DevicesListViewHolder(View itemView) {
            super(itemView);

            View main = itemView.findViewById(R.id.main);
            main.setOnClickListener(this);
            main.setOnLongClickListener(this);

            View mark = itemView.findViewById(R.id.mark);
            mark.setOnClickListener(this);
            View delete = itemView.findViewById(R.id.delete);
            delete.setOnClickListener(this);

            mDeviceName = (TextView) itemView.findViewById(R.id.deviceName);
            mDeviceSwitch = (Switch) itemView.findViewById(R.id.deviceSwitch);
            mStatusIcon = (ImageView) itemView.findViewById(R.id.statusIcon);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            mDeviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //ParticleAdapter.FastCallPowerSwitch(ParticleAdapter.DEFAULT_DEVICE_ID, isChecked);
                    // Change Current Device/Node
                    MainActivity.m_mainDevice.setDeviceID(mDeviceID);
                    if (MainActivity.m_mainDevice.isSwitch() ) {
                        MainActivity.m_mainDevice.KMSwitch(isChecked, "1");
                    } else {
                        MainActivity.m_mainDevice.PowerSwitch(isChecked ? xltDevice.STATE_ON : xltDevice.STATE_OFF);
                    }
                }
            });
        }

        public void bindView (int position) {
            mDeviceID = Integer.parseInt(MainActivity.deviceNodeIDs.get(position));
            mDeviceName.setText(MainActivity.deviceNames.get(position) + ": " + mDeviceID);
            int nState;
            int nType = MainActivity.m_mainDevice.getDeviceType(mDeviceID);
            if ( MainActivity.m_mainDevice.isSwitch(nType) ) {
                nState = MainActivity.m_mainDevice.getKMState(mDeviceID);
            } else {
                nState = MainActivity.m_mainDevice.getState(mDeviceID);
            }
            mDeviceSwitch.setChecked(nState > 0);
            mDeviceSwitch.setTag(mDeviceID);
            if( m_Switch.size() <= position ) {
                m_Switch.add(mDeviceSwitch);
            } else {
                m_Switch.set(position, mDeviceSwitch);
            }
            if( m_Icon.size() <= position ) {
                m_Icon.add(mStatusIcon);
            } else {
                m_Icon.set(position, mStatusIcon);
            }
            setSwitchState(mDeviceID, nState, MainActivity.m_mainDevice.getNodeAlive(mDeviceID), MainActivity.m_mainDevice.isSwitch(nType));
        }

        @Override
        public void onClick(View v) {
            int pos;
            switch (v.getId()) {
                case R.id.main:
                    MainActivity.m_mainDevice.setDeviceID(mDeviceID);
                    break;

                case R.id.mark:
                    pos = getAdapterPosition();
                    if (GlanceFragment.wndHandler != null) {
                        (GlanceFragment.wndHandler).showDeivceInfoUpdate(MainActivity.deviceNodeIDs.get(pos), MainActivity.deviceNames.get(pos), MainActivity.deviceNodeTypeIDs.get(pos));
                    }
                    notifyItemChanged(pos);
                    break;

                case R.id.delete:
                    pos = getAdapterPosition();
                    MainActivity.m_mainDevice.removeNodeFromDeviceList(Integer.parseInt(MainActivity.deviceNodeIDs.get(pos)));
                    MainActivity.deviceNames.remove(pos);
                    MainActivity.deviceNodeIDs.remove(pos);
                    MainActivity.deviceNodeTypeIDs.remove(pos);
                    notifyItemRemoved(pos);
                    Toast.makeText(v.getContext(), "Device has been removed", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public boolean onLongClick(View v) {
            switch (v.getId()) {
                case R.id.main:
                    // Change Current Device/Node
                    MainActivity.m_mainDevice.setDeviceID(mDeviceID);
                    int nType = MainActivity.m_mainDevice.getDeviceType(mDeviceID);
                    if ( !MainActivity.m_mainDevice.isSwitch(nType) ) {
                        // Bring to Control Activity only if node is not switch device
                        /// ToDo: may bring to multiple switch control screen, so far we only consider single switch
                        /// ToDo: Therefore, we don't need sub-screen.
                        if (MainActivity.m_eventHandler != null) {
                            Message msg = MainActivity.m_eventHandler.obtainMessage();
                            if (msg != null) {
                                Bundle bdlData = new Bundle();
                                bdlData.putInt("cmd", 1); // Menu
                                bdlData.putInt("item", R.id.nav_control); // Item
                                msg.setData(bdlData);
                                MainActivity.m_eventHandler.sendMessage(msg);
                            }
                        }
                    }
                    break;
            }
            return false;
        }
    }
}
