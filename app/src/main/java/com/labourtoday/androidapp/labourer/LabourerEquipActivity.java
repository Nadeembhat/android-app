package com.labourtoday.androidapp.labourer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.labourtoday.androidapp.R;

import java.util.ArrayList;

public class LabourerEquipActivity extends AppCompatActivity {
    private ArrayList<String> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labourer_equip);
        data = getIntent().getStringArrayListExtra("data");
        for (int i = 0; i < data.size(); i++) {
            Log.d("LabourerInfo", data.get(i));
        }
    }
}
