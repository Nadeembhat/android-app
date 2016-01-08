package com.labourtoday.androidapp.contractor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.R;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class HiringGridActivity extends AppCompatActivity {
    private final int groupPosition = 0;
    private final int LOG_OUT = 1;

    private GridView gridView;
    private SharedPreferences settings;
    private Drawer result;

    static final String[] typeWorkers = new String[] {
            "General Labour",
            "Carpenter",
            "Concrete",
            "Landscaper",
            "Painter",
            "Drywaller",
            "Roofer",
            "Machine Operator",
            "Plumber",
            "Electrician"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiring_grid);

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
                                    Intent hireIntent = new Intent(HiringGridActivity.this, ContractorProfileActivity.class);
                                    startActivity(hireIntent);
                                    return true;
                                case LOG_OUT:
                                    settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    settings.edit().remove(Constants.AUTH_TOKEN).apply();
                                    settings.edit().remove(Constants.LAST_LOGIN).apply();
                                    // Return to the welcome page
                                    Intent welcomeIntent = new Intent(HiringGridActivity.this, ContractorLoginActivity.class);
                                    startActivity(welcomeIntent);
                                    finish();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    }).build();
        }


        gridView = (GridView) findViewById(R.id.grid_worker);

        gridView.setAdapter(new GridAdapter(this, typeWorkers));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Toast.makeText(getApplicationContext(), ((TextView) v.findViewById(R.id.grid_item_label)).getText(), Toast.LENGTH_SHORT).show();
                Intent radioGrpIntent = new Intent(HiringGridActivity.this, RadioGroupActivity.class);
                radioGrpIntent.putExtra("workerType", ((TextView) v.findViewById(R.id.grid_item_label)).getText());
                startActivity(radioGrpIntent);
            }
        });
    }
}
