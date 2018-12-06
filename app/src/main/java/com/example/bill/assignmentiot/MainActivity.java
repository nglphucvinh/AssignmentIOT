package com.example.bill.assignmentiot;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.bill.assignmentiot.Notify.CHANNEL_1_ID;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MyActivity";

    public static final String POT_ID = "potID";
    public static final String POT_VALUE = "someValue";
    public static final String POT_TIME = "timeCurrent";
    public static final String POT_NAME = "potName";
    public static final String POT_TYPE = "potType";

    public static final String NOTIFICATION_TITLE = "Smart Pot";
    public static final String NOTIFICATION_MESSAGE = "Some of your plants are drying out, Water them to keep them healthy!";
    public boolean notiFlag = true;
    // Declare Notification Compat for API 25 and lower
    private NotificationManagerCompat notificationManager;

    DatabaseReference databasePot;

    ListView listViewPots;
    List<Param> Pots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Custom Toolbar for notification Icon
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Notification for API 25 or lower
        notificationManager = NotificationManagerCompat.from(this);
        databasePot = FirebaseDatabase.getInstance().getReference("db").child("user1").child("00:18:E4:00:11:E4").child("pot1").child("logs");

        // Try setting value in firebase
//        Param pot = new Param("Pot1","25", "01/12/2018 13:49:30", "0");
//        databasePot.child("1").setValue(pot);

        Pots = new ArrayList<>();

        // Inflate view on list
        listViewPots = (ListView) findViewById(R.id.listPots);
        listViewPots.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Getting the selected pot
                Param param = Pots.get(position);

                // Creating an intent
                Intent intent = new Intent(getApplicationContext(), DetailView.class);

                //putting param name and id to intent
                intent.putExtra(POT_ID, param.getID());
                intent.putExtra(POT_VALUE, param.getValue());
                intent.putExtra(POT_TIME, param.getCurrentTime());
                intent.putExtra(POT_TYPE, param.getType());

                //starting the activity with intent
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.notification_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.itemNotification:
                if (notiFlag){
                    Drawable notiOff = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_notifications_off, null);
                    item.setIcon(notiOff);
                    notiFlag = false;

                    Toast.makeText(this, "Notification turned off", Toast.LENGTH_SHORT).show();
                    return true;
                }
                else{
                    Drawable notiOn = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_notifications,null);
                    item.setIcon(notiOn);
                    notiFlag = true;

                    Toast.makeText(this, "Notification turned on", Toast.LENGTH_SHORT).show();
                    return true;
                }
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        databasePot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Pots.clear();

                for(DataSnapshot paramSnapshot : dataSnapshot.getChildren()){
                    Param pot = paramSnapshot.getValue(Param.class);

                    Pots.add(pot);
                    if (pot != null){
                        Integer a = Integer.parseInt(pot.getRawValue());
                        if (a <= 20){
                            sendOnChannel1();
                        }
                    }

                }

                PotList adapter = new PotList(MainActivity.this, Pots);
                listViewPots.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Log.w(this, "Failed to read value.", error.toException());
            }
        });
    }

    public void sendOnChannel1(){
        Intent activityIntent = new Intent(this, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);
        // Set Action
        Intent broadcastIntent = new Intent(this, NotificationReceiver.class);
        broadcastIntent.putExtra("toastMessage", "hello");
        PendingIntent actionIntent = PendingIntent.getBroadcast(this,0,broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Icon in Notification
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.plant);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setSmallIcon(R.drawable.ic_android_black)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText(NOTIFICATION_MESSAGE)
                .setLargeIcon(largeIcon)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.long_dummy_text))
                        .setBigContentTitle("Smart Pot")
                        .setSummaryText("Pot Status"))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setColor(Color.BLUE)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .addAction(R.mipmap.ic_launcher,"Toast",actionIntent)
                .build();

        notificationManager.notify(1,notification);
    }
}
