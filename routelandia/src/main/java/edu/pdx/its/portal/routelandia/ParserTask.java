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

import android.graphics.Color;
import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import edu.pdx.its.portal.routelandia.entities.*;

/**
 * Created by loc on 1/24/15.
 */
public class ParserTask extends AsyncTask<String, Integer, List<Station>> {

//    protected GoogleMap mMap;
//    protected PolylineOptions globalPoly;
//
//    public ParserTask(GoogleMap mMap, PolylineOptions globalPoly) {
//        this.mMap = mMap;
//        this.globalPoly = globalPoly;
//    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param jsonData The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected List<Station> doInBackground(String... jsonData) {

        List<Station> routes =  new ArrayList<>();

        try {
            JSONArray jObject = new JSONArray(jsonData[0]);
            JSONParser parser = new JSONParser();

            // Starts parsing data
            routes = parser.parseStationList(jObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     * <p/>
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param highwayList The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(List<Station> highwayList) {
        PolylineOptions line = new PolylineOptions();
        for (int i = 0; i < highwayList.size(); i++) {
            List<LatLng> points = highwayList.get(i).getLatLngList();
            if (points != null) {
                PolylineOptions lineOptions = new PolylineOptions();

//                globalPoly.addAll(points);
                line.addAll(points);
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.GREEN);
                lineOptions.geodesic(true);

//                mMap.addPolyline(lineOptions);
            }
        }

    }
}

