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

import edu.pdx.its.portal.routelandia.entities.APIException;
import edu.pdx.its.portal.routelandia.entities.TrafficStat;


public class DatePickUp extends Activity {
    private static final String TAG = "Activity: DatePickup";
    private TimePicker thisTimePicker;
    private Button btnDepartureDate;
    private int hour;
    private int minute;
    private int am_pm;
    protected String dayofweek;
    private Spinner weekDaySpinner;
    public String weekDay;
    protected LatLng startPoint;
    protected LatLng endPoint;
    protected String departureTime;
    protected ArrayList<TrafficStat> trafficStatList;
    private int TIME_PICKER_INTERVAL = 15;

    /**
     * Perform initialization of all fragments and loaders.
     *
     * @param savedInstanceState Bundle from Google SDK
     */
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

    /**
     * Handle user choose day of the week from the spinner*
     */
    private void addListenerOnWeekDaySpinnerSelection() {
        weekDaySpinner = (Spinner) findViewById(R.id.spinner);
        DayPickSelectedListener dayPickSelectedListener = new DayPickSelectedListener();
        weekDaySpinner.setOnItemSelectedListener(dayPickSelectedListener);
    }

    /**
     * Handle user choose time of the day*
     */
    private void addListenerOnTime(){
        thisTimePicker = (TimePicker) findViewById(R.id.timePicker);
        TimePickSelectedListener timePickSelectedListener = new TimePickSelectedListener();
        thisTimePicker.setOnTimeChangedListener(timePickSelectedListener);
    }

    /**
     * Display the current time* 
     */
    public void setCurrentTimeOnView() {

        thisTimePicker = (TimePicker) findViewById(R.id.timePicker);
        weekDaySpinner = (Spinner) findViewById(R.id.spinner);

        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        am_pm = c.get(Calendar.AM_PM);
        dayofweek =  getDayOfWeekInStr(c.get(Calendar.DAY_OF_WEEK));
        departureTime = (new StringBuilder().append(hour).append(":").append(minute)).toString();
        
        thisTimePicker.setCurrentHour(hour);
        thisTimePicker.setCurrentMinute(minute);
        // We have to do the -1 because DAY_OF_WEEK is 1-7, instead of 0-6.
        weekDaySpinner.setSelection(c.get(Calendar.DAY_OF_WEEK)-1);
    }

    /**
     * * 
     * @param value: number associate with date of the week
     * @return day of the week
     */
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

    /**
     * Method handle when user click on get statistics button* 
     */
    public void addListenerOnButton() {
        btnDepartureDate = (Button) findViewById(R.id.btnDepartureDate);
        btnDepartureDate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                
                try {
                    trafficStatList = (ArrayList) TrafficStat.getStatsResultListFor(startPoint, endPoint, departureTime, weekDay);
                }
                catch (APIException e) {
                    // TODO: RESTART ACTIVITY AFTER TELLING USER THAT THEY NEED TO DO SOMETHING!!
                    // (Did they pick bad points? Going to have to read the e.getResultWrapper().getParsedResponse() JSON to see...)
                    int response = e.getResultWrapper().getHttpStatus();
                    if(response == 400) {
                        new ErrorPopup("Error", "Please select two points along the same color highway segment.").givePopup(DatePickUp.this).show();

                        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                        startActivity(intent);
                    }
                    else if(response == 404 || response == 412){
                        new ErrorPopup("Error", "Could not complete request: \n\n" + e.getMessage()).givePopup(DatePickUp.this).show();
                    }
                    else if(response >= 500 && response < 600){
                        new ErrorPopup("Server Error", "There was an error on the server. Please try again later.").givePopup(DatePickUp.this).show();
                    }
                    else{
                        new ErrorPopup("Error", "Could not complete request: \n\n" + e.getMessage()).givePopup(DatePickUp.this).show();
                    }
                }

                if(trafficStatList == null){
                    Log.e(TAG, "No results returned from statistics query.");
                }
                else{
                    Intent intent = new Intent(getApplicationContext(),ListStat.class);
                    intent.putParcelableArrayListExtra("travel info", trafficStatList);
                    startActivity(intent);
                }
            }
        });
    }

    protected class DayPickSelectedListener implements AdapterView.OnItemSelectedListener {
        /**
         * * 
         * @param parent: parent view
         * @param view: current view
         * @param pos: position day in the spinner
         * @param id
         */
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
        }

        /**
         * Callback method to be invoked when the selection disappears from this
         * view. The selection can disappear for instance when touch is activated
         * or when the adapter becomes empty.
         *
         * @param parent The AdapterView that now contains no selected item.
         */
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            
        }
    }

    private class TimePickSelectedListener implements TimePicker.OnTimeChangedListener {
        /**
         * * 
         * @param timePicker: timepicker obk in the view 
         * @param hourOfDay The current hour.
         * @param minute The current minute.
         */
        @Override
        public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
            setTimePickerInterval(timePicker);

            StringBuilder s = new StringBuilder().append(hourOfDay).append(":").append(minute);
            departureTime = s.toString();

        }

        /**
         * change the minute interval to quarter* 
         * @param timePicker : timepicker obk in the view
         */
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