package com.nfcalarmclock;

import android.os.Bundle;
import android.content.res.Configuration;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import android.content.res.Resources;
import android.graphics.Rect;
// import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.content.Intent;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener
{
    private RecyclerView recyclerView;
    private AlarmAdapter adapter;
    private List<Alarm> alarmList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Toolbar toolbar = getToolbar();
        // setSupportActionBar(toolbar);

        // if (getSupportActionBar() == null)
        // {
        //     Toast.makeText(this, "Fuck the first action bar is null :(", 
        //                    Toast.LENGTH_LONG).show();
        // }
        // else
        // {
        //     Toast.makeText(this, "It works! :)", 
        //                    Toast.LENGTH_LONG).show();
        // }

        // DrawerLayout drawer = getNavigationDrawer();
        FloatingActionButton newalarm = getNewAlarmButton();
        // setupNavigationDrawer(drawer, toolbar);
        setupNewAlarmButton(newalarm);
        // getNavigationDrawerView().setNavigationItemSelectedListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.alarm_list);

        alarmList = new ArrayList<>();
        adapter = new AlarmAdapter(this, alarmList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        // recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        prepareAlarms();

        // if (getSupportActionBar() == null)
        // {
        //     Toast.makeText(this, "Fuck the action bar is definitely null :(", 
        //                    Toast.LENGTH_LONG).show();
        // }
        // else
        // {
        //     Toast.makeText(this, "It definitely works! :)", 
        //                    Toast.LENGTH_LONG).show();
        // }

        // // getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // // getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    // @Override
    // public boolean onSupportNavigateUp()
    // {  
    //     finish();  
    //     return true;  
    // }

    @Override
    public void onBackPressed()
    {
        DrawerLayout drawer = getNavigationDrawer();
        if (drawer.isDrawerOpen(GravityCompat.START))
        {
            drawer.closeDrawer(GravityCompat.START);
        }
        else
        {
            super.onBackPressed();
        }
    }

    /**
     * @brief Create the options menu in the action bar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        switch (id)
        {
        case android.R.id.home:
            getNavigationDrawer().openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        // Fragment fragment;
        // FragmentManager manager;
        // FragmentTransaction transaction;
        Intent intent;
        int id = item.getItemId();
        View view = getWindow().getDecorView().getRootView();

        switch(id)
        {
        case R.id.drawer_alarm_all:
            intent = new Intent(getApplicationContext(), AddAlarmActivity.class);
            break;
        case R.id.drawer_about:
            getNavigationDrawer().closeDrawer(GravityCompat.START);
            Snackbar.make(view, "Display About",
                          Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
            return true;
        case R.id.drawer_settings:
            getNavigationDrawer().closeDrawer(GravityCompat.START);
            Snackbar.make(view, "Display Settings",
                          Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
            return true;
        default:
            getNavigationDrawer().closeDrawer(GravityCompat.START);
            Snackbar.make(view, "Default ID",
                          Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
            return true;
        }

        // manager = getSupportFragmentManager();
        // transaction = manager.beginTransaction();
        // transaction.replace(R.id.fragment_new_alarm, fragment);
        // transaction.addToBackStack(null);
        // transaction.commit();

        // item.setChecked(true);
        // setTitle(item.getTitle());
        getNavigationDrawer().closeDrawer(GravityCompat.START);

        startActivity(intent);

        return true;
    }

    /**
     * Adding few alarm for testing
     */
    private void prepareAlarms()
    {
        // int[] covers = new int[]
        //     {
        //         R.drawable.album1,
        //         R.drawable.album2,
        //         R.drawable.album3,
        //         R.drawable.album4,
        //         R.drawable.album5,
        //         R.drawable.album6,
        //         R.drawable.album7,
        //         R.drawable.album8,
        //         R.drawable.album9,
        //         R.drawable.album10,
        //         R.drawable.album11
        //     };

        int[] covers = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 };

        Alarm a = new Alarm("True Romance", 13, covers[0]);
        alarmList.add(a);

        a = new Alarm("Xscpae", 8, covers[1]);
        alarmList.add(a);

        a = new Alarm("Maroon 5", 11, covers[2]);
        alarmList.add(a);

        a = new Alarm("Born to Die", 12, covers[3]);
        alarmList.add(a);

        a = new Alarm("Honeymoon", 14, covers[4]);
        alarmList.add(a);

        a = new Alarm("I Need a Doctor", 1, covers[5]);
        alarmList.add(a);

        a = new Alarm("Loud", 11, covers[6]);
        alarmList.add(a);

        a = new Alarm("Legend", 14, covers[7]);
        alarmList.add(a);

        a = new Alarm("Hello", 11, covers[8]);
        alarmList.add(a);

        a = new Alarm("Greatest Hits", 17, covers[9]);
        alarmList.add(a);

        adapter.notifyDataSetChanged();
    }

    /**
     * @brief Setup the Navigation Drawer.
     */
    private void setupNavigationDrawer(DrawerLayout drawer, Toolbar toolbar)
    {
        // ActionBar actionbar = getSupportActionBar();
        // actionbar.setDisplayHomeAsUpEnabled(true);
        // actionbar.setHomeAsUpIndicator(R.mipmap.ic_menu_white_24dp);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * @brief Setup the New Alarm button.
     */
    private void setupNewAlarmButton(FloatingActionButton button)
    {
        button.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(getApplicationContext(), AddAlarmActivity.class);
                    startActivity(intent);

                    // AddAlarmFragment addalarm = new AddAlarmFragment();
                    // FragmentManager manager = getSupportFragmentManager();
                    // FragmentTransaction transaction = manager.beginTransaction();
                    // transaction.replace(R.id.fragment_main, addalarm);
                    // transaction.addToBackStack(null);
                    // transaction.commit();

                    // Snackbar.make(view, "Replace with your own action",
                    //               Snackbar.LENGTH_LONG)
                    //     .setAction("Action", null).show();
                }
            });
    }

    /**
     * @brief Return the Toolbar.
     * 
     * @return Toolbar.
     */
    private Toolbar getToolbar()
    {
        return (Toolbar) findViewById(R.id.ab_toolbar);
    }

    /**
     * @brief Return the Navigation Drawer layout.
     * 
     * @return DrawerLayout.
     */
    private DrawerLayout getNavigationDrawer()
    {
        return (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    /**
     * @brief Return the Navigation View of the Navigation Drawer.
     * 
     * @return NavigationView.
     */
    private NavigationView getNavigationDrawerView()
    {
        return (NavigationView) findViewById(R.id.nvView);
    }

    /**
     * @brief Return the New Alarm button.
     * 
     * @return Floating action button.
     */
    private FloatingActionButton getNewAlarmButton()
    {
        return (FloatingActionButton) findViewById(R.id.fab_new_alarm);
    }
}
