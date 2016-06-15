package com.labourtoday.androidapp.contractor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.labourtoday.androidapp.ExpandableHeightGridView;
import com.labourtoday.androidapp.MainActivity;
import com.labourtoday.androidapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class HiringGridActivity extends AppCompatActivity {
    private ExpandableHeightGridView gridView;
    private GridAdapter adapter;
    private HashMap<String, String> selectedTypes;
    private Button button;
    ArrayList<String> data;

    static final String[] typeWorkers = new String[] {
            "General Labour",
            "Carpentry",
            "Concrete",
            "Landscaping",
            "Painting",
            "Dry Walling",
            "Roofing",
            "Machine Operation",
            "Plumbing",
            "Electrical"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hiring_grid);
        data = new ArrayList<>();
        selectedTypes = new HashMap<>();
        button = (Button) findViewById(R.id.button_next);
        button.setVisibility(View.GONE);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) {
            selectedTypes.put(data.getStringExtra("workerType"), data.getStringArrayListExtra("workerRequest").get(0));
            adapter.notifyDataSetChanged();
            this.data.add(data.getStringExtra("workerType"));
            this.data.addAll(data.getStringArrayListExtra("workerRequest"));
            button.setVisibility(View.VISIBLE);
        }
    }

    public void next(View view) {
        Intent jobDetailIntent = new Intent(HiringGridActivity.this, JobDetailActivity.class);
        jobDetailIntent.putStringArrayListExtra("data", data);
        startActivity(jobDetailIntent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (getIntent().getAction().equals("Rehire")) {
            Intent mainIntent = new Intent(new Intent(this, MainActivity.class));
            mainIntent.setAction("Rehire");
            startActivity(mainIntent);
            finish();
        }
    }

}
