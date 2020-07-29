package kr.co.kkensu.maptest;

import android.location.Location;

import androidx.annotation.Nullable;

/**
 * 현재 위치 정보를 받는 클래스들의 인터페이스
 */
public interface LocationReceiver {
    void setOnLocationUpdateListener(LocationUpdateListener listener);

    void removeOnLocationUpdateListener();

    void start();

    void stop();

    @Nullable
    Location getLastPoint();

    @Nullable
    void getLastPoint(Runnable success, Runnable failure);

    interface LocationUpdateListener {
        void onLocationReceived(Location point, float accuracy);
    }

    boolean isGPSEnabled();
    boolean isNetworkEnabled();
    void showSettingAlertDialog();
}
