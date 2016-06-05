package com.labourtoday.androidapp.labourer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.labourtoday.androidapp.ExpandableHeightGridView;
import com.labourtoday.androidapp.R;
import com.labourtoday.androidapp.contractor.GridAdapter;

import java.util.ArrayList;

public class LabourerGridActivity extends AppCompatActivity {
    private ExpandableHeightGridView gridView;
    private GridAdapter adapter;
    private ArrayList<String> data, selectedTypes;
    private Button button;

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
        data = new ArrayList<>();
        selectedTypes = new ArrayList<>();
        adapter = new GridAdapter(this, typeWorkers, selectedTypes);
        button = (Button) findViewById(R.id.button_next);
        button.setVisibility(View.GONE);
        gridView = (ExpandableHeightGridView) findViewById(R.id.grid_worker);
        gridView.setExpanded(true);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                Intent expSelectIntent = new Intent(LabourerGridActivity.this, ExperienceSelectActivity.class);
                expSelectIntent.putExtra("workerType", ((TextView) v.findViewById(R.id.grid_item_label)).getText());
                startActivityForResult(expSelectIntent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0) {
            selectedTypes.add(data.getStringExtra("workerType"));
            adapter.notifyDataSetChanged();
            this.data.add(data.getStringExtra("workerType"));
            this.data.add(data.getStringExtra("workerExp"));
            button.setVisibility(View.VISIBLE);
        }
    }

    public void next(View view) {
        Intent next = new Intent(LabourerGridActivity.this, LabourerInfoActivity.class);
        next.putStringArrayListExtra("data", data);
        startActivity(next);
    }

}
