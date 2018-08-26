package com.example.michaeleliot.interstorydrift;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;

public class FloorCreate extends AppCompatActivity {
    TextView mFloorName;
    EditText mFloorNameEntry;
    Button mConfirmButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor_create);
        mFloorName = findViewById(R.id.floor_name);
        mFloorNameEntry = findViewById(R.id.floor_name_entry);
        mConfirmButton = findViewById(R.id.confirm_button);
        mConfirmButton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String floorName = mFloorNameEntry.getText().toString();
                Intent intent = new Intent(FloorCreate.this, MainActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, floorName);
                intent.putExtra("floor_data", getIntent().getStringExtra("floor_data"));
                startActivity(intent);
            }
        });
    }

}
