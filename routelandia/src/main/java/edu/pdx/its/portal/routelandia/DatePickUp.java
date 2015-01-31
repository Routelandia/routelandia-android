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
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.DatePicker;

public class DatePickUp extends Activity{
    private TextView tvDisplayDate;
    private DatePicker datePickerResult;
    private Button btnDepartureDate;

    private int year;
    private int month;
    private int day;
    private String dayOfWeek;

    static final int DATE_DIALOG_ID = 100;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_pick_up);

        setCurrentDateOnView();
        addListenerOnButton();
    }

    //Display current date
    public void setCurrentDateOnView() {

        tvDisplayDate = (TextView) findViewById(R.id.tvDate);
        datePickerResult = (DatePicker) findViewById(R.id.datePickerResult);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        dayOfWeek = getDayOfWeek(c.get(Calendar.DAY_OF_WEEK));
        System.out.println(dayOfWeek);

        //Set current date into text view
        tvDisplayDate.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(month + 1).append("-").append(day).append("-")
                .append(year).append(" "));

        datePickerResult.init(year, month, day, null);

    }

    public void addListenerOnButton() {

        btnDepartureDate = (Button) findViewById(R.id.btnDepartureDate);
        btnDepartureDate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //ShowDialog(DATE_DIALOG_ID);
                //Save date in jason array and switch to map for now...
                Log.i("clicks", "you clicked start");
                Intent i = new Intent(
                        DatePickUp.this,
                        ListStat.class);
                startActivity(i);
            }
        });
    }

  /*  @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:

                return new DatePickerDialog(this, datePickerListener,
                        year, month,day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener datePickerListener
            = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            year = selectedYear;
            month = selectedMonth;
            day = selectedDay;

            tvDisplayDate.setText(new StringBuilder().append(month + 1)
                    .append("-").append(day).append("-").append(year)
                    .append(" "));

            datePickerResult.init(year, month, day, null);
        }
    };*/

    private String getDayOfWeek(int value) {
        String day = "";
        switch (value) {
            case 1:
                day = "Sunday";
                break;
            case 2:
                day = "Monday";
                break;
            case 3:
                day = "Tuesday";
                break;
            case 4:
                day = "Wednesday";
                break;
            case 5:
                day = "Thursday";
                break;
            case 6:
                day = "Friday";
                break;
            case 7:
                day = "Saturday";
                break;
        }
        return day;
    }

}
