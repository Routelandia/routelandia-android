package edu.pdx.its.portal.routelandia;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by loc on 1/24/15.
 */
public class ParserTask extends AsyncTask<String, Integer, List<Highway>> {

    protected GoogleMap mMap;
    protected PolylineOptions globalPoly;

    public ParserTask(GoogleMap mMap, PolylineOptions globalPoly) {
        this.mMap = mMap;
        this.globalPoly = globalPoly;
    }

    // Parsing the data in non-ui thread
    @Override
    protected List<Highway> doInBackground(String... jsonData) {

//        HashMap<Integer, List<LatLng>> routes = new HashMap<>();
        List<Highway> routes =  new ArrayList<>();
        try {
            JSONArray jObject = new JSONArray(jsonData[0]);
            JSONParser parser = new JSONParser();

            // Starts parsing data
            routes = parser.parse(jObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(List<Highway> result) {
        PolylineOptions line = new PolylineOptions();
        for (int i = 0; i < result.size(); i++) {
            List<LatLng> points = result.get(i).getLatLngList();
            if (points != null) {
                PolylineOptions lineOptions = new PolylineOptions();

                globalPoly.addAll(points);
                line.addAll(points);
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.GREEN);
                lineOptions.geodesic(true);

                mMap.addPolyline(lineOptions);
            }
        }

    }
}

