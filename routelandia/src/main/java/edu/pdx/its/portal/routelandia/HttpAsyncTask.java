package edu.pdx.its.portal.routelandia;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by loc on 1/30/15.
 */
public class HttpAsyncTask extends AsyncTask<String, Void, JSONArray>{

    protected LatLng startPoint ;
    protected LatLng endPoint ;
    protected String midpoint ; //backend call time field is midpoint
    protected String weekday ;// DayPickSelectedListener.weekDay;

    final String url = "http://capstoneaa.cs.pdx.edu/api/trafficstats";

    public HttpAsyncTask(LatLng startPoint, LatLng endPoint, String midpoint, String weekday) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.midpoint = midpoint;
        this.weekday = weekday;
    }

    @Override
    protected JSONArray doInBackground(String... params) {
        System.out.println("jsoncreated " + makingJson());
        return postJsonObject(url, makingJson());
    }
//    protected void onPostExecute(JSONArray result) {
//        super.onPostExecute(result);
//
//    }

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

    public JSONArray postJsonObject(String url, JSONObject jsonObject){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL

            //http://capstoneaa.cs.pdx.edu/api/trafficstats
            HttpPost httpPost = new HttpPost(url);


            // 4. convert JSONObject to JSON to String

             String json = jsonObject.toString();

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            se.setContentType("application/json;charset=UTF-8");
            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE,"application/json;charset=UTF-8"));

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
//            httpPost.setHeader("Accept", "application/json");
//            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            result = EntityUtils.toString(httpResponse.getEntity());
            Log.i("resulr", result);

//            // 9. receive response as inputStream
//            inputStream = httpResponse.getEntity().getContent();
//
////            Log.i("input stream", inputStream.toString());
//            // 10. convert inputstream to string
//            if(inputStream != null)
//                result = convertInputStreamToString(inputStream);
//            else
//                result = "Did not work!";
//            Log.i("result", result);
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        JSONArray jsonArray = null;
        JSONObject jsonObjectRespone = null;
        try {
            Object json = new JSONTokener(result).nextValue();
            if(json instanceof JSONObject){
                jsonObjectRespone = new JSONObject(result);
//                jsonArray.put(jsonObjectRespone);
            }
            else if (json instanceof JSONArray){
                jsonArray = new JSONArray(result);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.i("final", jsonArray.toString());

//        JSONArray jsonArray = null;
//        try {
//            jsonArray = new JSONArray(result);
//            Log.i("json response", jsonArray.toString());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
        // 11. return result
        return jsonArray;
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
