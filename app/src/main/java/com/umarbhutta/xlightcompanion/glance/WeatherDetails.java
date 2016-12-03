package com.umarbhutta.xlightcompanion.glance;

import android.graphics.Bitmap;

/**
 * Created by Umar Bhutta.
 */
public class WeatherDetails {
    private String mIcon;
    private double mTempF;
    private int mTempC;
    private double mApparentTempF;
    private int mApparentTempC;
    private int mHumidity;

    public WeatherDetails() {
        super();
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(final String mIcon) {
        this.mIcon = mIcon;
    }

    public int getTemp(final String unit)
    {
        if (unit == "fahrenheit") {
            return (int) mTempF;
        } else {
            return mTempC;
        }
    }

    public void setTemp(final double mTemp) {
        this.mTempF = mTemp;

        mTempC = (int) ((mTempF - 32.0) * (5.0/9.0) + 0.5);
    }

    public int getmHumidity() {
        return mHumidity;
    }

    public void setHumidity(final int humidity) {
        this.mHumidity = humidity;
    }

    public int getApparentTemp(final String unit)
    {
        if (unit == "fahrenheit") {
            return (int) mApparentTempF;
        } else {
            return mApparentTempC;
        }
    }

    public void setApparentTemp(final double mTemp) {
        mApparentTempF = mTemp;
        mApparentTempC = (int) ((mTempF - 32.0) * (5.0/9.0) + 0.5);
    }
}
