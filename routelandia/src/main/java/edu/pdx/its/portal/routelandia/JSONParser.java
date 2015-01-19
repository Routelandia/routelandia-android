package edu.pdx.its.portal.routelandia;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by loc on 1/17/15.
 */
public class JSONParser {
    public HashMap<Integer, List<LatLng>> parse (JSONArray jsArray){
        HashMap<Integer, List<LatLng>> segment = new HashMap<>();

        try{
            for (int i = 0; i < jsArray.length(); i++) {
                JSONObject jsonObject = (JSONObject) jsArray.get(i);

                int stationId = (int) jsonObject.get("stationid");

                if (!jsonObject.isNull("geojson_raw")) {
                    JSONObject segment_raw = (JSONObject) jsonObject.get("geojson_raw");

                    JSONArray jsonArray = segment_raw.getJSONArray("coordinates");

                    List<LatLng> listLatLong = new ArrayList<LatLng>();

                    for (int j = 0; j < jsonArray.length(); j++) {
                        double latitude = Double.parseDouble(((JSONArray) jsonArray.get(j)).get(1).toString());
                        double longtitude = Double.parseDouble(((JSONArray) jsonArray.get(j)).get(0).toString());
                        listLatLong.add(new LatLng(latitude, longtitude));
                    }

                    segment.put(stationId, listLatLong);
                }

//                JSONObject segmentRaw = jsArray.getJSONObject("segment_raw");

//            List<List> coordinates = (List<List>) segmentRaw.get("coordinates");
//            for (int i = 0; i <coordinates.size() ; i++) {
//                double latitute = (double) coordinates.get(i).get(1);
//                double longtitute = (double) coordinates.get(i).get(0);
//                LatLng latlng = new LatLng(latitute, longtitute);
//                listLatLong.add(latlng);
//            }

//                JSONArray coordinates = (JSONArray) segmentRaw.get("coordinates");
//                for (int j = 0; j < coordinates.length(); j++) {
//                    double latitude = Double.parseDouble(((JSONArray) coordinates.get(j)).get(1).toString());
//                    double longtitude = Double.parseDouble(((JSONArray) coordinates.get(j)).get(0).toString());
//                    LatLng latLng = new LatLng(latitude, longtitude);
//                    listLatLong.add(latLng);
//                }
//
//                HashMap<Integer, List<LatLng>> highway = new HashMap<>();
//                highway.put(stationId, listLatLong);
//                segment.add(highway);
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return segment;
    }
}
