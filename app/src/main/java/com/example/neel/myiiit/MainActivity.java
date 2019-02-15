package com.example.neel.myiiit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.neel.myiiit.attendance.Attendance;
import com.example.neel.myiiit.mess.Mess;
import com.example.neel.myiiit.network.Network;

public class MainActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    SharedPreferences preferences;
    Toolbar toolbar;
    TabLayout tabLayout;
    AppBarLayout appBarLayout;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appbar);
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
                        appBarLayout.setBackgroundColor(getResources().getColor(R.color.Home2));
                        break;
                    case 1:
                        getWindow().setStatusBarColor(getResources().getColor(R.color.Meals3));
                        tabLayout.setBackgroundColor(getResources().getColor(R.color.Meals2));
                        toolbar.setBackgroundColor(getResources().getColor(R.color.Meals1));
                        appBarLayout.setBackgroundColor(getResources().getColor(R.color.Meals2));
                        break;
                    case 2:
                        getWindow().setStatusBarColor(getResources().getColor(R.color.Cancel3));
                        tabLayout.setBackgroundColor(getResources().getColor(R.color.Cancel2));
                        toolbar.setBackgroundColor(getResources().getColor(R.color.Cancel1));
                        appBarLayout.setBackgroundColor(getResources().getColor(R.color.Cancel2));
                        break;
                    case 3:
                        getWindow().setStatusBarColor(getResources().getColor(R.color.Attd3));
                        tabLayout.setBackgroundColor(getResources().getColor(R.color.Attd2));
                        toolbar.setBackgroundColor(getResources().getColor(R.color.Attd1));
                        appBarLayout.setBackgroundColor(getResources().getColor(R.color.Attd2));
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Network.removeCredentials(this);
            Mess.getInstance(this).clearCache();
            Attendance.clearCache(this);

            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


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
                    return new HomeFragment();
                case 1:
                    return new MessFragment();
                case 2:
                    return new MessCancelFragment();
                case 3:
                    return new AttendanceFragment();
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
