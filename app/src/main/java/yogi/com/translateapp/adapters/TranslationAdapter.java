package yogi.com.translateapp.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import yogi.com.translateapp.data.RowObj;

/**
 * Created by Paul on 3/3/17.
 */

public class TranslationAdapter extends BaseAdapter {

    private static final String LOG_TAG = TranslationAdapter.class.getName();

    private ArrayList<RowObj> data;

    public enum ROW_TYPE {
        TRANSLATION,
        OCR
    }

    public TranslationAdapter() {
        super();
        data = new ArrayList<RowObj>();
    }

    public void addRow(RowObj row) {
        data.add(row);
        notifyDataSetChanged();
    }

    public int getCount() {
        return data.size();
    }

    public RowObj getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        RowObj obj = data.get(position);
        return obj.type;
    }

    public void clear() {
        data.clear();
    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {
        RowObj obj = data.get(position);
        View v = obj.getView(position, convertView, parent);
        return v;
    }

}
