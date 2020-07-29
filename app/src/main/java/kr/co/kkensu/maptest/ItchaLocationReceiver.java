package kr.co.kkensu.maptest;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import static android.content.Context.LOCATION_SERVICE;


public class ItchaLocationReceiver implements LocationReceiver {

    private Context context;
    private FusedLocationProviderClient fuseLocation;

    private Location curLocation;

    private long UPDATE_INTERVAL = 10000;
    private long FASTEST_INTERVAL = 3000;

    private LocationUpdateListener locationUpdateListener;

    private LocationRequest mLocationRequest;
    private LocationCallback mCallback;

    private LocationManager locationManager;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;

    public void setUpdateInterval(long update_interval) {
        this.UPDATE_INTERVAL = update_interval;
    }

    public void setFastestInterval(long fastest_interval) {
        this.FASTEST_INTERVAL = fastest_interval;
    }

    public ItchaLocationReceiver(Context context) {
        this.context = context;
        fuseLocation = LocationServices.getFusedLocationProviderClient(context);
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        mCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                if (locationResult == null) {
                    return;
                }

                curLocation = locationResult.getLastLocation();

                if (locationUpdateListener != null) {
                    locationUpdateListener.onLocationReceived(curLocation, curLocation.getAccuracy());
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                super.onLocationAvailability(locationAvailability);
            }
        };


        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
//        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
//            @Override
//            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
//
//            }
//        });

    }

    @Override
    public void setOnLocationUpdateListener(LocationUpdateListener listener) {
        this.locationUpdateListener = listener;
    }

    @Override
    public void removeOnLocationUpdateListener() {
        this.locationUpdateListener = null;
    }

    @Override
    public void start() {
        if (isLocationPermissionGranted(context)) {
            try {
                fuseLocation.requestLocationUpdates(mLocationRequest,
                        mCallback,
                        Looper.getMainLooper() /* Looper */);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        fuseLocation.removeLocationUpdates(mCallback);
    }

    private boolean isLocationPermissionGranted(Context context) {
        return (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED);
    }

    @Nullable
    @Override
    public Location getLastPoint() {
        if (curLocation == null) {
            return null;
        } else {
            return curLocation;
        }
    }

    @Nullable
    @Override
    public void getLastPoint(final Runnable success, final Runnable failure) {
        try {
            fuseLocation.getLastLocation()
                    .addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                Log.d("TAG", "latitude :::" + location.getLatitude() + " longitude :::::" + location.getLongitude());
                                curLocation = location;
//
                                locationUpdateListener.onLocationReceived(
                                        curLocation,
                                        curLocation.getAccuracy());

                                if (success != null)
                                    success.run();
                            } else {
                                if (failure != null)
                                    failure.run();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("TAG", "fail!!!!!!!!!!!!!!!1");

                            if (failure != null) {
                                failure.run();
                            }
                        }
                    });
        } catch (SecurityException e) {
            e.printStackTrace();
            if (failure != null)
                failure.run();
        }
    }

    @Override
    public boolean isGPSEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public boolean isNetworkEnabled() {
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void showSettingAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("위치정보 사용여부");
        builder.setMessage("GPS가 켜있지 않습니다. GPS를 켜시겠습니까?");
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}