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
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import org.achartengine.GraphicalView;
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
public class ListStat extends Activity {

    private Button mapbtn;
    private GraphicalView mChart;
    protected ArrayList<TrafficStat> trafficStatList;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_stats);

        trafficStatList = getIntent().getParcelableArrayListExtra("travel info");
        for (int j =0; j < trafficStatList.size(); j++){
            Log.i("RESULT", trafficStatList.get(j).toString());
        }
        addListenerOnButton();
        
        if(getRotation(getBaseContext()) == 1 || getRotation(getBaseContext()) == 1) {
            openChart();
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

    private void openChart(){

        int count = 5;
        Date[] dt = new Date[5];
        for(int i=0;i<count;i++){
            GregorianCalendar gc = new GregorianCalendar(2012, 10, i+1);
            dt[i] = gc.getTime();
        }

        int[] visits = { 2000,2500,2700,2100,2800};
        int[] views = {2200, 2700, 2900, 2800, 3200};

        // Creating TimeSeries for Visits
        TimeSeries visitsSeries = new TimeSeries("Visits");

        // Creating TimeSeries for Views
        TimeSeries viewsSeries = new TimeSeries("Views");

        // Adding data to Visits and Views Series
        for(int i=0;i<dt.length;i++){
            visitsSeries.add(dt[i], visits[i]);
            viewsSeries.add(dt[i],views[i]);
        }

        // Creating a dataset to hold each series
        XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

        // Adding Visits Series to the dataset
        dataset.addSeries(visitsSeries);

        // Adding Visits Series to dataset
        dataset.addSeries(viewsSeries);

        // Creating XYSeriesRenderer to customize visitsSeries
        XYSeriesRenderer visitsRenderer = new XYSeriesRenderer();
        visitsRenderer.setColor(Color.WHITE);
        visitsRenderer.setPointStyle(PointStyle.CIRCLE);
        visitsRenderer.setFillPoints(true);
        visitsRenderer.setLineWidth(2);
        visitsRenderer.setDisplayChartValues(true);

        // Creating XYSeriesRenderer to customize viewsSeries
        XYSeriesRenderer viewsRenderer = new XYSeriesRenderer();
        viewsRenderer.setColor(Color.YELLOW);
        viewsRenderer.setPointStyle(PointStyle.CIRCLE);
        viewsRenderer.setFillPoints(true);
        viewsRenderer.setLineWidth(2);
        viewsRenderer.setDisplayChartValues(true);

        // Creating a XYMultipleSeriesRenderer to customize the whole chart
        XYMultipleSeriesRenderer multiRenderer = new XYMultipleSeriesRenderer();

        multiRenderer.setChartTitle("Travel Time");
        multiRenderer.setXTitle("Time");
        multiRenderer.setYTitle("Duration");
        multiRenderer.setZoomButtonsVisible(true);

        // Adding visitsRenderer and viewsRenderer to multipleRenderer
        // Note: The order of adding dataseries to dataset and renderers to multipleRenderer
        // should be same
        multiRenderer.addSeriesRenderer(visitsRenderer);
        multiRenderer.addSeriesRenderer(viewsRenderer);

        // Getting a reference to LinearLayout of the MainActivity Layout
        LinearLayout chartContainer = (LinearLayout) findViewById(R.id.chart_container);

        // Creating a Time Chart
        mChart = (GraphicalView) ChartFactory.getTimeChartView(getBaseContext(), dataset, multiRenderer,"dd-MMM-yyyy");

        multiRenderer.setClickEnabled(true);
        multiRenderer.setSelectableBuffer(10);

        // Setting a click event listener for the graph
        mChart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Format formatter = new SimpleDateFormat("dd-MMM-yyyy");

                SeriesSelection seriesSelection = mChart.getCurrentSeriesAndPoint();

                if (seriesSelection != null) {
                    int seriesIndex = seriesSelection.getSeriesIndex();
                    String selectedSeries="Visits";
                    if(seriesIndex==0)
                        selectedSeries = "Visits";
                    else
                        selectedSeries = "Views";

                    // Getting the clicked Date ( x value )
                    long clickedDateSeconds = (long) seriesSelection.getXValue();
                    Date clickedDate = new Date(clickedDateSeconds);
                    String strDate = formatter.format(clickedDate);

                    // Getting the y value
                    int amount = (int) seriesSelection.getValue();

                    // Displaying Toast Message
                    Toast.makeText(
                            getBaseContext(),
                            selectedSeries + " on "  + strDate + " : " + amount ,
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Adding the Line Chart to the LinearLayout
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
                        tv.setText("hour");
                    } else if (j == 1) {
                        tv.setText("speed");
                    } else if (j == 2) {
                        tv.setText("duration");
                    } else {
                        tv.setText("accuracy");
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
                        tv.setText(trafficStats.get(i).getSpeed() + " miles/hour");
                    } else if (j == 2) {
                        tv.setText(trafficStats.get(i).getTravelTime() + " minutes");
                    } else {
                        tv.setText(trafficStats.get(i).getAccuracy() + " percentage accuracy");
                    }

                    row.addView(tv);

                }
            }

            tableLayout.addView(row);

        }
    }
}
