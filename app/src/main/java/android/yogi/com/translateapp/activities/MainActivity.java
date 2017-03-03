package android.yogi.com.translateapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.yogi.com.translateapp.R;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import consts.Consts;
import consts.Json;
import consts.Urls;
import consts.Urls.RequestType;
import fragments.PictureFragment;
import fragments.TranslateFragment;
import utils.ImagePicker;
import utils.Utils;

public class MainActivity extends BaseActivity implements TranslateFragment.OnFragmentInteractionListener{

    private static final String LOG_TAG = MainActivity.class.getName();

    private static final int PICK_IMAGE_ID = 123;

    public static ArrayList<String> langs = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupPageLayout();

        launchTranslateFragment();

        callGoogleForLangs();
    }

    private void launchTranslateFragment() {
        try {
            Fragment fragment = (Fragment) TranslateFragment.newInstance("", "");
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        } catch (Exception e) {
            Log.e(LOG_TAG, "launchTranslateFragment: ", e);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri){

    }

    public void getPicture () {
        Intent chooseImageIntent = ImagePicker.getPickImageIntent(MainActivity.this);
        startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
    }

    public void getTextFromVoice(boolean primaryLanguage) {
        if(primaryLanguage) {

        } else {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case PICK_IMAGE_ID:
                Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                if(bitmap != null && getSupportFragmentManager().findFragmentById(R.id.flContent) instanceof PictureFragment) {
                    PictureFragment pFrag = (PictureFragment)
                            getSupportFragmentManager().findFragmentById(R.id.flContent);
                    pFrag.setImageView(bitmap);

                    callGoogleOcr(bitmap);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    public void callGoogleToTranslate(String text, String requestedLang) {
        try {
            String encodedText = Utils.encodeParameter(text);
            String urlParams = "?key=" + Consts.GOOGLE_TRANSLATE_API_KEY + "&target=" + requestedLang + "&q=" + encodedText;
            URL url = new URL(Urls.GOOGLE_TRANSLATE + urlParams);
            String strUrl = url.toString();

            makeAPIRequest(strUrl, RequestType.TRANSLATE);
        } catch (MalformedURLException e){
            Log.e(LOG_TAG, "callGoogleToTranslate: ", e);
        }
    }

    public void callGoogleForLangs() {
        makeAPIRequest(Urls.GOOGLE_LANGS, RequestType.LANGS);
    }

    private void handleLangsResponse(String response) {
        /**
         * {
         "data": {
         "languages": [
         {
         "language": "af"
         },
         */
        try {
            JSONObject jsonObj = new JSONObject(response);
            JSONObject dataObj = jsonObj.getJSONObject(Json.DATA);
            JSONArray languages = dataObj.getJSONArray(Json.LANGUAGES);
            for (int i = 0; i < languages.length(); i++) {
                JSONObject language = languages.getJSONObject(i);
                String lang = language.getString(Json.LANGUAGE);
                langs.add(lang);
            }
        } catch(Exception e) {
            Log.e(LOG_TAG, "handleTranslateResponse: ", e);
        }

        Log.e(LOG_TAG, response);
    }

    private void handleTranslateResponse(String response){
        /**
         Sample Response from Google Translate API
         {
         "data": {
             "translations": [
                 {
                 "translatedText": "Pruebas",
                 "detectedSourceLanguage": "en"
                 }
             ]
             }
         }
         */
        try {
            JSONObject jsonObj = new JSONObject(response);
            JSONObject dataObj = jsonObj.getJSONObject(Json.DATA);
            JSONArray translations = dataObj.getJSONArray(Json.TRANSLATIONS);
            JSONObject translation = (JSONObject) translations.get(0);
            String translatedText = (String) translation.get(Json.TRANSLATED_TEXT);
            String detectedSrcLang = (String) translation.get(Json.DETECTED_SRC_LANG);

            updateTranslatedUi(translatedText);
        } catch(Exception e) {
            Log.e(LOG_TAG, "handleTranslateResponse: ", e);
        }
    }

    public void makeAPIRequest(String url, final RequestType lang) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(RequestType.LANGS.ordinal() == lang.ordinal()) {
                            handleLangsResponse(response);
                        } else if(RequestType.TRANSLATE.ordinal() == lang.ordinal()) {
                            handleTranslateResponse(response);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(LOG_TAG, "onErrorResponse: "+ error.toString());
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void updateTranslatedUi(String translatedText) {
        if(getSupportFragmentManager().findFragmentById(R.id.flContent) instanceof TranslateFragment) {
            TranslateFragment frag = (TranslateFragment)
                    getSupportFragmentManager().findFragmentById(R.id.flContent);
            frag.updateTranslatedUi(translatedText);
        }
    }

    public void callGoogleOcr(Bitmap bitmap) {
        if(getSupportFragmentManager().findFragmentById(R.id.flContent) instanceof TranslateFragment) {
            String ocrText = "";
            PictureFragment frag = (PictureFragment)
                    getSupportFragmentManager().findFragmentById(R.id.flContent);
            frag.setOcrText(ocrText);
        }
    }
}
