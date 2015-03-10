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
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
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
import java.util.List;

import edu.pdx.its.portal.routelandia.entities.*;

public class MapsActivity extends FragmentActivity {
    private final String TAG = "Maps Activity";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    protected PolylineOptions globalPoly = new PolylineOptions();
    protected List<Highway> highwayList = new ArrayList<>();
    protected HashMap<Integer, List<Station>> listOfStationsBaseOnHighwayid = new HashMap<>();
    protected LatLng startPoint;
    protected LatLng endPoint;
    protected Marker firstMarker;
    protected Marker secondMarker;

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

            try {
                // Get a list of all highways from the API
                highwayList = APIEntity.fetchListForEntity(Highway.class);
            } catch(APIException e) {
                Log.e(TAG, "CAUGHT API EXCEPTION, status code: ("+e.getResultWrapper().getHttpStatus()+"), message: "+e.getMessage());

                new ErrorPopup("Server Error", "Encountered an unrecoverable error trying to fetch highway data: \n\n" + e.getMessage()).givePopup(this).show();

                return; // Bail out of the function.
            }

            if(savedInstanceState != null){
                getItemsFromSaveBundle(savedInstanceState);

            }
            else {
                downloadStationsBasedOnHighway();
            }
            for (int i =0; i<highwayList.size(); i++){
                List<Station> stations = listOfStationsBaseOnHighwayid.get(highwayList.get(i).getHighwayid());
                int colorHighlightTheFreeWay = generatePairhighWayColor(highwayList.get(i).getHighwayid());
                drawHighway(stations, colorHighlightTheFreeWay);
            }
        }
        usersDragTheMarkers();

        goToDatePickUp();
        
        removeMarker();

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
                    new ErrorPopup("Error", "Please select a start and an end point along the same highway section.").givePopup(v.getContext()).show();
                }
                else{
                    Toast.makeText(MapsActivity.this, "Please pick 2 points close to the highway!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void downloadStationsBasedOnHighway() {
        for (int i = 0; i < this.highwayList.size(); i++) {
            // Get a list of all stations from the API.
            Highway tHighway = this.highwayList.get(i);
            String nestedStationsUrl = tHighway.getNestedEntityUrl(Station.class);
            List<Station> stationList = null;
            try {
                stationList= tHighway.fetchListForURLAsEntity(nestedStationsUrl, Station.class);
            } catch (APIException e) {
                Log.e(TAG, "API ERROR: could not fetch stations for highway " + tHighway.getHighwayid());

                new ErrorPopup("Server Error", "Failed to fetch data for highway "+tHighway.getHighwayid()+".\n\n"+e.getMessage()).givePopup(this).show();
            }
            // And add them to the list!
            listOfStationsBaseOnHighwayid.put(tHighway.getHighwayid(), stationList);
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
        if(highwayID == 9 || highwayID == 10 ){
            colorHighlightTheFreeWay = Color.rgb(255, 0, 0);
        }
        else if(highwayID == 5 || highwayID == 6 ){
            colorHighlightTheFreeWay = Color.rgb(0,255,0);
        }
        else if(highwayID == 52 || highwayID == 53 ){
            colorHighlightTheFreeWay = Color.rgb(0,0,255);
        }
        else if(highwayID == 7 || highwayID == 8 ){
            colorHighlightTheFreeWay = Color.rgb(0,0,0);
        }
        else if(highwayID == 11 || highwayID == 12 ){
            colorHighlightTheFreeWay = Color.rgb(255,0,255);
        }
        else if(highwayID == 50 || highwayID == 51 ){
            colorHighlightTheFreeWay = Color.rgb(0,255,255);
        }
        else if(highwayID == 3 || highwayID == 4 ){
            colorHighlightTheFreeWay = Color.rgb(255,0,128);
        }
        else if(highwayID == 501 || highwayID == 502 ){
            colorHighlightTheFreeWay = Color.rgb(128,0,255);
        }
        else if(highwayID == 1 || highwayID == 2 ){
            colorHighlightTheFreeWay = Color.rgb(0,128,255);
        }
        else if(highwayID == 54 || highwayID == 5 ){
            colorHighlightTheFreeWay = Color.rgb(0,255,128);
        }
        return colorHighlightTheFreeWay;
    }

    /**
     * Create a clear button so users can re-drag the markers
     */
    private void removeMarker() {
        Button clearButton = (Button) findViewById(R.id.button2);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        });
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

        for (int i=0; i<stations.size(); i++){
            if(stations.get(i).getLatLngList().size() !=0) {
                List<LatLng> points = stations.get(i).getLatLngList();
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