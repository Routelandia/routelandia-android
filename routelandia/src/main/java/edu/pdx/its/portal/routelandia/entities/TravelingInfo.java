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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by locle on 2/27/15.
 */
public class TravelingInfo extends APIEntity implements Parcelable {
    private static final String TAG = "TravelingInfo Entity";
    protected int hour;
    protected int minutes;
    protected double speed;
    protected double travelTime;
    protected double accuracy;


    public TravelingInfo(int hour, int minutes, double speed, double travelTime, double accuracy) {
        this.hour = hour;
        this.minutes = minutes;
        this.speed = speed;
        this.travelTime = travelTime;
        this.accuracy = accuracy;
    }

    public TravelingInfo(JSONObject jsonObject) throws JSONException {
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

    }
    
    public TravelingInfo(Parcel parcel){
        this.hour = parcel.readInt();
        this.minutes = parcel.readInt();
        this.speed = parcel.readDouble();
        this.travelTime = parcel.readDouble();
        this.accuracy = parcel.readDouble();
        
    }

    /* Implement ApiEntity methods */
    public String getListUrlComponent(){
        return API_ROOT+"trafficstats/";
    }
    public String getItemUrlComponent(int itemid){
        return API_ROOT+"";
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

    public String toString(){
        return "hour: " + hour + " minute: " + minutes + " speed: " + speed + " traveltime: " + travelTime + " accuracy: " + accuracy + "\n";
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
    }
    
    @SuppressWarnings("unused")
    public static final Creator<TravelingInfo> CREATOR = new Creator<TravelingInfo>() {
        @Override
        public TravelingInfo createFromParcel(Parcel source) {
            return new TravelingInfo(source);
        }

        @Override
        public TravelingInfo[] newArray(int size) {
            return new TravelingInfo[size];
        }
    };
}
