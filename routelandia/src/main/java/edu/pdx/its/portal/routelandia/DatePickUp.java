/*
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package edu.pdx.its.portal.routelandia;

import java.util.Calendar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.maps.model.LatLng;


public class DatePickUp extends Activity {
    private TextView tvDisplayDay;
    private TimePicker thisTimePicker;
    private Button btnDepartureDate;
    private int hour;
    private int minute;
    private int am_pm;
    static final int TIME_DIALOG_ID = 100;
    private Spinner weekDaySpinner;
    public String weekDay;// = "Sunday";
    protected LatLng startPoint;
    protected LatLng endPoint;
    protected String departureTime;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_pick_up);
     
        setCurrentTimeOnView();
        addListenerOnButton();
        weekDay = addListenerOnWeekDaySpinnerSelection();
        departureTime = addListenerOnTime();
        
        if(getIntent().getExtras() != null) {
            startPoint = new LatLng(getIntent().getExtras().getDouble("lat of first point"), 
                    getIntent().getExtras().getDouble("lng of first point"));

            endPoint = new LatLng(getIntent().getExtras().getDouble("lat of second point"),
                    getIntent().getExtras().getDouble("lng of second point"));

        }

    }

    private String addListenerOnWeekDaySpinnerSelection() {
        weekDaySpinner = (Spinner) findViewById(R.id.spinner);
        DayPickSelectedListener dayPickSelectedListener = new DayPickSelectedListener();
        weekDaySpinner.setOnItemSelectedListener(dayPickSelectedListener);
        return dayPickSelectedListener.getWeekDay();
    }

    private String addListenerOnTime(){
        thisTimePicker = (TimePicker) findViewById(R.id.timePicker);
        TimePickSelectedListener timePickSelectedListener = new TimePickSelectedListener();
        thisTimePicker.setOnTimeChangedListener(timePickSelectedListener);
        return timePickSelectedListener.getDepartureTime();
    }

    //Display current time
    public void setCurrentTimeOnView() {

        tvDisplayDay = (TextView) findViewById(R.id.tvTime);
        thisTimePicker = (TimePicker) findViewById(R.id.timePicker);

        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        am_pm = c.get(Calendar.AM_PM);
//        dayOfWeek = getPmAm(c.get(Calendar.AM_PM));

        tvDisplayDay.setText(
                new StringBuilder().append(hour)
                        .append(":").append(minute));

        thisTimePicker.setCurrentHour(hour);
        thisTimePicker.setCurrentMinute(minute);
    }

    public void addListenerOnButton() {

        btnDepartureDate = (Button) findViewById(R.id.btnDepartureDate);
        btnDepartureDate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                HttpAsyncTask httpAsyncTask = new HttpAsyncTask(startPoint, endPoint, departureTime, weekDay);
                httpAsyncTask.execute();
                Intent i = new Intent(getApplicationContext(),ListStat.class);
                startActivity(i);
            }
        });
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    private String getPmAm(int value) {
        String pmAm = "";
        switch (value) {
            case 1:
                if (am_pm == 1) {
                    pmAm = "pm";
                }
                break;
            case 2:
                if (am_pm == 0) {
                    pmAm = "am";
                }
                break;
        }
        return pmAm;
    }
}