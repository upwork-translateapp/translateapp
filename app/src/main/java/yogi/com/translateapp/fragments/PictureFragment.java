package yogi.com.translateapp.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import yogi.com.translateapp.R;
import yogi.com.translateapp.activities.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PictureFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PictureFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PictureFragment extends Fragment {

    private ImageView cameraIv;
    private TextView textTv;

    private Button fireBaseBtn;

    private TranslateFragment.OnFragmentInteractionListener mListener;

    public PictureFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PictureFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PictureFragment newInstance(String param1, String param2) {
        PictureFragment fragment = new PictureFragment();
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
        View view = inflater.inflate(R.layout.fragment_picture, container, false);

        cameraIv = (ImageView) view.findViewById(R.id.cameraIv);
        textTv = (TextView) view.findViewById(R.id.textTv);
        fireBaseBtn = (Button) view.findViewById(R.id.fireBaseBtn);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),R.drawable.camera);
        cameraIv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                activity.getPicture();
            }
        });
        cameraIv.setImageBitmap(icon);

        return view;
    }

    public void setImageView(Bitmap bm) {
        if(bm != null) {
            cameraIv.setImageBitmap(bm);
        }
    }

    public void setOcrText(String text){
        textTv.setText(text);
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
