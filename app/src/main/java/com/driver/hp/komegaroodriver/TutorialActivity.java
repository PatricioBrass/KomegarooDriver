package com.driver.hp.komegaroodriver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.driver.hp.komegaroodriver.TutorialesFragment.GifEightFragment;
import com.driver.hp.komegaroodriver.TutorialesFragment.GifFiveFragment;
import com.driver.hp.komegaroodriver.TutorialesFragment.GifFourFragment;
import com.driver.hp.komegaroodriver.TutorialesFragment.GifNineFragment;
import com.driver.hp.komegaroodriver.TutorialesFragment.GifOneFragment;
import com.driver.hp.komegaroodriver.TutorialesFragment.GifSevenFragment;
import com.driver.hp.komegaroodriver.TutorialesFragment.GifSixFragment;
import com.driver.hp.komegaroodriver.TutorialesFragment.GifTenFragment;
import com.driver.hp.komegaroodriver.TutorialesFragment.GifThreeFragment;
import com.driver.hp.komegaroodriver.TutorialesFragment.GifTwoFragment;
import com.driver.hp.komegaroodriver.TutorialesFragment.ImgFinalFragment;
import com.driver.hp.komegaroodriver.TutorialesFragment.ImgOneFragment;
import com.driver.hp.komegaroodriver.TutorialesFragment.ImgTwoFragment;

import me.relex.circleindicator.CircleIndicator;

public class TutorialActivity extends AppCompatActivity {

    public static final String MESSAGE_KEY="com.driver.hp.komegaroodriver.message_key";
    public Button saltar;
    private String uidDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        Intent intent = getIntent();
        uidDriver = intent.getStringExtra(MESSAGE_KEY);
        loginGo();
        ViewPager pager = (ViewPager) findViewById(R.id.viewPager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        saltar = (Button)findViewById(R.id.btnSaltar);
        saltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saltar();
            }
        });
    }

    public void loginGo(){
        Log.v("UDI!",String.valueOf(uidDriver));
        if(uidDriver!=null){
            saltar();
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {
                case 1: return ImgTwoFragment.newInstance("ImgTwoFragment, Instance 2");
                case 2: return GifOneFragment.newInstance("GifOneFragment, Instance 3");
                case 3: return GifTwoFragment.newInstance("GifTwoFragment, Instance 4");
                case 4: return GifThreeFragment.newInstance("GifThreeFragment, Instance 4");
                case 5: return GifFourFragment.newInstance("GifFourFragment, Instance 4");
                case 6: return GifFiveFragment.newInstance("GifFiveFragment, Instance 4");
                case 7: return GifSixFragment.newInstance("GifSixFragment, Instance 4");
                case 8: return GifSevenFragment.newInstance("GifSevenFragment, Instance 4");
                case 9: return GifEightFragment.newInstance("GifEightFragment, Instance 4");
                case 10: return GifNineFragment.newInstance("GifNineFragment, Instance 4");
                case 11: return GifTenFragment.newInstance("ImgFinalFragment, Instance 4");
                case 12: return ImgFinalFragment.newInstance("ImgFinalFragment, Instance 4");
                default: return ImgOneFragment.newInstance("ImgOneFragment, Instance 1");
            }
        }

        @Override
        public int getCount() {
            return 13;
        }
    }

    public void saltar(){
        Intent intent = new Intent(TutorialActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}

