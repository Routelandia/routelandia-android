package edu.pdx.its.portal.routelandia;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.json.JSONArray;
import org.json.JSONObject;
import android.app.Dialog;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

public class MapsActivity extends FragmentActivity implements
        LocationListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    ArrayList<LatLng> mMarkerPoints;
    //protected PolylineOptions globalPoly;
    //private static final ScheduledExecutorService worker =
           // Executors.newSingleThreadScheduledExecutor();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else { // Google Play Services are available

            // Initializing
            mMarkerPoints = new ArrayList<>();

            // Getting reference to SupportMapFragment of the activity_maps
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting Map for the SupportMapFragment
            mMap = fm.getMap();

            // Enable MyLocation Button in the Map
            mMap.setMyLocationEnabled(true);

            String url = "http://capstoneaa.cs.pdx.edu/api/stations.json";

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);

        }
        /*
         Setting onclick event listener for the map
         Created by Nasim
          */
       // Runnable task = new Runnable() {
          //  @Override
           // public void run() {
                mMap.setOnMapClickListener(new OnMapClickListener() {

                    @Override
                    public void onMapClick(LatLng point) {
                        //if point found in hashmap latlng do rest
                           // if (globalPoly.getPoints().contains(point)) {
                                // Creating MarkerOptions
                                MarkerOptions marker = new MarkerOptions();
                                List<MarkerOptions> markerList = new ArrayList<MarkerOptions>();
                                // Setting the position of the marker
                                marker.position(point);
                                mMarkerPoints.add(point);
                                if (mMarkerPoints.size() == 1) {
                                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                                    LatLng startPoint = marker.getPosition();
                                    markerList.add(marker);
                                } else if (mMarkerPoints.size() == 2) {
                                    marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                                    LatLng endPoint = marker.getPosition();
                                    markerList.add(marker);
                                    mMarkerPoints.clear();
                                }
                                mMap.addMarker(marker);
                            }
                  //  }
                });//mMap.setOnMapClickListener(new OnMapClickListener()

          //  }
       // };
       // worker.schedule(task, 20, TimeUnit.SECONDS);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link # setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.moveCamera( CameraUpdateFactory.newLatLngZoom(new LatLng(45.509534, -122.681081), 10.0f));
        }
    }

    /** A class to download data from Google Directions URL */
    public class DownloadTask extends AsyncTask<String, Void, String>{

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder sb  = new StringBuilder();

            String line;
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /** A class to parse the Google Directions in JSON format */
    public class ParserTask extends AsyncTask<String, Integer, HashMap<Integer,List<LatLng>> >{

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
            List<Integer> listStationIDFive = Arrays.asList(1130,3165,1001,1002,3191,1003,1004,1005,
                    3197,1006,1007,1008,1009,1010,1011,1012,3167,1013,1014,3117,3193,1015,3119,3184,
                    1016,1017,3168,1018,3169,3171,1019,1020,1021,3123,3124,1022,1023,1024,1025);
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

                    mMap.addPolyline(lineOptions);
                }
            }

        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}