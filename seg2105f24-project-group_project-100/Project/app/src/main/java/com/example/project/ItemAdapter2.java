package com.example.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Locale;

import android.widget.ArrayAdapter;


public class ItemAdapter2 extends ArrayAdapter<Item> {
    private Context context;
    private ArrayList<Item> items;

    public ItemAdapter2(Context context, ArrayList<Item> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Inflate custom layout if not already done
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_view2, parent, false);
        }

        // Get current item
        Item currentItem = items.get(position);

        // Bind data to views
        TextView nameView = convertView.findViewById(R.id.item_name);
        TextView descriptionView = convertView.findViewById(R.id.item_description);
        TextView feeView = convertView.findViewById(R.id.item_fee);
        TextView categoryView = convertView.findViewById(R.id.item_category);
        TextView timePeriodView = convertView.findViewById(R.id.item_time_period);
        TextView lessorView = convertView.findViewById(R.id.item_lessor);

        nameView.setText(currentItem.getName());
        descriptionView.setText(currentItem.getDescription());
        feeView.setText("Fee: $" + String.format(Locale.getDefault(), "%.2f", (currentItem.getFee())));
        categoryView.setText("Category: " + (currentItem.getCategory() != null ? currentItem.getCategory().getCategorieName() : "N/A"));
        timePeriodView.setText("Time Period: " + currentItem.getTimePeriod());
        lessorView.setText("Lessor: " + (currentItem.getLessorName() != null ? currentItem.getLessorName() : "Unknown"));

        return convertView;
    }
}
