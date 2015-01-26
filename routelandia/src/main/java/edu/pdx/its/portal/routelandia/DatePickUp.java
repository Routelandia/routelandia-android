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
                        TimePickUp.class);
                startActivity(i);
            }
        });
    }

    @Override
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
    };

}
