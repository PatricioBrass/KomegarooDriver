package com.driver.hp.komegaroodriver.TutorialesFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.driver.hp.komegaroodriver.R;

import java.io.IOException;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageButton;

public class GifOneFragment extends Fragment {

    public GifOneFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_gif_one, container, false);
        LinearLayout content = (LinearLayout) v.findViewById(R.id.gif_one);
        try {
            GifDrawable gifFromResource = new GifDrawable( getResources(), R.drawable.tuto_gif_1 );
        } catch (IOException e) {
            e.printStackTrace();
        }
        return v;
    }

    public static GifOneFragment newInstance(String text) {

        GifOneFragment f = new GifOneFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

}
