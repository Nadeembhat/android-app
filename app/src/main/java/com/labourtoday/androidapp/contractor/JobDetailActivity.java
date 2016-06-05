package com.labourtoday.androidapp.contractor;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
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

import com.labourtoday.androidapp.CustomDatePicker;
import com.labourtoday.androidapp.R;

import java.text.DateFormatSymbols;
import java.util.HashSet;
import java.util.Set;

public class JobDetailActivity extends AppCompatActivity {
    private ImageButton image;
    private EditText jobAddress, city, prov;
    private Switch hat, vest, belt;
    private TextView date, time;
    private Spinner days, weeks, months;
    private SharedPreferences settings;

    private String[] experienceList = new String[]{"No", "> 3 months experience (20/hr)", "> 6 months experience (22/hr)", "> 1 year experience (26/hr)", "Red seal (30/hr)"};
    // contractorDetail
    // jobDetail
    // data
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_detail);
        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        Button next = (Button) findViewById(R.id.next);
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

        String[] daysItems = new String[]{"days","1","2","3","4","5","6","7","8","9","10","11","12",
                "13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28",
                "29","30"};
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

        /*
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                time.setText("at " + Integer.toString(hourOfDay) + ":" + Integer.toString(minute));
            }
        });

        datePicker.init(2016, 5, 1, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date.setText(Integer.toString(dayOfMonth) + " " + getMonth(monthOfYear) +
                        ", " + Integer.toString(year));
            }
        });*/

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time.setText("at " + Integer.toString(timePicker.getCurrentHour()) + ":" + Integer.toString(timePicker.getCurrentMinute()));
                date.setText(Integer.toString(datePicker.getDayOfMonth()) + " " + getMonth(datePicker.getMonth()) +
                        ", " + Integer.toString(datePicker.getYear()));
                dialog.dismiss();
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                storeActivityData();
                Intent i = new Intent(JobDetailActivity.this, HireAgainActivity.class);
                i.putStringArrayListExtra("data", getIntent().getStringArrayListExtra("data"));
                startActivity(i);
                /*
                String url = Constants.URLS.TOKEN_AUTH.string;
                StringRequest workerRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                startActivity(new Intent(JobDetailActivity.this, HireAgainActivity.class));
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), "Unable to send request",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                ) {
                    @Override
                    //Create the body of the request
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        // Get the registration info from input fields and add them to the body of the request
                        String param = "";
                        for (int i = 0; i < data.size(); i++) {
                            param += data.get(i);
                            param += "\n";
                        }
                        params.put("data", param);
                        return params;
                    }
                };

                Volley.newRequestQueue(getApplicationContext()).add(workerRequest);
                */
            }
        });
    }

    public String getMonth(int month) {
        return new DateFormatSymbols().getMonths()[month];
    }

    public void storeActivityData() {
        Set<String> data = new HashSet<>();
        data.add("Date: " + date.getText().toString());
        data.add("Time: " + time.getText().toString());
        data.add("Address: " + jobAddress.getText().toString());
        data.add("City: " + city.getText().toString());
        data.add("Province: " + prov.getText().toString());
        data.add("Days: " + days.getSelectedItem());
        data.add("Weeks: " + weeks.getSelectedItem());
        data.add("Months: " + months.getSelectedItem());
        data.add("Hard hat required: " + Boolean.toString(hat.isChecked()));
        data.add("Safety vest required: " + Boolean.toString(vest.isChecked()));
        data.add("Tool belt and basic tools required: " + Boolean.toString(belt.isChecked()));
        settings.edit().putStringSet("jobDetail", data).apply();
    }

}


