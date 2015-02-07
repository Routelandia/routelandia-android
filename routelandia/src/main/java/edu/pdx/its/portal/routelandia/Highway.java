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

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by loc on 1/24/15.
 */
public class Highway {
    private String name;
    private int highwayid;
    private List<LatLng> latLngList = new ArrayList<>();

    public Highway(String name, int highwayid) {
        this.name = name;
        this.highwayid = highwayid;
    }

    public void addLatLng(LatLng latLng) {
        latLngList.add(latLng);
    }

    public List<LatLng> getLatLngList() {
        return latLngList;
    }
    
    public String toString(){
        return "Highway " + name + " has id : " + highwayid;
    }

    public int getHighwayid() {
        return highwayid;
    }
}
