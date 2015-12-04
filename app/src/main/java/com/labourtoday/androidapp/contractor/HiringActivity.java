package com.labourtoday.androidapp.contractor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HiringActivity extends AppCompatActivity {
    private final int PROFILE = 0;
    private final int LOG_OUT = 1;

    private Drawer result;
    String[] experienceList = new String[]{"No", "> 3 months experience", "> 6 months experience", "> 1 year experience", "Red seal"};

    private SharedPreferences settings;
    private ExpandableListView expListView;
    private ArrayList<ExperienceListGroup> expGroupList;
    private ExperienceListAdapter experienceListAdapter;
    private EditText jobAddr;

    private String genExp, genNum, carExp, carNum, conExp, conNum, dryExp, dryNum,
            paintingExp, paintingNum, landExp, landNum, moExp, moNum, roofExp, roofNum,
            brickExp, brickNum, elecExp, elecNum, plumbExp, plumbNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiring);

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
                                case PROFILE:
                                    Intent hireIntent = new Intent(HiringActivity.this, ContractorProfileActivity.class);
                                    startActivity(hireIntent);
                                    return true;
                                case LOG_OUT:
                                    settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                    settings.edit().remove(Constants.AUTH_TOKEN).apply();
                                    settings.edit().remove(Constants.LAST_LOGIN).apply();
                                    // Return to the welcome page
                                    Intent welcomeIntent = new Intent(HiringActivity.this, ContractorLoginActivity.class);
                                    startActivity(welcomeIntent);
                                    finish();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    }).build();
        }

        genExp = genNum = carExp = carNum = conExp = conNum = dryExp = dryNum =
                paintingExp = paintingNum = landExp = landNum = moExp = moNum = roofExp = roofNum =
                brickExp = brickNum = elecExp = elecNum = plumbExp = plumbNum = "0";

        settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        jobAddr = (EditText) findViewById(R.id.edit_address);
        setGroups();

        expListView = (ExpandableListView) findViewById(R.id.list_experiences);
        experienceListAdapter = new ExperienceListAdapter(this, expGroupList);
        expListView.setAdapter(experienceListAdapter);
        setListViewHeight(expListView);

        expListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                setListViewHeight(parent, groupPosition);
                return false;
            }
        });

        expListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;
            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    expListView.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });

        expListView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_hiring, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
        if (item.getItemId() == R.id.action_pay) {
            startActivity(new Intent(HiringActivity.this, PaymentActivity.class));
            return true;
        }
        */
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        }
        else {
            startActivity(new Intent(this, DatePickerActivity.class));
            finish();
        }
    }

    public String generateJobCode() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Intent i = getIntent();
        String jobCode = sharedPreferences.getString(Constants.CONTRACTOR, null).substring(0, 4);
        jobCode += i.getStringExtra("start_date");
        jobCode += i.getStringExtra("start_time");
        jobCode = jobCode.replaceAll("\\s+","");
        sharedPreferences.edit().putString("job_code", jobCode).apply();
        return jobCode;
    }

    public void requestWorker(View view) {
        setUpRequest();
        if (settings.getString(Constants.AUTH_TOKEN, "").equals("")) {
            Intent i = new Intent(getApplicationContext(), ContractorRegistrationActivity.class);
            i.putExtra("genExp", genExp);
            i.putExtra("genNum", genNum);
            i.putExtra("carExp", carExp);
            i.putExtra("carNum", carNum);
            i.putExtra("conExp", conExp);
            i.putExtra("conNum", conNum);
            i.putExtra("dryExp", dryExp);
            i.putExtra("dryNum", dryNum);
            i.putExtra("paintingExp", paintingExp);
            i.putExtra("paintingNum", paintingNum);
            i.putExtra("landExp", landExp);
            i.putExtra("landNum", landNum);
            i.putExtra("moExp", moExp);
            i.putExtra("moNum", moNum);
            i.putExtra("roofExp", roofExp);
            i.putExtra("roofNum", roofNum);
            i.putExtra("brickExp", brickExp);
            i.putExtra("brickNum", brickNum);
            i.putExtra("elecExp", elecExp);
            i.putExtra("elecNum", elecNum);
            i.putExtra("plumbExp", plumbExp);
            i.putExtra("plumbNum", plumbNum);
            startActivity(i);
        } else {
            String url = Constants.URLS.LABOURER_SEARCH.string;
            StringRequest workerRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Toast.makeText(getApplicationContext(), "Worker requested. We will get back to you soon.",
                                    Toast.LENGTH_LONG).show();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), "There are no workers available at the moment. Please try again soon.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
            )
            {
                @Override
                //Create the body of the request
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<>();

                    Intent i = getIntent();

                    //params.put("carpentry", Integer.toString(Arrays.asList(experienceList).indexOf(selectedCarpentry.getText())));
                    //params.put("concrete_forming", Integer.toString(Arrays.asList(experienceList).indexOf(selectedConcrete.getText())));
                    params.put("general_labour", Integer.toString(booleanToInt(settings.getBoolean(Constants.GENERAL_LABOUR, false))));
                    params.put("start_date", i.getStringExtra("start_date"));
                    params.put("start_time", i.getStringExtra("start_time"));
                    params.put("job_address", jobAddr.getText().toString());
                    params.put("wage", i.getStringExtra("wage"));
                    params.put("start_day", Integer.toString(dayToInt(i.getStringExtra("start_day"))));
                    params.put("job_code", generateJobCode());

                    return params;
                }

                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String>  params = new HashMap<>();
                    settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    params.put("Authorization", "Token " + settings.getString(Constants.AUTH_TOKEN, "noToken"));
                    return params;
                }
            };
            Volley.newRequestQueue(getApplicationContext()).add(workerRequest);
        }
    }

    private int booleanToInt(boolean generalLabour) {
        if (generalLabour)
            return 1;
        else
            return 0;
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

    public void setGroups() {
        String experiences[] = { "General labour", "Carpentry", "Concrete", "Dry walling",
                "Painting", "Landscaping", "Machine operating", "Roofing", "Brick laying", "Electrical", "Plumbing" };

        expGroupList = new ArrayList<ExperienceListGroup>();
        for (String exp : experiences) {
            ExperienceListGroup grp = new ExperienceListGroup();
            grp.setName(exp);
            ExperienceListChild child = new ExperienceListChild();
            switch(exp) {
                case "General labour":
                    child.setNumberPicker(R.id.edit_gen_num);
                    child.setRadioGroup(R.id.gen_radio_group);
                    break;
                case "Carpentry":
                    child.setNumberPicker(R.id.edit_car_num);
                    child.setRadioGroup(R.id.car_radio_group);
                    break;
                case "Concrete":
                    child.setNumberPicker(R.id.edit_con_num);
                    child.setRadioGroup(R.id.con_radio_group);
                    break;
                case "Dry walling":
                    child.setNumberPicker(R.id.edit_dry_num);
                    child.setRadioGroup(R.id.dry_radio_group);
                    break;
                case "Painting":
                    child.setNumberPicker(R.id.edit_paint_num);
                    child.setRadioGroup(R.id.paint_radio_group);
                    break;
                case "Landscaping":
                    child.setNumberPicker(R.id.edit_land_num);
                    child.setRadioGroup(R.id.land_radio_group);
                    break;
                case "Machine operating":
                    child.setNumberPicker(R.id.edit_mo_num);
                    child.setRadioGroup(R.id.mo_radio_group);
                    break;
                case "Roofing":
                    child.setNumberPicker(R.id.edit_roof_num);
                    child.setRadioGroup(R.id.roof_radio_group);
                    break;
                case "Brick laying":
                    child.setNumberPicker(R.id.edit_brick_num);
                    child.setRadioGroup(R.id.brick_radio_group);
                    break;
                case "Electrical":
                    child.setNumberPicker(R.id.edit_elec_num);
                    child.setRadioGroup(R.id.elec_radio_group);
                    break;
                case "Plumbing":
                    child.setNumberPicker(R.id.edit_plumb_num);
                    child.setRadioGroup(R.id.plumb_radio_group);
                    break;
                default:
                    break;
            }
            grp.setItem(child);
            expGroupList.add(grp);
        }
    }

    public void setUpRequest() {
        for (int i = 0; i < experienceListAdapter.getGroupCount(); i++) {
            ExperienceListChild child = (ExperienceListChild) experienceListAdapter.getChild(i, 0);
            RadioGroup radioGrp = (RadioGroup) findViewById(child.getRadioGroup());
            RadioButton selectedExp;
            EditText numWorkers;
            String num;
            try {
                selectedExp = (RadioButton) findViewById(radioGrp.getCheckedRadioButtonId());
            } catch (NullPointerException e) {
                continue;
            }
            try {
                numWorkers = (EditText) findViewById(child.getNumberPicker());
                num = numWorkers.getText().toString();
            } catch (NullPointerException e) {
                continue;
            }
            switch (i) {
                case 0:
                    genNum = num;
                    genExp = selectedExp.getText().toString();
                    break;
                case 1:
                    carNum = numWorkers.getText().toString();
                    carExp = selectedExp.getText().toString();
                    break;
                case 2:
                    conNum = numWorkers.getText().toString();
                    conExp = selectedExp.getText().toString();
                    break;
                case 3:
                    dryNum = numWorkers.getText().toString();
                    dryExp = selectedExp.getText().toString();
                    break;
                case 4:
                    paintingNum = numWorkers.getText().toString();
                    paintingExp = selectedExp.getText().toString();
                    break;
                case 5:
                    landNum = numWorkers.getText().toString();
                    landExp = selectedExp.getText().toString();
                    break;
                case 6:
                    moNum = numWorkers.getText().toString();
                    moExp = selectedExp.getText().toString();
                    break;
                case 7:
                    roofNum = numWorkers.getText().toString();
                    roofExp = selectedExp.getText().toString();
                    break;
                case 8:
                    brickNum = numWorkers.getText().toString();
                    brickExp = selectedExp.getText().toString();
                    break;
                case 9:
                    elecNum = numWorkers.getText().toString();
                    elecExp = selectedExp.getText().toString();
                    break;
                case 10:
                    plumbNum = numWorkers.getText().toString();
                    plumbExp = selectedExp.getText().toString();
                default:
                    break;
            }
        }
        Log.d("TESTING GENERAL LABOUR", genExp);
        Log.d("TESTING GENERAL LABOUR", genNum);

        Log.d("TESTING BRICK", brickExp);
        Log.d("TESTING BRICK", brickNum);
    }

    private void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    private void setListViewHeight(ExpandableListView listView, int group) {
        ExpandableListAdapter listAdapter = (ExpandableListAdapter) listView.getExpandableListAdapter();
        int totalHeight = 0;
        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(),
                View.MeasureSpec.EXACTLY);
        for (int i = 0; i < listAdapter.getGroupCount(); i++) {
            View groupItem = listAdapter.getGroupView(i, false, null, listView);
            groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

            totalHeight += groupItem.getMeasuredHeight();

            if (((listView.isGroupExpanded(i)) && (i != group))
                    || ((!listView.isGroupExpanded(i)) && (i == group))) {
                for (int j = 0; j < listAdapter.getChildrenCount(i); j++) {
                    View listItem = listAdapter.getChildView(i, j, false, null,
                            listView);
                    listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);

                    totalHeight += listItem.getMeasuredHeight();

                }
            }
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        int height = totalHeight + (listView.getDividerHeight() * (listAdapter.getGroupCount() - 1));
        if (height < 10)
            height = 200;
        params.height = height;
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

}



