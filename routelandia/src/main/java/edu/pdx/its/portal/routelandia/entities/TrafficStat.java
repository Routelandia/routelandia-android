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

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.pdx.its.portal.routelandia.ApiPoster;
import edu.pdx.its.portal.routelandia.AsyncResult;

/**
 * Created by locle on 2/27/15.
 */
public class TrafficStat extends APIEntity implements Parcelable {
    private static final String TAG = "TrafficStat Entity";
    protected int hour;
    protected int minutes;
    protected double speed;
    protected double travelTime;
    protected double accuracy;


    protected double distance;


    public TrafficStat(int hour, int minutes, double speed, double travelTime, double accuracy) {
        this.hour = hour;
        this.minutes = minutes;
        this.speed = speed;
        this.travelTime = travelTime;
        this.accuracy = accuracy;
    }

    public TrafficStat(JSONObject jsonObject) throws JSONException {
        //get hour, minute, speed, and travel time from each json obj
        this.hour    = Integer.parseInt(jsonObject.getString("hour"));
        this.minutes = Integer.parseInt(jsonObject.getString("minute"));
        this.speed   = 0.0;

        if (!jsonObject.isNull("speed")) {
            this.speed = Double.parseDouble(jsonObject.getString("speed"));
        }
        this.travelTime = 0.0;
        if (!jsonObject.isNull("traveltime")) {
            this.travelTime = Double.parseDouble(jsonObject.getString("traveltime"));
        }
        this.accuracy = 0.0;
        if(!jsonObject.isNull("accuracy")) {
            this.accuracy = Double.parseDouble(jsonObject.getString("accuracy"));
        }
        this.distance = 0.0;
        if(!jsonObject.isNull("distance")) {
            this.distance = Double.parseDouble(jsonObject.getString("distance"));
        }
    }
    
    public TrafficStat(Parcel parcel){
        this.hour = parcel.readInt();
        this.minutes = parcel.readInt();
        this.speed = parcel.readDouble();
        this.travelTime = parcel.readDouble();
        this.accuracy = parcel.readDouble();
        this.distance = parcel.readDouble();
    }

    public int getEntityId() {
        return -1; //This one is a bit special since it's not really an actual entity...
    }

    /* Simple accessors */
    public int getHour() {
        return hour;
    }
    public int getMinutes() {
        return minutes;
    }
    public double getSpeed() {
        return speed;
    }
    public double getTravelTime() {
        return travelTime;
    }
    public double getAccuracy() {
        return accuracy;
    }
    public double getDistance() {
        return distance;
    }

    public String toString(){
        return "hour: " + hour + " minute: " + minutes + " speed: " + speed + " traveltime: " + travelTime + " accuracy: " + accuracy + " distance: "+distance+"\n";
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's
     * marshalled representation.
     *
     * @return a bitmask indicating the set of special object types marshalled
     * by the Parcelable.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(hour);
        dest.writeInt(minutes);
        dest.writeDouble(speed);
        dest.writeDouble(travelTime);
        dest.writeDouble(accuracy);
        dest.writeDouble(distance);
    }


    @SuppressWarnings("unused")
    public static final Creator<TrafficStat> CREATOR = new Creator<TrafficStat>() {
        @Override
        public TrafficStat createFromParcel(Parcel source) {
            return new TrafficStat(source);
        }

        @Override
        public TrafficStat[] newArray(int size) {
            return new TrafficStat[size];
        }
    };


    /**
     * Create a JSONObject to be used in the POST request to fetch traffic stats.
     *
     * @param startPoint A LatLng representing the start-point of the route to get stats for
     * @param endPoint A LatLng representing the ending point of the route to get stats for.
     * @param midpoint A String representing the time (24-hour format) to use as the midpoint for the stat query.
     * @param weekday A String ("Monday", ...) representing the day of the week to get statistics for
     * @return A JSONObject suitable for POSTing to the trafficstats endpoint
     */
    private static JSONObject createPostObject(LatLng startPoint, LatLng endPoint, String midpoint, String weekday) {
        JSONObject retVal = new JSONObject();
        try {
            JSONObject startJsonObject = new JSONObject();
            startJsonObject.put("lng", startPoint.longitude);
            startJsonObject.put("lat", startPoint.latitude);

            JSONObject endJsonObject = new JSONObject();
            endJsonObject.put("lng", endPoint.longitude);
            endJsonObject.put("lat", endPoint.latitude);

            JSONObject time = new JSONObject();
            time.put("midpoint", midpoint);
            time.put("weekday", weekday);

            retVal.put("startpt", startJsonObject);
            retVal.put("endpt", endJsonObject);
            retVal.put("time", time);
        } catch (JSONException e) {
            Log.e(TAG, "Could not create object to POST!");
            e.printStackTrace();
        }

        return retVal;
    }


    /**
     * Take the parameters, make a POST request to get statistics, and give back the list of results.
     *
     * @param sp LatLng representing the starting point of the query
     * @param ep LatLng representing the ending point of the query
     * @param mid String representing the midpoint time (24 hour) of the query. (i.e. '17:15')
     * @param weekday String representing the weekday to query for. (i.e. 'Thursday')
     * @return A list of TrafficStat objects representing the API Results.
     */
    public static void getStatsResultListFor(LatLng sp, LatLng ep, String mid, String weekday, AsyncResult ar) {
        // TODO: This should not be hardcoded here, but inflected... Sadly, static methods and all...
        String postURL = API_ROOT+"trafficstats/";
        JSONObject postObj = createPostObject(sp, ep, mid, weekday);
        APIPostWrapper postReq = new APIPostWrapper(postURL, postObj);

        Log.i(TAG, "POSTing  to "+postURL+" : "+postObj.toString());
        ar.addActiveAsync(new ApiPoster<TrafficStat>(ar, "RESULT_TRAFFIC_STAT", TrafficStat.class, APIResultWrapper.ResultType.RESULT_AS_LIST).execute(postReq));
    }
}
