package com.app.bimya.assignment.activities.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.app.bimya.assignment.R;
import com.app.bimya.assignment.activities.interfaces.Connection;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Bimya on 9/2/2016.
 */
public class ConnectionManagerUtils implements Connection {

    private ProgressDialog dialog;
    private Context context;
    private Toast mToast;

    public ConnectionManagerUtils(Context context){
        this.context = context;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null
                && activeNetworkInfo.isConnectedOrConnecting();
    }

    public void showToast(final String toast) {
        ((Activity) context).runOnUiThread(new Runnable() {
            public void run() {

                mToast = Toast.makeText(context, "", Toast.LENGTH_LONG);
                try {
                    mToast.setText(toast);
                    mToast.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public String httpGETRequest(String url, final String message) {
            String result = "";
            if (message.length() > 1)
                ((Activity) context).runOnUiThread(new Runnable() {
                    public void run() {
                        dialog = ProgressDialog.show(context, "", message, true);
                        dialog.setCancelable(true);
                    }
                });
            OkHttpClient httpClient = new OkHttpClient();

            if (!url.contains("http"))
                url = context.getString(R.string.base_url) + url;
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = null;
            if (isNetworkAvailable()) {
                try {
                    response = httpClient.newCall(request).execute();
                    result = response.body().string();
                } catch (IOException e) {
                    showToast("Connection timed out");
                    result = "";
                }
            } else {
                showToast("No Internet Connection!");
            }
        return result;
    }

    @Override
    public String httpDELETERequest(String url,final String message) {
        String result = "";
        if (message.length() > 1)
            ((Activity) context).runOnUiThread(new Runnable() {
                public void run() {
                    dialog = ProgressDialog.show(context, "", message, true);
                    dialog.setCancelable(true);
                }
            });
        OkHttpClient httpClient = new OkHttpClient();

        if (!url.contains("http"))
            url = context.getString(R.string.base_url) + url;
        Request request = new Request.Builder()
                .url(url).delete()
                .build();
        Response response = null;
        if (isNetworkAvailable()) {
            try {
                response = httpClient.newCall(request).execute();
                result = response.body().string();
            } catch (IOException e) {
                showToast("Connection timed out");
                result = "";
            }
        } else {
            showToast("No Internet Connection!");
        }
        return result;
    }
}
