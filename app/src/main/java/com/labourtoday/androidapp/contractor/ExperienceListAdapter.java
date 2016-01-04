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
            LayoutInflater inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            switch(grp.getName()) {
                case "General labour":
                    convertView = inflater.inflate(R.layout.exp_gen, parent, false);
                    break;
                case "Carpentry":
                    convertView = inflater.inflate(R.layout.exp_car, parent, false);
                    break;
                case "Concrete":
                    convertView = inflater.inflate(R.layout.exp_con, parent, false);
                    break;
                case "Dry walling":
                    convertView = inflater.inflate(R.layout.exp_dry, parent, false);
                    break;
                case "Painting":
                    convertView = inflater.inflate(R.layout.exp_paint, parent, false);
                    break;
                case "Landscaping":
                    convertView = inflater.inflate(R.layout.exp_land, parent, false);
                    break;
                case "Machine operating":
                    convertView = inflater.inflate(R.layout.exp_mo, parent, false);
                    break;
                case "Roofing":
                    convertView = inflater.inflate(R.layout.exp_roof, parent, false);
                    break;
                case "Brick laying":
                    convertView = inflater.inflate(R.layout.exp_brick, parent, false);
                    break;
                case "Electrical":
                    convertView = inflater.inflate(R.layout.exp_elec, parent, false);
                    break;
                case "Plumbing":
                    convertView = inflater.inflate(R.layout.exp_plumb, parent, false);
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
