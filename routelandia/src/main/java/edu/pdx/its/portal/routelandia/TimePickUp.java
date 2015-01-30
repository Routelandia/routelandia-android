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
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;


public class TimePickUp extends Activity{
    private TextView tvDisplayTime;
    private TimePicker thisTimePicker;
    private Button btnDepartureTime;
    private int hour;
    private int minute;
    static final int TIME_DIALOG_ID = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_pick_up);
        setCurrentTimeOnView();
        addListenerOnButton();
    }

    //Display current time
    public void setCurrentTimeOnView() {

        tvDisplayTime = (TextView) findViewById(R.id.tvTime);
        thisTimePicker = (TimePicker) findViewById(R.id.timePicker1);

        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        tvDisplayTime.setText(
                new StringBuilder().append(pad(hour))
                        .append(":").append(pad(minute)));

        thisTimePicker.setCurrentHour(hour);
        thisTimePicker.setCurrentMinute(minute);
    }

    public void addListenerOnButton() {

        btnDepartureTime = (Button) findViewById(R.id.btnDepartureTime);
        btnDepartureTime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // showDialog(TIME_DIALOG_ID);
                //save time in jason array and switch back to map for now...
                Log.i("clicks", "you clicked start");
                Intent i = new Intent(
                        TimePickUp.this,
                        MapsActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case TIME_DIALOG_ID:
                //Set time picker as current time
                return new TimePickerDialog(this,
                        timePickerListener, hour, minute,false);
        }
        return null;
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute) {
                    hour = selectedHour;
                    minute = selectedMinute;

                    //Set current time into text view
                    tvDisplayTime.setText(new StringBuilder().append(pad(hour))
                            .append(":").append(pad(minute)));

                    //Set current time into time picker
                    thisTimePicker.setCurrentHour(hour);
                    thisTimePicker.setCurrentMinute(minute);
                }
            };

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }
}
