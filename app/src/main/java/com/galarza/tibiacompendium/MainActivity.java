package com.galarza.tibiacompendium;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.galarza.tibiacompendium.data.Utils;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public Fragment fragment;
    private DrawerLayout mDrawerLayout;

    public SharedPreferences preferences;

    public static final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /* Drawer views and actions */
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout,toolbar,R.string.open_navigation_drawer,R.string.close_navigation_drawer);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        FragmentManager fragmentManager = getSupportFragmentManager();

        if(savedInstanceState == null){
            fragmentManager.beginTransaction()
                    .replace(R.id.container,new HomeFragment())
                    .addToBackStack(null)
                    .commit();
        }

        preferences = getSharedPreferences(Utils.PREFS_NAME,MODE_PRIVATE);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_item:
                launchItemFragment();
                break;
            case R.id.action_character:
                launchCharacterFragment();
                break;
            case R.id.action_guild:
                launchGuildFragment();
                break;
        }
        mDrawerLayout.closeDrawers();
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(fragment instanceof HomeFragment){
            finish();
        }
        super.onBackPressed();
    }

    public void launchItemFragment(){
        int in = R.anim.slide_in_right;
        int out = R.anim.slide_out_left;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container,new ItemFragment())
                .setCustomAnimations(in,out,in,out)
                .addToBackStack(null)
                .commit();
    }

    public void launchCharacterFragment(){
        int in = R.anim.slide_in_right;
        int out = R.anim.slide_out_left;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new CharacterFragment())
                .setCustomAnimations(in,out,in,out)
                .addToBackStack(null)
                .commit();
    }

    public void launchGuildFragment(){
        int in = R.anim.slide_in_right;
        int out = R.anim.slide_out_left;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container,new GuildFragment())
                .setCustomAnimations(in,out,in,out)
                .addToBackStack(null)
                .commit();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class HomeFragment extends  Fragment{
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public HomeFragment newInstance() {
            HomeFragment fragment = new HomeFragment();
            Bundle args = new Bundle();
            args.putInt(Utils.ARG_TITLE_RESOURCE, R.string.title_home);
            fragment.setArguments(args);
            return fragment;
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ((MainActivity)getActivity()).fragment = this;
            /* Button listeners */
            final Button ItemsButton = (Button)rootView.findViewById(R.id.button_items);
            ItemsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)getActivity()).launchItemFragment();
                }
            });

            final Button characterButton = (Button)rootView.findViewById(R.id.button_character);
            characterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)getActivity()).launchCharacterFragment();
                }
            });
            final Button guildButton = (Button)rootView.findViewById(R.id.button_guild);
            guildButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((MainActivity)getActivity()).launchGuildFragment();
                }
            });
            return rootView;
        }

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        }

    }

}
