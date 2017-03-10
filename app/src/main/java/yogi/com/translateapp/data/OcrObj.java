package yogi.com.translateapp.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import yogi.com.translateapp.R;
import yogi.com.translateapp.activities.TranslateApp;
import yogi.com.translateapp.adapters.TranslationAdapter;

/**
 * Created by Paul on 3/3/17.
 */

public class OcrObj extends RowObj{

    public Bitmap bm;

    public String text;

    public OcrObj(Bitmap bm, String text) {
        super(TranslationAdapter.ROW_TYPE.OCR.ordinal());
        this.bm = bm;
        this.text = text;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        OcrViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            LayoutInflater inflater = (LayoutInflater) TranslateApp.getInstance().getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.row_ocr, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/
            holder = new OcrViewHolder();
            holder.text = (TextView) vi.findViewById(R.id.text);
            holder.image = (ImageView) vi.findViewById(R.id.image);

            vi.setTag( holder );
        } else {
            holder = (OcrViewHolder) vi.getTag();
        }

        holder.text.setText(this.text);
        holder.image.setImageBitmap(this.bm);

        return vi;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class OcrViewHolder {
        public TextView text;
        public ImageView image;
    }
}
