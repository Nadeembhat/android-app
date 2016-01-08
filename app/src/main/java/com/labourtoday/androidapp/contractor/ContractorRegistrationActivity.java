package com.labourtoday.androidapp.contractor;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.R;
import com.labourtoday.androidapp.gcm.TokenRegistrationService;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ContractorRegistrationActivity extends AppCompatActivity {
    private static final String TAG = "RegistrationActivity";
    private BroadcastReceiver tokenBroadcastReceiver; //listens for REGISTRATION_COMPLETE message from IDRegistrationService
    public static final String PUBLISHABLE_KEY = "pk_test_HIK07thV9P8ojo7HShUyAOTb";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private ProgressDialog progress;
    //Input fields for creating new User
    private EditText company, username, phoneNumber;
    private EditText password, passwordConfirm;
    private EditText first_name, last_name;

    private EditText cvc, cardNumber;
    private Spinner monthSpinner;
    private Spinner yearSpinner;
    private String tokenId;

    private String[] months = new String[]{"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};
    private String[] years = new String[]{"2016", "2017", "2018", "2019", "2020", "2021",
            "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030", "2031", "2032"};
    String[] experienceList = new String[]{"No", "> 3 months experience (20/hr)", "> 6 months experience (22/hr)", "> 1 year experience (26/hr)", "Red seal (30/hr)"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contractor_registration);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Sign up info");

        progress = new ProgressDialog(ContractorRegistrationActivity.this);
        progress.setMessage("Registering and finding your worker...");

        //Assign input fields to instance variables
        company = (EditText) findViewById(R.id.edit_company_name);
        username = (EditText) findViewById(R.id.edit_email);
        password = (EditText) findViewById(R.id.edit_password);
        passwordConfirm = (EditText) findViewById(R.id.confirm_password);
        first_name = (EditText) findViewById(R.id.edit_first_name);
        last_name = (EditText) findViewById(R.id.edit_last_name);
        phoneNumber = (EditText) findViewById(R.id.edit_phone_number);

        monthSpinner = (Spinner) findViewById(R.id.expMonth);
        yearSpinner = (Spinner) findViewById(R.id.expYear);
        cvc = (EditText) findViewById(R.id.cvc);
        cardNumber = (EditText) findViewById(R.id.number);

        ArrayAdapter<String> monthsAdapter = new ArrayAdapter<>(this, R.layout.spinner_custom, months);
        ArrayAdapter<String> yearsAdapter = new ArrayAdapter<>(this, R.layout.spinner_custom, years);
        monthsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(monthsAdapter);
        yearSpinner.setAdapter(yearsAdapter);

        ImageView stripe = (ImageView)findViewById(R.id.stripe_image);
        stripe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://stripe.com/about"));
                startActivity(intent);
            }
        });
    }

    /*
     *Submit the fields filled in by the user to the server to create a new user
     */
    public void registerUser(View view){
        /*User-entered inputs for username and password fields. Needed for login after registration*/
        final String username_input = username.getText().toString();
        final String password_input = password.getText().toString();
        final String password_confirm = passwordConfirm.getText().toString();
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
                            tokenId = token.getId();
                        }
                        public void onError(Exception error) {
                            Toast.makeText(getApplicationContext(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        } else if (!card.validateNumber()) {
            Toast.makeText(getApplicationContext(), "Invalid card number. Please try again", Toast.LENGTH_LONG).show();
            return;
        } else if (!card.validateExpiryDate()) {
            Toast.makeText(getApplicationContext(), "Invalid expiry date. Please try again", Toast.LENGTH_LONG).show();
            return;
        } else if (!card.validateCVC()) {
            Toast.makeText(getApplicationContext(), "Invalid cvc. Please try again", Toast.LENGTH_LONG).show();
            return;
        } else {
            Toast.makeText(getApplicationContext(), "Invalid card. Please try with another", Toast.LENGTH_LONG).show();
            return;
        }

        if (!password_input.equals(password_confirm)) {
            Toast.makeText(this, "Passwords do not match. Please try again.", Toast.LENGTH_LONG).show();
            return;
        }

        progress.show();
        tokenBroadcastReceiver = new BroadcastReceiver() { // Wait for TokenRegistrationService to send you the RegistrationId from GCM
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!intent.getAction().equals(Constants.REGISTRATION_COMPLETE)) {
                    return;
                }
                LocalBroadcastManager.getInstance(context).unregisterReceiver(this);

                final String registrationId = intent.getExtras().getString(Constants.REGISTRATION_ID); // Device id obtained from GCM

                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(Constants.REGISTRATION_ID, registrationId);;
                editor.apply();
                /* Create the user-list POST request*/
                String url = Constants.URLS.CONTRACTOR_LIST.string;
                StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                // Automatic login after registration using registered username and password
                                requestWorker();
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "failed to register user");
                            }
                        }
                )
                {
                    @Override
                    //Create the body of the request
                    protected Map<String, String> getParams()
                    {
                        Map<String, String>  params = new HashMap<>();
                        params.put(Constants.COMPANY_NAME, company.getText().toString());
                        params.put("username", username_input);
                        params.put(Constants.PASSWORD, password_input);
                        params.put(Constants.REGISTRATION_ID, registrationId);
                        params.put(Constants.FIRST_NAME, first_name.getText().toString());
                        params.put(Constants.LAST_NAME, last_name.getText().toString());
                        params.put(Constants.PHONE_NUMBER, formatPhoneNumber(phoneNumber.getText().toString()));
                        params.put(Constants.STRIPE_TOKEN, tokenId);
                        params.put("Content-Type","application/json");
                        return params;
                    }
                };
                Volley.newRequestQueue(getApplicationContext()).add(postRequest);
            }

        };

        LocalBroadcastManager.getInstance(this).registerReceiver(tokenBroadcastReceiver,
                new IntentFilter(Constants.REGISTRATION_COMPLETE)); //Set this receiver to look for the REGISTRATION_COMPLETE broadcast
        startIDRegistrationService(); //Start the service to obtain the device id
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(tokenBroadcastReceiver); //unregister this receiver if activity destroyed.
    }

    /**
     * Starts the (GCM) TokenRegistrationService if device has Google Play Services APK
     */
    public void startIDRegistrationService(){
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM and obtain deviceID
            Intent intent = new Intent(this, TokenRegistrationService.class);
            intent.setAction(Constants.ACTION_CREATE_USER);
            startService(intent);
        }
    }

    /**
     * Checks the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public String formatPhoneNumber(String phoneNumber) {
        char[] array = phoneNumber.toCharArray();
        for (int i = 0; i < array.length; i++) {
            if (!Character.isDigit(array[i])) {
                array[i] = ' ';
            }
        }
        String formatted = new String(array);
        return "+1" + formatted.replaceAll("\\s+", "");
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

    public void requestWorker() {
        String url = Constants.URLS.LABOURER_SEARCH.string;
        StringRequest workerRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Toast.makeText(getApplicationContext(), "Worker requested. We will get back to you soon.", Toast.LENGTH_LONG).show();
                        progress.dismiss();
                        Intent loginIntent = new Intent(ContractorRegistrationActivity.this, ContractorLoginActivity.class);
                        loginIntent.setAction(Constants.ACTION_LOGIN);
                        loginIntent.putExtra("username", username.getText().toString());
                        loginIntent.putExtra(Constants.PASSWORD, password.getText().toString());
                        startActivity(loginIntent);
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "There are no workers available at the moment. Please try again soon.",
                                Toast.LENGTH_LONG).show();
                        progress.dismiss();
                        Intent loginIntent = new Intent(ContractorRegistrationActivity.this, ContractorLoginActivity.class);
                        loginIntent.setAction(Constants.ACTION_LOGIN);
                        loginIntent.putExtra("username", username.getText().toString());
                        loginIntent.putExtra(Constants.PASSWORD, password.getText().toString());
                        startActivity(loginIntent);
                        finish();
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
                params.put("start_day", Integer.toString(dayToInt(i.getStringExtra("start_day"))));
                params.put("start_date", i.getStringExtra("start_date"));
                params.put("start_time", i.getStringExtra("start_time"));
                params.put("job_address", i.getStringExtra("job_address"));
                params.put("hat", i.getStringExtra("hat"));
                params.put("vest", i.getStringExtra("vest"));
                params.put("tool", i.getStringExtra("tool"));
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

}

