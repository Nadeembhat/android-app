package com.labourtoday.androidapp.labourer;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LabourerJobsAdapter extends BaseExpandableListAdapter{

    private Activity context;
    private ArrayList<String> jobs;

    public LabourerJobsAdapter(Activity ctx, ArrayList<String> jobs) {
        this.context = ctx;
        this.jobs = jobs;
    }

    @Override
    public int getGroupCount() {
        return jobs.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return jobs.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
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
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_item, parent);
        }
        TextView type, date, time, city, prov, wage;
        type = (TextView) convertView.findViewById(R.id.jobType);
        date = (TextView) convertView.findViewById(R.id.jobDate);
        time = (TextView) convertView.findViewById(R.id.jobTime);
        city = (TextView) convertView.findViewById(R.id.jobCity);
        prov = (TextView) convertView.findViewById(R.id.jobProvince);
        wage = (TextView) convertView.findViewById(R.id.jobWage);

        String job = jobs.get(groupPosition);
        String[] jobProperties = job.split(" ");
        type.setText(jobProperties[0]);
        date.setText(jobProperties[1]);
        time.setText(jobProperties[2]);
        city.setText(jobProperties[3]);
        prov.setText(jobProperties[4]);
        wage.setText(jobProperties[5]);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.child_item, parent);
        }

        TextView address, description, duration;
        address = (TextView) convertView.findViewById(R.id.jobAddress);
        description = (TextView) convertView.findViewById(R.id.jobDescription);
        duration = (TextView) convertView.findViewById(R.id.jobDuration);
        Button accept, decline;
        accept = (Button) convertView.findViewById(R.id.button_accept);
        decline = (Button) convertView.findViewById(R.id.button_decline);

        String job = jobs.get(groupPosition);
        final String[] jobProperties = job.split(" ");
        address.setText(jobProperties[6]);
        description.setText(jobProperties[7]);
        duration.setText(jobProperties[8]);

        accept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendResponse("accept", jobProperties[9]);
            }
        });
        decline.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendResponse("decline", jobProperties[9]);
            }
        });

        return convertView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void sendResponse(String option, String jobCode) {
        String url = Constants.URLS.LABOURER_RESPONSE.string;
        final String job = jobCode;
        final String opt  = option;
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("LabourerJobs", "failed to fetch jobs");
                    }
                }
        )
        {
            @Override
            //Create the body of the request
            protected Map<String, String> getParams() {
                Map<String, String>  params = new HashMap<>();
                params.put("job_code", job);
                params.put("option", opt);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<>();
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                params.put("Authorization", "Token " + settings.getString(Constants.AUTH_TOKEN, "noToken"));
                return params;
            }
        };
        Volley.newRequestQueue(context).add(postRequest);
    }
}
