package com.app.bimya.assignment.activities.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.bimya.assignment.R;
import com.app.bimya.assignment.activities.MessageDetailsActivity;
import com.app.bimya.assignment.activities.models.Message;

import java.util.ArrayList;

/**
 * Created by Bimya on 9/2/2016.
 */
public class MessageListAdapter extends RecyclerView.Adapter<MessageListAdapter.MyViewHolder> {

    private ArrayList<Message> messageList;
    private Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView subject, preview;

        public MyViewHolder(View view) {
            super(view);
            subject = (TextView) view.findViewById(R.id.subject);
            preview = (TextView) view.findViewById(R.id.preview);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message message = messageList.get(getPosition());
                    Intent intent = new Intent(context, MessageDetailsActivity.class);
                    intent.putExtra("messageId",message.getId());
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }


    public MessageListAdapter(Context context,ArrayList<
            Message> moviesList) {
        this.messageList = moviesList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_message_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.subject.setText(message.getSubject());
        holder.preview.setText(message.getPreview());
        if(message.getIsRead()){
            holder.itemView.setBackgroundColor(Color.LTGRAY);
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }
}