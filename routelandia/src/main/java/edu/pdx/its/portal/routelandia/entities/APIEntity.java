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

package edu.pdx.its.portal.routelandia.entities;

import android.util.Log;

import edu.pdx.its.portal.routelandia.ApiFetcher;
import edu.pdx.its.portal.routelandia.AsyncResult;

/**
 * Created by joshproehl on 3/3/15.
 */
public abstract class APIEntity {
    private static final String TAG = "APIEntity<>";

    // Define the root URL we'll concatenate individual resources URL's to
    public static final String API_ROOT = "http://capstoneaa.cs.pdx.edu/api/";

    public abstract int getEntityId();




    /**
     * Goes to the API Endpoint for the entity and returns the full list of instances.
     *
     * @param klass The entity class.
     * @param <T> The type of the objects that we'll be returning. (Entity class)
     * @param ar An instance of AsyncResult to return the result to
     * @param callback_tag A string identifying this result, so that the handling method can differentiate between multiple types.
     * @return A list of objects of the given type, representing all of the results returned by the API.
     */
    public static <T> void fetchListForEntity(Class<T> klass, AsyncResult ar, String callback_tag) {
        String entityListUrl = API_ROOT + klass.getSimpleName().toLowerCase() + "s/";
        fetchListForURLAsEntity(entityListUrl, klass, ar, callback_tag);
    }
    public static <T> void fetchListForURLAsEntity(String url, Class<T> klass, AsyncResult ar, String callback_tag) {
        Log.i(TAG, "Preparing async fetch <"+klass.getSimpleName()+"> list with URL: "+url);
        new ApiFetcher<T>(ar, callback_tag, klass, APIResultWrapper.ResultType.RESULT_AS_LIST).execute(url);
    }


    /**
     * Goes to the API Endpoint for the entity and returns the instance with the given ID
     *
     * @param iid The Entity ID.
     * @param klass The entity class.
     * @param <T> The type of the objects that we'll be returning. (Entity class)
     * @return A list of objects of the given type, representing all of the results returned by the API.
     */
    public static <T> void fetchIdForEntity(int iid, Class<T> klass, AsyncResult ar, String callback_tag) {
        String entityUrl = API_ROOT + klass.getSimpleName().toLowerCase() + "s/" + iid + "/";
        fetchItemAtURLAsEntity(entityUrl, klass, ar, callback_tag);
    }
    public static <T> void fetchItemAtURLAsEntity(String url, Class<T> klass, AsyncResult ar, String callback_tag) {
        Log.i(TAG, "Preparing to fetch by ID with URL: "+url);

        new ApiFetcher<T>(ar, callback_tag, klass, APIResultWrapper.ResultType.RESULT_AS_OBJECT).execute(url);

        /*
        try{
            APIResultWrapper resWrap = new ApiFetcher(ar).execute(url).get();
            JSONObject res = resWrap.getParsedResponse();
            if(resWrap.getHttpStatus() != 200) {
                // Apparently our HTTP response contained an error, so we'll be bailing now...
                throw new APIException("Problem communicating with the server...", resWrap);
            }
            JSONObject resObj = (JSONObject)res.get("results");
            return klass.getConstructor(JSONObject.class).newInstance(resObj);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage());
            // TODO: This should get back to the UI probably!
        } catch (InterruptedException|ExecutionException e) {
            Log.e(TAG, e.getMessage());
            // TODO: Probably should do *something* eh?
        } catch (NoSuchMethodException|InstantiationException|IllegalAccessException|InvocationTargetException e) {
            Log.e(TAG, "Generics problem! "+e.toString());
            e.printStackTrace();
            // TODO: The app needs to shut down now...
        }
        return null; // Should never arrive...
        */
    }


    /**
     * Gets the specific component of the canonical URL for getting lists of this entity.
     * Result of this string should be appended to API_ROOT.
     * i.e. if called on a Highway, gets "highways/".
     * @return String representing the URL component
     */
    public String getEntityListUrlComponent() {
        return this.getClass().getSimpleName().toLowerCase() + "s/";
    }
    public String getEntityListUrl() {
        return API_ROOT + getEntityListUrlComponent();
    }


    /**
     * Gets the specific component of the canonical URL for this specific instance.
     * Result of this string should be appended to API_ROOT.
     * i.e. if called on Highway id#9, gets "highways/9/".
     * @return
     */
    public String getEntityUrlComponent() {
        return this.getClass().getSimpleName().toLowerCase() + "s/" + this.getEntityId() + "/";
    }
    public String getEntityUrl() {
        return API_ROOT + getEntityUrlComponent();
    }


    /**
     * Gets the specific component of the canonical URL for the given entity class nested inside
     * the specific instance of this entity.
     * i.e. if called on highway id#9 and given Station, gets "highways/9/stations/"
     *
     * @param klass The NESTED entity class
     * @param <T>
     * @return
     */
    public <T> String getNestedEntityUrlComponent(Class<T> klass) {
        return this.getEntityUrlComponent() + klass.getSimpleName().toLowerCase() + "s/";
    }
    public <T> String getNestedEntityUrl(Class<T> klass) {
        return API_ROOT + getNestedEntityUrlComponent(klass);
    }


}
