package com.labourtoday.androidapp.contractor;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.labourtoday.androidapp.R;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {
    private Context context;
    private final String[] typeWorkers;
    private final ArrayList<String> selectedTypes;

    public GridAdapter(Context context, String[] values, ArrayList<String> selectedTypes) {
        this.context = context;
        this.typeWorkers = values;
        this.selectedTypes = selectedTypes;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;
        String worker = typeWorkers[position];
        if (convertView == null) {
            gridView = inflater.inflate(R.layout.grid_layout, null);

            // set value into textview
            TextView textView = (TextView) gridView.findViewById(R.id.grid_item_label);
            textView.setText(typeWorkers[position]);

            // set image based on selected text
            ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);

            switch (worker) {
                case "General Labour":
                    imageView.setImageResource(R.drawable.gen_labour);
                    break;
                case "Carpenter":
                    imageView.setImageResource(R.drawable.carpenter);
                    break;
                case "Concrete":
                    imageView.setImageResource(R.drawable.concrete);
                    break;
                case "Landscaper":
                    imageView.setImageResource(R.drawable.landscape);
                    break;
                case "Painter":
                    imageView.setImageResource(R.drawable.paint);
                    break;
                case "Drywaller":
                    imageView.setImageResource(R.drawable.drywall);
                    break;
                case "Roofer":
                    imageView.setImageResource(R.drawable.roof);
                    break;
                case "Machine Operator":
                    imageView.setImageResource(R.drawable.machine);
                    break;
                case "Plumber":
                    imageView.setImageResource(R.drawable.plumbing);
                    break;
                case "Electrician":
                    imageView.setImageResource(R.drawable.electrician);
                    break;
            }

        } else {
            gridView = convertView;
        }

        if (selectedTypes.contains(worker)) {
            gridView.setBackgroundColor(Color.parseColor("#B2DFDB"));
        }

        return gridView;
    }

    @Override
    public int getCount() {
        return typeWorkers.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
