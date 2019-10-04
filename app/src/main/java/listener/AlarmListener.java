package listener;

import android.view.MenuItem;

import model.Alarm;

public interface AlarmListener {

    public void startAlarm(Alarm alarm,int requestCode);
    public void cancelAlarm(Alarm alarm,int requestCode);
    public void onMenuAction(MenuItem item,int position);

}
