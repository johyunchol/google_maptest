package kr.co.kkensu.maptest;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

public class LocationService extends Service {
    public static final String TAG = "LocationService";
    public static final String CHANNEL_ID = "channel_id";
    public static final int NOTI_SERVICE_ID = 12345678;
    private final LocationServiceBinder binder = new LocationServiceBinder();
    private LocationManager mLocationManager;
    private NotificationManager notificationManager;

    private final int LOCATION_INTERVAL = 500;
    private final int LOCATION_DISTANCE = 10;

    private LocationReceiver receiver;
    private LocationReceiver.LocationUpdateListener locationUpdateListener;

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, ">>>>>>>>>>>>>>>>> onCreate");
        startTracking();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }

    public void startTracking() {
        receiver = LocationReceiverFactory.createItchaLocationReceiver(getApplicationContext());

        locationUpdateListener = new LocationReceiver.LocationUpdateListener() {
            @Override
            public void onLocationReceived(Location point, float accuracy) {
                /**
                 * TODO 위치를 받아오는 곳, 여기서 서버로 API를 전송하면 될 듯
                 */
                Log.e("TAG", String.format("location : %s", point.toString()));


                /** 만약 아래의 receiver.stop()을 호출할 경우 더이상 위치값을 받아오지 않음 **/
                // receiver.stop();
            }
        };
        receiver.setOnLocationUpdateListener(locationUpdateListener);
        receiver.start();

        startForeground(NOTI_SERVICE_ID, getNotification());
    }

    public void stopTracking() {
        onDestroy();
    }

    private Notification getNotification() {
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "test channel", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
            builder = new Notification.Builder(getApplicationContext(), CHANNEL_ID);
        } else {
            builder = new Notification.Builder(getApplicationContext());
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        String content = "노티피케이션 내용...";
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setContentTitle(getString(R.string.app_name))
                .setContentIntent(pendingIntent)
                .setContentText(content)
                .setStyle(new Notification.BigTextStyle().bigText(content));

        return builder.build();
    }


    public class LocationServiceBinder extends Binder {
        public LocationService getService() {
            return LocationService.this;
        }
    }
}
