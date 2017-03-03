package android.yogi.com.translateapp.activities;

import android.app.Application;

import consts.Consts;

/**
 * Created by Paul on 3/2/17.
 */

public class TranslateApp extends Application {
    // Singleton instance
    private static TranslateApp sInstance = null;

    private String userLang;
    private String transLang;

    public String getUserLang() {
        return userLang;
    }

    public void setUserLang(String userLang) {
        this.userLang = userLang;
    }

    public String getTransLang() {
        return transLang;
    }

    public void setTransLang(String transLang) {
        this.transLang = transLang;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Setup singleton instance
        sInstance = this;
        userLang = Consts.ENGLISH_CODE;
        transLang = Consts.SPANISH_CODE;
    }

    // Getter to access Singleton instance
    public static TranslateApp getInstance() {
        return sInstance ;
    }
}
