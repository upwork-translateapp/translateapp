package android.yogi.com.translateapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.yogi.com.translateapp.R;
import android.yogi.com.translateapp.activities.TranslateApp;

import java.util.Locale;

import static android.yogi.com.translateapp.activities.MainActivity.langs;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends BaseFragment {

    private TranslateFragment.OnFragmentInteractionListener mListener;

    private Button userLang;
    private Button translateLang;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            if (getArguments() != null) {
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        userLang = (Button) view.findViewById(R.id.userLang);
        userLang.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLangs(userLang);
            }
        });
        userLang.setText(TranslateApp.getInstance().getUserLang());

        translateLang = (Button) view.findViewById(R.id.translateLang);
        translateLang.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                displayLangs(translateLang);
            }
        });
        translateLang.setText(TranslateApp.getInstance().getTransLang());

        return view;
    }

    private String getLanguageName(String code) {
        Locale loc = new Locale(code);
        String name = loc.getDisplayLanguage(loc); // English
        return name;
    }

    private void displayLangs(final TextView view) {
        CharSequence charLangs[] = new CharSequence[langs.size()];

        for (int i = 0; i < langs.size(); i++) {
            String langCode = langs.get(i);
            String lang = langs.get(i) + " - " + getLanguageName(langCode);
            charLangs[i] = lang;
        }

        String pickLang = TranslateApp.getInstance().getResources().getString((R.string.pick_lang));

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(pickLang);
        builder.setItems(charLangs, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String lang = langs.get(which);
                String [] pieces = lang.split(" - ");
                String langCode = pieces[0].trim();
                if(view == userLang) {
                    TranslateApp.getInstance().setUserLang(langCode);
                } else if(view == translateLang) {
                    TranslateApp.getInstance().setTransLang(langCode);
                }
                view.setText(lang);
            }
        });
        builder.show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof TranslateFragment.OnFragmentInteractionListener) {
            mListener = (TranslateFragment.OnFragmentInteractionListener) context;
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
}
