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

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;
import edu.pdx.its.portal.routelandia.entities.*;

/**
 * Created by loc on 2/6/15.
 */
public class ParserListofHighway extends AsyncTask<String, Integer, List<Highway>>{
    protected List<Highway> highwayList = new ArrayList<>();


    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute}
     * by the caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates
     * on the UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected List<Highway> doInBackground(String... params) {
        try {
            JSONArray jObject = new JSONArray(params[0]);
            JSONParser parser = new JSONParser();

            // Starts parsing data
            highwayList.addAll(parser.parseListOfHighWay(jObject));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return highwayList;
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The
     * specified result is the value returned by {@link #doInBackground}.</p>
     * <p/>
     * <p>This method won't be invoked if the task was cancelled.</p>
     *
     * @param highways The result of the operation computed by {@link #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
//    @Override
//    protected void onPostExecute(List<Highway> highways) {
//        super.onPostExecute(highways);
//    }
}
