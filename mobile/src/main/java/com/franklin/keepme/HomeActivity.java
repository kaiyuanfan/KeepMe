package com.franklin.keepme;

/**
 * Created by franklin on 10/22/15.
 */

import java.util.ArrayList;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeActivity extends Activity {

    private HorizontalScrollView m_tabScroll = null;
    private LinearLayout m_tabBar = null;
    private ArrayList<TextView> m_tabList = new ArrayList<>();
    private TextView m_currentTab;
    private ViewPager m_contentPager = null;
    private ArrayList<View> m_contentViewList = new ArrayList<>();
    private LayoutInflater m_Inflater = null;
    private boolean isFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        m_tabScroll = (HorizontalScrollView)findViewById(R.id.tab_scroll);
        m_tabBar = (LinearLayout)findViewById(R.id.tab_bar);
        TabOnClickListener tabOnClickListener = new TabOnClickListener();
        for (int i = 0; i < 30;i++) {
            addTab(tabOnClickListener);
        }
        m_contentPager = (ViewPager)findViewById(R.id.tab_content_pager);
        m_contentPager.setAdapter(new TabContentViewPagerAdapter());
        m_contentPager.addOnPageChangeListener(new TabContentPager_OnPageChangeListener());
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if( isFirstLoad) {
            isFirstLoad = false;
             CheckTab(m_tabList.get(20));
        }
    }

    private void addTab(TabOnClickListener tabOnClickListener) {
        if (m_Inflater == null) {
            m_Inflater = getLayoutInflater();
        }
        View v = m_Inflater.inflate(R.layout.tab, null);
        m_contentViewList.add(v);
        TextView tv =(TextView) v.findViewById(R.id.content_tv);
        tv.setText("Tab " + m_contentViewList.size() + " Page！");
        TextView tab= new TextView(this);
        m_tabList.add(tab);
        tab.setId(m_tabList.size());
        tab.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        tab.setGravity(Gravity.CENTER);
        tab.setText("Tab" + m_tabList.size());
        tab.setTag(m_tabList.size() - 1);// 通过tag保存与内容视图关联的序号，从0开始计数
        tab.setOnClickListener(tabOnClickListener);
        tab.setBackgroundResource(R.drawable.tab_normal_shape);
        m_tabBar.addView(tab);
    }

    class TabContentViewPagerAdapter extends PagerAdapter {
        public TabContentViewPagerAdapter() {
        }

        @Override
        public void destroyItem(ViewGroup arg0, int arg1, Object arg2){
            arg0.removeView(m_contentViewList.get(arg1));
        }
        @Override
        public void finishUpdate(ViewGroup arg0) {
        }
        @Override
        public int getCount() {
            return m_contentViewList.size();
        }
        @Override
        public Object instantiateItem(ViewGroup arg0, int arg1) {
            arg0.addView(m_contentViewList.get(arg1), 0);
            return m_contentViewList.get(arg1);
        }
        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }
        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {
        }
        @Override
        public Parcelable saveState() {
            return null;
        }
        @Override
        public void startUpdate(ViewGroup arg0) {
        }
    }

    class TabContentPager_OnPageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            CheckTab(m_tabList.get(arg0));
        }
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }
    }

    class TabOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            CheckTab(v);
        }
    }

    private void CheckTab(View v) {
        if (m_currentTab != null) {
            m_currentTab.setBackgroundColor(getResources().getColor(R.color.background));
        }
        m_currentTab = (TextView) v;
        m_currentTab.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        m_contentPager.setCurrentItem((Integer)m_currentTab.getTag(), true);

        int viewLeft =v.getLeft();
        int viewRight =v.getRight();
        int scrollLeft = m_tabScroll.getScrollX();
        int scrollRight =scrollLeft + m_tabScroll.getWidth();

        if (viewLeft <scrollLeft) {
            m_tabScroll.scrollTo(viewLeft, 0);
        } else if (viewRight >scrollRight) {
            m_tabScroll.scrollBy(viewRight- scrollRight, 0);
        }
    }
}


