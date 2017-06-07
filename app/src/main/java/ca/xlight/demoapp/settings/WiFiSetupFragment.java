package ca.xlight.demoapp.settings;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import ca.xlight.demoapp.R;
import ca.xlight.demoapp.main.MainActivity;

/**
 * Created by sunboss on 2017-06-01.
 */

public class WiFiSetupFragment extends Fragment {
    RadioGroup m_authGroup;
    RadioGroup m_cipherGroup;
    TextView m_txtSSID, m_txtPassword;
    Button m_setupWiFi,m_btnClearCredentials, m_btnResetController, m_btnSafe, m_btnDFU;
    View m_view;
    int m_nAuth, m_nCipher;
    private AlertDialog.Builder builder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        m_view = inflater.inflate(R.layout.fragment_wifisetup, container, false);
        builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);

        m_authGroup = (RadioGroup)m_view.findViewById(R.id.authList);
        m_cipherGroup = (RadioGroup)m_view.findViewById(R.id.cipherList);
        m_setupWiFi = (Button) m_view.findViewById(R.id.btnSetupWiFi);
        m_btnClearCredentials = (Button) m_view.findViewById(R.id.btnClearCredentials);
        m_btnResetController = (Button) m_view.findViewById(R.id.btnResetController);
        m_btnSafe = (Button) m_view.findViewById(R.id.btnSafe);
        m_btnDFU = (Button) m_view.findViewById(R.id.btnDFU);
        m_txtSSID = (TextView) m_view.findViewById(R.id.editSSID);
        m_txtPassword = (TextView) m_view.findViewById(R.id.editPWD);
        m_nAuth = 0;
        m_nCipher = 0;

        int i;
        RadioButton tempButton;
        for( i = 0; i < MainActivity.mWiFiAuthNames.length; i++ ) {
            tempButton = new RadioButton(getContext());
            tempButton.setText(MainActivity.mWiFiAuthNames[i]);
            tempButton.setTag(i);
            m_authGroup.addView(tempButton, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tempButton.setChecked(i == 0);
        }

        for( i = 0; i < MainActivity.mWiFiCipherNames.length; i++ ) {
            tempButton = new RadioButton(getContext());
            tempButton.setText(MainActivity.mWiFiCipherNames[i]);
            tempButton.setTag(i);
            m_cipherGroup.addView(tempButton, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tempButton.setChecked(i == 0);
        }

        m_authGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                RadioButton tempButton = (RadioButton)m_view.findViewById(i);
                m_nAuth = (Integer) tempButton.getTag();
            }
        });

        m_cipherGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
                RadioButton tempButton = (RadioButton)m_view.findViewById(i);
                m_nCipher = (Integer) tempButton.getTag();
            }
        });

        m_setupWiFi.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if( m_txtSSID.getText().length() > 0 ) {
                    MainActivity.m_mainDevice.sysWiFiSetup(m_txtSSID.getText().toString(), m_txtPassword.getText().toString(), m_nAuth, m_nCipher);
                }
            }
        });

        m_btnClearCredentials.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                builder.setTitle("Warning");
                builder.setMessage("Are sure you want to clear all Wi-FI credentials on Controller?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MainActivity.m_mainDevice.sysControl("clear credentials");
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        m_btnResetController.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                builder.setTitle("Warning");
                builder.setMessage("Are sure you want to reset Controller?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MainActivity.m_mainDevice.sysControl("reset");
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        m_btnSafe.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                builder.setTitle("Warning");
                builder.setMessage("Are sure you want to bring Controller to Safe Mode?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MainActivity.m_mainDevice.sysControl("safe");
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        m_btnDFU.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                builder.setTitle("Warning");
                builder.setMessage("Are sure you want to bring Controller to DFU Mode?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MainActivity.m_mainDevice.sysControl("dfu");
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();
            }
        });

        return m_view;
    }
}