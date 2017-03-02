package android.yogi.com.translateapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.yogi.com.translateapp.R;
import android.yogi.com.translateapp.consts.Consts;
import android.yogi.com.translateapp.consts.Json;
import android.yogi.com.translateapp.utils.ImagePicker;
import android.yogi.com.translateapp.utils.Utils;

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

public class MainActivity extends BaseActivity {

    private static final String LOG_TAG = MainActivity.class.getName();

    private EditText translateEt;
    private TextView translatedTv;
    private TextView statusTv;

    private ImageView iSpeakIcon;
    private ImageView uSpeakIcon;
    private ImageView cameraIcon;
    private ImageView helpIcon;

    private ImageView hamburgerIcon;
    private ImageView settingsIcon;

    private static final int PICK_IMAGE_ID = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setViews();

        //String apiUrl = PropertyReader.getConfigValue(this, "client_id");

    }


    private void setViews() {
        statusTv = (TextView) findViewById(R.id.statusTv);

        hamburgerIcon = (ImageView) findViewById(R.id.hamburgerIcon);
        hamburgerIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        settingsIcon = (ImageView) findViewById(R.id.settingsIcon);
        settingsIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(i);
            }
        });

        translateEt = (EditText) findViewById(R.id.translateEt);
        translatedTv = (TextView) findViewById(R.id.translatedTv);
        translatedTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                translateText();
            }
        });

        iSpeakIcon = (ImageView) findViewById(R.id.iSpeakIcon);
        iSpeakIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getTextFromVoice(true);
            }
        });

        uSpeakIcon = (ImageView) findViewById(R.id.uSpeakIcon);
        uSpeakIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getTextFromVoice(false);
            }
        });

        cameraIcon = (ImageView) findViewById(R.id.cameraIcon);
        cameraIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(MainActivity.this);
                startActivityForResult(chooseImageIntent, PICK_IMAGE_ID);
            }
        });

        helpIcon = (ImageView) findViewById(R.id.helpIcon);
        helpIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = "tel:" + Consts.HELP_PHONE_NUMBER;
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));

                if(Utils.checkCallPhonePermission()) {
                    startActivity(callIntent);
                }
            }
        });
    }

    private void getTextFromVoice(boolean primaryLanguage) {
        if(primaryLanguage) {

        } else {

        }
    }

    private void translateText() {
        String text = translateEt.getText().toString();
        callGoogleToTranslate(text, "es");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case PICK_IMAGE_ID:
                Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);

                //Sample image for testing.
                Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.spanish_pic);

                callGoogleOcr(bitmap);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void callGoogleToTranslate(String text, String requestedLang) {
        try {
            String encoded = Utils.encodeParameter(text);
            URL url = new URL("https://www.googleapis.com/language/translate/v2?key=" + Consts.GOOGLE_API_KEY + "&target=" + requestedLang + "&q=" + encoded);
            String strUrl = url.toString();

            makeAPIRequest(strUrl);
        } catch (MalformedURLException e){
            Log.e(LOG_TAG, "callGoogleToTranslate: ", e);
        }
    }

    private void makeAPIRequest(String url) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
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

                            translatedTv.setText("");
                            translatedTv.setText(translatedText);
                        } catch(Exception e) {
                            Log.e(LOG_TAG, "onResponse: ", e);
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

    private void callGoogleOcr(Bitmap bitmap) {

    }
}
