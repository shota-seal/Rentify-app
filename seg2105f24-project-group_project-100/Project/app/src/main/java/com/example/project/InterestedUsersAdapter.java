package com.example.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
public class InterestedUsersAdapter extends android.widget.BaseAdapter {

    private Context context;
    private ArrayList<String> users;
    private OnUserActionListener listener;

    public InterestedUsersAdapter(Context context, ArrayList<String> users, OnUserActionListener listener) {
        this.context = context;
        this.users = users;
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.interested_user, parent, false);
        }

        String username = users.get(position);

        TextView userNameView = convertView.findViewById(R.id.user_name);
        Button acceptButton = convertView.findViewById(R.id.btn_accept);
        Button denyButton = convertView.findViewById(R.id.btn_deny);

        userNameView.setText(username);

        acceptButton.setOnClickListener(view -> listener.onUserAction(username, true));
        denyButton.setOnClickListener(view -> listener.onUserAction(username, false));

        return convertView;
    }

    public interface OnUserActionListener {
        void onUserAction(String username, boolean isAccept);
    }
}