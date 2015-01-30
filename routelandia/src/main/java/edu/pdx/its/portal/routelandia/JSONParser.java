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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by loc on 1/17/15.
 */
public class JSONParser {
//    public HashMap<Integer, List<LatLng>> parse(JSONArray jsArray) {
//        HashMap<Integer, List<LatLng>> segment = new HashMap<>();
//
//        try {
//            for (int i = 0; i < jsArray.length(); i++) {
//                JSONObject jsonObject = (JSONObject) jsArray.get(i);
//
//                int stationId = (int) jsonObject.get("stationid");
//
//                if (!jsonObject.isNull("geojson_raw")) {
//                    JSONObject segment_raw = (JSONObject) jsonObject.get("geojson_raw");
//
//                    JSONArray jsonArray = segment_raw.getJSONArray("coordinates");
//
//                    List<LatLng> listLatLong = new ArrayList<LatLng>();
//
//                    for (int j = 0; j < jsonArray.length(); j++) {
//                        double latitude = Double.parseDouble(((JSONArray) jsonArray.get(j)).get(1).toString());
//                        double longtitude = Double.parseDouble(((JSONArray) jsonArray.get(j)).get(0).toString());
//                        listLatLong.add(new LatLng(latitude, longtitude));
//                    }
//
//                    segment.put(stationId, listLatLong);
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return segment;
//    }

    public List<Highway> parse(JSONArray jsonArray){
        List<Highway> highwayList =  new ArrayList<>();
        try{
            for (int i = 0; i <jsonArray.length() ; i++) {
                JSONObject jsonObject = (JSONObject) jsonArray.get(i);
                int highwayid = jsonObject.getInt("highwayid");
                String highwayname = jsonObject.getString("highwayname");
                highwayList.add(i, new Highway(highwayname, highwayid));
                JSONObject fullGeoJson = (JSONObject) jsonObject.get("fullGeoJson");
                JSONArray coordinates = (JSONArray) fullGeoJson.get("coordinates");
                for (int j = 0; j <coordinates.length() ; j++) {
                    double latitude = Double.parseDouble(((JSONArray) coordinates.get(j)).get(1).toString());
                    double longtitude = Double.parseDouble(((JSONArray) coordinates.get(j)).get(0).toString());
                    highwayList.get(i).addLatLng(new LatLng(latitude, longtitude));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return highwayList;
    }
}
