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
}
