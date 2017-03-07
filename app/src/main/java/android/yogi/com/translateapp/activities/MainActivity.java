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
import android.widget.Toast;
import android.yogi.com.translateapp.R;
import android.yogi.com.translateapp.consts.Consts;
import android.yogi.com.translateapp.consts.Json;
import android.yogi.com.translateapp.consts.Urls;
import android.yogi.com.translateapp.fragments.TranslateFragment;
import android.yogi.com.translateapp.utils.ImagePicker;
import android.yogi.com.translateapp.utils.Utils;

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

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity implements TranslateFragment.OnFragmentInteractionListener{

    private static final String LOG_TAG = MainActivity.class.getName();

    private static final int PICK_IMAGE_ID = 123;

    private SpeechRecognizer sr;

    private StorageReference mStorageRef;

    private static final String TRANS_DIR = "translations";
    private static final String OCR_DIR = "ocr";
    private static final String STORAGE_REFERENCE = "reference";

    public static ArrayList<String> langs = new ArrayList<String>();

    private static final String txtEnding = ".txt";
    private static final String pngEnding = ".png";

    private String saveTranslateText = "";

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

    public void launchTwilioActivity() {
        Intent i = new Intent(MainActivity.this, TwilioActivity.class);
        startActivity(i);
    }

    private String generateFileName(String dir, String fileEnding){
        long ts = System.currentTimeMillis();
        String fileName = ts + fileEnding;
        String fullFileName = dir + "/" + fileName;
        return fullFileName;
    }

    public void uploadOcrToFireBase(String text, Bitmap bm) {
        String fileNamePng = generateFileName(OCR_DIR, pngEnding);
        String fileNameTxt = fileNamePng.replace(pngEnding, txtEnding);

        String translateText = "imageName=" + fileNamePng + " translation=" + text;
        try {
            uploadBytesToFireBase(translateText.getBytes("UTF-8"), fileNameTxt);
        } catch (UnsupportedEncodingException e){
            Log.e(LOG_TAG, "uploadOcrToFireBase: ", e);
            Toast.makeText(this, "Err uploading to OCR FireBase " + e.toString(), Toast.LENGTH_LONG).show();
        }

        //Upload image to Firebase
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();

        uploadBytesToFireBase(bytes, fileNamePng);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // If there's an upload in progress, save the reference so you can query it later
        if (mStorageRef != null) {
            outState.putString(STORAGE_REFERENCE, mStorageRef.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // If there was an upload in progress, get its reference and create a new StorageReference
        final String stringRef = savedInstanceState.getString(STORAGE_REFERENCE);
        if (stringRef == null) {
            return;
        }
        mStorageRef = FirebaseStorage.getInstance().getReferenceFromUrl(stringRef);

        // Find all UploadTasks under this StorageReference (in this example, there should be one)
        List<UploadTask> tasks = mStorageRef.getActiveUploadTasks();
        if (tasks.size() > 0) {
            // Get the task monitoring the upload
            UploadTask task = tasks.get(0);

            task.addOnSuccessListener(this, new OnSuccessListener<TaskSnapshot>() {
                @Override
                public void onSuccess(TaskSnapshot taskSnapshot) {

                }
            });
        }
    }

    public void uploadFirebaseTranslation(String text) {
        try {
            uploadBytesToFireBase(text.getBytes("UTF-8"), generateFileName(TRANS_DIR, txtEnding));
            saveTranslateText = "";
        } catch (UnsupportedEncodingException e) {
            Log.e(LOG_TAG, "uploadFirebaseTranslation: -", e);
        }
    }

    public void uploadBytesToFireBase(byte[] bytes, String fileName) {
        StorageReference fileRef = mStorageRef.child(fileName);

        UploadTask uploadTask = fileRef.putBytes(bytes);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "uploadFileToFireBase: onFailure" + e.toString(), Toast.LENGTH_LONG).show();
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
    }

    private AlertDialog builder;

    private void displayAlertDialog(String errMsg) {
        String error = TranslateApp.getInstance().getResources().getString(R.string.error);
        String ok = TranslateApp.getInstance().getResources().getString(R.string.ok);

        if(builder != null && !builder.isShowing()) {
            builder = new AlertDialog.Builder(this)
                    .setTitle(error)
                    .setMessage(errMsg)
                    .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }

    public void getTextFromVoice(boolean isUserLang){
        if (sr.isRecognitionAvailable(this)) {
            sr.setRecognitionListener(new SpeechListener(isUserLang));
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, "voice.recognition.test");

            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5);
            sr.startListening(intent);
        } else {
            String errMsg = TranslateApp.getInstance().getResources().getString(R.string.no_speech_recognition);
            Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show();
        }
    }

    public void stopListening() {
        sr.stopListening();
    }

    public class SpeechListener implements RecognitionListener  {

        public boolean isUserLang;

        private StringBuilder sbResults = new StringBuilder();

        public SpeechListener(boolean isUserLang) {
            this.isUserLang = isUserLang;
        }

        public void onReadyForSpeech(Bundle params) {
            Log.d(LOG_TAG, "onReadyForSpeech");
            sbResults.setLength(0);
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

            if(sbResults != null && sbResults.length() > 0) {
                addTranscriptRow(sbResults.toString(), this.isUserLang);
                callGoogleToTranslate(sbResults.toString(), !this.isUserLang);
            }
        }

        public void onError(int error) {
            String errMsg = "Error getting speech code=" + error;
            displayAlertDialog(errMsg);
        }

        public void onResults(Bundle results) {
            ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            for (int i = 0; i < data.size(); i++) {
                String nextData = data.get(i);
                if(nextData != null && nextData.length() > 0) {
                    sbResults.append(nextData);
                }
            }
        }

        public void onPartialResults(Bundle partialResults) {
            Log.d(LOG_TAG, "onPartialResults");
        }

        public void onEvent(int eventType, Bundle params) {
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

    public void callGoogleToTranslate(String text, boolean isUserLang) {
        try {
            String requestedLang = "";
            if(isUserLang) {
                requestedLang = TranslateApp.getInstance().getUserLang();
            } else {
                requestedLang = TranslateApp.getInstance().getTransLang();
            }
            String encodedText = Utils.encodeParameter(text);
            String urlParams = "?key=" + Consts.GOOGLE_TRANSLATE_API_KEY + "&target=" + requestedLang + "&q=" + encodedText;
            URL url = new URL(Urls.GOOGLE_TRANSLATE + urlParams);
            String strUrl = url.toString();

            makeAPIRequest(strUrl, Urls.RequestType.TRANSLATE, isUserLang);
        } catch (MalformedURLException e){
            Log.e(LOG_TAG, "callGoogleToTranslate: ", e);
        }
    }

    public void callGoogleForLangs() {
        makeAPIRequest(Urls.GOOGLE_LANGS, Urls.RequestType.LANGS, true);
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

    private void handleTranslateResponse(String response, boolean isUserLang){
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

            addTranscriptRow(translatedText, isUserLang);

            uploadFirebaseTranslation(saveTranslateText);
        } catch(Exception e) {
            Log.e(LOG_TAG, "handleTranslateResponse: ", e);
        }
    }

    public void makeAPIRequest(String url, final Urls.RequestType lang, final boolean isUserLang) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(Urls.RequestType.LANGS.ordinal() == lang.ordinal()) {
                            handleLangsResponse(response);
                        } else if(Urls.RequestType.TRANSLATE.ordinal() == lang.ordinal()) {
                            handleTranslateResponse(response, isUserLang);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String errMsg = error.toString();
                if(!Utils.isNetworkAvailable()) {
                    errMsg += " " + TranslateApp.getInstance().getResources().getString(R.string.no_internet);
                }

                Toast.makeText(MainActivity.this,
                        errMsg,
                        Toast.LENGTH_LONG).show();

                Log.e(LOG_TAG, "onErrorResponse: "+ error.toString());
            }
        });
        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void addTranscriptRow(String translatedText, boolean isUserLang) {
        if(getSupportFragmentManager().findFragmentById(R.id.flContent) instanceof TranslateFragment) {
            TranslateFragment frag = (TranslateFragment)
                    getSupportFragmentManager().findFragmentById(R.id.flContent);

            String langCode = "";
            if(isUserLang) {
                langCode = TranslateApp.getInstance().getUserLang();
                frag.addTranslateFromRow(translatedText);
            } else {
                langCode = TranslateApp.getInstance().getTransLang();
                frag.addTranslateToRow(translatedText);
            }

            if(saveTranslateText.length() > 0) {
                saveTranslateText += "\n";
            }

            String rowText = frag.makeTranslateStr(langCode, translatedText);
            saveTranslateText += rowText;
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
