package com.driver.hp.komegaroodriver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.driver.hp.komegaroodriver.TutorialesFragment.GifOneFragment;
import com.driver.hp.komegaroodriver.TutorialesFragment.GifTwoFragment;
import com.driver.hp.komegaroodriver.TutorialesFragment.ImgOneFragment;
import com.driver.hp.komegaroodriver.TutorialesFragment.ImgTwoFragment;

import me.relex.circleindicator.CircleIndicator;

public class TutorialActivity extends AppCompatActivity {
    private Button saltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        saltar = (Button)findViewById(R.id.btnSaltar);
        saltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TutorialActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {

                case 0: return ImgOneFragment.newInstance("ImgOneFragment, Instance 1");
                case 1: return ImgTwoFragment.newInstance("ImgTwoFragment, Instance 2");
                case 2: return GifOneFragment.newInstance("GifOneFragment, Instance 3");
                case 3: return GifTwoFragment.newInstance("GifTwoFragment, Instance 4");
                default: return ImgOneFragment.newInstance("ImgOneFragment, Instance 1");
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}

