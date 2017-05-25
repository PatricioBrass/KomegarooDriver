package com.driver.hp.komegaroodriver.TutorialesFragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.driver.hp.komegaroodriver.R;
import com.driver.hp.komegaroodriver.TutorialActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ImgFinalFragment extends Fragment {


    public ImgFinalFragment() {
        // Required empty public constructor
    }
    private Button saltar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =inflater.inflate(R.layout.fragment_img_final, container, false);
        saltar = (Button)v.findViewById(R.id.btn_tuto);
        saltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TutorialActivity) getActivity()).saltar();
            }
        });
        return v;
    }

    public static ImgFinalFragment newInstance(String text) {

        ImgFinalFragment f = new ImgFinalFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);

        return f;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((TutorialActivity) getActivity()).saltar.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((TutorialActivity) getActivity()).saltar.setVisibility(View.VISIBLE);
    }
}
