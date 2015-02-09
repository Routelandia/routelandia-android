package edu.pdx.its.portal.routelandia;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by loc on 1/30/15.
 */
public class HttpAsyncTask extends AsyncTask<String, Void, JSONObject>{

    //private LatLng startPoint = new LatLng(45.509534, -122.681081);
    //private LatLng endPoint =  new LatLng(45.509534, -122.681081);
    private LatLng startPoint = MapsActivity.startPoint;
    private LatLng endPoint =  MapsActivity.endPoint;
    String midpoint = "17:30";
    String weekday = DayPickSelectedListener.weekDay;

    String url = "http://capstoneaa.cs.pdx.edu/api/trafficstats";

    @Override
    protected JSONObject doInBackground(String... params) {
        return postJsonObject(url, makingJson());
    }
    protected void onPostExecute(JSONObject result) {
        super.onPostExecute(result);

    }

    public JSONObject makingJson() {

        JSONObject jsonObject = new JSONObject();
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

            jsonObject.put("startpt", startJsonObject);
            jsonObject.put("endpt", endJsonObject);
            jsonObject.put("time", time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject postJsonObject(String url, JSONObject jsonObject){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL

            //http://capstoneaa.cs.pdx.edu/api/trafficstats
            HttpPost httpPost = new HttpPost(url);

            System.out.println("url: " + url);

            // 4. convert JSONObject to JSON to String

             String json = jsonObject.toString();

            System.out.println("my json : " + json);
            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        JSONObject json = null;
        try {
            JSONArray jsonArray = new JSONArray(result);
            json = jsonArray.getJSONObject(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // 11. return result
        System.out.println("json from the response: " + json);
        return json;
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }
}
