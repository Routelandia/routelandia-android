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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import edu.pdx.its.portal.routelandia.ApiFetcher;

/**
 * This class accepts the raw JSON returned by an API endpoint and gives us a way to handle some of
 * the additional data about the request, and easily access the data we need!
 *
 * Created by joshproehl on 3/3/15.
 */
public class APIResultWrapper<T> {
    private static final String TAG = "APIResultWrapper";

    private String callback_tag;
    private String rawResponse;
    private JSONObject parsedResponse;
    private Class<T> target_class;
    private ArrayList<T> listResponse;
    private T objectResponse;
    private int httpStatus;

    public static enum ResultType {
        RESULT_AS_LIST, RESULT_AS_OBJECT
    }


    private ResultType result_type;

    private ArrayList<Exception> exceptions;

    public APIResultWrapper(ResultType rt, Class<T> klass) {
        this.result_type = rt;
        this.target_class = klass;
        exceptions = new ArrayList<>();
    }

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


    //region Generated getters and setters
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

    public int getHttpStatus() {
        return httpStatus;
    }
    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public ArrayList<Exception> getExceptions() {
        return exceptions;
    }
    public void addException(Exception e) {
        this.exceptions.add(e);
    }

    public String getCallbackTag() {
        return callback_tag;
    }
    public void setCallbackTag(String t) {
        this.callback_tag = t;
    }

    public ResultType getResultType() {
        return result_type;
    }

    public void setListResponse(ArrayList<T> listResponse) {
        this.listResponse = listResponse;
    }

    public ArrayList<T> getListResponse() {
        return listResponse;
    }

    public T getObjectResponse() {
        return objectResponse;
    }

    public void setObjectResponse(T objectResponse) {
        this.objectResponse = objectResponse;
    }
    //endregion
}
