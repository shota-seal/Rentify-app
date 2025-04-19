package com.example.project;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CategorieList extends ArrayAdapter<Categorie> {
    private Activity context;
    List<Categorie> categories;

    public CategorieList(Activity context, List<Categorie> categories) {
        super(context, R.layout.layout_categorie_list, categories);
        this.context = context;
        this.categories = categories;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View listViewItem = inflater.inflate(R.layout.layout_categorie_list, null, true);

        TextView textViewName = (TextView) listViewItem.findViewById(R.id.textViewName);
        TextView textViewDescription = (TextView) listViewItem.findViewById(R.id.textViewDescription);

        Categorie categorie = categories.get(position);
        textViewName.setText(categorie.getCategorieName());
        textViewDescription.setText(String.valueOf(categorie.getDescription()));
        return listViewItem;
    }
}