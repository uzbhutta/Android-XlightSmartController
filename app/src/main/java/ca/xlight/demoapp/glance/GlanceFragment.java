package ca.xlight.demoapp.glance;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import ca.xlight.demoapp.SDK.CloudAccount;
import ca.xlight.demoapp.SDK.xltDevice;
import ca.xlight.demoapp.Tools.DataReceiver;
import ca.xlight.demoapp.main.MainActivity;
import ca.xlight.demoapp.R;
import ca.xlight.demoapp.main.SimpleDividerItemDecoration;
import ca.xlight.demoapp.control.DevicesListAdapter;
import ca.xlight.demoapp.swipeitemlayout.SwipeItemLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Umar Bhutta.
 */
public class GlanceFragment extends Fragment {
    private View root;
    private com.github.clans.fab.FloatingActionButton fab;
    public static GlanceFragment wndHandler;

    TextView txtLocation, outsideTemp, degreeSymbol, roomTemp, roomHumidity, roomBrightness, outsideHumidity, apparentTemp;
    ImageView imgWeather;

    public static String DEVICE_NAME = "DEVICE_NAME";
    public static String DEVICE_NODE_ID = "DEVICE_NODE_ID";
    public static String DEVICE_NODE_TYPE = "DEVICE_NODE_TYPE";

    private static final String TAG = MainActivity.class.getSimpleName();
    private DevicesListAdapter devicesListAdapter;
    private RecyclerView devicesRecyclerView;
    private WeatherDetails mWeatherDetails;

    private Handler m_handlerGlance;

    private Bitmap icoDefault, icoClearDay, icoClearNight, icoRain, icoSnow, icoSleet, icoWind, icoFog;
    private Bitmap icoCloudy, icoPartlyCloudyDay, icoPartlyCloudyNight;
    private static int ICON_WIDTH = 70;
    private static int ICON_HEIGHT = 75;

    private String strLocation;
    private double latitude, longitude;

    private class MyDataReceiver extends DataReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            roomTemp.setText(MainActivity.m_mainDevice.m_Data.m_RoomTemp + "\u00B0");
            roomHumidity.setText(MainActivity.m_mainDevice.m_Data.m_RoomHumidity + "\u0025");
            roomBrightness.setText(MainActivity.m_mainDevice.m_Data.m_RoomBrightness + "\u0025");
        }
    }
    private final MyDataReceiver m_DataReceiver = new MyDataReceiver();

    @Override
    public void onDestroyView() {
        wndHandler = null;
        devicesRecyclerView.setAdapter(null);
        MainActivity.m_mainDevice.removeDataEventHandler(m_handlerGlance);
        if( MainActivity.m_mainDevice.getEnableEventBroadcast() ) {
            getContext().unregisterReceiver(m_DataReceiver);
        }
        super.onDestroyView();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        wndHandler = this;
        root = inflater.inflate(R.layout.fragment_glance, container, false);

        fab = (com.github.clans.fab.FloatingActionButton) root.findViewById(R.id.fab);
        txtLocation = (TextView) root.findViewById(R.id.location);
        outsideTemp = (TextView) root.findViewById(R.id.outsideTemp);
        degreeSymbol = (TextView) root.findViewById(R.id.degreeSymbol);
        outsideHumidity = (TextView) root.findViewById(R.id.valLocalHumidity);
        apparentTemp = (TextView) root.findViewById(R.id.valApparentTemp);
        roomTemp = (TextView) root.findViewById(R.id.valRoomTemp);
        roomTemp.setText(MainActivity.m_mainDevice.m_Data.m_RoomTemp + "\u00B0");
        roomHumidity = (TextView) root.findViewById(R.id.valRoomHumidity);
        roomHumidity.setText(MainActivity.m_mainDevice.m_Data.m_RoomHumidity + "\u0025");
        roomBrightness = (TextView) root.findViewById(R.id.valRoomBrightness);
        roomBrightness.setText(MainActivity.m_mainDevice.m_Data.m_RoomBrightness + "\u0025");
        imgWeather = (ImageView) root.findViewById(R.id.weatherIcon);

        Resources res = getResources();
        Bitmap weatherIcons = decodeResource(res, R.drawable.weather_icons_1, 420, 600);
        icoDefault = Bitmap.createBitmap(weatherIcons, 0, 0, ICON_WIDTH, ICON_HEIGHT);
        icoClearDay = Bitmap.createBitmap(weatherIcons, ICON_WIDTH, 0, ICON_WIDTH, ICON_HEIGHT);
        icoClearNight = Bitmap.createBitmap(weatherIcons, ICON_WIDTH * 2, 0, ICON_WIDTH, ICON_HEIGHT);
        icoRain = Bitmap.createBitmap(weatherIcons, ICON_WIDTH * 5, ICON_HEIGHT * 2, ICON_WIDTH, ICON_HEIGHT);
        icoSnow = Bitmap.createBitmap(weatherIcons, ICON_WIDTH * 4, ICON_HEIGHT * 3, ICON_WIDTH, ICON_HEIGHT);
        icoSleet = Bitmap.createBitmap(weatherIcons, ICON_WIDTH * 5, ICON_HEIGHT * 3, ICON_WIDTH, ICON_HEIGHT);
        icoWind = Bitmap.createBitmap(weatherIcons, 0, ICON_HEIGHT * 3, ICON_WIDTH, ICON_HEIGHT);
        icoFog = Bitmap.createBitmap(weatherIcons, 0, ICON_HEIGHT * 2, ICON_WIDTH, ICON_HEIGHT);
        icoCloudy = Bitmap.createBitmap(weatherIcons, ICON_WIDTH , ICON_HEIGHT * 5, ICON_WIDTH, ICON_HEIGHT);
        icoPartlyCloudyDay = Bitmap.createBitmap(weatherIcons, ICON_WIDTH, ICON_HEIGHT, ICON_WIDTH, ICON_HEIGHT);
        icoPartlyCloudyNight = Bitmap.createBitmap(weatherIcons, ICON_WIDTH * 2, ICON_HEIGHT, ICON_WIDTH, ICON_HEIGHT);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onFabPressed(view);
            }
        });

        if( MainActivity.m_mainDevice.getEnableEventBroadcast() ) {
            IntentFilter intentFilter = new IntentFilter(xltDevice.bciSensorData);
            intentFilter.setPriority(3);
            getContext().registerReceiver(m_DataReceiver, intentFilter);
        }

        if( MainActivity.m_mainDevice.getEnableEventSendMessage() ) {
            m_handlerGlance = new Handler() {
                public void handleMessage(Message msg) {
                    int intValue = msg.getData().getInt("DHTt", -255);
                    if (intValue != -255) {
                        roomTemp.setText(intValue + "\u00B0");
                    }
                    intValue = msg.getData().getInt("DHTh", -255);
                    if (intValue != -255) {
                        roomHumidity.setText(intValue + "\u0025");
                    }
                    intValue = msg.getData().getInt("ALS", -255);
                    if (intValue != -255) {
                        roomBrightness.setText(intValue + "\u0025");
                    }
                }
            };
            MainActivity.m_mainDevice.addDataEventHandler(m_handlerGlance);
        }

        //setup recycler view
        devicesRecyclerView = (RecyclerView) root.findViewById(R.id.devicesRecyclerView);
        //create list adapter
        devicesListAdapter = new DevicesListAdapter();
        //attach adapter to recycler view
        devicesRecyclerView.setAdapter(devicesListAdapter);
        //set LayoutManager for recycler view
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        //attach LayoutManager to recycler view
        devicesRecyclerView.setLayoutManager(layoutManager);
        //divider lines
        devicesRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));

        devicesRecyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(getContext()));

        // Get ControllerID
        int controllerId = getContext().getSharedPreferences(MainActivity.keySettings, Activity.MODE_PRIVATE).getInt(MainActivity.keyControllerID, 0);
        if( controllerId == 2 ) {
            // Waterloo
            strLocation = "Waterloo, ON";
            latitude = 43.4643;
            longitude = -80.5204;
        } else if( controllerId == 3 ) {
            // Gu'an, Hebei, China
            strLocation = "Gu An, China";
            latitude = 39.44;
            longitude = 116.29;

        } else {
            // Suzhou
            strLocation = "Suzhou, China";
            latitude = 31.2989;
            longitude = 120.5852;
        }
        // Waterloo
        //final String strLocation = "Waterloo, ON";
        //double latitude = 43.4643;
        //double longitude = -80.5204;
        // Suzhou
        //final String strLocation = "Suzhou, China";
        //double latitude = 31.2989;
        //double longitude = 120.5852;
        // Gu'an, Hebei, China
        //final String strLocation = "Gu An, China";
        //double latitude = 39.44;
        //double longitude = 116.29;

        String forecastUrl = "https://api.forecast.io/forecast/" + CloudAccount.DarkSky_apiKey + "/" + latitude + "," + longitude;

        if (isNetworkAvailable()) {
            OkHttpClient client = new OkHttpClient();
            //build request
            Request request = new Request.Builder()
                    .url(forecastUrl)
                    .build();
            //put request in call object to use for returning data
            Call call = client.newCall(request);
            //make async call
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {

                }

                @Override
                public void onResponse(Response response) throws IOException {
                    try {
                        String jsonData = response.body().string();
                        if (response.isSuccessful()) {
                            mWeatherDetails = getWeatherDetails(jsonData);
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }
                            });
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException | JSONException e) {
                        Log.e(TAG, "Exception caught: " + e);
                    }
                }
            });
        } else {
            //if network isn't available
            Toast.makeText(getActivity(), "Please connect to the network before continuing.", Toast.LENGTH_SHORT).show();
        }

        return root;
    }

    private void updateDisplay() {
        imgWeather.setImageBitmap(getWeatherIcon(mWeatherDetails.getIcon()));
        txtLocation.setText(mWeatherDetails.getLocation());
        outsideTemp.setText(" " + mWeatherDetails.getTemp("celsius"));
        degreeSymbol.setText("\u00B0");
        outsideHumidity.setText(mWeatherDetails.getmHumidity() + "\u0025");
        apparentTemp.setText(mWeatherDetails.getApparentTemp("celsius") + "\u00B0");

        roomTemp.setText(MainActivity.m_mainDevice.m_Data.m_RoomTemp + "\u00B0");
        roomHumidity.setText(MainActivity.m_mainDevice.m_Data.m_RoomHumidity + "\u0025");
        roomBrightness.setText(MainActivity.m_mainDevice.m_Data.m_RoomBrightness + "\u0025");
    }

    private WeatherDetails getWeatherDetails(String jsonData) throws JSONException {
        WeatherDetails weatherDetails = new WeatherDetails();

        //make JSONObject for all JSON
        JSONObject forecast = new JSONObject(jsonData);

        //JSONObject for nested JSONObject inside 'forecast' for current weather details
        JSONObject currently = forecast.getJSONObject("currently");

        weatherDetails.setLocation(strLocation);
        weatherDetails.setTemp(currently.getDouble("temperature"));
        weatherDetails.setIcon(currently.getString("icon"));
        weatherDetails.setApparentTemp(currently.getDouble("apparentTemperature"));
        weatherDetails.setHumidity((int)(currently.getDouble("humidity") * 100 + 0.5));

        return weatherDetails;
    }

    private void alertUserAboutError() {
        Toast.makeText(getActivity(), "There was an error retrieving weather data.", Toast.LENGTH_SHORT).show();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;

        //check if network is available and connected to web
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        }

        return isAvailable;
    }

    private Bitmap decodeResource(Resources resources, final int id, final int newWidth, final int newHeight) {
        TypedValue value = new TypedValue();
        resources.openRawResource(id, value);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTargetDensity = value.density;
        Bitmap loadBmp = BitmapFactory.decodeResource(resources, id, opts);

        int width = loadBmp.getWidth();
        int height = loadBmp.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap newBmp = Bitmap.createBitmap(loadBmp, 0, 0, width, height, matrix, true);
        return newBmp;
    }

    public Bitmap getWeatherIcon(final String iconName) {
        if( iconName.equalsIgnoreCase("clear-day") ) {
            return icoClearDay;
        } else if( iconName.equalsIgnoreCase("clear-night") ) {
            return icoClearNight;
        } else if( iconName.equalsIgnoreCase("rain") ) {
            return icoRain;
        } else if( iconName.equalsIgnoreCase("snow") ) {
            return icoSnow;
        } else if( iconName.equalsIgnoreCase("sleet") ) {
            return icoSleet;
        } else if( iconName.equalsIgnoreCase("wind") ) {
            return icoWind;
        } else if( iconName.equalsIgnoreCase("fog") ) {
            return icoFog;
        } else if( iconName.equalsIgnoreCase("cloudy") ) {
            return icoCloudy;
        } else if( iconName.equalsIgnoreCase("partly-cloudy-day") ) {
            return icoPartlyCloudyDay;
        } else if( iconName.equalsIgnoreCase("partly-cloudy-night") ) {
            return icoPartlyCloudyNight;
        } else {
            return icoDefault;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String incomingName = data.getStringExtra(DEVICE_NAME);
                String incomingID = data.getStringExtra(DEVICE_NODE_ID);
                String incomingType = data.getStringExtra(DEVICE_NODE_TYPE);

                int pos = searchDeviceID(incomingID);
                if( pos >= 0 ) {
                    // Update
                    MainActivity.deviceNames.set(pos, incomingName);
                    MainActivity.deviceNodeTypeIDs.set(pos, incomingType);
                    MainActivity.m_mainDevice.setDeviceType(Integer.parseInt(incomingID), Integer.parseInt(incomingType));
                    MainActivity.m_mainDevice.setDeviceName(Integer.parseInt(incomingID), incomingName);
                    devicesListAdapter.notifyItemChanged(pos);
                } else {
                    // Add new
                    MainActivity.deviceNames.add(incomingName);
                    MainActivity.deviceNodeIDs.add(incomingID);
                    MainActivity.deviceNodeTypeIDs.add(incomingType);
                    MainActivity.m_mainDevice.addNodeToDeviceList(Integer.parseInt(incomingID), Integer.parseInt(incomingType), incomingName);
                    devicesListAdapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), "Device has been successfully added", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void onFabPressed(View view) {
        showDeivceInfoUpdate("", "", "");
    }

    public int searchDeviceID(String nid) {
        for (int pos = 0; pos < MainActivity.deviceNodeIDs.size(); pos++) {
            if (MainActivity.deviceNodeIDs.get(pos).equalsIgnoreCase(nid) ) return pos;
        }
        return -1;
    }

    public void showDeivceInfoUpdate(String nid, String name, String type) {
        Intent intent = new Intent(getContext(), AddNewDevice.class);
        intent.putExtra(DEVICE_NODE_ID, nid);
        intent.putExtra(DEVICE_NAME, name);
        intent.putExtra(DEVICE_NODE_TYPE, type);
        startActivityForResult(intent, 1);
    }
}
