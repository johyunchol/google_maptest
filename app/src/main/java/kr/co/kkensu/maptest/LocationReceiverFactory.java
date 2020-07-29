package kr.co.kkensu.maptest;

import android.content.Context;

/**
 * LocationReceiver 생성
 */
public class LocationReceiverFactory {
    public static LocationReceiver createItchaLocationReceiver(Context context) {
        return new ItchaLocationReceiver(context);
    }

//    public static LocationReceiver createLocationReceiver(Context context) {
//        return new VanillaLocationReceiver(context);
//    }
}
