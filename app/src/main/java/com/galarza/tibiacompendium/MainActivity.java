package com.galarza.tibiacompendium;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.galarza.tibiacompendium.data.Utils;

public class MainActivity extends AppCompatActivity {
    private CharSequence mTitle;

    public Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container,HomeFragment.newInstance())
                .addToBackStack(null)
                .commit();

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
    public static class HomeFragment extends  Fragment{
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
            super.onAttach(context);
            Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
            toolbar.setTitle(getArguments().getInt(Utils.ARG_TITLE_RESOURCE));

        }

    }

}
