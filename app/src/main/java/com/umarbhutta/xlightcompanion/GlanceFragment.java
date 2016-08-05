package com.umarbhutta.xlightcompanion;

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
public class GlanceFragment extends Fragment {
    private com.github.clans.fab.FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_glance, container, false);

        fab = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.fab);

        //setup recycler view
        RecyclerView devicesRecyclerView = (RecyclerView) view.findViewById(R.id.devicesRecyclerView);
        //create list adapter
        DevicesListAdapter devicesListAdapter = new DevicesListAdapter();
        //attach adapter to recycler view
        devicesRecyclerView.setAdapter(devicesListAdapter);
        //set LayoutManager for recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //attach LayoutManager to recycler view
        devicesRecyclerView.setLayoutManager(layoutManager);
        //divider lines
        devicesRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

        return view;
    }
}
