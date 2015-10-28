package com.labourtoday.androidapp.contractor;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.R;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {
    private ProgressDialog progress;
    private EditText cardNumber;
    private EditText cvc;
    private EditText hoursWorked;
    private Spinner monthSpinner;
    private Spinner yearSpinner;
    private RatingBar ratingBar;
    private Button payButton;
    public static final String PUBLISHABLE_KEY = "pk_test_gb8kyYk3nOHBWsCIsaq3fWwj";

    private String[] months = new String[]{"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};
    private String[] years = new String[]{"2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        monthSpinner = (Spinner) findViewById(R.id.expMonth);
        yearSpinner = (Spinner) findViewById(R.id.expYear);
        cvc = (EditText) findViewById(R.id.cvc);
        cardNumber = (EditText) findViewById(R.id.number);
        hoursWorked = (EditText) findViewById(R.id.edit_hours_worked);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        payButton = (Button) findViewById(R.id.pay_button);
        progress = new ProgressDialog(PaymentActivity.this);
        progress.setMessage("Payment Processing...");

        ArrayAdapter<String> monthsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        ArrayAdapter<String> yearsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);

        monthSpinner.setAdapter(monthsAdapter);
        yearSpinner.setAdapter(yearsAdapter);

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
                    double amount = Double.parseDouble(s.toString()) * 19;
                    payButton.setText("Pay $" + new DecimalFormat("#.##").format(amount));
                }
            }
        });
    }

    public void pay(View view) {
        rateLabourer();

        Card card = new Card(
                cardNumber.getText().toString(),
                monthToInt(monthSpinner.getSelectedItem().toString()),
                Integer.parseInt(yearSpinner.getSelectedItem().toString()),
                cvc.getText().toString()
        );

        if (card.validateCard()) {
            new Stripe().createToken(
                    card,
                    PUBLISHABLE_KEY,
                    new TokenCallback() {
                        public void onSuccess(Token token) {
                            Log.d("PaymentActivity", token.getId());
                            final String token_id = token.getId();
                            StringRequest postRequest = new StringRequest(Request.Method.POST,
                                    Constants.URLS.PAYMENT.string, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    Toast.makeText(getApplicationContext(), "Payment Successful", Toast.LENGTH_LONG).show();
                                }
                            },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d("PaymentActivity", "Error making payment");
                                        }
                                    }
                            ) {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> paramsMap = new HashMap<String, String>();
                                    paramsMap.put(Constants.STRIPE_TOKEN, token_id);
                                    paramsMap.put("hours_worked", hoursWorked.getText().toString());
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

                        public void onError(Exception error) {
                            Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else if (!card.validateNumber()) {
            Toast.makeText(getApplicationContext(), "Invalid card number. Please try again", Toast.LENGTH_LONG).show();
        } else if (!card.validateExpiryDate()) {
            Toast.makeText(getApplicationContext(), "Invalid expiry date. Please try again", Toast.LENGTH_LONG).show();
        } else if (!card.validateCVC()) {
            Toast.makeText(getApplicationContext(), "Invalid cvc. Please try again", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Invalid card. Please try with another", Toast.LENGTH_LONG).show();
        }
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
                params.put(Constants.RATING, new DecimalFormat("#.##").format(ratingBar.getRating()));
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

    private int monthToInt(String month) {
        switch (month) {
            case "January":
                return 1;
            case "February":
                return 2;
            case "March":
                return 3;
            case "April":
                return 4;
            case "May":
                return 5;
            case "June":
                return 6;
            case "July":
                return 7;
            case "August":
                return 8;
            case "September":
                return 9;
            case "October":
                return 10;
            case "November":
                return 11;
            case "December":
                return 12;
            default:
                return 0;
        }
    }
}
