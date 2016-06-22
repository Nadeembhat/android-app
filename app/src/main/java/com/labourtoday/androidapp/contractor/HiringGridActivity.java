package com.labourtoday.androidapp.contractor;

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

import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.ExpandableHeightGridView;
import com.labourtoday.androidapp.MainActivity;
import com.labourtoday.androidapp.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HiringGridActivity extends AppCompatActivity {
    private ExpandableHeightGridView gridView;
    private GridAdapter adapter;
    private SharedPreferences settings;
    private HashMap<String, String> selectedTypes;
    private HashMap<String, ArrayList<Integer>> prevMap;
    private Button button;
    HashMap<Integer, String> data;
    private String jsonRequest;
    private Map<String, Integer> expMap;

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
        setContentView(R.layout.activity_hiring_grid);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        data = new HashMap<>();
        initializeMap();
        jsonRequest = "\"job_skills\":[";
        selectedTypes = new HashMap<>();
        prevMap = new HashMap<>();
        button = (Button) findViewById(R.id.button_next);
        button.setVisibility(View.GONE);
        adapter = new GridAdapter(this, typeWorkers, selectedTypes);
        gridView = (ExpandableHeightGridView) findViewById(R.id.grid_worker);
        gridView.setExpanded(true);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent workerSelectIntent = new Intent(HiringGridActivity.this, WorkerSelectActivity.class);
                String item = ((TextView) v.findViewById(R.id.grid_item_label)).getText().toString();
                workerSelectIntent.putExtra("workerType", ((TextView) v.findViewById(R.id.grid_item_label)).getText());
                if (prevMap.containsKey(item)) {
                    workerSelectIntent.putIntegerArrayListExtra("previousRequest",
                            prevMap.get(item));
                }
                startActivityForResult(workerSelectIntent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) {
            String updatedDisplayValue = data.getStringArrayListExtra("workerRequest").get(0);
            if (data.getStringArrayListExtra("workerRequest").size() > 1) {
                updatedDisplayValue += "...";
            }
            ArrayList<Integer> workers = data.getIntegerArrayListExtra("workerRequestCurrent");
            selectedTypes.put(data.getStringExtra("workerType"), updatedDisplayValue);
            prevMap.put(data.getStringExtra("workerType"), workers);
            adapter.notifyDataSetChanged();
            if (jsonRequest.contains("]")) {
                jsonRequest = jsonRequest.replace("]", ",");
            } else if (jsonRequest.contains("]}")) {
                jsonRequest = jsonRequest.replace("]}", ",");
            }
            for (int i = 0; i < workers.size(); i++) {
                if (workers.get(i) == 0) {
                    continue;
                }
                jsonRequest += "{\"skill\":\"";
                jsonRequest += data.getStringExtra("workerType");
                jsonRequest += "\",\"experience\":";
                jsonRequest += i;
                jsonRequest += ",\"num_required_workers\":";
                jsonRequest += workers.get(i);
                jsonRequest += "},";
            }

            button.setVisibility(View.VISIBLE);
        }
    }

    public void next(View view) {
        jsonRequest = jsonRequest.substring(0, jsonRequest.length() - 1);
        Log.i("HiringGrid", jsonRequest);

        if (!jsonRequest.substring(0, 13).contains("job_skills")) {
            jsonRequest = "\"job_skills\":" + jsonRequest;
        }
        jsonRequest = "{" + jsonRequest;
        jsonRequest += "]}";

        JSONObject finalObj;
        try {
            finalObj = new JSONObject(jsonRequest);
        } catch (JSONException e) {
            Toast.makeText(this, "Request error. Please try again", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONArray tempArray;
        try {
            tempArray = finalObj.getJSONArray("job_skills");
            int initLength = tempArray.length();
            for (int i = 0; i < initLength; i++) {
                JSONObject obj = tempArray.getJSONObject(i);
                for (int j = i + 1; j < initLength; j++) {
                    JSONObject obj2 = tempArray.getJSONObject(j);
                    if (obj.getString("skill").equals(obj2.getString("skill")) &&
                            obj.getInt("experience") == obj2.getInt("experience")) {
                        tempArray.remove(i);
                        initLength--;
                        i--;
                        break;
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Client error. Please try again later", Toast.LENGTH_SHORT).show();
            return;
        }

        jsonRequest = tempArray.toString();
        //jsonRequest = "{\"job_skills\":" + jsonRequest;
        //jsonRequest += "}";
        data.put(0, jsonRequest);
        Intent jobDetailIntent = new Intent(HiringGridActivity.this, JobDetailActivity.class);
        jobDetailIntent.putExtra("data", data);
        startActivity(jobDetailIntent);


        /*
        jsonRequest = jsonRequest.substring(0, jsonRequest.length() - 1);
        jsonRequest += "]";
        data.put(0, jsonRequest);
        Log.i("HiringGridActivity", jsonRequest);
        Intent jobDetailIntent = new Intent(HiringGridActivity.this, JobDetailActivity.class);
        jobDetailIntent.putExtra("data", data);
        startActivity(jobDetailIntent);
        */
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (settings.getString(Constants.LAST_LOGIN, "").equals(Constants.CONTRACTOR)) {
            Intent mainIntent = new Intent(new Intent(this, MainActivity.class));
            startActivity(mainIntent);
            finish();
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
