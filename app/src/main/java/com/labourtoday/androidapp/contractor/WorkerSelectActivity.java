package com.labourtoday.androidapp.contractor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.labourtoday.androidapp.R;

import java.util.ArrayList;

public class WorkerSelectActivity extends AppCompatActivity {
    private Spinner six_months, year, two_year, red;
    private final int WORKER_TYPE_SELECT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker_select);

        TextView workerType = (TextView) findViewById(R.id.text_type_worker);
        workerType.setText(getIntent().getStringExtra("workerType"));
        six_months = (Spinner) findViewById(R.id.spinner_six_months);
        year = (Spinner) findViewById(R.id.spinner_one_years);
        two_year = (Spinner) findViewById(R.id.spinner_two_years);
        red = (Spinner) findViewById(R.id.spinner_red_seal);

        Integer[] items = new Integer[]{0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<>(this, R.layout.spinner_custom, items);
        six_months.setAdapter(adapter);
        year.setAdapter(adapter);
        two_year.setAdapter(adapter);
        red.setAdapter(adapter);
    }

    public void next(View button) {
        Intent i = new Intent();
        if (!noneSelected()) {
            i.putExtra("workerType", getIntent().getStringExtra("workerType"));
            i.putStringArrayListExtra("workerRequest", getSelectData());
            i.setAction("workerTypeSelect");
            setResult(WORKER_TYPE_SELECT, i);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Please select workers", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean noneSelected() {
        if (six_months.getSelectedItem().toString().equals("0") &&
                year.getSelectedItem().toString().equals("0") &&
                two_year.getSelectedItem().toString().equals("0") &&
                red.getSelectedItem().toString().equals("0")) {
            return true;
        }
        return false;
    }

    public ArrayList<String> getSelectData() {
        ArrayList<String> data = new ArrayList<>();
        data.add("Six Months Experience: " + six_months.getSelectedItem().toString());
        data.add("One Year Experience: " + year.getSelectedItem().toString());
        data.add("Two Years Experience: " + two_year.getSelectedItem().toString());
        data.add("Red Seal: " + red.getSelectedItem().toString());
        return data;
    }

    @Override
    public void onBackPressed() {
        setResult(1);
        finish();
    }
}
