package com.example.bill.assignmentiot;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DetailView extends AppCompatActivity {

    TextView textViewPot;
    TextView textViewHumid;
    TextView textViewPlant;
    TextView textViewID;
//    TextView textViewStatus;

    DatabaseReference databasePlants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_view);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        textViewPot = (TextView) findViewById(R.id.textPot);
        textViewHumid = (TextView) findViewById(R.id.textHumid);
//        textViewPlant = (TextView) findViewById(R.id.textPlant);
//        textViewID = (TextView) findViewById(R.id.textViewID);
//        textViewStatus = (TextView) findViewById(R.id.textViewStatus);

        Intent intent = getIntent();
        String pot = intent.getStringExtra(MainActivity.POT_ID);
        String humid = intent.getStringExtra(MainActivity.POT_VALUE);

        textViewPot.setText(pot);
        textViewHumid.setText(humid);

//        databasePlants = FirebaseDatabase.getInstance().getReference("plants").child(pot);

    }

    // Graphing starts when detail view is inflated
    @Override
    protected void onStart() {
        super.onStart();
        mPostReference.addValueEventListener(valueEventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                yData = new ArrayList<>();
                float i =0;
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    i=i+1;
                    String SV = ds.child("value").getValue().toString();
                    Float SensorValue = Float.parseFloat(SV);
                    yData.add(new Entry(i,SensorValue));
                }
                final LineDataSet lineDataSet = new LineDataSet(yData,"Humidity");
                LineData data = new LineData(lineDataSet);
                Temp_linechart.setData(data);
                Temp_linechart.notifyDataSetChanged();
                Temp_linechart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Fail to load post", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
