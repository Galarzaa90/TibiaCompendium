package com.galarza.tibiacompendium;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.galarza.tibiacompendium.data.Utils;

public class MainActivity extends AppCompatActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    public Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        int in = R.anim.slide_in_right;
        int out = R.anim.slide_out_left;
        switch(position){
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container,ItemFragment.newInstance())
                        .setCustomAnimations(in,out,in,out)
                        .addToBackStack(null)
                        .commit();
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container,CharacterFragment.newInstance())
                        .setCustomAnimations(in,out,in,out)
                        .addToBackStack(null)
                        .commit();
                break;
            case 3:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, GuildFragment.newInstance())
                        .setCustomAnimations(in,out,in,out)
                        .addToBackStack(null)
                        .commit();
                break;
            default:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, HomeFragment.newInstance())
                        .setCustomAnimations(in,out,in,out)
                        .addToBackStack(null)
                        .commit();
                break;
        }
    }

    public void onSectionAttached(int titleResource) {
        mTitle = getString(titleResource);
    }

    private void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle(mTitle);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(fragment instanceof HomeFragment){
            finish();
        }
        super.onBackPressed();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class HomeFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static HomeFragment newInstance() {
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
                    int in = R.anim.slide_in_right;
                    int out = R.anim.slide_out_left;
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container,ItemFragment.newInstance())
                            .setCustomAnimations(in,out,in,out)
                            .addToBackStack(null)
                            .commit();
                }
            });

            final Button characterButton = (Button)rootView.findViewById(R.id.button_character);
            characterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int in = R.anim.slide_in_right;
                    int out = R.anim.slide_out_left;
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container,CharacterFragment.newInstance())
                            .setCustomAnimations(in,out,in,out)
                            .addToBackStack(null)
                            .commit();
                }
            });
            final Button guildButton = (Button)rootView.findViewById(R.id.button_guild);
            guildButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int in = R.anim.slide_in_right;
                    int out = R.anim.slide_out_left;
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container,GuildFragment.newInstance())
                            .setCustomAnimations(in,out,in,out)
                            .addToBackStack(null)
                            .commit();
                }
            });
            return rootView;
        }

        @Override
        public void onAttach(Context context) {
            ((MainActivity) context).onSectionAttached(
                    getArguments().getInt(Utils.ARG_TITLE_RESOURCE));
            super.onAttach(context);

        }

    }

}
