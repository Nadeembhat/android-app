package com.labourtoday.androidapp.contractor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.labourtoday.androidapp.R;

public class GridAdapter extends BaseAdapter {
    private Context context;
    private final String[] typeWorkers;

    public GridAdapter(Context context, String[] values) {
        this.context = context;
        this.typeWorkers = values;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View gridView;

        if (convertView == null) {
            gridView = inflater.inflate(R.layout.grid_layout, null);

            // set value into textview
            TextView textView = (TextView) gridView.findViewById(R.id.grid_item_label);
            textView.setText(typeWorkers[position]);

            // set image based on selected text
            ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);

            String worker = typeWorkers[position];

            if (worker.equals("General Labour")) {
                imageView.setImageResource(R.drawable.gen_labour);
            } else if (worker.equals("Carpenter")) {
                imageView.setImageResource(R.drawable.carpenter);
            } else if (worker.equals("Concrete")) {
                imageView.setImageResource(R.drawable.concrete);
            } else if (worker.equals("Landscaper")) {
                imageView.setImageResource(R.drawable.landscape);
            } else if (worker.equals("Painter")) {
                imageView.setImageResource(R.drawable.paint);
            } else if (worker.equals("Drywaller")) {
                imageView.setImageResource(R.drawable.drywall);
            } else if (worker.equals("Roofer")) {
                imageView.setImageResource(R.drawable.roof);
            } else if (worker.equals("Machine Operator")) {
                imageView.setImageResource(R.drawable.machine);
            } else if (worker.equals("Plumber")) {
                imageView.setImageResource(R.drawable.plumbing);
            } else if (worker.equals("Electrician")) {
                imageView.setImageResource(R.drawable.electrician);
            } else {
                imageView.setImageResource(R.drawable.concrete);
            }

        } else {
            gridView = convertView;
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
