package android.yogi.com.translateapp.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.yogi.com.translateapp.R;
import android.yogi.com.translateapp.activities.TranslateApp;
import android.yogi.com.translateapp.adapters.TranslationAdapter.ROW_TYPE;

/**
 * Created by Paul on 3/3/17.
 */

public class TranslationObj extends RowObj {

    public String text;

    public TranslationObj(String text) {
        super(ROW_TYPE.TRANSLATION.ordinal());
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
            vi = inflater.inflate(R.layout.row_translation, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/
            holder = new ViewHolder();
            holder.text = (TextView) vi.findViewById(R.id.text);

            vi.setTag( holder );
        } else {
            holder = (ViewHolder) vi.getTag();
        }

        holder.text.setText(this.text);
        return vi;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder {
        public TextView text;
    }
}
