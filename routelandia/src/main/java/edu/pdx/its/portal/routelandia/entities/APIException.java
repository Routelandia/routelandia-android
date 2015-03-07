package edu.pdx.its.portal.routelandia.entities;

/**
 * Represents some sort of API Exception that occurred
 *
 * Created by joshproehl on 3/5/15.
 */
public class APIException extends Exception {
    APIResultWrapper res;

    public APIException(String message, APIResultWrapper res) {
        super(message);
        this.res = res;
    }

    public APIResultWrapper getResultWrapper() {
        return res;
    }

}
