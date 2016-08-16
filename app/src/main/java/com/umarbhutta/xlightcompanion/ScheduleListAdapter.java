package com.umarbhutta.xlightcompanion;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

/**
 * Created by Umar Bhutta.
 */
public class ScheduleListAdapter extends RecyclerView.Adapter {
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_list_item, parent, false);
        return new ScheduleListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ScheduleListViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return ScheduleFragment.name.size();
    }

    private class ScheduleListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mScheduleTime, mScheduleDays, mScheduleName;
        private Switch mScheduleSwitch;

        public ScheduleListViewHolder(View itemView) {
            super(itemView);
            mScheduleTime = (TextView) itemView.findViewById(R.id.scheduleTime);
            mScheduleDays = (TextView) itemView.findViewById(R.id.scheduleDays);
            mScheduleName = (TextView) itemView.findViewById(R.id.scheduleName);
            mScheduleSwitch = (Switch) itemView.findViewById(R.id.scheduleSwitch);

            itemView.setOnClickListener(this);
        }

        public void bindView(int position) {
            mScheduleName.setText(ScheduleFragment.name.get(position));
            mScheduleTime.setText(ScheduleFragment.time.get(position));
            mScheduleDays.setText(ScheduleFragment.days.get(position));
            mScheduleSwitch.setEnabled(true);
        }

        @Override
        public void onClick(View v) {
         //TODO: handle toggling off switch
        }
    }
}
