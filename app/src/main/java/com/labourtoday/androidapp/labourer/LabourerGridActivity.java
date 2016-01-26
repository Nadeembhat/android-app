package com.labourtoday.androidapp.labourer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.labourtoday.androidapp.Constants;
import com.labourtoday.androidapp.ExpandableHeightGridView;
import com.labourtoday.androidapp.R;
import com.labourtoday.androidapp.contractor.GridAdapter;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import java.util.Arrays;

public class LabourerGridActivity extends AppCompatActivity {

    private final int groupPosition = 0;
    private final int LOG_OUT = 1;

    private ExpandableHeightGridView gridView;
    private SharedPreferences settings;
    private Drawer result;
    private String[] experienceList = new String[]{"No", "> 3 months experience (20/hr)", "> 6 months experience (22/hr)", "> 1 year experience (26/hr)", "Red seal (30/hr)"};

    private String gen, carpentry, concrete, land, paint, dry, roof, mo, plumbing, elec;

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
        setContentView(R.layout.activity_labourer_grid);
        gen = carpentry = concrete = land = paint = dry = roof = mo = plumbing = elec = "0";
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
                                    Intent hireIntent = new Intent(LabourerGridActivity.this, LabourerProfileActivity.class);
                                    startActivity(hireIntent);
                                    return true;
                                case LOG_OUT:
                                    settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    settings.edit().remove(Constants.AUTH_TOKEN).apply();
                                    settings.edit().remove(Constants.LAST_LOGIN).apply();
                                    // Return to the welcome page
                                    Intent welcomeIntent = new Intent(LabourerGridActivity.this, LabourerLoginActivity.class);
                                    startActivity(welcomeIntent);
                                    finish();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    }).build();
        }


        gridView = (ExpandableHeightGridView) findViewById(R.id.grid_worker);
        gridView.setExpanded(true);
        gridView.setAdapter(new GridAdapter(this, typeWorkers));

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                // Toast.makeText(getApplicationContext(), ((TextView) v.findViewById(R.id.grid_item_label)).getText(), Toast.LENGTH_SHORT).show();
                //Intent radioGrpIntent = new Intent(HiringGridActivity.this, RadioGroupActivity.class);
                //radioGrpIntent.putExtra("workerType", ((TextView) v.findViewById(R.id.grid_item_label)).getText());
                //startActivity(radioGrpIntent);
                showPopup(v);
            }
        });
    }

    public void showPopup(final View anchorView) {

        final View popupView = getLayoutInflater().inflate(R.layout.popup_radio, null);

        final PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        // Example: If you have a TextView inside `popup_layout.xml`
        TextView tv = (TextView) anchorView.findViewById(R.id.grid_item_label);
        final TextView workerType = (TextView) popupView.findViewById(R.id.text_type_worker);
        workerType.setText(tv.getText().toString());
        final RadioGroup radioGroup = (RadioGroup) popupView.findViewById(R.id.checkboxes);
        Button next = (Button) popupView.findViewById(R.id.button_next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton radioButton = (RadioButton) popupView.findViewById(radioGroup.getCheckedRadioButtonId());
                if (radioButton == null) {
                    Toast.makeText(getApplicationContext(), "Please select your experience level", Toast.LENGTH_SHORT).show();
                    return;
                }
                switch (workerType.getText().toString()) {
                    case "General Labour":
                        gen = Integer.toString(Arrays.asList(experienceList).indexOf(radioButton.getText().toString()));
                        break;
                    case "Carpenter":
                        carpentry = Integer.toString(Arrays.asList(experienceList).indexOf(radioButton.getText().toString()));
                        break;
                    case "Concrete":
                        concrete = Integer.toString(Arrays.asList(experienceList).indexOf(radioButton.getText().toString()));
                        break;
                    case "Drywaller":
                        dry = Integer.toString(Arrays.asList(experienceList).indexOf(radioButton.getText().toString()));
                        break;
                    case "Painter":
                        paint = Integer.toString(Arrays.asList(experienceList).indexOf(radioButton.getText().toString()));
                        break;
                    case "Landscaper":
                        land = Integer.toString(Arrays.asList(experienceList).indexOf(radioButton.getText().toString()));
                        break;
                    case "Machine Operator":
                        mo = Integer.toString(Arrays.asList(experienceList).indexOf(radioButton.getText().toString()));
                        break;
                    case "Roofer":
                        roof = Integer.toString(Arrays.asList(experienceList).indexOf(radioButton.getText().toString()));
                        break;
                    case "Plumber":
                        plumbing = Integer.toString(Arrays.asList(experienceList).indexOf(radioButton.getText().toString()));
                        break;
                    case "Electrician":
                        elec = Integer.toString(Arrays.asList(experienceList).indexOf(radioButton.getText().toString()));
                        break;
                    default:
                        break;
                }
                anchorView.setBackgroundColor(Color.parseColor("#80CBC4"));
                popupWindow.dismiss();

            }
        });

        // If the PopupWindow should be focusable
        popupWindow.setFocusable(true);

        // If you need the PopupWindow to dismiss when when touched outside
        popupWindow.setBackgroundDrawable(new ColorDrawable());

        //int location[] = new int[2];

        // Get the View's(the one that was clicked in the Fragment) location
        //anchorView.getLocationOnScreen(location);

        // Using location, the PopupWindow will be displayed right under anchorView
        popupWindow.showAtLocation(gridView, Gravity.CENTER, 0, 15);

    }

    public void nextLabourer(View view) {
        Intent i = new Intent(LabourerGridActivity.this, LabourerAvailabilityActivity.class);
        i.putExtra("general_labour", gen);
        i.putExtra("carpentry", carpentry);
        i.putExtra("concrete", concrete);
        i.putExtra("landscaping", land);
        i.putExtra("painting", paint);
        i.putExtra("drywalling", dry);
        i.putExtra("roofing", roof);
        i.putExtra("machine_operation", mo);
        i.putExtra("plumbing", plumbing);
        i.putExtra("electrical", elec);
        startActivity(i);
    }
}
