package com.app.bimya.assignment.activities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.app.bimya.assignment.R;
import com.app.bimya.assignment.activities.adapters.MessageListAdapter;
import com.app.bimya.assignment.activities.interfaces.Connection;
import com.app.bimya.assignment.activities.models.Message;
import com.app.bimya.assignment.activities.utils.ConnectionManagerUtils;
import com.app.bimya.assignment.activities.utils.DividerItemDecoration;
import com.baoyz.widget.PullRefreshLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class MessageLisActivity extends AppCompatActivity {

    @InjectView(R.id.pullRefreshLayout)
    PullRefreshLayout pullRefreshLayout;
    @InjectView(R.id.info)
    protected TextView info;

    RecyclerView recyclerView;

    private static Handler handler;

    private ArrayList<Message> messageList = new ArrayList<Message>();
    MessageListAdapter messageListAdapter;
    private Gson gson;
    private Toast mToast;

    private int SET_VIEW = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        gson = new Gson();
        mToast = Toast.makeText(MessageLisActivity.this, "", Toast.LENGTH_LONG);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        getMessageList();
                    }
                }).start();
            }
        });

        handler = new Handler() {
            public void handleMessage(android.os.Message paramAnonymousMessage) {
                if (paramAnonymousMessage.what == SET_VIEW) {
                    pullRefreshLayout.setRefreshing(false);
                    populateList();
                }
            }
        };
    }

    public void populateList() {
        if(messageList.size()>0) {
            messageListAdapter = new MessageListAdapter(MessageLisActivity.this, messageList);

            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.addItemDecoration(new DividerItemDecoration(MessageLisActivity.this));
            recyclerView.setAdapter(messageListAdapter);
        }else
            info.setVisibility(View.VISIBLE);
    }

    public void getMessageList() {
        try {
            messageList.clear();
            if (isNetworkAvailable()) {
                messageList.clear();
                Connection cm = new ConnectionManagerUtils(MessageLisActivity.this);
                String resultJson = cm.httpGETRequest(
                        "message", "");
                final JSONArray messageJsonArray = new JSONArray(resultJson);
                messageList.clear();
                for (int i = 0; i < messageJsonArray.length(); i++) {
                    JSONObject c = messageJsonArray.getJSONObject(i);
                    Message message = gson.fromJson(c.toString(),
                            Message.class);
                    messageList.add(message);
                }
            } else {
                MessageLisActivity.this.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        makeToast("Internet not available");
                    }
                });
            }
        } catch (Exception e) {
        }
        android.os.Message msg = android.os.Message.obtain();
        msg.what = SET_VIEW;
        handler.sendMessage(msg);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) MessageLisActivity.this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null
                && activeNetworkInfo.isConnectedOrConnecting();
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
        pullRefreshLayout.setRefreshing(true);
        new Thread(new Runnable() {
            public void run() {
                getMessageList();
            }
        }).start();
    }

}
