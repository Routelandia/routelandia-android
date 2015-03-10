package edu.pdx.its.portal.routelandia;

import android.app.AlertDialog;
import android.content.Context;

/**
 * Created by John on 3/7/2015.
 */
public class ErrorPopup {
    String eTitle;
    String eMessage;

    public ErrorPopup(String title, String message) {
        eTitle = title;
        eMessage = message;
    }

    public AlertDialog.Builder givePopup(Context context){
        AlertDialog.Builder messageBox = new AlertDialog.Builder(context);
        messageBox.setTitle(eTitle);
        messageBox.setMessage(eMessage);
        messageBox.setCancelable(false);
        messageBox.setNeutralButton("OK", null);
        return messageBox;
    }
}
