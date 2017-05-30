package com.umarbhutta.xlightcompanion.glance;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.umarbhutta.xlightcompanion.SDK.CloudAccount;
import com.umarbhutta.xlightcompanion.SDK.xltDevice;
import com.umarbhutta.xlightcompanion.Tools.DataReceiver;
import com.umarbhutta.xlightcompanion.main.MainActivity;
import com.umarbhutta.xlightcompanion.R;
import com.umarbhutta.xlightcompanion.main.SimpleDividerItemDecoration;
import com.umarbhutta.xlightcompanion.control.DevicesListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by Umar Bhutta.
 */
public class GlanceFragment extends Fragment {
    private com.github.clans.fab.FloatingActionButton fab;
    TextView txtLocation, outsideTemp, degreeSymbol, roomTemp, roomHumidity, roomBrightness, outsideHumidity, apparentTemp;
    ImageView imgWeather;

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView devicesRecyclerView;
    WeatherDetails mWeatherDetails;

    private Handler m_handlerGlance;

    private Bitmap icoDefault, icoClearDay, icoClearNight, icoRain, icoSnow, icoSleet, icoWind, icoFog;
    private Bitmap icoCloudy, icoPartlyCloudyDay, icoPartlyCloudyNight;
    private static int ICON_WIDTH = 70;
    private static int ICON_HEIGHT = 75;

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
        View view = inflater.inflate(R.layout.fragment_glance, container, false);

        fab = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.fab);
        txtLocation = (TextView) view.findViewById(R.id.location);
        outsideTemp = (TextView) view.findViewById(R.id.outsideTemp);
        degreeSymbol = (TextView) view.findViewById(R.id.degreeSymbol);
        outsideHumidity = (TextView) view.findViewById(R.id.valLocalHumidity);
        apparentTemp = (TextView) view.findViewById(R.id.valApparentTemp);
        roomTemp = (TextView) view.findViewById(R.id.valRoomTemp);
        roomTemp.setText(MainActivity.m_mainDevice.m_Data.m_RoomTemp + "\u00B0");
        roomHumidity = (TextView) view.findViewById(R.id.valRoomHumidity);
        roomHumidity.setText(MainActivity.m_mainDevice.m_Data.m_RoomHumidity + "\u0025");
        roomBrightness = (TextView) view.findViewById(R.id.valRoomBrightness);
        roomBrightness.setText(MainActivity.m_mainDevice.m_Data.m_RoomBrightness + "\u0025");
        imgWeather = (ImageView) view.findViewById(R.id.weatherIcon);

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
        devicesRecyclerView = (RecyclerView) view.findViewById(R.id.devicesRecyclerView);
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

        // Waterloo
        //final String strLocation = "Waterloo, ON";
        //double latitude = 43.4643;
        //double longitude = -80.5204;
        // Suzhou
        final String strLocation = "Suzhou, China";
        double latitude = 31.2989;
        double longitude = 120.5852;
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
                            mWeatherDetails.setLocation(strLocation);
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

        return view;
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
}
