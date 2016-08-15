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

import java.util.ArrayList;

/**
 * Created by Umar Bhutta.
 */
public class ScenarioFragment extends Fragment {

    private com.github.clans.fab.FloatingActionButton fab;

    public static String SCENARIO_NAME = "SCENARIO_NAME";
    public static String SCENARIO_INFO = "SCENARIO_INFO";

    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> info = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_scenario, container, false);

        fab = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.fab);

        //setup recycler view
        RecyclerView scenarioRecyclerView = (RecyclerView) view.findViewById(R.id.scenarioRecyclerView);
        //create list adapter
        ScenarioListAdapter scenarioListAdapter = new ScenarioListAdapter();
        //attach adapter to recycler view
        scenarioRecyclerView.setAdapter(scenarioListAdapter);
        //set LayoutManager for recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //attach LayoutManager to recycler view
        scenarioRecyclerView.setLayoutManager(layoutManager);
        //divider lines
        scenarioRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFabPressed(view);
            }
        });

        return view;
    }

    private void onFabPressed(View view) {
        Intent intent = new Intent(getContext(), AddScenarioActivity.class);
        startActivity(intent);
    }
}
