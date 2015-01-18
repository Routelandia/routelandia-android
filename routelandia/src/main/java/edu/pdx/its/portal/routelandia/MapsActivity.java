package edu.pdx.its.portal.routelandia;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Location;
import android.app.Activity;


import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MapsActivity extends FragmentActivity implements
        OnMapClickListener , OnMapLongClickListener{

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private TextView mTapTextView;
    private Marker marker;
    private ArrayList<LatLng> arrayPoint = null;
    PolylineOptions polylineoptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
        arrayPoint = new ArrayList<LatLng>();
        //Nasim needs to cleanup
        //SupportMapFragment mf = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        // mMap = mf.getMap();
        //mMap.setMyLocationEnabed(true);
        //mMap.setOnMapClickListner(this);
        //mMap.setOnMapLongClickListner(this);

        /*googleMap.setOnMapClickListner(new GoogleMap.OnMapClickListener() {
            @overide
            public void onMapClick (LatLng latlng){
                mapFragment.addMarkerToMap(latlng);
            }
        });
        if(googleMap.getMyLocation() != null){
            double lat = googleMap.getMyLocation().getLatitude();

        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
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
            mMap.setOnMapClickListener((GoogleMap.OnMapClickListener) this);
            mMap.setOnMapLongClickListener((GoogleMap.OnMapLongClickListener) this);

            //some blue line I place on portland bridge
            Polyline line = mMap.addPolyline(new PolylineOptions()
                    .add(new LatLng(45.540716, -122.679678), new LatLng(45.535297, -122.686308))
                    .width(5)
                    .color(Color.CYAN)
                    .geodesic(true));
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(45.540716, -122.679678)).title("Marker"));
        // mMap.addMarker(new MarkerOptions().position(new LatLng(45.509534, -122.681081)).title("Marker"));
    }

    @Override
    public void onMapClick(LatLng point){
        //add marker
        MarkerOptions marker = new MarkerOptions();
        marker.position(point);
        mMap.addMarker(marker);
        //Draw line
        polylineoptions  = new PolylineOptions();
        arrayPoint.add(point);
        polylineoptions.addAll(arrayPoint);
        mMap.addPolyline(polylineoptions);

        //mTapTextView.setText("tapped, point=" + point);
    }

    @Override
    public void onMapLongClick(LatLng point){
        mMap.clear();
        arrayPoint.clear();
        //mTapTextView.setText("long pressed, point=" + point);
    }

    private String downloadURL(String strURL) throws IOException{
        String data = "";
        InputStream inputStream = null;
        HttpURLConnection connection = null;
        try{
            URL url = new URL(strURL);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            inputStream = connection.getInputStream();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();
            String line;

            while((line = bufferedReader.readLine()) != null){
                stringBuffer.append(line);
            }
            data = stringBuffer.toString();

            bufferedReader.close();

        }catch (Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally {
            inputStream.close();
            connection.disconnect();
        }
        return data;
    }


}