package ca.xlight.demoapp.main;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewDebug;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import ca.xlight.demoapp.SDK.BLE.BLEPairedDeviceList;
import ca.xlight.demoapp.SDK.CloudAccount;
import ca.xlight.demoapp.SDK.xltDevice;
import ca.xlight.demoapp.R;
import ca.xlight.demoapp.control.ControlFragment;
import ca.xlight.demoapp.glance.GlanceFragment;
import ca.xlight.demoapp.scenario.ScenarioFragment;
import ca.xlight.demoapp.schedule.ScheduleFragment;
import ca.xlight.demoapp.settings.SettingsFragment;
import ca.xlight.demoapp.settings.WiFiSetupFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //constants for testing lists
    public static String keySettings = "Settings";
    public static String keyControllerID = "ControllerID";
    public static String keyBridgeID = "BridgeID";
    public static String keyDeviceCount = "DeviceCount";
    public static String keyDeviceList = "DeviceList";

    private SharedPreferences m_sp;
    public static int mControllerId, mBridgeId;
    public static String[] mControllerNames;
    public static String[] mBridgeNames;
    public static String[] mWiFiAuthNames;
    public static String[] mWiFiCipherNames;
    public static String[] mDeviceTypes;
    public static String[] mDeviceTypeIDs;
    public static ArrayList<String> deviceNames = new ArrayList<>();
    public static ArrayList<String> deviceNodeIDs = new ArrayList<>();
    public static ArrayList<String> deviceNodeTypeIDs = new ArrayList<>();
    public static final String[] scheduleTimes = {"10:30 AM", "12:45 PM", "02:00 PM", "06:45 PM", "08:00 PM", "11:30 PM"};
    public static final String[] scheduleDays = {"Mo Tu We Th Fr", "Every day", "Mo We Th Sa Su", "Tomorrow", "We", "Mo Tu Fr Sa Su"};
    public static final String[] scenarioNames = {"Brunching", "Guests", "Naptime", "Dinner", "Sunset", "Bedtime"};
    public static final String[] scenarioDescriptions = {"A red color at 52% brightness", "A blue-green color at 100% brightness", "An amber color at 50% brightness", "Turn off", "A warm-white color at 100% brightness", "A green color at 52% brightness"};
    public static final String[] filterNames = {"Breathe", "Music Match", "Flash"};

    public static xltDevice m_mainDevice;
    public static Handler m_eventHandler;
    public static Handler m_bcsHandler;

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        saveSettings();
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        saveSettings();
        super.onDestroy();
    }

    public void saveSettings() {
        SharedPreferences.Editor editor = m_sp.edit();
        editor.putInt(keyControllerID, mControllerId);
        editor.putInt(keyBridgeID, mBridgeId);

        editor.putInt(keyDeviceCount, deviceNames.size());
        for (int i = 0; i <  deviceNames.size(); i++) {
            String item = deviceNodeIDs.get(i) + "," + deviceNodeTypeIDs.get(i) + "," + deviceNames.get(i);
            editor.putString(keyDeviceList + String.valueOf(i), item);
        }

        editor.commit();
    }

    public void loadSettings() {
        m_sp = getSharedPreferences(keySettings, MODE_PRIVATE);
        mControllerId = m_sp.getInt(keyControllerID, 0);
        mBridgeId = m_sp.getInt(keyBridgeID, 0);

        deviceNames.clear();
        deviceNodeIDs.clear();
        deviceNodeTypeIDs.clear();

        int dev_cnt = m_sp.getInt(keyDeviceCount, 0);
        if (dev_cnt > 0) {
            for (int i = 0; i <  dev_cnt; i++) {
                String item = m_sp.getString(keyDeviceList + String.valueOf(i), "");
                if (item.length() > 4) {
                    String [] temps = item.split(",");
                    if( temps.length >= 3 ) {
                        deviceNodeIDs.add(temps[0]);
                        deviceNodeTypeIDs.add(temps[1]);
                        deviceNames.add(temps[2]);
                    }
                }
            }
        }

        if (deviceNames.size() == 0 ) {
            deviceNames.add("Living Room");
            deviceNames.add("Bedroom");
            deviceNames.add("Dining Room");
            deviceNodeIDs.add("1");
            deviceNodeIDs.add("8");
            deviceNodeIDs.add("9");
            deviceNodeTypeIDs.add(String.valueOf(xltDevice.DEFAULT_DEVICE_TYPE));
            deviceNodeTypeIDs.add(String.valueOf(xltDevice.DEFAULT_DEVICE_TYPE));
            deviceNodeTypeIDs.add(String.valueOf(xltDevice.DEFAULT_DEVICE_TYPE));
        }
    }

    public void selectBridge() {
        boolean bAuto = true;

        if( mBridgeId > 0 ) {
            if( mBridgeId == 2 ) {
                if( m_mainDevice.useBridge(xltDevice.BridgeType.BLE) )
                    bAuto = false;
            } else {
                if( m_mainDevice.useBridge(xltDevice.BridgeType.Cloud) )
                    bAuto = false;
            }
        }

        m_mainDevice.setAutoBridge(bAuto);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Load Settings
        loadSettings();

        m_bcsHandler = new Handler() {
            public void handleMessage(Message msg) {
                String bridgeName = (String) msg.obj;
                switch( msg.what ) {
                    case xltDevice.BCS_CONNECTED:
                        if( bridgeName.equalsIgnoreCase(xltDevice.BridgeType.BLE.name()) || bridgeName.equalsIgnoreCase(xltDevice.BridgeType.Cloud.name()) ) {
                            selectBridge();
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().setSubtitle(m_mainDevice.getBridgeInfo());
                            }
                        }
                        break;

                    case xltDevice.BCS_NOT_CONNECTED:
                    case xltDevice.BCS_CONNECTION_FAILED:
                    case xltDevice.BCS_CONNECTION_LOST:
                        if (getSupportActionBar() != null) {
                            getSupportActionBar().setSubtitle(m_mainDevice.getBridgeInfo());
                        }
                        break;

                    case xltDevice.BCS_FUNCTION_ACK:
                        Toast.makeText(getApplicationContext(), (msg.arg1 == 1 ? "OK" : "Failed"), Toast.LENGTH_SHORT).show();
                        break;

                    case xltDevice.BCS_FUNCTION_COREID:
                        Toast.makeText(getApplicationContext(), "CoreID: " + bridgeName, Toast.LENGTH_LONG).show();
                        break;
                }
            }
        };

        // Check Bluetooth
        BLEPairedDeviceList.init(this);
        if( BLEPairedDeviceList.IsSupported() && !BLEPairedDeviceList.IsEnabled() ) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, BLEPairedDeviceList.REQUEST_ENABLE_BT);
        }

        // Initialize SmartDevice SDK
        m_mainDevice = new xltDevice();
        m_mainDevice.Init(this);
        //m_mainDevice.setBridgePriority(xltDevice.BridgeType.BLE, 9);

        // Setup Device/Node List
        for( int lv_idx = 0; lv_idx < deviceNodeTypeIDs.size(); lv_idx++ ) {
            m_mainDevice.addNodeToDeviceList(Integer.parseInt(deviceNodeIDs.get(lv_idx)), Integer.parseInt(deviceNodeTypeIDs.get(lv_idx)), deviceNames.get(lv_idx));
        }
        m_mainDevice.setDeviceID(Integer.parseInt(deviceNodeIDs.get(0)));

        // Connect to Controller
        // Get ControllerID
        mControllerNames = getResources().getStringArray(R.array.controller_list);
        mBridgeNames = getResources().getStringArray(R.array.bridge_list);
        String strControllerID = CloudAccount.DEVICE_ID;
        if( mControllerId < CloudAccount.DEVICE_IDS.length ) {
            strControllerID = CloudAccount.DEVICE_IDS[mControllerId];
        }
        m_mainDevice.Connect(strControllerID, m_bcsHandler);

        // Set Bridge
        selectBridge();

        // Set SmartDevice Event Notification Flag
        //m_mainDevice.setEnableEventSendMessage(false);
        //m_mainDevice.setEnableEventBroadcast(true);

        mWiFiAuthNames = getResources().getStringArray(R.array.wifi_auth_list);
        mWiFiCipherNames = getResources().getStringArray(R.array.wifi_cipher_list);
        mDeviceTypes = getResources().getStringArray(R.array.device_type_list);
        mDeviceTypeIDs = getResources().getStringArray(R.array.device_type_list_value);

        //setup drawer layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        displayView(R.id.nav_glance);
        navigationView.getMenu().getItem(0).setChecked(true);

        m_eventHandler = new Handler() {
            public void handleMessage(Message msg) {
                int nCmd = msg.getData().getInt("cmd", -1);
                if( nCmd == 1) {
                    // Menu
                    int nItem = msg.getData().getInt("item", -1);
                    if( nItem >= 0 ) {
                        displayView(nItem);
                    }
                }
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BLEPairedDeviceList.REQUEST_ENABLE_BT) {
            BLEPairedDeviceList.init(this);
        }
    }

    public void displayView(int viewId) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);

        switch (viewId) {
            case R.id.nav_glance:
                fragment = new GlanceFragment();
                title  = "Glance";
                break;
            case R.id.nav_control:
                fragment = new ControlFragment();
                title = "Control";
                break;
            case R.id.nav_schedule:
                fragment = new ScheduleFragment();
                title = "Schedule";
                break;
            case R.id.nav_scenario:
                fragment = new ScenarioFragment();
                title = "Scenario";
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                title = "Settings";
                break;
            case R.id.nav_wifisetup:
                fragment = new WiFiSetupFragment();
                title = "Controller Wi-Fi";
                break;
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.placeholder, fragment);
            ft.commit();
        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setSubtitle(m_mainDevice.getBridgeInfo());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            displayView(R.id.nav_settings);
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        displayView(item.getItemId());
        return true;
    }
}
