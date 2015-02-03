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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by loc on 1/24/15.
 */
public class DownloadTask extends AsyncTask<String, Void, String> {

    protected GoogleMap mMap;
    protected PolylineOptions globalPoly;

    /**
     * Constructor for DownloadTask class
     * @param mMap is a field Google Map in MapsActivity class
     * @param globalPoly is field polylineOptions in MapsActivity
     */
    public DownloadTask(GoogleMap mMap, PolylineOptions globalPoly) {
        this.mMap = mMap;
        this.globalPoly = globalPoly;
    }

    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param url The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPostExecute
     */
    @Override
    protected String doInBackground(String... url) {

        // For storing data from web service
        String data = "";

        try {
            // Fetching the data from web service
            data = downloadUrl(url[0]);
        } catch (Exception e) {
            Log.d("Background Task", e.toString());
        }
        return data;
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     * <p/>
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param result The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

        //Create ParserTask class to parse the JSON from the backend
        ParserTask parserTask = new ParserTask(mMap, globalPoly);

        // Invokes the thread for parsing the JSON data
        parserTask.execute(result);

    }

    /**
     * A method to download json data from url
     * @param strUrl is the URL API endpoint to download the highway information
     * @return the string json
     * @throws IOException
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream;
        HttpURLConnection urlConnection;

        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

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
            Log.d("Exception while downloading url", e.toString());
        }

        return data;
    }
}
