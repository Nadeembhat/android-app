package com.labourtoday.androidapp.contractor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.R;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class RadioGroupActivity extends AppCompatActivity {
    private final int groupPosition = 0;
    private final int LOG_OUT = 1;

    private SharedPreferences settings;
    private Drawer result;

    private TextView workerType;
    private RadioGroup radioGroup;
    private RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_radio_group);

        workerType = (TextView) findViewById(R.id.text_type_worker);
        workerType.setText(getIntent().getStringExtra("workerType"));
        radioGroup = (RadioGroup) findViewById(R.id.checkboxes);

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Labour Today");

        if (!settings.getString(Constants.AUTH_TOKEN, "").equals("")) {
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
                                case groupPosition:
                                    Intent hireIntent = new Intent(RadioGroupActivity.this, ContractorProfileActivity.class);
                                    startActivity(hireIntent);
                                    return true;
                                case LOG_OUT:
                                    settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    settings.edit().remove(Constants.AUTH_TOKEN).apply();
                                    settings.edit().remove(Constants.LAST_LOGIN).apply();
                                    // Return to the welcome page
                                    Intent welcomeIntent = new Intent(RadioGroupActivity.this, ContractorLoginActivity.class);
                                    startActivity(welcomeIntent);
                                    finish();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    }).build();
        }
    }

    public void next(View button) {
        Intent i = new Intent(RadioGroupActivity.this, DatePickerActivity.class);
        radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());

        if (radioButton != null) {
            i.putExtra("workerExp", radioButton.getText().toString());
            i.putExtra("workerType", workerType.getText().toString());
            startActivity(i);
        } else {
            Toast.makeText(getApplicationContext(), "Please select an experience level", Toast.LENGTH_SHORT).show();
        }
    }
}
