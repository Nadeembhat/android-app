package com.labourtoday.androidapp.labourer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.labourtoday.androidapp.R;

public class ExperienceSelectActivity extends AppCompatActivity {
    private TextView workerType;
    private RadioGroup radioGroup;
    private final int WORKER_TYPE_SELECT = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experience_select);
        workerType = (TextView) findViewById(R.id.text_type_worker);
        workerType.setText(getIntent().getStringExtra("workerType"));
        radioGroup = (RadioGroup) findViewById(R.id.radio_experience);
    }

    @Override
    public void onBackPressed() {
        setResult(1);
        finish();
    }

    public void next(View button) {
        Intent i = new Intent();
        RadioButton radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
        if (radioButton != null) {
            i.putExtra("workerType", getIntent().getStringExtra("workerType"));
            i.putExtra("workerExp", radioButton.getText().toString());
            i.setAction("workerTypeSelect");
            setResult(WORKER_TYPE_SELECT, i);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), "Please select an experience level", Toast.LENGTH_SHORT).show();
        }
    }
}
