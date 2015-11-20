package vision.resmgr;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import vision.fastfiletransfer.R;
import vision.resmgr.adapter.ResTypeAdapter;

public class ResMgrActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resmgr);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        ResTypeAdapter resTypeAdapter = new ResTypeAdapter(this, getSupportFragmentManager(), getFragments());
        viewPager.setAdapter(resTypeAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private ResTypeFragment[] getFragments() {

        ResTypeFragment[] fragments = new ResTypeFragment[4];

        fragments[0] = ResTypeFragment.newInstance(IResMgr.PAGE_APP, R.string.apk);
        fragments[1] = ResTypeFragment.newInstance(IResMgr.PAGE_IMAGE, R.string.img);
        fragments[2] = ResTypeFragment.newInstance(IResMgr.PAGE_AUDIO, R.string.audio);
        fragments[3] = ResTypeFragment.newInstance(IResMgr.PAGE_VIDEO, R.string.video);

        return fragments;
    }

}
