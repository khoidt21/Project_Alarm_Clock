package receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import service.AlarmService;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Boolean check = intent.getExtras().getBoolean("music_flag");
        if(check==true){
            Intent intentMusic = new Intent(context, AlarmService.class);
            context.startService(intentMusic);
        }else{
            Intent intentMusic = new Intent(context, AlarmService.class);
            context.stopService(intentMusic);
        }

        Intent myIntent = new Intent(context, AlarmService.class);
        context.startService(intent);
    }

}
