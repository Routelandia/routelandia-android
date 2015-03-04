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

import org.json.JSONObject;

/**
 * A wrapper object containing something that can be passed to the ApiPoster
 *
 * Created by joshproehl on 3/4/15.
 */
public class APIPostWrapper {
    private String fullUrl;
    private JSONObject postObj;

    public APIPostWrapper(String u, JSONObject o){
        this.fullUrl = u;
        this.postObj = o;
    }

    public String getFullUrl() { return fullUrl; }
    public JSONObject getPostObj() { return postObj; }
}
