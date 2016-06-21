package com.labourtoday.androidapp.labourer;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.R;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LabourerEquipActivity extends AppCompatActivity {
    private RadioGroup license, car, belt, boots, hat, vest;
    private CheckBox sfa, swfa, cprc;
    private SharedPreferences settings;
    private HashMap<Integer, String> data;
    private final int EMPTY = -1;
    private String equipment;
    private Drawer result;

    private String action, profileData;
    private ProgressDialog progress;
    private Map<String, RadioGroup> equipRadioMap;
    private Map<String, CheckBox> equipCheckBoxMap;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labourer_equip);
        JSONArray jsonArray;
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        result = new DrawerBuilder().withActivity(this).withToolbar(toolbar)
                .withSavedInstance(savedInstanceState).build();
        result.getActionBarDrawerToggle().setDrawerIndicatorEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        data = (HashMap<Integer, String>) getIntent().getSerializableExtra("data");
        license = (RadioGroup) findViewById(R.id.radio_drivers);
        car = (RadioGroup) findViewById(R.id.radio_car);
        belt = (RadioGroup) findViewById(R.id.radio_belt);
        boots = (RadioGroup) findViewById(R.id.radio_boots);
        hat = (RadioGroup) findViewById(R.id.radio_hat);
        vest = (RadioGroup) findViewById(R.id.radio_vest);

        sfa = (CheckBox) findViewById(R.id.chkSFA);
        swfa = (CheckBox) findViewById(R.id.chkSWFA);
        cprc = (CheckBox) findViewById(R.id.chkCPRC);

        settings = PreferenceManager.getDefaultSharedPreferences(this);
        equipment = "\"equipment\":[";
        try {
            action = getIntent().getAction();
            profileData = getIntent().getStringExtra("equipment");
            jsonArray = new JSONArray(profileData);
            equipRadioMap = new HashMap<>();
            equipCheckBoxMap = new HashMap<>();
            initMaps();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject obj = jsonArray.getJSONObject(i);
                if (equipRadioMap.get(obj.get("name")) != null) {
                    RadioGroup radioGroup = equipRadioMap.get(obj.get("name"));
                    ((RadioButton) radioGroup.getChildAt(0)).setChecked(true);
                    continue;
                }
                if (equipCheckBoxMap.get(obj.get("name")) != null) {
                    equipCheckBoxMap.get(obj.get("name")).setChecked(true);
                }
            }
        } catch (Exception e) {
            action = "";
            profileData = "";
        }

        progress = new ProgressDialog(this);
        progress.setMessage("Updating profile");
    }

    public void next(View v) {
        if (checkEmpty()) {
            Toast.makeText(getApplicationContext(), "Please answer all questions", Toast.LENGTH_SHORT).show();
            return;
        }
        equipment = equipment.substring(0, 13);
        RadioButton lbutton = (RadioButton) findViewById(license.getCheckedRadioButtonId());
        RadioButton cbutton = (RadioButton) findViewById(car.getCheckedRadioButtonId());
        RadioButton bbutton = (RadioButton) findViewById(belt.getCheckedRadioButtonId());
        RadioButton bsbutton = (RadioButton) findViewById(boots.getCheckedRadioButtonId());
        RadioButton hbutton = (RadioButton) findViewById(hat.getCheckedRadioButtonId());
        RadioButton vbutton = (RadioButton) findViewById(vest.getCheckedRadioButtonId());

        add(lbutton, "Driver's Licence");
        add(cbutton, "Vehicle");
        add(bbutton, "Tool Belt with Basic Tools");
        add(bsbutton, "Steel Toe Boots");
        add(hbutton, "Hard Hat");
        add(vbutton, "Safety Vest");

        if (sfa.isChecked()) {
            equipment += "\"" + "Standard First Aid" + "\",";
        }

        if (swfa.isChecked()) {
            equipment += "\"" + "Standard Workplace First Aid" + "\",";
        }

        if (cprc.isChecked()) {
            equipment += "\"" + "CPR C & AED" + "\",";
        }

        equipment = equipment.substring(0, equipment.length() - 1);
        equipment += "]";

        // Log.i("EQUIPMENT", equipment);
        
        if (action.equals("")) {
            data.put(6, equipment);
            Intent i = new Intent(this, ReferenceActivity.class);
            i.putExtra("data", data);
            startActivity(i);
        } else {
            progress.show();
            String url = Constants.URLS.WORKER_DETAIL.string;
            JsonObjectRequest workerRequest;
            equipment = "{" + equipment;
            equipment += "}";
            try {
                workerRequest = new JsonObjectRequest(Request.Method.PUT, url, new JSONObject(equipment),
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

    private boolean checkEmpty() {
        if (license.getCheckedRadioButtonId() == EMPTY ||
                car.getCheckedRadioButtonId() == EMPTY ||
                belt.getCheckedRadioButtonId() == EMPTY ||
                boots.getCheckedRadioButtonId() == EMPTY ||
                hat.getCheckedRadioButtonId() == EMPTY ||
                vest.getCheckedRadioButtonId() == EMPTY) {
            return true;
        }
        return false;
    }

    private void add(RadioButton btn, String equip) {
        if (btn.getText().toString().equals("Yes")) {
            equipment += "\"" + equip + "\",";
        }
    }

    private void initMaps() {
        equipRadioMap.put("Driver's Licence", license);
        equipRadioMap.put("Vehicle", car);
        equipRadioMap.put("Tool Belt with Basic Tools", belt);
        equipRadioMap.put("Steel Toe Boots", boots);
        equipRadioMap.put("Hard Hat", hat);
        equipRadioMap.put("Safety Vest", vest);

        ((RadioButton) license.getChildAt(1)).setChecked(true);
        ((RadioButton) car.getChildAt(1)).setChecked(true);
        ((RadioButton) belt.getChildAt(1)).setChecked(true);
        ((RadioButton) boots.getChildAt(1)).setChecked(true);
        ((RadioButton) hat.getChildAt(1)).setChecked(true);
        ((RadioButton) vest.getChildAt(1)).setChecked(true);

        equipCheckBoxMap.put("Standard First Aid", sfa);
        equipCheckBoxMap.put("Standard Workplace First Aid", swfa);
        equipCheckBoxMap.put("CPR C & AED", cprc);
    }
}
