package android.yogi.com.translateapp.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.yogi.com.translateapp.R;
import android.yogi.com.translateapp.activities.MainActivity;
import android.yogi.com.translateapp.activities.TranslateApp;
import android.yogi.com.translateapp.adapters.TranslationAdapter;
import android.yogi.com.translateapp.data.OcrObj;
import android.yogi.com.translateapp.data.TranslationObj;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TranslateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TranslateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TranslateFragment extends BaseFragment {

    private static final String LOG_TAG = TranslateFragment.class.getName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    protected static final String ARG_PARAM1 = "param1";
    protected static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText translateEt;

    private ImageView iSpeakIcon;
    private ImageView uSpeakIcon;
    private ImageView helpIcon;
    private ImageView cameraIcon;

    private Button translateBtn;
    private Switch langSwitch;

    private OnFragmentInteractionListener mListener;

    private ListView listview;

    // get data from the table by the ListAdapter
    private TranslationAdapter listAdapter;

    public TranslateFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TranslateFragment.
     */
    public static TranslateFragment newInstance(String param1, String param2) {
        TranslateFragment fragment = new TranslateFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (getArguments() != null) {
                mParam1 = getArguments().getString(ARG_PARAM1);
                mParam2 = getArguments().getString(ARG_PARAM2);
            }
        }
    }

    public void clearTranslationBoxes() {
        listAdapter.clear();
        listAdapter.notifyDataSetChanged();
        translateEt.setText("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_translate, container, false);

        langSwitch = (Switch) view.findViewById(R.id.langSwitch);
        langSwitch.setText(TranslateApp.getInstance().getUserLang());
        langSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String userLang = TranslateApp.getInstance().getUserLang();
                String transLang = TranslateApp.getInstance().getTransLang();

                TranslateApp.getInstance().setUserLang(transLang);
                TranslateApp.getInstance().setTransLang(userLang);

                langSwitch.setText(transLang);
            }
        });

        translateBtn = (Button) view.findViewById(R.id.translateBtn);
        translateBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = translateEt.getText().toString();

                MainActivity activity = (MainActivity) getActivity();
                activity.addTranscriptRow(text, true);
                activity.callGoogleToTranslate(text, false);
            }
        });

        translateEt = (EditText) view.findViewById(R.id.translateEt);

        iSpeakIcon = (ImageView) view.findViewById(R.id.iSpeakIcon);
        iSpeakIcon.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    MainActivity activity = (MainActivity) getActivity();
                    activity.getTextFromVoice(true);
                } else if (event.getAction() == MotionEvent.ACTION_UP){
                    MainActivity activity = (MainActivity) getActivity();
                    activity.stopListening();
                }
                return true;
            }
        });

        uSpeakIcon = (ImageView) view.findViewById(R.id.uSpeakIcon);
        uSpeakIcon.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    MainActivity activity = (MainActivity) getActivity();
                    activity.getTextFromVoice(false);
                } else if (event.getAction() == MotionEvent.ACTION_UP){
                    MainActivity activity = (MainActivity) getActivity();
                    activity.stopListening();
                }
                return true;
            }
        });

        helpIcon = (ImageView) view.findViewById(R.id.helpIcon);
        helpIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                activity.launchTwilioActivity();
            }
        });

        cameraIcon = (ImageView) view.findViewById(R.id.cameraIcon);
        cameraIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                activity.getPicture();
            }
        });

        Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.camera);
        cameraIcon.setImageBitmap(icon);

        listview = (ListView) view.findViewById(R.id.listview);

        // get data from the table by the ListAdapter
        listAdapter = new TranslationAdapter();

        listview.setAdapter(listAdapter);

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void addTranslateFromRow(String text) {
        addTranslateFromRow(TranslateApp.getInstance().getUserLang(), text);
    }

    public void addTranslateFromRow(String lang, String text) {
        String display = "{" + lang + "} - " + text;
        TranslationObj transObj = new TranslationObj(display);
        listAdapter.addRow(transObj);
    }

    public void addTranslateToRow(String text) {
        addTranslateToRow(TranslateApp.getInstance().getTransLang(), text);
    }

    public void addTranslateToRow(String lang, String text) {
        String display = makeTranslateStr(lang, text);
        TranslationObj transObj = new TranslationObj(display);
        listAdapter.addRow(transObj);
    }

    public void addOcrRow(Bitmap bm, String ocr, String fromLang, String toLang) {
        String display = makeOcrStr(ocr, fromLang, toLang);
        OcrObj transObj = new OcrObj(bm, display);
        listAdapter.addRow(transObj);
    }

    public String makeTranslateStr(String lang, String text) {
        String display = "> {" + lang + "} - " + text;
        return display;
    }

    public String makeOcrStr(String ocr, String fromLang, String toLang) {
        String display = "{" + fromLang + "} > {" + toLang + "} - " + ocr;
        return display;
    }
}
