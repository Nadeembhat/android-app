package com.labourtoday.androidapp.contractor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.labourtoday.androidapp.R;

import java.util.ArrayList;

public class HireAgainActivity extends AppCompatActivity {
    private ArrayList<String> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hire_again);
        data = getIntent().getStringArrayListExtra("data");
        for (int i = 0; i < data.size(); i++) {
            Log.d("JobDetailActivity", data.get(i));
        }
    }

    public void next(View v) {
        Intent i = new Intent(HireAgainActivity.this, HiringGridActivity.class);
        i.putStringArrayListExtra("data", new ArrayList<String>());
        startActivity(i);
    }
}
