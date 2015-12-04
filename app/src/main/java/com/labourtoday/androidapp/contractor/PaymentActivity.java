package com.labourtoday.androidapp.contractor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {
    private ProgressDialog progress;
    private EditText hoursWorked, hourlyWage;
    private EditText workerId;

    private SharedPreferences settings;

    private RatingBar ratingBar;
    private Button payButton;

    private final int PROFILE = 0;
    private final int LOG_OUT = 1;

    private Drawer result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        workerId = (EditText) findViewById(R.id.edit_worker_id);
        hoursWorked = (EditText) findViewById(R.id.edit_hours_worked);
        hourlyWage = (EditText) findViewById(R.id.edit_wage);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        payButton = (Button) findViewById(R.id.pay_button);
        progress = new ProgressDialog(PaymentActivity.this);
        progress.setMessage("Payment Processing...");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Payment");

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
                                Intent hireIntent = new Intent(PaymentActivity.this, ContractorProfileActivity.class);
                                startActivity(hireIntent);
                                return true;
                            case LOG_OUT:
                                settings.edit().remove(Constants.AUTH_TOKEN).apply();
                                settings.edit().remove(Constants.LAST_LOGIN).apply();
                                // Return to the welcome page
                                Intent welcomeIntent = new Intent(PaymentActivity.this, ContractorLoginActivity.class);
                                startActivity(welcomeIntent);
                                finish();
                                return true;
                            default:
                                return false;
                        }
                    }
                })
                .build();

        hoursWorked.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    payButton.setText("Pay");
                } else {
                    double amount = Double.parseDouble(s.toString()) * Integer.parseInt(hourlyWage.getText().toString());
                    payButton.setText("Pay $" + new DecimalFormat("#.##").format(amount));
                }
            }
        });
    }

    public void pay(View view) {
        if (workerId.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Enter your worker's ID (Important!)", Toast.LENGTH_LONG).show();
            return;
        }
        progress.show();
        rateLabourer();
        StringRequest postRequest = new StringRequest(Request.Method.POST,
                Constants.URLS.PAYMENT.string, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Payment Successful", Toast.LENGTH_LONG).show();
                progress.dismiss();
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "Payment Successful", Toast.LENGTH_LONG).show();
                        progress.dismiss();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> paramsMap = new HashMap<String, String>();
                paramsMap.put("hours_worked", hoursWorked.getText().toString());
                paramsMap.put("worker_id", workerId.getText().toString());
                return paramsMap;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Token " +
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(Constants.AUTH_TOKEN, "noTokenFound"));
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(postRequest);

    }

    private void rateLabourer() {
        String url = Constants.URLS.LABOURER_DETAIL.string;
        StringRequest putRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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
            //Create the body of the request
            protected Map<String, String> getParams() {
                Map<String, String>  params = new HashMap<>();
                params.put(Constants.RATING, new DecimalFormat("#.00").format(ratingBar.getRating()));
                params.put("worker_id", workerId.getText().toString());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Token " +
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(Constants.AUTH_TOKEN, "noTokenFound"));
                return params;
            }
        };
        Volley.newRequestQueue(getApplicationContext()).add(putRequest);
    }
}
