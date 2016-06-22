package com.labourtoday.androidapp.contractor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.MainActivity;
import com.labourtoday.androidapp.R;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ContractorProfileActivity extends AppCompatActivity {
    private TextView nameContractor, companyContractor;
    private SharedPreferences settings;
    private Drawer result;
    private ProgressDialog progress;
    private JSONObject contractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contractor_profile2);
        nameContractor = (TextView) findViewById(R.id.name_contractor);
        companyContractor = (TextView) findViewById(R.id.company_contractor);

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if (!settings.getString(Constants.AUTH_TOKEN, "").equals("") &&
                settings.getString(Constants.LAST_LOGIN, "").equals(Constants.CONTRACTOR)) {
            result = new DrawerBuilder()
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .withSavedInstance(savedInstanceState)
                    .addDrawerItems(
                            new PrimaryDrawerItem().withName("Sign out")
                    )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            switch (position) {
                                case 0:
                                    settings.edit().remove(Constants.AUTH_TOKEN).apply();
                                    settings.edit().remove(Constants.LAST_LOGIN).apply();
                                    Intent mainIntent = new Intent(ContractorProfileActivity.this, MainActivity.class);
                                    startActivity(mainIntent);
                                    finish();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    })
                    .build();
        }
        progress = new ProgressDialog(this);
        progress.setMessage("Retrieving info");
    }

    @Override
    protected void onResume(){
        super.onResume();
        String url = Constants.URLS.CONTRACTORS.string;
        JsonObjectRequest contractorRequest = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progress.dismiss();
                        contractor = response;
                        // Log.i("Worker", worker.toString());
                        try {
                            nameContractor.setText(contractor.getString("first_name") + " "
                                    + contractor.getString("last_name"));
                            companyContractor.setText(contractor.getString("company"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
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
        Volley.newRequestQueue(getApplicationContext()).add(contractorRequest);
    }

    public void hire(View view) {
        startActivity(new Intent(ContractorProfileActivity.this, HiringGridActivity.class));
    }
}
