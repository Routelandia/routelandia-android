package edu.pdx.its.portal.routelandia;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class DayPickSelectedListener implements OnItemSelectedListener {
    protected String weekDay;
    public static int week_day;
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
         Toast.makeText(parent.getContext(),
              "Departure day: " + parent.getItemAtPosition(pos).toString(),
               Toast.LENGTH_SHORT).show();
       String s = parent.getItemAtPosition(pos).toString();
       weekDay = s;
       // Backend might prefer integer for days
       week_day = getDayOfWeek(s);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        //TODO
    }
    
    public String getWeekDay() {
        return weekDay;
    }

    private int getDayOfWeek(String value) {
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

