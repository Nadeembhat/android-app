package com.labourtoday.androidapp.contractor;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.labourtoday.androidapp.R;

import java.util.ArrayList;

public class ExperienceListAdapter extends BaseExpandableListAdapter {
    private Context ctx;
    private ArrayList<ExperienceListGroup> groups;
    private SharedPreferences settings;

    public ExperienceListAdapter(Context ctx, ArrayList<ExperienceListGroup> groups) {
        this.ctx = ctx;
        this.groups = groups;
        settings = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).getItem();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final ExperienceListGroup exp = (ExperienceListGroup) getGroup(groupPosition);
        final String expText = exp.getName();

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.exp_group_item, null);
        }

        TextView expTextView = (TextView) convertView.findViewById(R.id.exp_group);
        expTextView.setText(expText);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //if (convertView == null) {
            ExperienceListGroup grp = (ExperienceListGroup) getGroup(groupPosition);
            LayoutInflater infalInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            switch(grp.getName()) {
                case "General labour":
                    convertView = infalInflater.inflate(R.layout.exp_gen, null);
                    break;
                case "Carpentry":
                    convertView = infalInflater.inflate(R.layout.exp_car, null);
                    break;
                case "Concrete":
                    convertView = infalInflater.inflate(R.layout.exp_con, null);
                    break;
                case "Dry walling":
                    convertView = infalInflater.inflate(R.layout.exp_dry, null);
                    break;
                case "Painting":
                    convertView = infalInflater.inflate(R.layout.exp_paint, null);;
                    break;
                case "Landscaping":
                    convertView = infalInflater.inflate(R.layout.exp_land, null);
                    break;
                case "Machine operating":
                    convertView = infalInflater.inflate(R.layout.exp_mo, null);
                    break;
                case "Roofing":
                    convertView = infalInflater.inflate(R.layout.exp_roof, null);;
                    break;
                case "Brick laying":
                    convertView = infalInflater.inflate(R.layout.exp_brick, null);
                    break;
                case "Electrical":
                    convertView = infalInflater.inflate(R.layout.exp_elec, null);
                    break;
                case "Plumbing":
                    convertView = infalInflater.inflate(R.layout.exp_plumb, null);
                    break;
                default:
                    break;
            }
        //}
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
