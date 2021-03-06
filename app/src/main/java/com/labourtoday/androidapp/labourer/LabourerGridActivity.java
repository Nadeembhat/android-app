package com.labourtoday.androidapp.labourer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.ExpandableHeightGridView;
import com.labourtoday.androidapp.R;
import com.labourtoday.androidapp.contractor.GridAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LabourerGridActivity extends AppCompatActivity {
    private ExpandableHeightGridView gridView;
    private GridAdapter adapter;
    private HashMap<Integer, String> data;
    private HashMap<String, String> selectedTypes;
    private Button button;
    private SharedPreferences settings;
    private String jsonData, action, skills;
    private Map<String, Integer> expMap;
    private ProgressDialog progress;

    static final String[] typeWorkers = new String[] {
            "General Labour",
            "Carpentry",
            "Concrete",
            "Landscaping",
            "Painting",
            "Dry Walling",
            "Roofing",
            "Machine Operation",
            "Plumbing",
            "Electrical"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labourer_grid);
        initializeMap();
        JSONArray jsonArray;

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        data = new HashMap<>();
        jsonData = "{\"skills\":[";
        selectedTypes = new HashMap<>();
        adapter = new GridAdapter(this, typeWorkers, selectedTypes);
        button = (Button) findViewById(R.id.button_next);

        gridView = (ExpandableHeightGridView) findViewById(R.id.grid_worker);
        gridView.setExpanded(true);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent expSelectIntent = new Intent(LabourerGridActivity.this, ExperienceSelectActivity.class);
                expSelectIntent.putExtra("workerType", ((TextView) v.findViewById(R.id.grid_item_label)).getText());
                startActivityForResult(expSelectIntent, 0);
            }
        });

        try {
            action = getIntent().getAction();
            skills = getIntent().getStringExtra("skills");
            jsonArray = new JSONArray(skills);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                if (jsonData.contains("]")) {
                    jsonData = jsonData.replace("]", ",");
                }
                jsonData += "{\"skill\":\"";
                jsonData += obj.get("skill").toString();
                jsonData += "\",\"experience\":";
                jsonData += expMap.get(obj.get("experience").toString());
                jsonData += "},";
                selectedTypes.put(obj.get("skill").toString(), obj.get("experience").toString());
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            action = "";
            skills = "";
        }

        if (action.equals("")) {
            button.setVisibility(View.GONE);
        }

        progress = new ProgressDialog(LabourerGridActivity.this);
        progress.setMessage("Updating profile");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) {
            selectedTypes.put(data.getStringExtra("workerType"), data.getStringExtra("workerExp"));
            adapter.notifyDataSetChanged();
            if (jsonData.contains("]}")) {
                jsonData = jsonData.replace("]}", ",");
            } else if (jsonData.contains("]")) {
                jsonData = jsonData.replace("]", ",");
            }
            jsonData += "{\"skill\":\"";
            jsonData += data.getStringExtra("workerType");
            jsonData += "\",\"experience\":";
            jsonData += expMap.get(data.getStringExtra("workerExp"));
            jsonData += "},";
            button.setVisibility(View.VISIBLE);
        }
    }

    public void next(View view) {
        jsonData = jsonData.substring(0, jsonData.length() - 1);
        jsonData += "]}";

        JSONObject finalObj;
        try {
            finalObj = new JSONObject(jsonData);
        } catch (JSONException e) {
            Toast.makeText(this, "Request error. Please try again", Toast.LENGTH_SHORT).show();
            progress.dismiss();
            return;
        }

        JSONArray tempArray = new JSONArray();
        try {
            tempArray = finalObj.getJSONArray("skills");
            int initLength = tempArray.length();
            for (int i = 0; i < initLength; i++) {
                JSONObject obj = tempArray.getJSONObject(i);
                for (int j = i + 1; j < initLength; j++) {
                    JSONObject obj2 = tempArray.getJSONObject(j);
                    if (obj.getString("skill").equals(obj2.getString("skill"))) {
                        tempArray.remove(i);
                        initLength--;
                        i--;
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Request error. Please try again", Toast.LENGTH_SHORT).show();
            progress.dismiss();
            return;
        }

        if (action.equals("")) {
            jsonData = tempArray.toString();
            jsonData = "{\"skills\":" + jsonData;
            jsonData += "}";
            data.put(0, jsonData);
            Log.i("LabourerGrid", "If action is empty: " + jsonData);
            Intent next = new Intent(LabourerGridActivity.this, WorkerAvailabilityActivity.class);
            next.putExtra("data", data);
            startActivity(next);
        } else {
            progress.show();
            String url = Constants.URLS.WORKER_DETAIL.string;

            jsonData = tempArray.toString();
            jsonData = "{\"skills\":" + jsonData;
            jsonData += "}";
            Log.i("GridActivity", "If action is not empty: " + jsonData);
            JsonObjectRequest workerRequest;

            try {
                workerRequest = new JsonObjectRequest(Request.Method.PUT, url, new JSONObject(jsonData),
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                progress.dismiss();
                                finish();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Request error. Please try again", Toast.LENGTH_SHORT).show();
                                progress.dismiss();
                            }
                        }
                ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization","Token " + settings.getString(Constants.AUTH_TOKEN, ""));
                        return headers;
                    }
                };
            } catch (JSONException e) {
                Toast.makeText(this, "Request error. Please try again", Toast.LENGTH_SHORT).show();
                progress.dismiss();
                return;
            }

            Volley.newRequestQueue(getApplicationContext()).add(workerRequest);
        }
    }

    private void initializeMap() {
        expMap = new HashMap<>();
        expMap.put("More than 6 months", 1);
        expMap.put("More than 1 year", 2);
        expMap.put("More than 2 years", 3);
        expMap.put("Red seal", 4);
    }
}
