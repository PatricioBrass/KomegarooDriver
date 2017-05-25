package com.driver.hp.komegaroodriver.TutorialesFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.driver.hp.komegaroodriver.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class GifThreeFragment extends Fragment {


    public GifThreeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gif_three, container, false);
    }

    public static GifThreeFragment newInstance(String text) {

        GifThreeFragment f = new GifThreeFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

}
