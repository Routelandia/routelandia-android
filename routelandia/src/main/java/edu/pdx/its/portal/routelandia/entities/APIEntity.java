package edu.pdx.its.portal.routelandia.entities;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import edu.pdx.its.portal.routelandia.ApiFetcher;

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
     * @return A list of objects of the given type, representing all of the results returned by the API.
     */
    public static <T> List<T> fetchListForEntity(Class<T> klass) {
        String entityListUrl = API_ROOT + klass.getSimpleName().toLowerCase() + "s/";
        return fetchListForURLAsEntity(entityListUrl, klass);
    }
    public static <T> List<T> fetchListForURLAsEntity(String url, Class<T> klass) {
        Log.i(TAG, "Preparing to fetch <"+klass.getSimpleName()+"> list with URL: "+url);
        List<T> retVal = new ArrayList<>();

        try{
            JSONObject res = ((APIResultWrapper)new ApiFetcher().execute(url).get()).getParsedResponse();
            JSONArray resArray = (JSONArray)res.get("results");
            for (int i = 0; i <resArray.length() ; i++) {
                //Create a entity object from the JSONObject for each array index and add it to the list
                JSONObject tObj = (JSONObject)resArray.get(i);
                retVal.add(klass.getConstructor(JSONObject.class).newInstance(tObj));
            }
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
        return retVal;
    }


    /**
     * Goes to the API Endpoint for the entity and returns the instance with the given ID
     *
     * @param iid The Entity ID.
     * @param klass The entity class.
     * @param <T> The type of the objects that we'll be returning. (Entity class)
     * @return A list of objects of the given type, representing all of the results returned by the API.
     */
    public static <T> T fetchIdForEntity(int iid, Class<T> klass) {
        String entityUrl = API_ROOT + klass.getSimpleName().toLowerCase() + "s/" + iid + "/";
        return fetchItemAtURLAsEntity(entityUrl, klass);
    }
    public static <T> T fetchItemAtURLAsEntity(String url, Class<T> klass) {
        Log.i(TAG, "Preparing to fetch by ID with URL: "+url);

        try{
            JSONObject res = ((APIResultWrapper)new ApiFetcher().execute(url).get()).getParsedResponse();
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
    }


    /**
     * Gets the specific component of the canonical URL for getting lists of this entity.
     * Result of this string should be appended to API_ROOT.
     * i.e. if called on a Highway, gets "highways/".
     * @return String representing the URL component
     */
    public String getEntityListUrlComponent() {
        return this.getClass().getSimpleName().toLowerCase() + "/";
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
