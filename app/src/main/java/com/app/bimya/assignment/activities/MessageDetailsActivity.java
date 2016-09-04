package com.app.bimya.assignment.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.bimya.assignment.R;
import com.app.bimya.assignment.activities.interfaces.Connection;
import com.app.bimya.assignment.activities.models.Message;
import com.app.bimya.assignment.activities.utils.ConnectionManagerUtils;
import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MessageDetailsActivity extends AppCompatActivity {

    @InjectView(R.id.toolbar)
    protected Toolbar toolbar;
    //    @InjectView(R.id.pullRefreshLayout)
//    protected PullRefreshLayout pullRefreshLayout;
    @InjectView(R.id.subject)
    protected TextView tvSubject;
    @InjectView(R.id.preview)
    protected TextView tvPreview;
    @InjectView(R.id.body)
    protected TextView tvBody;
    @InjectView(R.id.participants)
    protected TextView participants;

    private Intent intent;
    private int messageId;
    private String participantsString;

    private JSONArray participantsJsonArray;

    private Gson gson;
    private Message message;
    private Toast mToast;
    private Handler handler;

    private int SET_VIEW = 1, NAVIGATE_TO_MESSAGELIST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_details);
        ButterKnife.inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        gson = new Gson();
        mToast = Toast.makeText(MessageDetailsActivity.this, "", Toast.LENGTH_LONG);
        intent = getIntent();
        messageId = intent.getIntExtra("messageId", 0);
//        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
//
//            @Override
//            public void onRefresh() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                getMessageDetails();
            }
        }).start();
//            }
//        });

        handler = new Handler() {
            public void handleMessage(android.os.Message paramAnonymousMessage) {
                if (paramAnonymousMessage.what == SET_VIEW) {
                    //   pullRefreshLayout.setRefreshing(false);
                    setView();
                } else if (paramAnonymousMessage.what == NAVIGATE_TO_MESSAGELIST) {
                    Intent intent = new Intent(MessageDetailsActivity.this, MessageLisActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        };

    }

    public void setView() {
        tvSubject.setText(message.getSubject());
        tvPreview.setText(message.getPreview());
        tvBody.setText(message.getBody());

        participants.setText(participantsString);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.action_remove) {
            new Thread(new Runnable() {

                @Override
                public void run() {
                    deleteMessage();
                }
            }).start();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void getMessageDetails() {
        try {
            if (isNetworkAvailable()) {
                Connection cm = new ConnectionManagerUtils(MessageDetailsActivity.this);
                String resultJson = cm.httpGETRequest(
                        "message/" + messageId, "");
                final JSONObject messageJson = new JSONObject(resultJson);
                message = new Message();
                message.setBody(messageJson.getString("body"));
                message.setId(messageJson.getInt("id"));
                message.setPreview(messageJson.getString("preview"));
                message.setSubject(messageJson.getString("subject"));
                message.setIsRead(messageJson.getBoolean("isRead"));
                message.setIsStarred(messageJson.getBoolean("isStarred"));
                participantsJsonArray = messageJson.getJSONArray("participants");
                participantsString = "Participants : ";
                for (int i = 0; i < participantsJsonArray.length(); i++) {
                    JSONObject c = participantsJsonArray.getJSONObject(i);
                    participantsString = participantsString + "," + c.getString("name");

                }
//                message = gson.fromJson(messageJson.toString(),
//                l        Message.class);
            } else {
                MessageDetailsActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        makeToast("Internet not available");
                    }
                });
            }
        } catch (Exception e) {
            int i = 0;
        }
        android.os.Message msg = android.os.Message.obtain();
        msg.what = SET_VIEW;
        handler.sendMessage(msg);
    }

    public void deleteMessage() {
        try {
            if (isNetworkAvailable()) {
                Connection cm = new ConnectionManagerUtils(MessageDetailsActivity.this);
                String resultJson = cm.httpDELETERequest(
                        "message/" + messageId, "");
                final JSONObject messageJson = new JSONObject(resultJson);

            } else {
                MessageDetailsActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        makeToast("Internet not available");
                    }
                });
            }
        } catch (Exception e) {
            int i = 0;
        }
        android.os.Message msg = android.os.Message.obtain();
        msg.what = NAVIGATE_TO_MESSAGELIST;
        handler.sendMessage(msg);
    }

    public void makeToast(String toast) {
        try {
            mToast.setText(toast);
            mToast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        //  pullRefreshLayout.setRefreshing(true);
        new Thread(new Runnable() {
            public void run() {
                getMessageDetails();
            }
        }).start();
    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) MessageDetailsActivity.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null
                && activeNetworkInfo.isConnectedOrConnecting();
    }

}
