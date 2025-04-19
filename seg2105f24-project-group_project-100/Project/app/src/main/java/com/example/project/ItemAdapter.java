package com.example.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Locale;

import android.widget.ArrayAdapter;


public class ItemAdapter extends ArrayAdapter<Item> {
    private Context context;
    private ArrayList<Item> items;

    public ItemAdapter(Context context, ArrayList<Item> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate custom layout if not already done
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_view, parent, false);
        }

        // Get current item
        Item currentItem = items.get(position);

        // Bind data to views
        TextView nameView = convertView.findViewById(R.id.item_name);
        TextView descriptionView = convertView.findViewById(R.id.item_description);
        TextView feeView = convertView.findViewById(R.id.item_fee);
        TextView categoryView = convertView.findViewById(R.id.item_category);
        TextView timePeriodView = convertView.findViewById(R.id.item_time_period);
        TextView numberOfInterestedUsers = convertView.findViewById(R.id.number_of_interested_users);

        nameView.setText(currentItem.getName());
        descriptionView.setText(currentItem.getDescription());
        feeView.setText("Fee: $" + String.format(Locale.getDefault(), "%.2f", (currentItem.getFee())));
        categoryView.setText("Category: " + currentItem.getCategory().getCategorieName());
        timePeriodView.setText("Time Period: " + currentItem.getTimePeriod());
        numberOfInterestedUsers.setText("Number of interested users: " + currentItem.getNumberInterestedUsers());

        return convertView;
    }
}
