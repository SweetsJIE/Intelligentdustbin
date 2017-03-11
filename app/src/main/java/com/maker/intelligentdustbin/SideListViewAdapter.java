package com.maker.intelligentdustbin;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by sweets on 17/2/18.
 */

public class SideListViewAdapter extends ArrayAdapter<Dustbin_icon> {

    private int resourceId;

    public SideListViewAdapter(Context context, int textViewResourceId, List<Dustbin_icon> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Dustbin_icon dustbin_icon  = getItem(position);
        View view;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
        } else {
            view = convertView;
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.dustbin_image);
        TextView textView = (TextView) view.findViewById(R.id.dustbin_number);
        imageView.setImageResource(dustbin_icon.getImageId());
        textView.setText(dustbin_icon.getName());

        TextPaint paint = textView.getPaint();//字体加粗
        paint.setFakeBoldText(true);

        return view;
    }
}
