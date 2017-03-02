package android.yogi.com.translateapp;

import android.app.Application;

/**
 * Created by Paul on 3/2/17.
 */

public class TranslateApplication extends Application {

    // Singleton instance
    private static TranslateApplication sInstance = null;

    @Override
    public void onCreate() {
        super.onCreate();
        // Setup singleton instance
        sInstance = this;
    }

    // Getter to access Singleton instance
    public static TranslateApplication getInstance() {
        return sInstance ;
    }
}
