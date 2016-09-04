package com.app.bimya.assignment.activities.interfaces;

/**
 * Created by Bimya on 9/2/2016.
 */
public interface Connection {

    String httpGETRequest(String url, String message);
    String httpDELETERequest(String url, String message);

}
