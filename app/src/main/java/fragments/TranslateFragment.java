package fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.yogi.com.translateapp.R;
import android.yogi.com.translateapp.activities.MainActivity;
import android.yogi.com.translateapp.activities.TranslateApp;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TranslateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TranslateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TranslateFragment extends BaseFragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    protected static final String ARG_PARAM1 = "param1";
    protected static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private EditText translateEt;
    private TextView translatedTv;

    private ImageView speakIcon;

    private Button translateBtn;
    private Button clearBtn;

    private Switch langSwitch;

    private OnFragmentInteractionListener mListener;

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
    // TODO: Rename and change types and number of parameters
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

    private void setSwitchText(boolean isChecked) {
        //check the current state before we display the screen
        if(isChecked){
            String userLang = TranslateApp.getInstance().getUserLang();
            langSwitch.setText(userLang);
        } else {
            String transLang = TranslateApp.getInstance().getTransLang();
            langSwitch.setText(transLang);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_translate, container, false);

        translateBtn = (Button) view.findViewById(R.id.translateBtn);
        translateBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = translateEt.getText().toString();
                MainActivity activity = (MainActivity) getActivity();
                activity.callGoogleToTranslate(text, "es");
            }
        });

        clearBtn = (Button) view.findViewById(R.id.clearBtn);
        clearBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                translatedTv.setText("");
                translateEt.setText("");
            }
        });

        langSwitch = (Switch) view.findViewById(R.id.langSwitch);
        langSwitch.setChecked(true);
        setSwitchText(langSwitch.isChecked());

        //attach a listener to check for changes in state
        langSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                setSwitchText(isChecked);
            }
        });

        translateEt = (EditText) view.findViewById(R.id.translateEt);

        translatedTv = (TextView) view.findViewById(R.id.translatedTv);
        translatedTv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        speakIcon = (ImageView) view.findViewById(R.id.iSpeakIcon);
        speakIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                activity.getTextFromVoice(true);
            }
        });

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

    public void updateTranslatedUi(String translatedText) {
        translatedTv.setText("");
        translatedTv.setText(translatedText);
    }
}
