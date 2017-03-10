package yogi.com.translateapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import yogi.com.translateapp.R;
import yogi.com.translateapp.fragments.TranslateFragment;

/**
 * Created by Paul on 3/2/17.
 */

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String LOG_TAG = BaseActivity.class.getName();

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    protected void setupPageLayout() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(yogi.com.translateapp.R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_clear : {
                if(getSupportFragmentManager().findFragmentById(R.id.flContent) instanceof TranslateFragment) {
                    TranslateFragment tFrag = (TranslateFragment)
                            getSupportFragmentManager().findFragmentById(R.id.flContent);
                    tFrag.clearTranslationBoxes();
                }

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public void makePhoneCall() {

        String number = "tel:" + yogi.com.translateapp.consts.Consts.HELP_PHONE_NUMBER;
        Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(number));

        if(yogi.com.translateapp.utils.Utils.checkCallPhonePermission()) {
            startActivity(callIntent);
        }
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Fragment fragment = null;
        Class fragmentClass = null;
        if (id == yogi.com.translateapp.R.id.nav_settings) {
            fragmentClass = yogi.com.translateapp.fragments.SettingsFragment.class;
        } else if (id == yogi.com.translateapp.R.id.nav_translate) {
            fragmentClass = yogi.com.translateapp.fragments.TranslateFragment.class;
        }

        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            Log.e(LOG_TAG, "onNavigationItemSelected: ", e);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(yogi.com.translateapp.R.id.flContent, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(yogi.com.translateapp.R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
