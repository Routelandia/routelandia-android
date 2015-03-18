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

package edu.pdx.its.portal.routelandia.entities;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.pdx.its.portal.routelandia.ApiFetcher;

/**
 * Created by loc on 2/6/15.
 */
public class Station extends APIEntity implements Serializable {
    private static final String TAG = "Station Entity";
    protected int stationid;
    protected int highwayid;
    protected int linkedListPosition;
    List<LatLng> latLngList = new ArrayList<>();

    // TODO: This really should be more dynamic.. But we're going to go with it for now.
    public static String getListUrlNestedInHighway(int highwayid) {
        return "highways/"+highwayid+"/stations/";
    }

    /**
     * * constructor with argument stationid
     * @param stationid: station number in the highway
     */
    public Station(int stationid) {
        this.stationid = stationid;
    }

    /**
     * constructor with argument stationid and linkedListPosition* 
     * @param stationid
     * @param linkedListPosition
     */
    public Station(int stationid, int linkedListPosition) {
        this.stationid = stationid;
        this.linkedListPosition = linkedListPosition;
    }

    public Station(JSONObject json) throws JSONException {
        this.stationid = json.getInt("stationid");
        this.highwayid = json.getInt("highwayid");

        if (!json.isNull("geojson_raw")) {
            JSONObject geojsonRaw = json.getJSONObject("geojson_raw");

            //Create json array from coordinates
            JSONArray coordinates = (JSONArray) geojsonRaw.get("coordinates");

            //for each json array coordinates, create latlng and
            //add it to the list latlng of its station
            for (int j = 0; j < coordinates.length(); j++) {
                double latitude = Double.parseDouble(((JSONArray) coordinates.get(j)).get(1).toString());
                double longtitude = Double.parseDouble(((JSONArray) coordinates.get(j)).get(0).toString());
                this.addLatLng(new LatLng(latitude, longtitude));
            }
        }
    }


    public int getEntityId() {
        return stationid;
    }

    /**
     * * add new latlng location to the list of latlng in station
     * @param latLng: a new location
     */
    public void addLatLng(LatLng latLng) {
        latLngList.add(latLng);
    }

    /**
     * * 
     * @return the list of latlng in the station
     */
    public List<LatLng> getLatLngList() {
        return latLngList;
    }

    /**
     * concatenate the value in class into string* 
     * @return  the content in class bt String literal
     */
    public String toString(){
        String temp = "station: " + stationid + " has postion at  " + String.valueOf(linkedListPosition) + '\n';
        
        if (latLngList.size() == 0){
            temp += "no geojson_raw" + '\n';
        }
        else {
            for (int i = 0; i < latLngList.size(); i++) {
                temp += latLngList.get(i).latitude + " " + latLngList.get(i).longitude + '\n';
            }
        }
        return temp;
    }

    /**
     * implement write out LatLng object*
     * @param out
     * @throws IOException
     */
    private void writeObject(ObjectOutputStream out) throws IOException {        
        for (int i = 0; i <latLngList.size() ; i++) {
            out.writeDouble(latLngList.get(i).latitude);
            out.writeDouble(latLngList.get(i).longitude);    
        }
        
    }

    /**
     * implement get back the latlng object after serializable*
     * @param in
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        latLngList.add(new LatLng(in.readDouble(), in.readDouble()));
    }

    public int getHighwayId() { return this.highwayid; }
}
