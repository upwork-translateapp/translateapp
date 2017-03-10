package yogi.com.translateapp.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import yogi.com.translateapp.R;

/**
 * Created by Paul on 3/2/17.
 */

public class PropertyReader {

    private static final String LOG_TAG = PropertyReader.class.getName();

    public static String getConfigValue(Context context, String name) {
        Resources resources = context.getResources();

        try {
            InputStream rawResource = resources.openRawResource(R.raw.google_service_account);
            Properties properties = new Properties();
            properties.load(rawResource);
            return properties.getProperty(name);
        } catch (Resources.NotFoundException e) {
            Log.e(LOG_TAG, "Unable to find the config file: " + e.getMessage());
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to open config file.");
        }

        return null;
    }
}
