package com.umarbhutta.xlightcompanion.glance;

import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
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
    TextView outsideTemp, degreeSymbol, roomTemp;

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView devicesRecyclerView;
    WeatherDetails mWeatherDetails;

    private Handler m_handlerGlance;

    @Override
    public void onDestroyView() {
        devicesRecyclerView.setAdapter(null);
        MainActivity.m_mainDevice.removeDataEventHandler(m_handlerGlance);
        super.onDestroyView();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_glance, container, false);

        fab = (com.github.clans.fab.FloatingActionButton) view.findViewById(R.id.fab);
        outsideTemp = (TextView) view.findViewById(R.id.outsideTemp);
        degreeSymbol = (TextView) view.findViewById(R.id.degreeSymbol);
        roomTemp = (TextView) view.findViewById(R.id.valRoomTemp);
        roomTemp.setText(MainActivity.m_mainDevice.m_Data.m_RoomTemp + "\u00B0");

        m_handlerGlance = new Handler() {
            public void handleMessage(Message msg) {
                int intValue =  msg.getData().getInt("DHTt", -255);
                if( intValue != -255 ) {
                    roomTemp.setText(intValue + "\u00B0");
                }
            }
        };
        MainActivity.m_mainDevice.addDataEventHandler(m_handlerGlance);

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

        String apiKey = "b6756abd11c020e6e9914c9fb4730169";
        double latitude = 43.4643;
        double longitude = -80.5204;
        String forecastUrl = "https://api.forecast.io/forecast/" + apiKey + "/" + latitude + "," + longitude;

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

        return view;
    }

    private void updateDisplay() {
        outsideTemp.setText(" " + mWeatherDetails.getTemp("celsius"));
        degreeSymbol.setText("\u00B0");
        roomTemp.setText(MainActivity.m_mainDevice.m_Data.m_RoomTemp + "\u00B0");
    }

    private WeatherDetails getWeatherDetails(String jsonData) throws JSONException {
        WeatherDetails weatherDetails = new WeatherDetails();

        //make JSONObject for all JSON
        JSONObject forecast = new JSONObject(jsonData);

        //JSONObject for nested JSONObject inside 'forecast' for current weather details
        JSONObject currently = forecast.getJSONObject("currently");

        weatherDetails.setTemp(currently.getDouble("temperature"));
        weatherDetails.setIcon(currently.getString("icon"));

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
}
