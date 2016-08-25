package com.umarbhutta.xlightcompanion.glance;

/**
 * Created by Umar Bhutta.
 */
public class WeatherDetails {
    private String mIcon;
    private double mTempF;
    private int mTempC;

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String mIcon) {
        this.mIcon = mIcon;
    }

    public int getTemp(String unit)
    {
        if (unit == "fahrenheit") {
            return (int) mTempF;
        } else {
            return mTempC;
        }
    }

    public void setTemp(double mTemp) {
        this.mTempF = mTemp;

        mTempC = (int) ((mTempF - 32.0) * (5.0/9.0));
    }
}
