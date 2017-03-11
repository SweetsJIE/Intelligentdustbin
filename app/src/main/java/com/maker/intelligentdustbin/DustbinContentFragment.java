package com.maker.intelligentdustbin;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by sweets on 17/2/18.
 */

public class DustbinContentFragment extends Fragment {
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dustbin_content_frag, container, false);
        return view;
    }

    //float longitude, float latitude
    //刷新fragment数据
    public void refresh(String text) {
        //TextView textview = (TextView) view.findViewById(R.id.test);
        //textview.setText(text);
    }
}
