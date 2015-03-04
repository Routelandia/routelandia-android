package edu.pdx.its.portal.routelandia.entities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class accepts the raw JSON returned by an API endpoint and gives us a way to handle some of
 * the additional data about the request, and easily access the data we need!
 *
 * Created by joshproehl on 3/3/15.
 */
public class APIResultWrapper {
    private static final String TAG = "APIResultWrapper";

    private String rawResponse;
    private JSONObject parsedResponse;


    /**
     * Gets the "results" object out of the raw JSON.
     *
     * Our fetcher/parser ensures that this will exist.
     *
     * @return The array of result objects for this API Response
     */
    public JSONArray getResults() throws JSONException {
        return (JSONArray)this.parsedResponse.get("results");
    }


    // Generated getters and setters below
    public String getRawResponse() {
        return rawResponse;
    }

    public void setRawResponse(String rawResponse) {
        this.rawResponse = rawResponse;
    }

    public JSONObject getParsedResponse() {
        return parsedResponse;
    }

    public void setParsedResponse(JSONObject parsedResponse) {
        this.parsedResponse = parsedResponse;
    }
}
