package com.handsome.robot.Activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;

import com.handsome.robot.R;

/**
 * Created by Joern on 2017/08/22.
 */

public class DrawerActivity extends Activity implements DrawerAdapter.OnItemClickListener  {
    private DrawerLayout drawerLayout;
    private RecyclerView leftDrawer;
    private ArrayAdapter<String> adapter;
    private final String[] items = new String[] { "选项一：", "选项二：", "选项三：",
            "选项4：", "选项5：", "选项6：" };

    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;

    private SizeFragment mSizeFragment;
    private BleFragment mbleFragment;
    private LocationFragment mlocationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);

        //初始化抽屉以及左边菜单
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftDrawer = (RecyclerView) findViewById(R.id.left_drawer);

        leftDrawer.setHasFixedSize(true);
        leftDrawer.setLayoutManager(new LinearLayoutManager(this));
        leftDrawer.setAdapter(new DrawerAdapter(items, this));
        leftDrawer.setBackgroundColor(Color.WHITE);

        // enable ActionBar app icon to behave as action to toggle nav drawer

      /*  getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);*/

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,   /*nav drawer image to replace 'Up' caret */
                R.string.drawer_open,   /*"open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                /*getActionBar().setTitle(mTitle);*/
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                /*getActionBar().setTitle(mDrawerTitle);*/
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);

      /*  if (savedInstanceState == null) {
            selectItem(0);
        }*/

    }


    @Override
    public void onClick(View view, int position) {
        selectItem(position);
    }


    private void selectItem(int position) {
        // update the main content by replacing fragments
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        //采用add方式进行fragment切换
        if (mbleFragment == null) {
            mbleFragment = BleFragment.newInstance("尺寸测量");
            transaction.add(R.id.center_main_content, mbleFragment);
        }
        hideAllFragment(transaction);
        transaction.show(mbleFragment);

        /*transaction.replace(R.id.center_main_content, mSizeFragment);*/
        transaction.commit();

        // update selected item title, then close the drawer
        drawerLayout.closeDrawer(leftDrawer);
    }

    //隐藏所有的fragment
    private void hideAllFragment(FragmentTransaction transaction) {
        if (mSizeFragment != null) {
            transaction.hide(mSizeFragment);
        }
        if (mlocationFragment != null) {
            transaction.hide(mlocationFragment);
        }
        if (mbleFragment != null) {
            transaction.hide(mbleFragment);
        }
    }

}
