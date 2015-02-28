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

/**
 * Created by locle on 2/27/15.
 */
public class TravelingInfo {
    protected int hour;
    protected int minutes;
    protected double speed;
    protected double travelTime;


    public TravelingInfo(int hour, int minutes, double speed, double travelTime) {
        this.hour = hour;
        this.minutes = minutes;
        this.speed = speed;
        this.travelTime = travelTime;
    }

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

    public String toString(){
        return "hour: " + hour + " minute: " + minutes + " speed: " + speed + " traveltime: " + travelTime +'\n';
    }
}
