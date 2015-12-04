package com.labourtoday.androidapp.contractor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.R;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.GregorianCalendar;

public class DatePickerActivity extends AppCompatActivity {
    private TimePicker timePicker;
    private DatePicker datePicker;
    private SharedPreferences settings;

    private final int PROFILE = 0;
    private final int LOG_OUT = 1;

    private Drawer result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date_picker);

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Date & time worker is required");
        getSupportActionBar().setSubtitle("When do you need your worker?");

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

        Button next = (Button) findViewById(R.id.next);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        datePicker = (DatePicker) findViewById(R.id.datePicker);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String startDate = datePicker.getYear() + " " + datePicker.getMonth() + " " + datePicker.getDayOfMonth();
                final String startTime = timePicker.getCurrentHour() + " " + timePicker.getCurrentMinute();

                Intent i = new Intent(getApplicationContext(), HiringActivity.class);
                i.putExtra("start_date", startDate);
                i.putExtra("start_time", startTime);
                i.putExtra("start_day", DateFormat.format("EEEE",
                        new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth())).toString());
                startActivity(i);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        }
        if (settings.getString(Constants.AUTH_TOKEN, "").equals("")) {
            super.onBackPressed();
        }
    }
}


