package com.example.jiangqianghua.viewpagerindicator;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.jqh.view.ViewPagerIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private ViewPager mViewPager ;
    private ViewPagerIndicator mIndicator ;
    private List<String> mTitles = Arrays.asList("短信","收藏","推荐","短信1","收藏1","推荐1","短信2","收藏2","推荐2");
    private List<VPSimpleFragment> mContents = new ArrayList<VPSimpleFragment>();
    private FragmentPagerAdapter mAdapter   ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        initViews();


        initData();

        mIndicator.setmTabVisibleCount(6);
        mIndicator.setTableItemTitles(mTitles);
        mViewPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mViewPager,0);

        // 因为滚动事件在mIndicator内部，需要拿出来，需要监听就可以
        mIndicator.setOnPagechangeListener(new ViewPagerIndicator.PageOnChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initData()
    {
        for(String title:mTitles)
        {
            VPSimpleFragment fragment = VPSimpleFragment.newInstance(title);
            mContents.add(fragment);
        }

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mContents.get(position);
            }

            @Override
            public int getCount() {
                return mContents.size();
            }
        };
    }
    private void initViews()
    {
        mViewPager = (ViewPager)findViewById(R.id.id_viewpager);
        mIndicator = (ViewPagerIndicator)findViewById(R.id.id_indicator);
    }
}
