package com.umarbhutta.xlightcompanion;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Umar Bhutta.
 */
public class ScheduleFragment extends Fragment {
    private com.github.clans.fab.FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule, container, false);

        fab = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.fab);

        //setup recycler view
        RecyclerView scheduleRecyclerView = (RecyclerView) view.findViewById(R.id.scheduleRecyclerView);
        //create list adapter
        ScheduleListAdapter scheduleListAdapter = new ScheduleListAdapter();
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
        startActivity(intent);
    }
}
