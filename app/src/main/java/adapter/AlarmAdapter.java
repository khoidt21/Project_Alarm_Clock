
package adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.ToggleButton;
import com.example.PRM391x_AlarmClock_khoidtFX01411.AddAlarmActivity;
import com.example.PRM391x_AlarmClock_khoidtFX01411.R;
import dbhelper.AlarmDbHelper;
import listener.AlarmListener;
import java.util.List;
import model.Alarm;

    public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder>  {

    List<Alarm> alarms;
    AlarmListener alarmListener;
    AlarmDbHelper alarmDbHelper;
    private Context context;

    public AlarmAdapter(Context context,List<Alarm> alarms, AlarmListener l) {
        this.alarms = alarms;
        this.context = context;
        this.alarmListener = l;
    }

    public AlarmAdapter() {
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.alarm_item, viewGroup, false);
        return new ViewHolder(view);
    }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int i) {

            // xu ly set du lieu len textview

            TextView hourShow = viewHolder.itemView.findViewById(R.id.txtHour);
            TextView amPm = viewHolder.itemView.findViewById(R.id.txtAmPm);
            TextView event = viewHolder.itemView.findViewById(R.id.txtEvent);
            final ToggleButton toggleButton = viewHolder.itemView.findViewById(R.id.tglAlarm);
            final Alarm alarm = alarms.get(i);
            final int hour = alarms.get(i).getHour();
            final int minute = alarms.get(i).getMinute();
            String eventAlarm = alarms.get(i).getEvent();

            if(alarms.get(i).getMinute() < 10){
                hourShow.setText(hour + ":" + 0 + minute);
            }
            else {
                hourShow.setText(hour + ":" + minute);
            }
            String am_pm = (hour < 12) ? "AM" : "PM";

            amPm.setText(am_pm);
            event.setText(alarms.get(i).getEvent());
            final boolean toggle = alarms.get(i).isToggleOnOff();
            if(toggle == false){
                toggleButton.setChecked(false);
            }
            else if(toggle == true) {
                toggleButton.setChecked(true);
            }
            // update trang thai button
            toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        alarm.setToggleOnOff(true);
                        alarmDbHelper = new AlarmDbHelper(context);
                        alarmListener.startAlarm(alarm,i);
                        alarmDbHelper.updateAlarm(alarm,hour,minute);
                    }else{
                        alarm.setToggleOnOff(false);
                        alarmListener.cancelAlarm(alarm,i);
                        alarmDbHelper = new AlarmDbHelper(context);
                        alarmDbHelper.updateAlarm(alarm,hour,minute);
                    }
                }
            });
        }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, PopupMenu.OnMenuItemClickListener {

        public ViewHolder(View view){
             super(view);
             view.setOnCreateContextMenuListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {

            int position = getLayoutPosition();
            alarmListener.onMenuAction(item,position);
            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            // Khoi tao doi tuong PopupMenu
            PopupMenu popupMenu = new PopupMenu(v.getContext(),v);
            popupMenu.getMenuInflater().inflate(R.menu.menu_main,popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(this);
            popupMenu.show();
        }
    }

    @Override
    public int getItemCount() {
        return alarms.size();
    }
}