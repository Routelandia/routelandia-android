package edu.pdx.its.portal.routelandia;

import edu.pdx.its.portal.routelandia.entities.APIResultWrapper;

/**
 * Something that can be passed to an APIFetcher() to be used for the callback.
 *
 * Created by joshproehl on 3/10/15.
 */
public interface AsyncResult {
    void onApiResult(APIResultWrapper result);
}
