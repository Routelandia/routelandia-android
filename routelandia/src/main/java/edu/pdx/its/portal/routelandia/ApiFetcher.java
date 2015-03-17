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

import android.util.Log;

import edu.pdx.its.portal.routelandia.entities.*;
import android.os.AsyncTask;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * This class is designed to be the one-stop-shop for getting JSON results back from the API.
 *
 * Created by joshproehl on 3/2/15.
 */
public class ApiFetcher<T> extends AsyncTask<String, Integer, APIResultWrapper> {
    private final String TAG = "ApiFetcher";

    private AsyncResult delegate;
    private String callback_tag;
    private Class<T> target_class;


    public APIResultWrapper.ResultType fetch_type;



    public ApiFetcher(AsyncResult d, String callback_tag, Class<T> klass, APIResultWrapper.ResultType rt) {
        this.delegate = d;
        this.callback_tag = callback_tag;
        this.target_class = klass;
        this.fetch_type = rt;
    }


    @Override
    protected APIResultWrapper doInBackground(String... params) {
        APIResultWrapper retVal = new APIResultWrapper<T>(fetch_type, target_class);
        retVal.setCallbackTag(callback_tag);

        try {
            // Fetch the HTTP Result and parse it into the JSON to be returned.
            String rawResult = fetchRawResult(params[0], retVal);
            retVal.setParsedResponse(parseRawResult(rawResult));

            if(fetch_type == APIResultWrapper.ResultType.RESULT_AS_LIST) {
                // This is a list response type, so we're going to convert the parsedResponse into
                // an array of objects...
                ArrayList<T> objArr = new ArrayList<>();

                try {
                    if (retVal.getHttpStatus() != 200) {
                        // Apparently our HTTP response contained an error, so we'll be bailing now...

                        retVal.addException(new APIException("Problem communicating with the server...", retVal));
                    }
                    JSONObject res = retVal.getParsedResponse();
                    JSONArray resArray = (JSONArray) res.get("results");
                    for (int i = 0; i < resArray.length(); i++) {
                        //Create a entity object from the JSONObject for each array index and add it to the list
                        JSONObject tObj = (JSONObject) resArray.get(i);
                        objArr.add(target_class.getConstructor(JSONObject.class).newInstance(tObj));
                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.getMessage());
                    // TODO: This should get back to the UI probably!
                //} catch (InterruptedException | ExecutionException e) {
                //    Log.e(TAG, e.getMessage());
                //    // TODO: Probably should do *something* eh?
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    Log.e(TAG, "Generics problem! " + e.toString());
                    e.printStackTrace();
                    // TODO: The app needs to shut down now...
                }

                retVal.setListResponse(objArr);
            } else if(fetch_type == APIResultWrapper.ResultType.RESULT_AS_OBJECT ) {
                // Things

            }

        } catch (IOException e) {
            Log.e(TAG, callback_tag + ": Error doing background API Fetch: " + e.toString());
            retVal.addException(e);
        } catch (JSONException e) {
            Log.e(TAG, callback_tag + ": Could not parse raw result into JSON..." + e.toString());
            retVal.addException(e);
        }
        return retVal;
    }

    @Override
    protected void onPostExecute(APIResultWrapper result) {
        delegate.onApiResult(result);
    }


    /**
     * Go get a string response from the given URL.
     *
     * @param stringURL The URL to go and fetch!
     * @return a string containing the returned result of the HTTP request.
     * @throws IOException
     */
    private String fetchRawResult(String stringURL, APIResultWrapper retVal) throws IOException {
        String data = "";
        InputStream iStream;
        HttpURLConnection urlConnection;

        Log.i(TAG, "Fetching result from " + stringURL);

        try {
            URL url = new URL(stringURL);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Make sure we've got a good response from the API.
            int status = urlConnection.getResponseCode();
            retVal.setHttpStatus(status);

            // Reading data from url
            iStream = urlConnection.getInputStream();

            // Create bufferedReader from input
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(iStream));

            StringBuilder stringBuilder = new StringBuilder();

            String line;

            //append all line from buffered Reader into string builder
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            //convert the string builder into string and update its for data
            data = stringBuilder.toString();

            //close the buffered reader
            bufferedReader.close();

        } catch (Exception e) {
            Log.e(TAG, callback_tag + ": Error in getting raw HTTP result: "+e.toString());
            retVal.addException(e);
        }

        retVal.setRawResponse(data);
        return data;
    }


    /**
     * Return the result as a JSON Object, ensuring that it always has a "results" field.
     *
     * (Some of this logic won't be necessary eventually, as the API is going to standardize the output,
     * but in the meantime it's very handy to assume that the resulting JSON object will always have
     * a specific form.)
     *
     * @param jsonIn the string value of the JSON we're going to parse.
     * @return JSONObject representing the parsed result...
     */
    private JSONObject parseRawResult(String jsonIn) throws JSONException {
        Object json = new JSONTokener(jsonIn).nextValue();

        if (json instanceof JSONArray) {
            Log.i(TAG, callback_tag + ": Found a returned JSONArray");
            // This use case is to support legacy API results which returned a raw array,
            // and morph them into the new structure which specifically has a "result" field
            // in the returned object holding the array.
            JSONArray resArray = (JSONArray) json;
            JSONObject newRes = new JSONObject();
            newRes.put("results", resArray);
            return newRes;
        } else {
            JSONObject resObj = (JSONObject) json;
            if(resObj.has("results")) {
                Log.i(TAG, callback_tag + ": Found a JSONObject to work with, and it already has results!");
                return (JSONObject) json;
            } else {
                // Apparently the API handed us an object that didn't have a results value,
                // so we're going to take their whole result and shove it in a result value
                Log.i(TAG, callback_tag + ": Found an old-style JSONObject, manipulating to have results field.");
                JSONObject newRes = new JSONObject();
                newRes.put("results", (JSONObject)json);
                return newRes;
            }
        }
    }
}
