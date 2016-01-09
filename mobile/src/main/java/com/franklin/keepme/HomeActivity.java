package com.franklin.keepme;


import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.SpinnerAdapter;
import android.widget.Toolbar;
import com.franklin.keepme.Utils.DateUtils;
import android.widget.Spinner;

public class HomeActivity extends Activity {

    public static final int NUM_OF_MONTH = 12;

    private Calendar currentDate = Calendar.getInstance();
    private int year = currentDate.get(Calendar.YEAR), curYear = year;
    private int month = currentDate.get(Calendar.MONTH), curMonth = month;
    private int day = currentDate.get(Calendar.DATE), curDay = day;
    private int week = currentDate.get(Calendar.DAY_OF_WEEK)-1, curWeek = week;
    private int numOfDayInMonth = currentDate.getActualMaximum(Calendar.DAY_OF_MONTH);

    private HorizontalScrollView m_tabScroll = null;
    private LinearLayout m_tabBar = null;
    private ArrayList<TextView> m_tabList = new ArrayList<>(numOfDayInMonth);
    private ArrayList<View> m_contentViewList = new ArrayList<>(numOfDayInMonth);
    private ArrayList<TextView> m_spinnerList = new ArrayList<>(NUM_OF_MONTH);
    private Spinner spinner;
    private TextView m_currentTab;
    private ViewPager m_contentPager = null;
    private TabOnClickListener tabOnClickListener = new TabOnClickListener();
    private LayoutInflater m_Inflater = null;
    private TextView spinnerText;
    private boolean isFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        spinnerText = new TextView(this);
        m_tabScroll = (HorizontalScrollView) findViewById(R.id.tab_scroll);
        m_tabBar = (LinearLayout) findViewById(R.id.tab_bar);

        for (int i = 1; i <= numOfDayInMonth; i++) {
            addTab(tabOnClickListener, i);
        }
        m_contentPager = (ViewPager) findViewById(R.id.tab_content_pager);
        m_contentPager.setAdapter(new TabContentViewPagerAdapter());
        m_contentPager.addOnPageChangeListener(new TabContentPager_OnPageChangeListener());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);
        //getActionBar().setDisplayShowTitleEnabled(false);

        initializeSpinner();
        spinner = (Spinner) findViewById(R.id.toolbar_spinner);
        spinner.setAdapter(new TextViewSpinnerAdapter());
        spinner.setSelection(month, true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentDate.add(Calendar.MONTH, position - month);
                currentDate.set(Calendar.DATE, 1);
                month = currentDate.get(Calendar.MONTH);
                day = 1;
                week = currentDate.get(Calendar.DAY_OF_WEEK)-1;
                numOfDayInMonth = currentDate.getActualMaximum(Calendar.DAY_OF_MONTH);
                onMonthChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void onMonthChanged() {
        m_tabList = new ArrayList<>(numOfDayInMonth);
        m_contentViewList = new ArrayList<>(numOfDayInMonth);
        m_tabBar.removeAllViews();
        for (int i = 1; i <= numOfDayInMonth; i++) {
            addTab(tabOnClickListener, i);
        }
        m_contentPager.setAdapter(new TabContentViewPagerAdapter());
        m_contentPager.addOnPageChangeListener(new TabContentPager_OnPageChangeListener());
        m_tabScroll.fullScroll(HorizontalScrollView.FOCUS_LEFT);
        spinner.setSelection(month);
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isFirstLoad) {
            isFirstLoad = false;
            CheckTab(m_tabList.get(day-1));
        }
    }

    private void addTab(TabOnClickListener tabOnClickListener, int dayInMonth) {
        m_contentViewList.add(null);

        TextView tab = new TextView(this);
        m_tabList.add(tab);
        tab.setId(m_tabList.size());
        tab.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        tab.setGravity(Gravity.CENTER);
        tab.setText(DateUtils.WEEK[(week+dayInMonth+350-day)%7] + dayInMonth);
        tab.setTag(dayInMonth - 1);// 通过tag保存与内容视图关联的序号，从0开始计数
        tab.setOnClickListener(tabOnClickListener);
        tab.setBackgroundResource(R.drawable.tab_normal_shape);
        m_tabBar.addView(tab);
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
        m_contentPager.setCurrentItem((Integer) m_currentTab.getTag(), true);

        int viewLeft = v.getLeft();
        int viewRight = v.getRight();
        int scrollLeft = m_tabScroll.getScrollX();
        int scrollRight = scrollLeft + m_tabScroll.getWidth();

        if (viewLeft < scrollLeft) {
            m_tabScroll.scrollTo(viewLeft, 0);
        } else if (viewRight > scrollRight) {
            m_tabScroll.scrollBy(viewRight - scrollRight, 0);
        }
    }

    class TabContentViewPagerAdapter extends PagerAdapter {
        @Override
        public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
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
            if (m_contentViewList.get(arg1) == null) {
                initializeViewPager(arg1);
            }
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

    private void initializeViewPager(int arg1) {
        if (m_Inflater == null) {
            m_Inflater = getLayoutInflater();
        }
        View v = m_Inflater.inflate(R.layout.tab, null);
        m_contentViewList.set(arg1, v);
        TextView tv = (TextView) v.findViewById(R.id.content_tv);
        tv.setText("Tab " + (arg1 + 1) + " Page！" + month);
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

    private void initializeSpinner() {
        LayoutInflater layoutInflater = getLayoutInflater();

        for (int month = 0; month < 12; month++) {
            TextView monthItem = (TextView) layoutInflater.inflate(android.R.layout.simple_spinner_dropdown_item, null);
            monthItem.setText(DateUtils.MONTH[month] + year);
            monthItem.setTextColor(getResources().getColor(R.color.text));
            monthItem.setTextSize(15);
            m_spinnerList.add(monthItem);
        }
    }

    private class TextViewSpinnerAdapter implements SpinnerAdapter {
        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return m_spinnerList.get(position);
        }

        @Override
        public void registerDataSetObserver(DataSetObserver observer) {
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver observer) {
        }

        @Override
        public int getCount() {
            return m_spinnerList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            spinnerText.setText(DateUtils.MONTH[month] + year);
            return spinnerText;
        }

        @Override
        public int getItemViewType(int position) {
            return 1;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return m_spinnerList.isEmpty();
        }
    }

}


