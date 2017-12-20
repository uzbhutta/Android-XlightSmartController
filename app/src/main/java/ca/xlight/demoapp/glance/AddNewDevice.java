package ca.xlight.demoapp.glance;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import ca.xlight.demoapp.R;
import ca.xlight.demoapp.main.MainActivity;

public class AddNewDevice extends AppCompatActivity {
    private TextView m_txtName, m_txtNodeID;
    private Button m_btnDone;
    private Spinner m_typeSpinner;

    private String  m_deviceName, m_node_id, m_type_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_device);

        TextView m_txtTitle = (TextView) findViewById(R.id.lblTitleDevInfo);
        m_txtName = (TextView) findViewById(R.id.editDeviceName);
        m_txtNodeID = (TextView) findViewById(R.id.editNodeID);
        m_btnDone = (Button) findViewById(R.id.btnAddDone);

        Intent data = getIntent();
        String incomingID = data.getStringExtra(GlanceFragment.DEVICE_NODE_ID);
        String w_title, incomingName, incomingType;
        int selectedType = -1;
        if (incomingID.length() > 0) {
            incomingName = data.getStringExtra(GlanceFragment.DEVICE_NAME);
            incomingType = data.getStringExtra(GlanceFragment.DEVICE_NODE_TYPE);
            for (int i = 0; i < MainActivity.mDeviceTypeIDs.length; i++ ) {
                if (MainActivity.mDeviceTypeIDs[i].equalsIgnoreCase(incomingType)  ) {
                    selectedType = i;
                    break;
                }
            }
            w_title = "Update Device";
            m_txtNodeID.setKeyListener(null);
        } else {
            incomingName = "";
            w_title = "Add Device";
        }
        m_txtTitle.setText(w_title);
        m_txtNodeID.setText(incomingID);
        m_txtName.setText(incomingName);

        //initialize device type spinner
        m_typeSpinner = (Spinner) findViewById(R.id.DeviceTypeSpinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> deviceAdapter = new ArrayAdapter<>(this, R.layout.add_device_type_spinner_item, MainActivity.mDeviceTypes);
        // Specify the layout to use when the list of choices appears
        deviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the scenarioAdapter to the spinner
        m_typeSpinner.setAdapter(deviceAdapter);
        m_typeSpinner.setSelection(selectedType, true);

        //on click for add button
        m_btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                m_deviceName = m_txtName.getText().toString();
                m_node_id = m_txtNodeID.getText().toString();

                //get value of scenario spinner
                int index = m_typeSpinner.getSelectedItemPosition();
                m_type_id = MainActivity.mDeviceTypeIDs[index];

                //send data to update the list
                Intent returnIntent = getIntent();
                returnIntent.putExtra(GlanceFragment.DEVICE_NAME, m_deviceName);
                returnIntent.putExtra(GlanceFragment.DEVICE_NODE_ID, m_node_id);
                returnIntent.putExtra(GlanceFragment.DEVICE_NODE_TYPE, m_type_id);
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });
    }

}
