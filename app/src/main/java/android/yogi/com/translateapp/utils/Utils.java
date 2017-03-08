package android.yogi.com.translateapp.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import android.yogi.com.translateapp.activities.TranslateApp;

import java.io.ByteArrayOutputStream;
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

    public static boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) TranslateApp.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String convertBitmapToBase64(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        return encoded;
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
