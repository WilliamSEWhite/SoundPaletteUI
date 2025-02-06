package com.soundpaletteui.Infrastructure.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.soundpaletteui.Infrastructure.Models.LocationModel;
import com.soundpaletteui.R;

import java.util.ArrayList;

public class CountrySelectAdapter extends ArrayAdapter<LocationModel> {
    private Context context;
    // Your custom values for the spinner (User)
    private ArrayList<LocationModel> values;

    public CountrySelectAdapter(Context context, int textViewResourceId,
                               ArrayList<LocationModel> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public int getCount(){
        return values.size();
    }

    @Override
    public LocationModel getItem(int position){
        return values.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    public ArrayList<LocationModel> getValues() {
        return values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View currentItemView = convertView;

        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_country_dropdown, parent, false);
        }

        TextView label = currentItemView.findViewById(R.id.countryName);
        label.setText(values.get(position).LocationName);
        return currentItemView;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        View currentItemView = convertView;

        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_country_dropdown_option, parent, false);
        }

        TextView label = currentItemView.findViewById(R.id.countryName);
        label.setText(values.get(position).LocationName);

        return currentItemView;
    }
}
