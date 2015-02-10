package edu.pdx.its.portal.routelandia;

import android.widget.TimePicker;

/**
 * Created by Nasim on 2/7/2015.
 */
public class TimePickSelectedListener implements TimePicker.OnTimeChangedListener {
    protected String departureTime;

    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        StringBuilder s = new StringBuilder().append(hourOfDay).append(":").append(minute);
        departureTime = s.toString();
    }

    public String getDepartureTime() {
        return departureTime;
    }
}
