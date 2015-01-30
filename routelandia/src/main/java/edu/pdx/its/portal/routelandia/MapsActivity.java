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
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.app.DialogFragment;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;


import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    ArrayList<LatLng> mMarkerPoints;
    ArrayList<LatLng> startEnd = new ArrayList<LatLng>();
    protected PolylineOptions globalPoly = new PolylineOptions();
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

           // String url = "http://capstoneaa.cs.pdx.edu/api/stations.json";
            String url = "http://capstoneaa.cs.pdx.edu/api/highways.json";
            DownloadTask downloadTask = new DownloadTask(mMap, globalPoly);

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);

        }

        mMap.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                    drawMarker(point);
            }
        });

        Button button3 = (Button) findViewById(R.id.button3);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                Log.i("clicks", "you clicked start");
                Intent i = new Intent(
                        MapsActivity.this,
                        DatePickUp.class);
                startActivity(i);
            }
        });

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
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.509534, -122.681081), 10.0f));
        }
    }

    private void drawMarker(LatLng point) {
        //isLocationOnPath(LatLng point, java.util.List<LatLng> polyline, boolean geodesic)
        //Same as isLocationOnPath(LatLng, List, boolean, double) with a default tolerance of 0.1 meters.
        List<LatLng> drawnPoints = globalPoly.getPoints();
        if (PolyUtil.isLocationOnPath(point, drawnPoints, true, 200.0)) {
            MarkerOptions marker = new MarkerOptions();
            List<MarkerOptions> markerList = new ArrayList<MarkerOptions>();
            // Setting the position of the marker
            marker.position(point);
            mMarkerPoints.add(point);
            if (mMarkerPoints.size() == 1) {
                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                LatLng startPoint = marker.getPosition();
                startEnd.add(startPoint);
                markerList.add(marker);
                mMap.addMarker(marker);
            } else if (mMarkerPoints.size() == 2) {
                marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                LatLng endPoint = marker.getPosition();
                startEnd.add(endPoint);
                markerList.add(marker);
                //mMarkerPoints.clear();
                mMap.addMarker(marker);
            }
        }
    }
}
