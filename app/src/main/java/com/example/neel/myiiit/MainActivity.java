package com.example.neel.myiiit;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    MessCancelFragment messCancelFragment;
    MessFragment messFragment;
    AttendanceFragment attendanceFragment;
    HomeFragment homeFragment;
    Toolbar toolbar;
    TabLayout tabLayout;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager =  findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        tabLayout =  findViewById(R.id.tabs);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        getWindow().setStatusBarColor(getResources().getColor(R.color.Home3));
        tabLayout.setBackgroundColor(getResources().getColor(R.color.Home2));
        toolbar.setBackgroundColor(getResources().getColor(R.color.Home1));

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        getWindow().setStatusBarColor(getResources().getColor(R.color.Home3));
                        tabLayout.setBackgroundColor(getResources().getColor(R.color.Home2));
                        toolbar.setBackgroundColor(getResources().getColor(R.color.Home1));
                        break;
                    case 1:
                        getWindow().setStatusBarColor(getResources().getColor(R.color.Meals3));
                        tabLayout.setBackgroundColor(getResources().getColor(R.color.Meals2));
                        toolbar.setBackgroundColor(getResources().getColor(R.color.Meals1));
                        break;
                    case 2:
                        getWindow().setStatusBarColor(getResources().getColor(R.color.Cancel3));
                        tabLayout.setBackgroundColor(getResources().getColor(R.color.Cancel2));
                        toolbar.setBackgroundColor(getResources().getColor(R.color.Cancel1));
                        break;
                    case 3:
                        getWindow().setStatusBarColor(getResources().getColor(R.color.Attd3));
                        tabLayout.setBackgroundColor(getResources().getColor(R.color.Attd2));
                        toolbar.setBackgroundColor(getResources().getColor(R.color.Attd1));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0:
                    homeFragment = new HomeFragment();
                    return homeFragment;
                case 1:
                    messFragment = new MessFragment();
                    return  messFragment;
                case 2:
                    messCancelFragment = new MessCancelFragment();
                    return messCancelFragment;
                case 3:
                    attendanceFragment = new AttendanceFragment();
                    return attendanceFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
