package utils;

import android.content.pm.PackageManager;
import android.util.Log;
import android.yogi.com.translateapp.activities.TranslateApp;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Paul on 3/2/17.
 */

public class Utils {

    private static final String LOG_TAG = Utils.class.getName();

    public static boolean checkCallPhonePermission() {
        return checkPermission("android.permission.CALL_PHONE");
    }

    public static String encodeParameter(String param) {
        String encoded = null;
        try {
            encoded = URLEncoder.encode(param, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e(LOG_TAG, "callGoogleToTranslate: ", e);
        }

        return encoded;
    }

    private static boolean checkPermission(String permission) {
        int res = TranslateApp.getInstance().checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }
}
