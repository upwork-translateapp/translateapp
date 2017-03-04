package android.yogi.com.translateapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.yogi.com.translateapp.R;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.firebase.storage.UploadTask.TaskSnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import consts.Consts;
import consts.Json;
import consts.Urls;
import consts.Urls.RequestType;
import fragments.TranslateFragment;
import utils.ImagePicker;
import utils.Utils;

public class MainActivity extends TwilioActivity implements TranslateFragment.OnFragmentInteractionListener{

    private static final String LOG_TAG = MainActivity.class.getName();

    private static final int PICK_IMAGE_ID = 123;

    private SpeechRecognizer sr;

    private StorageReference mStorageRef;

    private static final String TRANS_DIR = "translations";
    private static final String OCR_DIR = "ocr";

    public static ArrayList<String> langs = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupPageLayout();

        launchTranslateFragment();

        callGoogleForLangs();

        sr = SpeechRecognizer.createSpeechRecognizer(this);

        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    private String generateFileName(String dir){
        long ts = System.currentTimeMillis();
        String fileName = dir + ts + ".txt";
        return fileName;
    }

    public void uploadOcrToFireBase(String text, Bitmap bm) {
        StorageReference rootRef = mStorageRef.getRoot();
        String fileName = generateFileName(OCR_DIR);
        StorageReference fileRef = rootRef.child(fileName);

        uploadTranslationToFireBase(text);
    }

    public void uploadTranslationToFireBase(String text) {
        StorageReference rootRef = mStorageRef.getRoot();
        String fileName = generateFileName(TRANS_DIR);
        StorageReference fileRef = rootRef.child(fileName);

        try {
            byte[] bytes = text.getBytes("UTF-8");
            UploadTask uploadTask = fileRef.putBytes(bytes);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    displayAlertDialog("Err uploading to FireBase " + e.toString());
                    Log.e(LOG_TAG, "uploadFileToFireBase: onFailure" + e.toString());
                }
            }).addOnSuccessListener(new OnSuccessListener<TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Log.e(LOG_TAG, "downloadUrl" + downloadUrl.toString());
                }
            });
        } catch (UnsupportedEncodingException e){
            Log.e(LOG_TAG, "uploadFileToFireBase: ", e);
            displayAlertDialog("Err uploading to FireBase " + e.toString());
        }
    }

    private void displayAlertDialog(String errMsg) {
        String error = TranslateApp.getInstance().getResources().getString(R.string.error);
        String ok = TranslateApp.getInstance().getResources().getString(R.string.ok);

        new AlertDialog.Builder(this)
                .setTitle(error)
                .setMessage(errMsg)
                .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void getTextFromVoice(boolean isUserLang){
        if (sr.isRecognitionAvailable(this)) {
            if(isUserLang) {
                String lang = TranslateApp.getInstance().getUserLang();
                sr.setRecognitionListener(new SpeechListener(lang));
            } else {
                String lang = TranslateApp.getInstance().getTransLang();
                sr.setRecognitionListener(new SpeechListener(lang));
            }
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "voice.recognition.test");

            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
            sr.startListening(intent);
        } else {
            String errMsg = TranslateApp.getInstance().getResources().getString(R.string.no_speech_recognition);
            displayAlertDialog(errMsg);
        }
    }

    public void stopListening() {
        sr.stopListening();
    }

    public class SpeechListener implements RecognitionListener  {

        public String lang;

        private StringBuilder sb = new StringBuilder();
        private StringBuilder sbResults = new StringBuilder();

        public SpeechListener(String lang) {
            this.lang = lang;
        }

        public void onReadyForSpeech(Bundle params) {
            Log.d(LOG_TAG, "onReadyForSpeech");
            sb.setLength(0);
            sbResults.setLength(0);

            String txtFromSpeech = TranslateApp.getInstance().getResources().getString(R.string.text_from_speech);
            StringBuilder sb = new StringBuilder();
            sb.append(txtFromSpeech);
            sb.append(" ");
        }

        public void onBeginningOfSpeech() {
            Log.d(LOG_TAG, "onBeginningOfSpeech");
        }

        public void onRmsChanged(float rmsdB) {
            Log.d(LOG_TAG, "onRmsChanged");
        }

        public void onBufferReceived(byte[] buffer) {
            Log.d(LOG_TAG, "onBufferReceived");
        }

        public void onEndOfSpeech() {
            Log.d(LOG_TAG, "onEndofSpeech");

            sb.append(sbResults.toString());
            sb.append("\n");

            String translatedSpeech = TranslateApp.getInstance().getResources().getString(R.string.translated_speech);
            sb.append(" ");
            sb.append(translatedSpeech);
            sb.append("\n");

            addTranslatedRow(sb.toString());
            callGoogleToTranslate(sbResults.toString(), this.lang);
        }

        public void onError(int error) {
            String errMsg = "Error getting speech code=" + error;
            displayAlertDialog(errMsg);
        }

        public void onResults(Bundle results) {
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++) {
                sbResults.append(data.get(i));
            }

            Log.e(LOG_TAG, "result= " + sbResults.toString());
        }

        public void onPartialResults(Bundle partialResults) {
            Log.d(LOG_TAG, "onPartialResults");
        }

        public void onEvent(int eventType, Bundle params) {
            Log.d(LOG_TAG, "onEvent " + eventType);
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case PICK_IMAGE_ID:
                Bitmap bitmap = ImagePicker.getImageFromResult(this, resultCode, data);
                if(bitmap != null && getSupportFragmentManager().findFragmentById(R.id.flContent) instanceof TranslateFragment) {
                    callGoogleOcr(bitmap);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    public void callGoogleToTranslate(String text) {
        callGoogleToTranslate(text, TranslateApp.getInstance().getTransLang());
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

            addTranslatedRow(translatedText);

            //TODO Here upload to Firebase
            uploadTranslationToFireBase(translatedText);
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

    private void addTranslatedRow(String translatedText) {
        if(getSupportFragmentManager().findFragmentById(R.id.flContent) instanceof TranslateFragment) {
            TranslateFragment frag = (TranslateFragment)
                    getSupportFragmentManager().findFragmentById(R.id.flContent);
            frag.addTranslateToRow(translatedText);

            String saveTranslateText = frag.makeTranslateStr(TranslateApp.getInstance().getTransLang(), translatedText);
            uploadTranslationToFireBase(saveTranslateText);
        }
    }

    public void callGoogleOcr(Bitmap bitmap) {
        if(getSupportFragmentManager().findFragmentById(R.id.flContent) instanceof TranslateFragment) {
            String ocrText = "RESPONSE FROM GOOGLE";

            TranslateFragment frag = (TranslateFragment)
                    getSupportFragmentManager().findFragmentById(R.id.flContent);

            String fromLang = TranslateApp.getInstance().getUserLang();
            String toLang = TranslateApp.getInstance().getTransLang();

            frag.addOcrRow(bitmap, ocrText, fromLang, toLang);

            String saveOcrText = frag.makeOcrStr(ocrText, fromLang, toLang);
            uploadOcrToFireBase(saveOcrText, bitmap);
        }
    }
}
