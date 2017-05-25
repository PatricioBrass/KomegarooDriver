package com.driver.hp.komegaroodriver.TutorialesFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.driver.hp.komegaroodriver.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImgFinal2Fragment extends Fragment {


    public ImgFinal2Fragment() {
        // Required empty public constructor
    }
    private Button exit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_img_final2, container, false);
        exit = (Button)v.findViewById(R.id.btn_tuto2);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
                getActivity().finish();
            }
        });
        return v;
    }

    public static ImgFinal2Fragment newInstance(String text) {

        ImgFinal2Fragment f = new ImgFinal2Fragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);

        return f;
    }

}
