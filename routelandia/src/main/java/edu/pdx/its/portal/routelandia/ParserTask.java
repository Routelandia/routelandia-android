package edu.pdx.its.portal.routelandia;

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by loc on 1/19/15.
 */
public class ParserTask extends AsyncTask<String, Integer, HashMap<Integer,List<LatLng>> > {

    protected GoogleMap mMap;

    public ParserTask(GoogleMap googleMap) {
        this.mMap = googleMap;
    }

    // Parsing the data in non-ui thread
    @Override
    protected HashMap<Integer, List<LatLng>> doInBackground(String... jsonData) {

        HashMap<Integer, List<LatLng>> routes = new HashMap<>();

        try{
            JSONArray jObject = new JSONArray(jsonData[0]);
            JSONParser parser = new JSONParser();

            // Starts parsing data
            routes = parser.parse(jObject);
        }catch(Exception e){
            e.printStackTrace();
        }
        return routes;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(HashMap<Integer, List<LatLng>> result) {
            /*List<LatLng> points = result.get(1064);
            System.out.println(points);
            PolylineOptions lineOptions = new PolylineOptions();

            lineOptions.addAll(points);
            lineOptions.width(10);
            lineOptions.color(Color.GREEN);

            mMap.addPolyline(lineOptions);*/
        List<Integer> listStationIDFive = Arrays.asList(1130, 3165, 1001, 1002, 3191, 1003, 1004, 1005,
                3197, 1006, 1007, 1008, 1009, 1010, 1011, 1012, 3167, 1013, 1014, 3117, 3193, 1015, 3119, 3184,
                1016, 1017, 3168, 1018, 3169, 3171, 1019, 1020, 1021, 3123, 3124, 1022, 1023, 1024, 1025);
        List<Integer> listStationID2 = Arrays.asList(1071,1070,1069,1068,1067,3157,1066,1065,3155,1064,1063,3153);
        // Has null values that crashes app
        //List<Integer> listStationID3 = Arrays.asList(1138,3178,1081,1082,1128,1084,1083,1085,1086,1087,1088,1089,3180,1090,1091,3161,1148,3163,1123);
        for (int i = 0; i < listStationIDFive.size(); i++) {
            List<LatLng> points = result.get(listStationIDFive.get(i));
            if(points != null) {
                PolylineOptions lineOptions = new PolylineOptions();

                //globalPoly.addAll(points);
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.GREEN);



                mMap.addPolyline(lineOptions);
            }
        }
        for (int i = 0; i < listStationID2.size(); i++) {
            List<LatLng> points = result.get(listStationID2.get(i));
            if(points != null) {
                PolylineOptions lineOptions = new PolylineOptions();

                //globalPoly.addAll(points);
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

//                    mMap.addPolyline(lineOptions);
                mMap.addPolyline(lineOptions);
            }
        }

    }
}
