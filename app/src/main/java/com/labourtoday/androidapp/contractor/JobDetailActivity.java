package com.labourtoday.androidapp.contractor;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.CustomDatePicker;
import com.labourtoday.androidapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JobDetailActivity extends AppCompatActivity {
    private ImageButton image;
    private EditText jobAddress, city, prov;
    private Switch hat, vest, belt;
    private TextView date, time;
    private Spinner days, weeks, months;
    private SharedPreferences settings;
    private ProgressDialog progress;
    private String jsonData;
    private HashMap<Integer, String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        progress = new ProgressDialog(this);
        progress.setMessage("Requesting workers");
        jobAddress = (EditText) findViewById(R.id.edit_address);
        city = (EditText) findViewById(R.id.edit_city);
        prov = (EditText) findViewById(R.id.edit_province);
        hat = (Switch) findViewById(R.id.switch_hat);
        vest = (Switch) findViewById(R.id.switch_vest);
        belt = (Switch) findViewById(R.id.switch_belt);
        image = (ImageButton) findViewById(R.id.imageButton);
        date = (TextView) findViewById(R.id.text_date);
        time = (TextView) findViewById(R.id.text_time);
        days = (Spinner) findViewById(R.id.spinner_days);
        weeks = (Spinner) findViewById(R.id.spinner_weeks);
        months = (Spinner) findViewById(R.id.spinner_months);
        data = (HashMap<Integer, String>) getIntent().getSerializableExtra("data");
        String[] daysItems = new String[]{"days","1","2","3","4","5","6"};
        String[] weeksItems = new String[]{"weeks","1","2","3"};
        String[] monthsItems = new String[]{"months","1","2","3","4","5","6","7","8","9","10","11","12"};
        ArrayAdapter<String> daysAdapter = new ArrayAdapter<>(this, R.layout.spinner_custom, daysItems);
        ArrayAdapter<String> weeksAdapter = new ArrayAdapter<>(this, R.layout.spinner_custom, weeksItems);
        ArrayAdapter<String> monthsAdapter = new ArrayAdapter<>(this, R.layout.spinner_custom, monthsItems);
        days.setAdapter(daysAdapter);
        weeks.setAdapter(weeksAdapter);
        months.setAdapter(monthsAdapter);

        final Dialog dialog = new Dialog(JobDetailActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog);
        final TimePicker timePicker = (TimePicker) dialog.findViewById(R.id.timePicker);
        final CustomDatePicker datePicker = (CustomDatePicker) dialog.findViewById(R.id.datePicker);
        Button dialogButton = (Button) dialog.findViewById(R.id.button_dialog);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time.setText("at " + Integer.toString(timePicker.getCurrentHour()) + ":" + Integer.toString(timePicker.getCurrentMinute()));
                date.setText(Integer.toString(datePicker.getYear()) + "-" + datePicker.getMonth() + "-" + Integer.toString(datePicker.getDayOfMonth()));
                dialog.dismiss();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
    }

    //public String getMonth(int month) {return new DateFormatSymbols().getMonths()[month];}

    public void requestWorkers(View view) {
        if (date.getText().toString().equals("") ||
                time.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter the starting date for your workers",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (days.getSelectedItem().equals("days") && weeks.getSelectedItem().equals("weeks")
                && months.getSelectedItem().equals("months")) {
            Toast.makeText(getApplicationContext(), "Please enter how long you need the workers",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (jobAddress.getText().toString().equals("")
                || city.getText().toString().equals("")
                || prov.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Please enter address/city/province",
                    Toast.LENGTH_LONG).show();
            return;
        }

        if (prov.getText().toString().length() > 2) {
            Toast.makeText(getApplicationContext(), "Enter a valid province (eg. BC)",
                    Toast.LENGTH_LONG).show();
            return;
        }

        progress.show();

        StringBuilder sb = new StringBuilder(date.getText().toString());
        sb.insert(0, "\"date\":\"");
        sb.append("\"");
        data.put(1, sb.toString());

        sb = new StringBuilder(time.getText().toString().substring(3));
        sb.insert(0, "\"time\":\"");
        sb.append("\"");
        data.put(2, sb.toString());

        sb = new StringBuilder(jobAddress.getText().toString());
        sb.insert(0, "\"address\":\"");
        sb.append("\"");
        data.put(3, sb.toString());

        sb = new StringBuilder(city.getText().toString());
        sb.insert(0, "\"city\":\"");
        sb.append("\"");
        data.put(4, sb.toString());

        sb = new StringBuilder(prov.getText().toString());
        sb.insert(0, "\"province\":\"");
        sb.append("\"");
        data.put(5, sb.toString());

        String daysStr = days.getSelectedItem().toString();
        if (daysStr.equals("days")) {
            daysStr = "0";
        }
        sb = new StringBuilder(daysStr);
        sb.insert(0, "\"days\":");
        data.put(6, sb.toString());

        String weeksStr = weeks.getSelectedItem().toString();
        if (weeksStr.equals("weeks")) {
            weeksStr = "0";
        }
        sb = new StringBuilder(weeksStr);
        sb.insert(0, "\"weeks\":");
        data.put(7, sb.toString());

        String monthsStr = months.getSelectedItem().toString();
        if (monthsStr.equals("months")) {
            monthsStr = "0";
        }
        sb = new StringBuilder(monthsStr);
        sb.insert(0, "\"months\":");
        data.put(8, sb.toString());

        sb = new StringBuilder("\"equipment\":[");

        if (hat.isChecked()) {
            sb.append("\"");
            sb.append(hat.getText().toString());
            sb.append("\",");
        }

        if (vest.isChecked()) {
            sb.append("\"");
            sb.append(vest.getText().toString());
            sb.append("\",");
        }

        if (belt.isChecked()) {
            sb.append("\"");
            sb.append(belt.getText().toString());
            sb.append("\",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        data.put(9, sb.toString());

        sb = new StringBuilder();
        sb.append("{\"job_skills\":");
        for (int i = 0; i < data.size(); i++) {
            sb.append(data.get(i));
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("}");

        jsonData = sb.toString();

        Log.i("FFINAL", jsonData);

        String url = Constants.URLS.JOBS.string;
        JsonObjectRequest jobRequest;
        try {
            jobRequest = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(jsonData),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            progress.dismiss();
                            startActivity(new Intent(JobDetailActivity.this, HireAgainActivity.class));
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
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Request error. Please try again", Toast.LENGTH_SHORT).show();
            progress.dismiss();
            return;
        }
        Volley.newRequestQueue(getApplicationContext()).add(jobRequest);
    }

}



