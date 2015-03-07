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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import edu.pdx.its.portal.routelandia.entities.TrafficStat;


public class DatePickUp extends Activity {
    private TextView tvDisplayDay;
    private TimePicker thisTimePicker;
    private Button btnDepartureDate;
    private int hour;
    private int minute;
    private int am_pm;
    protected String dayofweek;
    static final int TIME_DIALOG_ID = 100;
    private Spinner weekDaySpinner;
    public String weekDay;// = "Sunday";
    protected LatLng startPoint;
    protected LatLng endPoint;
    protected String departureTime;
    protected ArrayList<TrafficStat> trafficStatList;
    private int TIME_PICKER_INTERVAL = 15;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_pick_up);
     
        setCurrentTimeOnView();

        addListenerOnWeekDaySpinnerSelection();
        addListenerOnTime();
        
        if(getIntent().getExtras() != null) {
            startPoint = new LatLng(getIntent().getExtras().getDouble("lat of first point"), 
                    getIntent().getExtras().getDouble("lng of first point"));

            endPoint = new LatLng(getIntent().getExtras().getDouble("lat of second point"),
                    getIntent().getExtras().getDouble("lng of second point"));

        }
        addListenerOnButton();
    }


    private void addListenerOnWeekDaySpinnerSelection() {
        weekDaySpinner = (Spinner) findViewById(R.id.spinner);
        DayPickSelectedListener dayPickSelectedListener = new DayPickSelectedListener();
        weekDaySpinner.setOnItemSelectedListener(dayPickSelectedListener);
        //return dayPickSelectedListener.getWeekDay();
    }

    private void addListenerOnTime(){
        thisTimePicker = (TimePicker) findViewById(R.id.timePicker);
        TimePickSelectedListener timePickSelectedListener = new TimePickSelectedListener();
        thisTimePicker.setOnTimeChangedListener(timePickSelectedListener);
        //return timePickSelectedListener.getDepartureTime();
    }

    //Display current time
    public void setCurrentTimeOnView() {

        tvDisplayDay = (TextView) findViewById(R.id.tvTime);
        thisTimePicker = (TimePicker) findViewById(R.id.timePicker);

        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        am_pm = c.get(Calendar.AM_PM);
        dayofweek =  getDayOfWeekInStr(c.get(Calendar.DAY_OF_WEEK));

        tvDisplayDay.setText(
                new StringBuilder().append(hour)
                        .append(":").append(minute));

        thisTimePicker.setCurrentHour(hour);
        thisTimePicker.setCurrentMinute(minute);
    }

    public String getDayOfWeekInStr(int value) {
        String day = "Sunday";
        switch (value) {
            case 0:
                day = "Sunday";
                break;
            case 1:
                day = "Monday";
                break;
            case 2:
                day = "Tuesday";
                break;
            case 3:
                day = "Wednesday";
                break;
            case 4:
                day = "Thursday";
                break;
            case 5:
                day = "Friday";
                break;
            case 6:
                day = "Saturday";
                break;
        }
        return day;
    }

    public void addListenerOnButton() {

        btnDepartureDate = (Button) findViewById(R.id.btnDepartureDate);
        btnDepartureDate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //System.out.print(weekDay);
                trafficStatList = (ArrayList)TrafficStat.getStatsResultListFor(startPoint, endPoint, departureTime, weekDay);

                if(trafficStatList == null){
                    Log.e("RESULT", "No results returned from statistics query.");
                }
                else{
                    // For now just print them out.
    //                    for (int j =0; j < travelingInfoList.size(); j++){
    //                        Log.i("RESULT", travelingInfoList.get(j).toString());
    //                    }
                }

                Intent intent = new Intent(getApplicationContext(),ListStat.class);
                intent.putParcelableArrayListExtra("travel info", trafficStatList);
                startActivity(intent);
            }
        });
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    // for jason use if needed
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

    protected class DayPickSelectedListener implements AdapterView.OnItemSelectedListener {
        //protected int week_day;
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            Toast.makeText(parent.getContext(),
                    "Departure day: " + parent.getItemAtPosition(pos).toString(),
                    Toast.LENGTH_SHORT).show();
            String s = parent.getItemAtPosition(pos).toString();
            if(s == null){
                weekDay = dayofweek;
            }else {
                weekDay = s;
            }
            // Backend might prefer integer for days
            //week_day = getDayOfWeekInInt(s);
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            //TODO
        }


        public String getWeekDay() {
            return weekDay;
        }

        // Backend might prefer integer for days
        private int getDayOfWeekInInt(String value) {
            int day = 0;
            switch (value) {
                case "Sunday":
                    day = 0;
                    break;
                case "Monday":
                    day = 1;
                    break;
                case "Tuesday":
                    day = 2;
                    break;
                case "Wednesday":
                    day = 3;
                    break;
                case "Thursday":
                    day = 4;
                    break;
                case "Friday":
                    day = 5;
                    break;
                case "Saturday":
                    day = 6;
                    break;
            }
            return day;
        }
    }

    private class TimePickSelectedListener implements TimePicker.OnTimeChangedListener {


        @Override
        public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
            setTimePickerInterval(timePicker);

            StringBuilder s = new StringBuilder().append(hourOfDay).append(":").append(minute);
            departureTime = s.toString();

        }

        @SuppressLint("NewApi")
        private void setTimePickerInterval(TimePicker timePicker) {
            try {
                Class<?> classForid = Class.forName("com.android.internal.R$id");

                Field field = classForid.getField("minute");
                NumberPicker minutePicker = (NumberPicker) timePicker
                        .findViewById(field.getInt(null));

                minutePicker.setMinValue(0);
                minutePicker.setMaxValue(7);
                ArrayList<String> displayedValues = new ArrayList<>();
                for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                    displayedValues.add(String.format("%02d", i));
                }
                for (int i = 0; i < 60; i += TIME_PICKER_INTERVAL) {
                    displayedValues.add(String.format("%02d", i));
                }
                minutePicker.setDisplayedValues(displayedValues
                        .toArray(new String[0]));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}