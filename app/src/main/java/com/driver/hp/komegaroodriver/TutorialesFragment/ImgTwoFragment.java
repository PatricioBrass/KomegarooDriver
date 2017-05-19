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
public class ImgTwoFragment extends Fragment {


    public ImgTwoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_img_two, container, false);
    }

    public static ImgTwoFragment newInstance(String text) {

        ImgTwoFragment f = new ImgTwoFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

}
