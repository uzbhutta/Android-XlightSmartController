package ca.xlight.demoapp.schedule;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import ca.xlight.demoapp.R;
import ca.xlight.demoapp.main.SimpleDividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by Umar Bhutta.
 */
public class ScheduleFragment extends Fragment {
    private com.github.clans.fab.FloatingActionButton fab;

    public static String SCENARIO_NAME = "SCENARIO_NAME";
    public static String SCHEDULE_HOUR = "SCHEDULE_HOUR";
    public static String SCHEDULE_MINUTE = "SCHEDULE_MINUTE";
    public static String SCHEDULE_AMPM = "SCHEDULE_AMPM";
    public static String SCHEDULE_ISREPEAT = "SCHEDULE_ISREPEAT";
    public static String SCHEDULE_WEEKDAYS = "SCHEDULE_WEEKDAYS";

    public static ArrayList<String> name = new ArrayList<>();
    public static ArrayList<String> time = new ArrayList<>();
    public static ArrayList<String> days = new ArrayList<>();

    ScheduleListAdapter scheduleListAdapter;
    RecyclerView scheduleRecyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        fab = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.fab);

        //setup recycler view
        scheduleRecyclerView = (RecyclerView) view.findViewById(R.id.scheduleRecyclerView);
        //create list adapter
        scheduleListAdapter = new ScheduleListAdapter();
        //attach adapter to recycler view
        scheduleRecyclerView.setAdapter(scheduleListAdapter);
        //set LayoutManager for recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //attach LayoutManager to recycler view
        scheduleRecyclerView.setLayoutManager(layoutManager);
        //divider lines
        scheduleRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFabPressed(view);
            }
        });

        return view;
    }

    private void onFabPressed(View view) {
        Intent intent = new Intent(getContext(), AddScheduleActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String incomingName = data.getStringExtra(SCENARIO_NAME);
                String incomingHour = data.getStringExtra(SCHEDULE_HOUR);
                String incomingMinute = data.getStringExtra(SCHEDULE_MINUTE);
                String incomingAMPM = data.getStringExtra(SCHEDULE_AMPM);
                boolean incomingIsRepeat = data.getBooleanExtra(SCHEDULE_ISREPEAT, false);
                String incomingWeekdays = data.getStringExtra(SCHEDULE_WEEKDAYS);

                name.add(incomingName);
                time.add(incomingHour + ":" + incomingMinute + " " + incomingAMPM);
                days.add(incomingWeekdays);

                scheduleListAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "The schedule has been successfully added", Toast.LENGTH_SHORT).show();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getActivity(), "No new schedule was added to the list", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
