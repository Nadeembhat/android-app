package com.labourtoday.androidapp.labourer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.labourtoday.androidapp.MainActivity;
import com.labourtoday.androidapp.R;

public class LabourerMessageActivity extends AppCompatActivity {
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_labourer_message);
/*
        for (String s : settings.getStringSet("labourerDetail", new HashSet<String>())) {
            Log.i("LM", s);
        }
        ArrayList<String> list = getIntent().getStringArrayListExtra("data");
        for (int i = 0; i < list.size(); i++) {
            Log.i("LM", list.get(i));
        }
        for (String s : settings.getStringSet("reference", new HashSet<String>())) {
            Log.i("LM", s);
        }
        for (String s : settings.getStringSet("availability", new HashSet<String>())) {
            Log.i("LM", s);
        }
        for (String s : settings.getStringSet("equipment", new HashSet<String>())) {
            Log.i("LM", s);
        }
        for (String s : settings.getStringSet("notificationPreference", new HashSet<String>())) {
            Log.i("LM", s);
        }
*/
        relativeLayout = (RelativeLayout) findViewById(R.id.screen);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                next();
            }
        });
    }

    public void next() {
        Intent i = new Intent(LabourerMessageActivity.this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setAction("Exit");
        startActivity(i);
        finish();
    }
}
