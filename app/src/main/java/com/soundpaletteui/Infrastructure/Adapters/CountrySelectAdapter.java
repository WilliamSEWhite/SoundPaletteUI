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

// Adapter for displaying location options (like countries) in a dropdown list when a user creates or edits their account.
public class CountrySelectAdapter extends ArrayAdapter<LocationModel> {
    private Context context;
    private ArrayList<LocationModel> values;

    public CountrySelectAdapter(Context context, int textViewResourceId,
                               ArrayList<LocationModel> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    @Override
    // Returns the number of location options
    public int getCount(){
        return values.size();
    }

    @Override
    // Returns a location at a given position
    public LocationModel getItem(int position){
        return values.get(position);
    }

    @Override
    // Returns the ID of a location (here, just the position)
    public long getItemId(int position){
        return position;
    }

    // Returns all the location values
    public ArrayList<LocationModel> getValues() {
        return values;
    }

    @Override
    // Displays the selected item inside the spinner (collapsed view)
    public View getView(int position, View convertView, ViewGroup parent) {
        View currentItemView = convertView;

        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_country_dropdown, parent, false);
        }

        TextView label = currentItemView.findViewById(R.id.countryName);
        label.setText(values.get(position).getLocationName());
        return currentItemView;
    }

    @Override
    // Displays each option in the dropdown list (when the spinner is expanded)
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        View currentItemView = convertView;

        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_country_dropdown_option, parent, false);
        }

        TextView label = currentItemView.findViewById(R.id.countryName);
        label.setText(values.get(position).getLocationName());

        return currentItemView;
    }
}
