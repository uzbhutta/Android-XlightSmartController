package com.umarbhutta.xlightcompanion.settings;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.main.MainActivity;

/**
 * Created by Umar Bhutta.
 */
public class SettingsFragment extends Fragment {
    RadioGroup m_controllerGroup;
    RadioGroup m_bridgeGroup;
    View m_view;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        m_view = inflater.inflate(R.layout.fragment_settings, container, false);

        m_controllerGroup = (RadioGroup)m_view.findViewById(R.id.controllerList);
        m_bridgeGroup = (RadioGroup)m_view.findViewById(R.id.bridgeList);

        int i;
        RadioButton tempButton;
        for( i = 0; i < MainActivity.mControllerNames.length; i++ ) {
            tempButton = new RadioButton(getContext());
            tempButton.setText(MainActivity.mControllerNames[i]);
            tempButton.setTag(i);
            m_controllerGroup.addView(tempButton, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tempButton.setChecked(i == MainActivity.mControllerId);
        }

        for( i = 0; i < MainActivity.mBridgeNames.length; i++ ) {
            tempButton = new RadioButton(getContext());
            tempButton.setText(MainActivity.mBridgeNames[i]);
            tempButton.setTag(i);
            m_bridgeGroup.addView(tempButton, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tempButton.setChecked(i == MainActivity.mBridgeId);
        }

        m_controllerGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                RadioButton tempButton = (RadioButton)m_view.findViewById(i);
                MainActivity.mControllerId = (Integer) tempButton.getTag();
            }
        });

        m_bridgeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                RadioButton tempButton = (RadioButton)m_view.findViewById(i);
                MainActivity.mBridgeId = (Integer) tempButton.getTag();
            }
        });

        return m_view;
    }
}
