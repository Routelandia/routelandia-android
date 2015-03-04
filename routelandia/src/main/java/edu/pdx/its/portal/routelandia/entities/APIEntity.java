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
    // Define the root URL we'll concatenate individual resources URL's to
    public static final String API_ROOT = "http://capstoneaa.cs.pdx.edu/api/";


    /**
     * Goes to the API Endpoint for the entity and returns the full list of instances.
     *
     * @param klass The entity class.
     * @param <T> The type of the objects that we'll be returning. (Entity class)
     * @return A list of objects of the given type, representing all of the results returned by the API.
     */
    public static <T> List<T> fetchListForEntity(Class<T> klass) {
        String TAG = "APIEntity<" + klass.getSimpleName() + ">";
        List<T> retVal = new ArrayList<>();

        String entityListUrl = API_ROOT + klass.getSimpleName().toLowerCase() + "s/";
        Log.i(TAG, "Preparing to fetch list with URL: "+entityListUrl);

        try{
            JSONObject res = new ApiFetcher().execute(entityListUrl).get();
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
    public static <T> fetchIdForEntity(int iid, Class<T> klass) {
        String TAG = "APIEntity<" + klass.getSimpleName() + ">";

        String entityListUrl = API_ROOT + klass.getSimpleName().toLowerCase() + "s/" + iid + "/";
        Log.i(TAG, "Preparing to fetch by ID with URL: "+entityListUrl);

        try{
            JSONObject res = new ApiFetcher().execute(entityListUrl).get();
            JSONObject resObj = (JSONObject)res.get("results");
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
}
