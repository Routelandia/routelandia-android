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

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import edu.pdx.its.portal.routelandia.entities.*;

public class MapsActivity extends ActionBarActivity implements AsyncResult {
    private final String TAG = "Maps Activity";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    protected PolylineOptions globalPoly = new PolylineOptions();
    protected List<Highway> highwayList = new ArrayList<>();
    protected HashMap<Integer, List<Station>> listOfStationsBaseOnHighwayid = new HashMap<>();
    protected LatLng startPoint;
    protected LatLng endPoint;
    protected Marker firstMarker;
    protected Marker secondMarker;
    private ProgressDialog loadingDialog;
    private int activeAsyncs = 0;

    private final String RESULT_HIGHWAY_LIST = "result_highway_list";
    private final String RESULT_STATION_LIST = "result_station_list";

    /**
     * Perform initialization of all fragments and loaders.
     *
     * @param savedInstanceState Bundle from Google SDK
     */
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
            // Getting reference to SupportMapFragment of the activity_maps
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting Map for the SupportMapFragment
            mMap = fm.getMap();

            if(savedInstanceState != null){
                getItemsFromSaveBundle(savedInstanceState);

                // Redraw the map
                for (HashMap.Entry<Integer, List<Station>> entry : listOfStationsBaseOnHighwayid.entrySet()) {
                    int highwayid = (int)entry.getKey();
                    List<Station> tStationList = entry.getValue();
                    int colorHighlightTheFreeWay = generatePairhighWayColor(highwayid);
                    drawHighway(tStationList, colorHighlightTheFreeWay);
                }
            }
            else {
                activeAsyncs = 0;    // Make sure that we don't get stuck somehow
                initLoadingDialog();
                fetchHighwayData();
            }
        }
        usersDragTheMarkers();

        goToDatePickUp();
    }

    /**
     * Start up the dialog to prevent user from clicking while things are loading...
     */
    private void initLoadingDialog() {
        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage("Loading data...");
        loadingDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
    }
    /**
     * Start the async running to go and get highway data. Will draw fetched data on the map.
     */
    private void fetchHighwayData() {
        APIEntity.fetchListForEntity(Highway.class, this, RESULT_HIGHWAY_LIST);
    }

    /**
     * Erase anything drawn on the map, and clear our downloaded information out.
     * Essentially resets the app to the beginning.
     */
    private void clearMap() {
        mMap.clear();
        listOfStationsBaseOnHighwayid = new HashMap<>();
    }

    /**
     * The user has dropped markers, get rid of them.
     */
    private void clearMarkers() {
        if(firstMarker == null && secondMarker == null){
            Toast.makeText(MapsActivity.this, "You have no marker to remove", Toast.LENGTH_LONG).show();
        }
        if(firstMarker != null){
            firstMarker.remove();
            firstMarker = null;
        }
        if(secondMarker != null){
            secondMarker.remove();
            secondMarker = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.miClearMap:
                return true;
            case R.id.miRefresh:
                if(firstMarker != null || secondMarker != null) {
                    // Don't make the "you don't have markers" text pop up unless it needs to
                    clearMarkers();
                }
                clearMap();
                initLoadingDialog();
                fetchHighwayData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * overwrite onMapClickListener to let users drag markerOptions in the map*
     */
    private void usersDragTheMarkers() {
        mMap.setOnMapClickListener(new OnMapClickListener() {
            @Override
            public void onMapClick(LatLng point) {
                drawMarker(point);
            }
        });
    }

    /**
     * Create a time and data button so users can go to the next page
     * which allow them to choose the time when they want to commute*
     */
    private void goToDatePickUp() {
        Button timeAndDateButton = (Button) findViewById(R.id.button3);
        timeAndDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(firstMarker != null && secondMarker != null) {
                    Intent i = new Intent(getApplicationContext(), DatePickUp.class).
                            putExtra("lat of first point", startPoint.latitude).
                            putExtra("lng of first point", startPoint.longitude).
                            putExtra("lat of second point", endPoint.latitude).
                            putExtra("lng of second point", endPoint.longitude);
                    startActivity(i);
                } else {
                    new ErrorPopup("Error", "Please select a start and an end point along the same color of highway section.").givePopup(v.getContext()).show();
                }
            }
        });
    }

    private void downloadStationsBasedOnHighway() {
        Iterator hItr = this.highwayList.iterator();
        while(hItr.hasNext()) {
            // Get a list of all stations from the API.
            Highway tHighway = (Highway)hItr.next();
            String nestedStationsUrl = tHighway.getNestedEntityUrl(Station.class);
            tHighway.fetchListForURLAsEntity(nestedStationsUrl, Station.class, this, RESULT_STATION_LIST);
        }
    }

    /**
     * get all the data from save bundle* 
     * @param savedInstanceState: bundle from the activities
     */
    private void getItemsFromSaveBundle(Bundle savedInstanceState) {
        //get the hashmap list of station before users rotate the phone
        listOfStationsBaseOnHighwayid = (HashMap<Integer, List<Station>>) savedInstanceState.get("a hashmap of list stations");

        //if users drag first marker, get the latlng back and re-create that marker
        if(savedInstanceState.get("lat of first marker") != null) {
            LatLng latLngOfFirstMarker = new LatLng((Double) savedInstanceState.get("lat of first marker"), (Double) savedInstanceState.get("lng of first marker"));
            firstMarker = mMap.addMarker(new MarkerOptions().position(latLngOfFirstMarker).
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).
                    draggable(true).title("Start"));
            startPoint = firstMarker.getPosition();
        }

        //if users drag second marker, get the latlng back and re-create that marker
        if(savedInstanceState.get("lat of second marker") != null) {
            LatLng latLngOfSecondMarker = new LatLng((Double) savedInstanceState.get("lat of second marker"), (Double) savedInstanceState.get("lng of second marker"));
            secondMarker = mMap.addMarker(new MarkerOptions().position(latLngOfSecondMarker).
                    icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).
                    draggable(true).title("End"));
            endPoint = secondMarker.getPosition();
        }
    }

    /**
     * * 
     * @param highwayID: highway number
     * @return the pair color based on pair high
     */
    private int generatePairhighWayColor(int highwayID) {
        int colorHighlightTheFreeWay = 0;
        if(highwayID == 9 || highwayID == 10 ){ //highway 210
            colorHighlightTheFreeWay = Color.rgb(255, 0, 0);
        }
        else if(highwayID == 5 || highwayID == 6 ){ //I-405
            colorHighlightTheFreeWay = Color.rgb(0,255,0);
        }
        else if(highwayID == 52 || highwayID == 53 ){ //highway 500
            colorHighlightTheFreeWay = Color.rgb(0,0,255);
        }
        else if(highwayID == 7 || highwayID == 8 ){ //I-84
            colorHighlightTheFreeWay = Color.rgb(0,0,0);
        }
        else if(highwayID == 11 || highwayID == 12 ){ //highway 26
            colorHighlightTheFreeWay = Color.rgb(255,0,255);
        }
        else if(highwayID == 50 || highwayID == 51 ){ //highway 14
            colorHighlightTheFreeWay = Color.rgb(255,0,0);
        }
        else if(highwayID == 3 || highwayID == 4 ){ //I-205 Oregon
            colorHighlightTheFreeWay = Color.rgb(128,0,255);
        }
        else if(highwayID == 501 || highwayID == 502 ){ //I-5 Washington
            colorHighlightTheFreeWay = Color.rgb(128,0,255);
        }
        else if(highwayID == 1 || highwayID == 2 ){ //I-5 Oregon
            colorHighlightTheFreeWay = Color.rgb(0,128,255);
        }
        else if(highwayID == 54 || highwayID == 55 ){ //I-205 Washington
            colorHighlightTheFreeWay = Color.rgb(0,255,0);
        }
        return colorHighlightTheFreeWay;
    }


    /**
     * The method that will be called when we get back the results of our API Query for highways.
     *
     * @param resWrap the result object.
     */
    public void onApiResult(APIResultWrapper resWrap) {
        Log.i(TAG, "Got API Result for " + resWrap.getCallbackTag() + ", which has " + resWrap.getExceptions().size() + " exceptions!");

        switch(resWrap.getCallbackTag()) {
            case RESULT_HIGHWAY_LIST:
                if(resWrap.getExceptions().size() > 0) {
                    String errStr = "";
                    Iterator eItr = resWrap.getExceptions().iterator();
                    while(eItr.hasNext()) {
                        // TODO: Handle specific exception differently?
                        Exception tEx = (Exception)eItr.next();
                        errStr += tEx.getMessage() + "\n\n";
                    }

                    new ErrorPopup("Error fetching highways...", errStr).givePopup(this).show();
                } else {
                    // Got a list of highways successfully!
                    // Set it into our local var, and go fetch stations for each highway.
                    Log.i(TAG, "Got back a list of "+resWrap.getListResponse().size()+" highways.");
                    this.highwayList = (ArrayList<Highway>)resWrap.getListResponse();
                    downloadStationsBasedOnHighway();
                }
                break;
            case RESULT_STATION_LIST:
                if(resWrap.getExceptions().size() > 0) {
                    String errStr = "";
                    Iterator eItr = resWrap.getExceptions().iterator();
                    while(eItr.hasNext()) {
                        // TODO: Handle specific exception differently?
                        Exception tEx = (Exception)eItr.next();
                        errStr += tEx.getMessage() + "\n\n";
                    }

                    new ErrorPopup("Error fetching stations for highway...", errStr).givePopup(this).show();
                } else {
                    Log.i(TAG, "Got back a list of "+resWrap.getListResponse().size()+" stations.");
                    List<Station> stationList = resWrap.getListResponse();
                    if(stationList.size() > 0) {
                        int highwayid = stationList.get(1).getHighwayId();

                        listOfStationsBaseOnHighwayid.put(highwayid, stationList);
                        int colorHighlightTheFreeWay = generatePairhighWayColor(highwayid);
                        drawHighway(stationList, colorHighlightTheFreeWay);
                    }
                }
                break;
        }

        activeAsyncs--;

        if(activeAsyncs == 0) {
            loadingDialog.dismiss();
        }
    }

    public void addActiveAsync(AsyncTask t) {
        activeAsyncs++;
    }

    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link # setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
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

            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.509534, -122.681081), 10.0f));
        }
    }

    /**
     * Save all appropriate fragment state.
     *
     * @param outState to write into the byte code
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //save the hashmap of list station
        outState.putSerializable("a hashmap of list stations", listOfStationsBaseOnHighwayid);

        //save the location of first marker
        if(firstMarker != null) {
            outState.putSerializable("lat of first marker", firstMarker.getPosition().latitude);
            outState.putSerializable("lng of first marker", firstMarker.getPosition().longitude);
        }

        //save the location of second marker
        if(secondMarker !=null) {
            outState.putSerializable("lat of second marker", secondMarker.getPosition().latitude);
            outState.putSerializable("lng of second marker", secondMarker.getPosition().longitude);
        }
    }

    /**
     * The function check if users tap a point close 200m to the freeway
     * then drag a markerOptions
     * @param point which is users tap on the map
     */
    private void drawMarker(LatLng point) {
        List<LatLng> drawnPoints = globalPoly.getPoints();
        if (PolyUtil.isLocationOnPath(point, drawnPoints, true, 200.0)) {
            if(firstMarker == null){
                firstMarker = mMap.addMarker(new MarkerOptions().position(point).
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)).
                        draggable(true).title("Start"));
                startPoint = firstMarker.getPosition();
            }
            else if(secondMarker == null ){
                secondMarker = mMap.addMarker(new MarkerOptions().position(point).
                        icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)).
                        draggable(true).title("End"));
                endPoint = secondMarker.getPosition();
            }
        }
    }

    /**
     * draw polyline for each station based on its list latlng*
     * @param stations: in its highway
     */
    public void drawHighway(List<Station> stations, int color){
        Iterator<Station> stationIter = stations.iterator();
        while(stationIter.hasNext()) {
            Station s = stationIter.next();
            if(s.getLatLngList().size() !=0) {
                List<LatLng> points = s.getLatLngList();
                if (points != null) {
                    globalPoly.addAll(points);
                    PolylineOptions polylineOptions = new PolylineOptions();
                    polylineOptions.addAll(points).width(10).color(color).geodesic(true);
                    mMap.addPolyline(polylineOptions);
                }
            }
        }
    }  
}