package service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import com.example.PRM391x_AlarmClock_khoidtFX01411.R;

public class AlarmService extends Service {

    MediaPlayer mediaPlayer;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
            mediaPlayer = MediaPlayer.create(this,R.raw.song4);
            mediaPlayer.start();
            return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.start();
    }
}
