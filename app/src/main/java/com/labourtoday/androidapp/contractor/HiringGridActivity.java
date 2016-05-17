package com.labourtoday.androidapp.contractor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.labourtoday.androidapp.ExpandableHeightGridView;
import com.labourtoday.androidapp.R;

import java.util.ArrayList;

public class HiringGridActivity extends AppCompatActivity {
    private ExpandableHeightGridView gridView;
    private GridAdapter adapter;
    private ArrayList<String> selectedTypes = new ArrayList<>();
    private ArrayList<String> data;
    private Button button;
    private SharedPreferences settings;

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
        button = (Button) findViewById(R.id.button_next);
        button.setVisibility(View.GONE);
        data = getIntent().getStringArrayListExtra("data");
        adapter = new GridAdapter(this, typeWorkers, selectedTypes);
        gridView = (ExpandableHeightGridView) findViewById(R.id.grid_worker);
        gridView.setExpanded(true);
        gridView.setAdapter(adapter);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent workerSelectIntent = new Intent(HiringGridActivity.this, WorkerSelectActivity.class);
                workerSelectIntent.putExtra("workerType", ((TextView) v.findViewById(R.id.grid_item_label)).getText());
                startActivityForResult(workerSelectIntent, 0);
            }
        });
        addUserDetails();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) {
            selectedTypes.add(data.getStringExtra("workerType"));
            adapter.notifyDataSetChanged();
            this.data.add(data.getStringExtra("workerType") + ":");
            this.data.addAll(data.getStringArrayListExtra("workerRequest"));
            button.setVisibility(View.VISIBLE);
        }
    }

    public void next(View view) {
        Intent next = new Intent(HiringGridActivity.this, JobDetailActivity.class);
        next.putStringArrayListExtra("data", this.data);
        startActivity(next);
    }

    public void addUserDetails() {
        //Set<String> data = new HashSet<>();
        data.add("Your Details:");
        data.add("Email: " + settings.getString("Email", ""));
        data.add("Name: " + settings.getString("Name", ""));
        data.add("Company: " + settings.getString("Company", ""));
        // data.add("Password: " + password.getText().toString());
        data.add("Phone Number: " + settings.getString("Phone Number", ""));
        data.add("Requested Worker Details:");
        //settings.edit().putStringSet("contractorDetail", data).apply();
    }

}
