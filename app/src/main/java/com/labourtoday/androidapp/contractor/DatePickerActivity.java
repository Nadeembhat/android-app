package com.labourtoday.androidapp.contractor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.R;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

public class DatePickerActivity extends AppCompatActivity {
    private TimePicker timePicker;
    private DatePicker datePicker;
    private SharedPreferences settings;
    private EditText jobAddress, city, prov;
    private RadioButton hat, vest, tool;

    private final int PROFILE = 0;
    private final int LOG_OUT = 1;
    private String[] experienceList = new String[]{"No", "> 3 months experience (20/hr)", "> 6 months experience (22/hr)", "> 1 year experience (26/hr)", "Red seal (30/hr)"};

    private Drawer result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Worker details");

        if (!settings.getString(Constants.AUTH_TOKEN, "").equals("")
                && settings.getString(Constants.LAST_LOGIN, "").equals(Constants.CONTRACTOR)) {
            result = new DrawerBuilder()
                    .withActivity(this)
                    .withToolbar(toolbar)
                    .withSavedInstance(savedInstanceState)
                    .addDrawerItems(
                            new PrimaryDrawerItem().withName("Profile"),
                            new SecondaryDrawerItem().withName("Log out")
                    )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            switch (position) {
                                case PROFILE:
                                    Intent hireIntent = new Intent(DatePickerActivity.this, ContractorProfileActivity.class);
                                    startActivity(hireIntent);
                                    return true;
                                case LOG_OUT:
                                    settings.edit().remove(Constants.AUTH_TOKEN).apply();
                                    settings.edit().remove(Constants.LAST_LOGIN).apply();
                                    // Return to the welcome page
                                    Intent welcomeIntent = new Intent(DatePickerActivity.this, ContractorLoginActivity.class);
                                    startActivity(welcomeIntent);
                                    finish();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    })
                    .build();
        }

        Log.d("WTFfewwf", getIntent().getStringExtra("workerType"));

        Button next = (Button) findViewById(R.id.next);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        jobAddress = (EditText) findViewById(R.id.edit_address);
        city = (EditText) findViewById(R.id.edit_city);
        prov = (EditText) findViewById(R.id.edit_province);
        hat = (RadioButton) findViewById(R.id.button_hat);
        vest = (RadioButton) findViewById(R.id.button_vest);
        tool = (RadioButton) findViewById(R.id.button_tool);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String startDate = datePicker.getYear() + " " + datePicker.getMonth() + " " + datePicker.getDayOfMonth();
                final String startTime = timePicker.getCurrentHour() + " " + timePicker.getCurrentMinute();
                Intent i;
                if (jobAddress.getText().toString().equals("")
                        || city.getText().toString().equals("")
                        || prov.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please enter address/city/province", Toast.LENGTH_LONG).show();
                    return;
                }
                if (settings.getString(Constants.AUTH_TOKEN, "").equals("")) {
                    i = new Intent(getApplicationContext(), ContractorRegistrationActivity.class);
                    i.putExtra("start_date", startDate);
                    i.putExtra("start_time", startTime);
                    i.putExtra("start_day", DateFormat.format("EEEE",
                            new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth())).toString());
                    i.putExtra("job_address", jobAddress.getText().toString());
                    i.putExtra("city", city.getText().toString());
                    i.putExtra("province", prov.getText().toString());
                    i.putExtra("hat", Integer.toString(boolToInt(hat.isChecked())));
                    i.putExtra("vest", Integer.toString(boolToInt(vest.isChecked())));
                    i.putExtra("tool", Integer.toString(boolToInt(tool.isChecked())));
                    i.putExtra("workerExp", getIntent().getStringExtra("workerExp"));
                    i.putExtra("workerType", getIntent().getStringExtra("workerType"));
                    startActivity(i);
                } else if (!settings.getString(Constants.AUTH_TOKEN, "").equals("") &&
                        settings.getString(Constants.LAST_LOGIN, "").equals(Constants.CONTRACTOR)) {
                    String url = Constants.URLS.LABOURER_SEARCH.string;
                    StringRequest workerRequest = new StringRequest(Request.Method.POST, url,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Toast.makeText(getApplicationContext(), "Worker requested. We will get back to you soon.", Toast.LENGTH_LONG).show();
                                    Intent mainIntent = new Intent(DatePickerActivity.this, ContractorMainActivity.class);
                                    startActivity(mainIntent);
                                    finish();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                }
                            }
                    )
                    {
                        @Override
                        //Create the body of the request
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<>();

                            Intent i = getIntent();
                            params.put(workerTypeRequestFormat(i.getStringExtra("workerType")), Integer.toString(Arrays.asList(experienceList).indexOf(i.getStringExtra("workerExp"))));
                            params.put("start_day", DateFormat.format("EEEE",
                                    new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth())).toString());
                            params.put("start_date", datePicker.getYear() + " " + datePicker.getMonth() + " " + datePicker.getDayOfMonth());
                            params.put("start_time", timePicker.getCurrentHour() + " " + timePicker.getCurrentMinute());
                            params.put("job_address", jobAddress.getText().toString());
                            params.put("city", city.getText().toString());
                            params.put("province", prov.getText().toString());
                            params.put("hat", Integer.toString(boolToInt(hat.isChecked())));
                            params.put("vest", Integer.toString(boolToInt(vest.isChecked())));
                            params.put("tool", Integer.toString(boolToInt(tool.isChecked())));
                            params.put("wage", Integer.toString(calculateWage(Arrays.asList(experienceList).indexOf(i.getStringExtra("workerExp")))));
                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String>  params = new HashMap<>();
                            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                            params.put("Authorization", "Token " + settings.getString(Constants.AUTH_TOKEN, "noToken"));
                            return params;
                        }
                    };
                    Volley.newRequestQueue(getApplicationContext()).add(workerRequest);
                }
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        }
        super.onBackPressed();
    }

    private int dayToInt(String day) {
        switch (day) {
            case "Monday":
                return 1;
            case "Tuesday":
                return 2;
            case "Wednesday":
                return 3;
            case "Thursday":
                return 4;
            case "Friday":
                return 5;
            case "Saturday":
                return 6;
            case "Sunday":
                return 7;
            default:
                return 0;
        }
    }

    private String workerTypeRequestFormat(String workerType) {
        switch (workerType) {
            case "General Labour":
                return "general_labour";
            case "Carpenter":
                return "carpentry";
            case "Concrete":
                return "concrete_forming";
            case "Drywaller":
                return "dry_walling";
            case "Painter":
                return "painting";
            case "Landscaper":
                return "landscaping";
            case "Machine Operator":
                return "machine_operating";
            case "Roofer":
                return "roofing";
            case "Plumber":
                return "plumbing";
            case "Electrician":
                return "electrical";
            default:
                return "";
        }
    }

    private int calculateWage(int index) {
        Intent i = getIntent();
        switch (index) {
            case 1:
                return 20;
            case 2:
                return 22;
            case 3:
                return 26;
            default:
                return 0;

        }
    }

    private int boolToInt(boolean checked) {
        if (checked)
            return 1;
        else
            return 0;
    }

}


