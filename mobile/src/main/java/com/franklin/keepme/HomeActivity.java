package com.franklin.keepme;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.franklin.keepme.DB.DBContract;
import com.franklin.keepme.DB.DBManager;
import com.franklin.keepme.Utils.DateUtils;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemLongClickListener;


public class HomeActivity extends AppCompatActivity {

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
    String[] spinnerArray = new String[12];
    private Spinner spinner;
    private TextView m_currentTab;
    private ViewPager m_contentPager = null;
    private TabOnClickListener tabOnClickListener = new TabOnClickListener();
    private LayoutInflater m_Inflater = null;
    private boolean isFirstLoad = true;
    private boolean isMonthChanged = false;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private ItemLongClickListener itemLongClickListener;
    private ItemOnClickListener itemOnClickListener;
    private DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        dbManager = ((Application) getApplication()).getDbManager();
        initializeSpinner();
        initializeScrollView();
        initializeViewPager();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent intent = new Intent(HomeActivity.this, createActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbManager.closeDB();
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (isFirstLoad) {
            isFirstLoad = false;
            CheckTab(m_tabList.get(day-1));
        }
    }

    private void initializeSpinner() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);
        getActionBar().setDisplayShowTitleEnabled(false);

        for (int month = 0; month < 12; month++) {
            spinnerArray[month] = DateUtils.MONTH[month] + year;
        }
        spinner = (Spinner) findViewById(R.id.home_toolbar_spinner);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerArray);
        spinner.setAdapter(spinnerArrayAdapter);
        spinner.setSelection(month, true);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentDate.add(Calendar.MONTH, position - month);
                currentDate.set(Calendar.DATE, 1);
                month = currentDate.get(Calendar.MONTH);
                day = 1;
                week = currentDate.get(Calendar.DAY_OF_WEEK) - 1;
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
        CheckTab(m_tabList.get(0));
        isMonthChanged = true;
    }

    private void initializeScrollView() {
        m_tabScroll = (HorizontalScrollView) findViewById(R.id.tab_scroll);
        m_tabBar = (LinearLayout) findViewById(R.id.tab_bar);

        for (int i = 1; i <= numOfDayInMonth; i++) {
            addTab(tabOnClickListener, i);
        }
    }

    private void initializeViewPager() {
        m_contentPager = (ViewPager) findViewById(R.id.tab_content_pager);
        m_contentPager.setAdapter(new TabContentViewPagerAdapter());
        m_contentPager.addOnPageChangeListener(new TabContentPager_OnPageChangeListener());
        itemLongClickListener = new ItemLongClickListener();
        itemOnClickListener = new ItemOnClickListener();
        m_contentPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                int count = ((ListView) m_contentViewList.get(position).findViewById(R.id.todo_list)).getCount();
                setBackGroundNumber(count);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
        });
    }

    private void addTab(TabOnClickListener tabOnClickListener, int dayInMonth) {
        m_contentViewList.add(null);
        if (m_Inflater == null) {
            m_Inflater = getLayoutInflater();
        }
        TextView tab = (TextView) m_Inflater.inflate(R.layout.scrollable_bar_item, m_tabBar, false);
        m_tabList.add(tab);
        tab.setId(m_tabList.size());
        tab.setText(DateUtils.WEEK[(week + dayInMonth + 350 - day) % 7] + dayInMonth);
        tab.setTag(dayInMonth - 1);// 通过tag保存与内容视图关联的序号，从0开始计数
        tab.setOnClickListener(tabOnClickListener);
        m_tabBar.addView(tab);
    }

    class TabOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            CheckTab(v);
        }
    }

    private void CheckTab(View v) {
        int index = (Integer) v.getTag();
        currentDate.set(Calendar.DAY_OF_MONTH, index + 1);
        if (m_currentTab != null) {
            //m_currentTab.setBackgroundColor(getResources().getColor(R.color.background));
            m_currentTab.setSelected(false);
        }
        m_currentTab = (TextView) v;m_currentTab.setSelected(true);
        //m_currentTab.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
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

    private void setBackGroundNumber(int count) {
        TextView backgroundImage = (TextView) findViewById(R.id.home_background_picture);
        if (count == 1) {
            backgroundImage.setText("1  Task ");
        } else if (count > 1) {
            backgroundImage.setText(count + "  Tasks ");
        } else {
            backgroundImage.setText("no  Task ");
        }
    }

    class TabContentViewPagerAdapter extends PagerAdapter {
        @Override
        public void destroyItem(ViewGroup arg0, int arg1, Object arg2) {
            arg0.removeView(m_contentViewList.get(arg1));
        }

        @Override
        public int getCount() {
            return numOfDayInMonth;
        }

        @Override
        public Object instantiateItem(ViewGroup arg0, int arg1) {
            if (m_contentViewList.get(arg1) == null) {
                initializeItemsList(arg1);
            }
            arg0.addView(m_contentViewList.get(arg1), 0);
            return m_contentViewList.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (arg1);
        }
    }

    private void initializeItemsList(int arg1) {
        if (m_Inflater == null) {
            m_Inflater = getLayoutInflater();
        }
        View v = m_Inflater.inflate(R.layout.todo_items_list, null);
        m_contentViewList.set(arg1, v);
        final ListView listView = (ListView) v.findViewById(R.id.todo_list);
        Calendar calendar = (Calendar) currentDate.clone();
        calendar.set(Calendar.DAY_OF_MONTH, arg1 + 1);
        final boolean isCurrentItem = calendar.get(Calendar.YEAR) == currentDate.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == currentDate.get(Calendar.DAY_OF_YEAR);
        String date = dateFormat.format(calendar.getTime());
        new AsyncTask<String, Void, List>() {
            @Override
            protected List doInBackground(String... params) {
                return dbManager.retrieveData(params[0]);
            }
            protected void onPostExecute(List result) {
                listView.setAdapter(new TodoListAdapter(HomeActivity.this, R.layout.todo_item_cell, result));
                if (isCurrentItem) {
                    setBackGroundNumber(result.size());
                }
                if (isMonthChanged) {
                    isMonthChanged = false;
                    setBackGroundNumber(result.size());
                }
            }
        }.execute(date);
        listView.setOnItemLongClickListener(itemLongClickListener);
        listView.setOnItemClickListener(itemOnClickListener);
    }

    class TabContentPager_OnPageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageSelected(int arg0) {
            CheckTab(m_tabList.get(arg0));
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {}

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {}
    }

    public class TodoListAdapter extends ArrayAdapter<DBContract> {
        private int resourceId;

        public TodoListAdapter(Context context, int resource, List<DBContract> objects) {
            super(context, resource, objects);
            resourceId = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            DBContract item = getItem(position);
            if (convertView == null || convertView.getTag() != item) {
                convertView = m_Inflater.inflate(resourceId, parent, false);

                TextView title = (TextView) convertView.findViewById(R.id.todo_item_title);
                title.setText(item.title);
                TextView time = (TextView) convertView.findViewById(R.id.todo_item_text_time);
                time.setText(item.fromTime.substring(11, 16) + " - " + item.toTime.substring(11, 16));

                TextView description = (TextView) convertView.findViewById(R.id.todo_item_text_description);
                if (item.description.length() == 0) {
                    LinearLayout descriptionLayout = (LinearLayout) convertView.findViewById(R.id.home_list_item_description);
                    descriptionLayout.setVisibility(View.GONE);
                } else {
                    description.setText(item.description);
                }

                TextView notify = (TextView) convertView.findViewById(R.id.todo_item_text_notify);
                int index = Math.abs(item.notify) - 1;
                if (index == 0) {
                    LinearLayout alarm = (LinearLayout) convertView.findViewById(R.id.home_list_item_alarm);
                    alarm.setVisibility(View.GONE);
                } else if (index > 1) {
                    String[] notifyText = getResources().getStringArray(R.array.notify_array);
                    notify.setText(notifyText[index] + " before");
                } else {
                    String[] notifyText = getResources().getStringArray(R.array.notify_array);
                    notify.setText(notifyText[index]);
                }
                convertView.setTag(item);
            }
            LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.home_list_item_expand);
            if (item.notify > 0) {
                linearLayout.setVisibility(View.GONE);
            } else {
                linearLayout.setVisibility(View.VISIBLE);
            }
            return convertView;
        }
    }
    class ItemLongClickListener implements OnItemLongClickListener {
        @Override
        public boolean onItemLongClick(final AdapterView<?> parent, final View view, final int position, long id) {
            final LinearLayout layout = (LinearLayout) view;
            TextView textView = (TextView) layout.findViewById(R.id.todo_item_title);
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("Delete"); //设置标题
            builder.setMessage("Delete current item " + textView.getText() + '?');
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ArrayAdapter adapter = (ArrayAdapter) parent.getAdapter();
                    adapter.remove(adapter.getItem(position));
                    new AsyncTask<Long, Void, Integer>() {
                        @Override
                        protected Integer doInBackground(Long ... params) {
                            return dbManager.deleteData(params[0]);
                        }
                    }.execute(((DBContract) layout.getTag())._id);
                    setBackGroundNumber(adapter.getCount());
                    dialog.dismiss(); //关闭dialog
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();
            return false;
        }
    }
    class ItemOnClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ArrayAdapter adapter = (ArrayAdapter) parent.getAdapter();
            DBContract data = (DBContract) view.getTag();
            data.notify = - data.notify;
            adapter.notifyDataSetChanged();
        }
    }

}



