package com.example.PRM391x_AlarmClock_khoidtFX01411;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.PRM391x_AlarmClock_khoidtFX01411.R;

import java.util.ArrayList;
import java.util.Calendar;

import adapter.AlarmAdapter;
import dbhelper.AlarmDbHelper;
import lib.DividerItemDecoration;
import listener.AlarmListener;
import model.Alarm;
import receiver.AlarmReceiver;

public class MainActivity extends AppCompatActivity implements AlarmListener, View.OnCreateContextMenuListener {

    private static final int REQUEST_CODE = 0 ;
    Toolbar toolbar;
    RecyclerView recyclerView;
    public ArrayList<Alarm> listAlarm = new ArrayList<>();
    public static final int REQUEST_ALARM = 2048;
    AlarmManager alarmManager;
    AlarmAdapter alarmAdapter;

    RecyclerView.LayoutManager layoutManager;

    String title = "Alarm";

    AlarmDbHelper alarmDB = null;
    Alarm updateAlarm;
    int hourOld;
    int minuteOld;
    int indexOfAlarm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set title cho action bar
        initView();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        alarmDB = new AlarmDbHelper(getApplicationContext());;
        listAlarm = alarmDB.getAlarms();
        setAlarmToAdapter();
    }

    public void initView(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        // xu ly border recyclerView
        recyclerView.addItemDecoration(new DividerItemDecoration(getResources()));
        // khoi tao alarmManager
        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
    }
    // ham set alarm vao adapter
    public void setAlarmToAdapter(){

        alarmAdapter = new AlarmAdapter(this, listAlarm, this);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(alarmAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_on_toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.add_alarm :
                Intent myIntent = new Intent(MainActivity.this, AddAlarmActivity.class);
                myIntent.putExtra("action","add");
                MainActivity.this.startActivityForResult(myIntent, REQUEST_ALARM);
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // lay gia tri du lieu moi tu man hinh add hoac edit
            try{
            int hour = data.getIntExtra("HOUR", 0);
            int minute = data.getIntExtra("MINUTE", 0);
            String event = data.getStringExtra("EVENT");



        // add alarm
        if (requestCode == REQUEST_ALARM) {
                boolean isToggleOnOff = false;
                // Tao doi tuong alarm moi
                Alarm alarm = new Alarm(hour,minute,event,isToggleOnOff);
                listAlarm.add(alarm);
                insertNewAlarm(alarm);
            }
        // update alarm
            else if(requestCode == 1022){
                 if(resultCode == 1025) {

                     updateAlarm.setHour(hour);
                     updateAlarm.setMinute(minute);
                     updateAlarm.setEvent(event);
                     alarmDB.updateAlarm(updateAlarm, hourOld, minuteOld);
                     alarmAdapter.notifyItemChanged(indexOfAlarm);
                 }
            }
            }catch (Exception ex){
                System.out.println(ex.getMessage());
            }
        }


    //ham them 1 alar moi
    private void insertNewAlarm(Alarm alarm) {
        //cap nhat them moi mot alarm
        int index = alarmAdapter.getItemCount();
        alarmAdapter.notifyItemInserted(index);
        alarmDB.addAlarm(alarm);
    }

    @Override
    public void startAlarm(Alarm alarm, int requestCode) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        calendar.set(Calendar.MINUTE, alarm.getMinute());
        Intent intent_alarm_receiver = new Intent(MainActivity.this, AlarmReceiver.class);
        intent_alarm_receiver.putExtra("music_flag",true);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,requestCode,intent_alarm_receiver,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),10000,pendingIntent);

    }

    @Override
    public void cancelAlarm(Alarm alarm, int requestCode) {
        Intent intent_alarm_receiver = new Intent(MainActivity.this,AlarmReceiver.class);
        intent_alarm_receiver.putExtra("music_flag",false);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,requestCode,intent_alarm_receiver,PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        sendBroadcast(intent_alarm_receiver);
        Toast.makeText(this, "Alarm stopped!", Toast.LENGTH_SHORT).show();

    }
    // ham xu ly menu edit and delete
    @Override
    public void onMenuAction(MenuItem item, int position) {
        updateAlarm = listAlarm.get(position);
        indexOfAlarm = position;
        switch (item.getItemId()){
            case R.id.menuEdit:
                 Intent intent = new Intent(MainActivity.this,AddAlarmActivity.class);

                 hourOld = listAlarm.get(indexOfAlarm).getHour();
                 minuteOld = listAlarm.get(indexOfAlarm).getMinute();
                 intent.putExtra("action","edit");
                 startActivityForResult(intent,1022);
                 break;

            case R.id.menuDelete:
                 int hour = listAlarm.get(indexOfAlarm).getHour();
                 int minute = listAlarm.get(indexOfAlarm).getMinute();
                 alarmDB.deleteAlarm(hour,minute);
                 if (indexOfAlarm == listAlarm.size() - 1) {
                    listAlarm.remove(indexOfAlarm);
                    alarmAdapter.notifyItemRemoved(indexOfAlarm);
                    cancelAlarm(listAlarm.get(indexOfAlarm - 1),indexOfAlarm);
                 } else {
                    int shift = 1;
                    while (true) {
                        try {
                            listAlarm.remove(indexOfAlarm);
                            alarmAdapter.notifyItemRemoved(indexOfAlarm);
                            break;
                        } catch (IndexOutOfBoundsException e) {
                            shift++;
                        }
                    }
                 }
                 break;
        }
    }
}