package com.example.bill.assignmentiot;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DetailView extends AppCompatActivity {

    public static final String MAC_01 = "00:18:E4:00:11:E4";
    TextView textViewPot; // Name of the Pot
    TextView textViewHumid;
    TextView textViewPlant;
    TextView textViewDate;
//    TextView textViewStatus;
    ImageView imageWaterCan;


    DatabaseReference databasePlants;
    DatabaseReference databaseControl;

    private LineChart lineChart;
    ArrayList<Entry> yData;
    DatabaseReference mPostReference;
    ValueEventListener valueEventListener;

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
        textViewPlant = (TextView) findViewById(R.id.textPlant);
        textViewDate = (TextView) findViewById(R.id.textViewID);
//        textViewStatus = (TextView) findViewById(R.id.textViewStatus);

        Intent intent = getIntent();
        final String pot = intent.getStringExtra(MainActivity.POT_ID);
        String humid = intent.getStringExtra(MainActivity.POT_VALUE);
        String date = intent.getStringExtra(MainActivity.POT_TIME);
        String type = intent.getStringExtra(MainActivity.POT_TYPE);
        Toast.makeText(this, type, Toast.LENGTH_SHORT).show();

        textViewPot.setText(pot);
        textViewHumid.setText(humid);
        textViewDate.setText(date);

        databasePlants = FirebaseDatabase.getInstance().getReference("plant").child(type);
        databasePlants.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name;
                Plants plant = dataSnapshot.getValue(Plants.class);
                if (plant != null){
                    name = plant.getName();
                }
                else name = "undefined";
                textViewPlant.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DetailView.this, "read plant failed", Toast.LENGTH_SHORT).show();
            }
        });



        lineChart = (LineChart) findViewById(R.id.LineChart);

        mPostReference = FirebaseDatabase.getInstance().getReference("db").child("user1").child("0").child("data");
        databaseControl = FirebaseDatabase.getInstance().getReference("db").child("user0").child(MAC_01).child("pot1");


        imageWaterCan = (ImageView) findViewById(R.id.imageViewWaterCanIcon);
        imageWaterCan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DetailView.this, "Hello", Toast.LENGTH_SHORT).show();
                String code = "R" + pot + "00" + MAC_01;
                Commands key = new Commands(code);
                databaseControl.child("commands").setValue(key);
            }
        });
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
                lineChart.setData(data);
                lineChart.notifyDataSetChanged();
                lineChart.invalidate();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DetailView.this, "Fail to load post", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.automation_menu, menu);
        return true;
    }
}
