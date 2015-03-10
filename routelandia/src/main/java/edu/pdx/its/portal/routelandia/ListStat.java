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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart;
import org.achartengine.model.XYSeries;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.SeriesSelection;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import edu.pdx.its.portal.routelandia.entities.TrafficStat;

import static android.graphics.Paint.Align.CENTER;

public class ListStat extends ActionBarActivity {

    private Button mapbtn;
    private GraphicalView mChart;
    protected ArrayList<TrafficStat> trafficStatList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.list_stats);

        trafficStatList = getIntent().getParcelableArrayListExtra("travel info");
        for (int j =0; j < trafficStatList.size(); j++){
            Log.i("RESULT", trafficStatList.get(j).toString());
        }

        float avgAccuracy = getAverageAccuracy(trafficStatList);
        if(avgAccuracy < 60) {
          Toast.makeText(ListStat.this, "Warning! Low Accuracy Percentage!", Toast.LENGTH_LONG).show();
        }
        ((TextView) findViewById(R.id.tvPredictionAccuracy)).setText(avgAccuracy + "%");

        double length = 0;
        if(trafficStatList.size() > 0) {
            length = trafficStatList.get(0).getDistance();
        }
        ((TextView) findViewById(R.id.tvRouteLength)).setText(length + " miles");

        addListenerOnButton();
        
        if(getRotation(getBaseContext()) == 1 || getRotation(getBaseContext()) == 1) {
            if(trafficStatList.size() ==0){
                Toast.makeText(ListStat.this, "please re pick 2 points", Toast.LENGTH_SHORT).show();
            }
            else{
                openChart();
            }
        }
        else{
            TableLayout tableLayout = (TableLayout) findViewById(R.id.tableLayout);
            int columns = 4;
            int rows = trafficStatList.size() ;
            
            if(trafficStatList.size() ==0){
                Toast.makeText(ListStat.this, "please re pick 2 points", Toast.LENGTH_SHORT).show();
            }
            else{
                buildTable(rows, columns, trafficStatList, tableLayout);
            }
            
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openChart(){
        XYSeries durationSeries = new XYSeries("Duration of travel");
        int timeLength = trafficStatList.size();
        for(int i = 0; i < timeLength; i++){
            durationSeries.add((i + 1), trafficStatList.get(i).getTravelTime());
        }

        XYSeries speedSeries = new XYSeries("Speed");
        for(int i = 0; i < timeLength; i++){
            speedSeries.add((i + 1), trafficStatList.get(i).getSpeed());
        }

        //Collects all series and adds them under one object here called data
        XYMultipleSeriesDataset data = new  XYMultipleSeriesDataset();
        data.addSeries(durationSeries);
        data.addSeries(speedSeries);

        //Gives the Bar it's property
        XYSeriesRenderer durRenderer = new XYSeriesRenderer();
        durRenderer.setColor(Color.GREEN);
        durRenderer.setDisplayChartValues(true);
        durRenderer.setChartValuesTextAlign(Paint.Align.CENTER);
        durRenderer.setChartValuesTextSize(15);

        XYSeriesRenderer speedRenderer = new XYSeriesRenderer();
        speedRenderer.setColor(Color.CYAN);
        speedRenderer.setDisplayChartValues(true);
        speedRenderer.setChartValuesTextAlign(Paint.Align.CENTER);
        speedRenderer.setChartValuesTextSize(15);

        XYMultipleSeriesRenderer mRender = new XYMultipleSeriesRenderer();
        mRender.setBarSpacing(1);
        mRender.addSeriesRenderer(durRenderer);
        mRender.addSeriesRenderer(speedRenderer);
        mRender.setChartTitle("Travel Time and Speed");
        mRender.setXTitle("Departure Time");
        mRender.setYTitle("Duration and Speed");
        mRender.setZoomButtonsVisible(true);
        mRender.setLabelsTextSize(20);
        mRender.setLegendTextSize(15);
        mRender.setChartTitleTextSize(28);
        mRender.setAxisTitleTextSize(25);
        mRender.setXAxisMin(trafficStatList.get(1).getHour());
        mRender.setXAxisMax(trafficStatList.size());

        for (int i = 0; i < timeLength; i++)
        {
            mRender.addTextLabel(i + 1, String.valueOf((int) trafficStatList.get(i).getHour() + ":" + (int) trafficStatList.get(i).getMinutes() ));
        }
        mRender.setXLabelsAlign(CENTER);
        mRender.setXLabels(0);

        // Getting a reference to LinearLayout of the MainActivity Layout
        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.chart_container);

        // Creating a Time Chart
        mChart = (GraphicalView) ChartFactory.getBarChartView(getBaseContext(), data, mRender, BarChart.Type.DEFAULT);
        //return intent;
        mRender.setClickEnabled(true);
        mRender.setSelectableBuffer(10);

        mChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeriesSelection seriesSelection = mChart.getCurrentSeriesAndPoint();
                if (seriesSelection != null) {
                    int seriesIndex = seriesSelection.getSeriesIndex();
                    String selectedSeries = "Duration";
                    if(seriesIndex==0)
                        selectedSeries = "Duration";
                    else
                        selectedSeries = "Speed";
                }
            }
        });

        chartContainer.addView(mChart);
    }

    public void addListenerOnButton() {

        mapbtn = (Button) findViewById(R.id.map);
        mapbtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //ShowDialog(DATE_DIALOG_ID);
                //Save date in jason array and switch to map for now...
                Log.i("clicks", "you clicked start");
                Intent i = new Intent(
                        ListStat.this,
                        MapsActivity.class);
                startActivity(i);
            }
        });
    }

    public int getRotation(Context context){
        final int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getOrientation();
        switch (rotation) {
            case Surface.ROTATION_0:
                return 1; //portrait
            case Surface.ROTATION_90:
                return 0;//landscape
            case Surface.ROTATION_180:
                return 1;//reverse portrait
            default:
                return 0;//reverse landscape
        }
    }

    private void buildTable(int rows, int cols, ArrayList<TrafficStat> trafficStats, TableLayout tableLayout) {

        // outer for loop
        for (int i = -1; i < rows; i++) {

            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                    TableRow.LayoutParams.MATCH_PARENT));

            if (i == -1) {
                for (int j = 0; j < cols; j++) {

                    TextView tv = new TextView(this);
                    tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.MATCH_PARENT));
                    tv.setBackgroundResource(R.drawable.cell_shape);
                    tv.setPadding(40, 40, 40, 40);
                    tv.setTextColor(Color.BLACK);
                    if (j == 0) {
                        tv.setText("Time");
                    } else if (j == 1) {
                        tv.setText("Speed");
                    } else if (j == 2) {
                        tv.setText("Travel Time");
                    } else {
                        tv.setText("Prediction Accuracy");
                    }
                    row.addView(tv);
                }
            }
            else{
                // inner for loop
                for (int j = 0; j < cols; j++) {

                    TextView tv = new TextView(this);
                    tv.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,
                            TableRow.LayoutParams.MATCH_PARENT));
                    tv.setBackgroundResource(R.drawable.cell_shape);
                    tv.setPadding(40, 40, 40, 40);
                    tv.setTextColor(Color.BLACK);
                    if (j == 0) {
                        if (trafficStats.get(i).getMinutes() == 0) {

                            tv.setText(trafficStats.get(i).getHour() + ":"
                                    + trafficStats.get(i).getMinutes()
                                    + trafficStats.get(i).getMinutes());

                        } else {
                            tv.setText(trafficStats.get(i).getHour() + ":"
                                    + trafficStats.get(i).getMinutes());
                        }
                    } else if (j == 1) {
                        tv.setText(trafficStats.get(i).getSpeed() + " MPH");
                    } else if (j == 2) {
                        tv.setText(trafficStats.get(i).getTravelTime() + " Min");
                    } else {
                        tv.setText(trafficStats.get(i).getAccuracy() + "%");
                    }

                    row.addView(tv);

                }
            }

            tableLayout.addView(row);

        }
    }

    /**
     * Take a list of traffic stats and get the average accuracy
     *
     * @param l The list to get avg accuracy for
     * @return float representing the average accuracy of this list
     */
    private float getAverageAccuracy(ArrayList<TrafficStat> l) {
        float sum=0;
        if(!l.isEmpty()) {
            for (TrafficStat ts : l) {
               sum += ts.getAccuracy();
            }
            return sum / l.size();
        }
        return 0;
    }
}
