package com.labourtoday.androidapp.contractor;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

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

import java.util.HashMap;
import java.util.Map;

public class PaymentActivity extends AppCompatActivity {
    PopupWindow popUp;
    private EditText cardNumber;
    private EditText cvc;
    private Spinner monthSpinner;
    private Spinner yearSpinner;
    private ImageButton up, down;
    public static final String PUBLISHABLE_KEY = "pk_test_gb8kyYk3nOHBWsCIsaq3fWwj";

    private String[] months = new String[]{"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};
    private String[] years = new String[]{"2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        monthSpinner = (Spinner) findViewById(R.id.expMonth);
        yearSpinner = (Spinner) findViewById(R.id.expYear);
        cvc = (EditText) findViewById(R.id.cvc);
        cardNumber = (EditText) findViewById(R.id.number);
        up = (ImageButton) findViewById(R.id.thumbup);
        down = (ImageButton) findViewById(R.id.thumbdown);

        ArrayAdapter<String> monthsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, months);
        ArrayAdapter<String> yearsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);

        monthSpinner.setAdapter(monthsAdapter);
        yearSpinner.setAdapter(yearsAdapter);
    }

    public void pay(View view) {

        Card card = new Card(
                cardNumber.getText().toString(),
                Integer.parseInt(monthSpinner.getSelectedItem().toString()),
                Integer.parseInt(yearSpinner.getSelectedItem().toString()),
                cvc.getText().toString()
        );

        boolean validation = card.validateCard();
        if (validation) {
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
                                    Toast.makeText(getApplicationContext(), "Payment Successful",
                                            Toast.LENGTH_LONG).show();
                                    displayPopUp();
                                }
                            },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.d("SENDMESSAGE", "ERROR");
                                            error.printStackTrace();
                                        }
                                    }
                            )
                            {
                                @Override
                                protected Map<String, String> getParams() {
                                    Map<String, String> paramsMap = new HashMap<String, String>();
                                    paramsMap.put(Constants.STRIPE_TOKEN, token_id);
                                    paramsMap.put(Constants.PHONE_NUMBER, getIntent().getStringExtra("phoneNumber"));
                                    paramsMap.put(Constants.REGISTRATION_ID,
                                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(Constants.REGISTRATION_ID, ""));
                                    return paramsMap;
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

    public void displayPopUp() {
        // Get the instance of the LayoutInflater, use the context of this activity
        // Inflate the view from a predefined XML layout
        View layout = getLayoutInflater().inflate(R.layout.post_payment, (ViewGroup) findViewById(R.id.popup_container));
        // create a 300px width and 470px height PopupWindow
        popUp = new PopupWindow(layout, 300, 470, true);
        popUp.setBackgroundDrawable(new ColorDrawable());

        // display the popup in the center
        popUp.showAtLocation(layout, Gravity.CENTER, 0, 0);
    }

    public void rating(View v) {
        Toast.makeText(getApplicationContext(), "Rating recorded. Thank you.", Toast.LENGTH_LONG).show();
    }




}
