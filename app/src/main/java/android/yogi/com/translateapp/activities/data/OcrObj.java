package android.yogi.com.translateapp.activities.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.yogi.com.translateapp.R;
import android.yogi.com.translateapp.activities.TranslateApp;
import android.yogi.com.translateapp.activities.adapters.TranslationAdapter.ROW_TYPE;

/**
 * Created by Paul on 3/3/17.
 */

public class OcrObj extends RowObj{

    public Bitmap bm;

    public String text;

    public OcrObj(Bitmap bm, String text) {
        super(ROW_TYPE.OCR.ordinal());
        this.bm = bm;
        this.text = text;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            LayoutInflater inflater = (LayoutInflater) TranslateApp.getInstance().getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);
            vi = inflater.inflate(R.layout.row_ocr, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/
            holder = new ViewHolder();
            holder.text = (TextView) vi.findViewById(R.id.text);
            holder.image = (ImageView) vi.findViewById(R.id.image);

            vi.setTag( holder );
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        holder.text.setText(this.text);
        holder.image.setImageBitmap(this.bm);

        return vi;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder {
        public TextView text;
        public ImageView image;
    }
}
