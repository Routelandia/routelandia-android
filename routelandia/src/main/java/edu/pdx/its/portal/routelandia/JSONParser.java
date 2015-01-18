package edu.pdx.its.portal.routelandia;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by loc on 1/17/15.
 */
public class JSONParser {
    public List<HashMap<Integer, List<LatLng>>> parse (JSONObject jsonObject){
        List<HashMap<Integer, List<LatLng>>> segment = new ArrayList<>();
        try{
            int stationId = (int) jsonObject.get("stationid");
            JSONObject segmentRaw = jsonObject.getJSONObject("segment_raw");
            List<List> coordinates = (List<List>) segmentRaw.get("coordinates");
            List<LatLng> listLatLong = new ArrayList<>();
            for (int i = 0; i <coordinates.size() ; i++) {
                double latitute = (double) coordinates.get(i).get(1);
                double longtitute = (double) coordinates.get(i).get(0);
                LatLng latlng = new LatLng(latitute, longtitute);
                listLatLong.add(latlng);
            }
            HashMap<Integer, List<LatLng>> highway =  new HashMap<>();
            highway.put(stationId, listLatLong);
            segment.add(highway);
        }
        catch (JSONException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return segment;
    }
}
