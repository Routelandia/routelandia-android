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
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import edu.pdx.its.portal.routelandia.entities.APIException;
import edu.pdx.its.portal.routelandia.entities.APIPostWrapper;
import edu.pdx.its.portal.routelandia.entities.APIResultWrapper;
import edu.pdx.its.portal.routelandia.entities.TrafficStat;

/**
 * Takes an API Postable and returns the result of the POST operation.
 *
 * Created by loc on 1/30/15.
 */
public class ApiPoster<T> extends AsyncTask<APIPostWrapper, Void, APIResultWrapper>{
    private final String TAG = "APIPoster";

    private AsyncResult delegate;
    private String callback_tag;
    private Class<T> target_class;
    private APIResultWrapper.ResultType result_type;

    public ApiPoster(AsyncResult d, String callback_tag, Class<T> klass, APIResultWrapper.ResultType rt) {
        this.delegate = d;
        this.callback_tag = callback_tag;
        this.target_class = klass;
        this.result_type = rt;
    }

    @Override
    protected APIResultWrapper doInBackground(APIPostWrapper... params) {
        // Foolishly assume that only the first param matters...
        APIResultWrapper retVal = new APIResultWrapper<T>(result_type, target_class);
        postJsonObjectToUrl(params[0].getFullUrl(), params[0].getPostObj(), retVal);

        ArrayList<TrafficStat> objArr = new ArrayList<>();
        try {
            if(retVal.getHttpStatus() != 200) {
                Log.e(TAG, "API Returned non-200, throwing error!");
                Log.e(TAG, retVal.getParsedResponse().toString());
                retVal.addException(new APIException("Error on POST", retVal));
            }
            JSONObject parsedRawRes = retVal.getParsedResponse();
            JSONArray jResult = parsedRawRes.getJSONArray("results");
            Log.i(TAG, "Got results array: "+jResult);
            // Now loop through all the results and make the list
            if (jResult == null) {
                // No items were found? Sounds suspicious, but I guess we're done.
                Log.i("RESULT", "Apparently nothing was in the results array...");
            } else {
                try {
                    for (int i = 0; i < jResult.length(); i++) {
                        //Create a JSONObject and use it to construct a TravelingInfo to add to the list
                        JSONObject jsonObject = (JSONObject) jResult.get(i);
                        objArr.add(new TrafficStat(jsonObject));
                    }
                } catch (JSONException e) {
                    // TODO: Should we bubble this up or fail out if this happens?
                    Log.e(TAG, "Ignored a specific result due to JSON Exception!");
                }
            }
        } catch(JSONException je) {
            // Going to log a message, but not abort the app... We'll just pretend there were no results.
            // This should never happen, as the parser should ensure that the results field exists
            Log.e(TAG, "Could not parse result out of JSON array");
        }
        retVal.setListResponse(objArr);
        return retVal;
    }

    @Override
    protected void onPostExecute(APIResultWrapper result) {
        delegate.onApiResult(result);
    }


    public JSONObject postJsonObjectToUrl(String url, JSONObject jsonObject, APIResultWrapper retVal){
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

            // Make sure we've got a good response from the API.
            int status =  httpResponse.getStatusLine().getStatusCode();
            retVal.setHttpStatus(status);

            result = EntityUtils.toString(httpResponse.getEntity());
            //Log.i("RAW HTTP RESULT", result);
            retVal.setRawResponse(result);

        } catch (Exception e) {
            Log.e("InputStream", e.getLocalizedMessage());
        }

        try {
            Object json = new JSONTokener(result).nextValue();
            if(json instanceof JSONObject){
                retVal.setParsedResponse((JSONObject)json);
                return (JSONObject)json;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // If we got this far, we got something very wrong...
        Log.e(TAG, "Didn't get a valid JSON response!");
        return null;
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
