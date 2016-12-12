package com.umarbhutta.xlightcompanion.control;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

    private class MyStatusReceiver extends StatusReceiver {
        public Switch m_mainSwitch = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            if( m_mainSwitch != null ) {
                m_mainSwitch.setChecked(MainActivity.m_mainDevice.getState() > 0);
            }
        }
    }
    private final MyStatusReceiver m_StatusReceiver = new MyStatusReceiver();

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
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
        return 3;
    }

    private class DevicesListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mDeviceName;
        private Switch mDeviceSwitch;

        public DevicesListViewHolder(View itemView) {
            super(itemView);
            mDeviceName = (TextView) itemView.findViewById(R.id.deviceName);
            mDeviceSwitch = (Switch) itemView.findViewById(R.id.deviceSwitch);

            itemView.setOnClickListener(this);

            mDeviceSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    //ParticleAdapter.FastCallPowerSwitch(ParticleAdapter.DEFAULT_DEVICE_ID, isChecked);
                    MainActivity.m_mainDevice.PowerSwitch(isChecked);
                }
            });
        }

        public void bindView (int position) {
            mDeviceName.setText(MainActivity.deviceNames[position]);
            if (position == 0) {
                // Main device
                mDeviceSwitch.setChecked(MainActivity.m_mainDevice.getState() > 0);
                m_StatusReceiver.m_mainSwitch = mDeviceSwitch;

                if( MainActivity.m_mainDevice.getEnableEventSendMessage() ) {
                    m_handlerDeviceList = new Handler() {
                        public void handleMessage(Message msg) {
                            int intValue = msg.getData().getInt("State", -255);
                            if (intValue != -255) {
                                mDeviceSwitch.setChecked(MainActivity.m_mainDevice.getState() > 0);
                            }
                        }
                    };
                    MainActivity.m_mainDevice.addDeviceEventHandler(m_handlerDeviceList);
                }
            }
        }

        @Override
        public void onClick(View v) {
        }
    }
}
