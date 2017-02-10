package com.galarza.tibiacompendium;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.galarza.tibiacompendium.data.Death;
import com.galarza.tibiacompendium.data.NetworkUtils;
import com.galarza.tibiacompendium.data.Parser;
import com.galarza.tibiacompendium.data.Player;
import com.galarza.tibiacompendium.data.Utils;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

public class CharacterFragment extends Fragment {
    private Player mPlayer = null;

    /* Views used in the async task */
    private ViewGroup mContainerCharacter;
    private ViewGroup mContainerLoading;
    private ViewGroup mContainerNoResults;

    private ImageView mGender;
    private ImageView mWarning;
    private TextView mName;
    private TextView mSummary;
    private TextView mResidence;
    private TextView mMarriageTitle;
    private TextView mMarriage;
    private TextView mHouseTitle;
    private TextView mHouse;
    private TextView mGuildTitle;
    private TextView mGuild;
    private TextView mAchievements;
    private TextView mFormerNamesTitle;
    private TextView mFormerNames;
    private TextView mFormerWorldTitle;
    private TextView mFormerWorld;
    private TextView mLastLogin;

    private TextView mComment;
    private ViewGroup mDeaths;
    private ViewGroup mCharacters;

    private ViewGroup mContainerComment;
    private ViewGroup mContainerDeaths;
    private ViewGroup mContainerChars;

    public static CharacterFragment newInstance() {
        CharacterFragment fragment = new CharacterFragment();
        Bundle args = new Bundle();
        args.putInt(Utils.ARG_TITLE_RESOURCE, R.string.title_search_char);
        fragment.setArguments(args);
        return fragment;
    }

    public static CharacterFragment searchInstance(String name){
        CharacterFragment fragment = newInstance();
        Bundle args = fragment.getArguments();
        args.putString(Utils.ARG_PLAYER_NAME,name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_character, container, false);

        ((MainActivity)getActivity()).fragment = this;

        /* Views used in the async task */
        mContainerCharacter = (ViewGroup) rootView.findViewById(R.id.container_character);
        mContainerLoading = (ViewGroup) rootView.findViewById(R.id.container_loading);
        mContainerNoResults = (ViewGroup) rootView.findViewById(R.id.container_no_results);

        mGender = (ImageView)rootView.findViewById(R.id.gender);
        mWarning = (ImageView)rootView.findViewById(R.id.warning);
        mName = (TextView)rootView.findViewById(R.id.name);
        mSummary = (TextView)rootView.findViewById(R.id.summary);
        mResidence = (TextView)rootView.findViewById(R.id.residence);
        mMarriage = (TextView)rootView.findViewById(R.id.marriage);
        mHouse = (TextView)rootView.findViewById(R.id.house);
        mAchievements = (TextView)rootView.findViewById(R.id.achievements);
        mGuild = (TextView)rootView.findViewById(R.id.guild);
        mFormerNames = (TextView)rootView.findViewById(R.id.former_names);
        mFormerWorld = (TextView)rootView.findViewById(R.id.former_world);
        mLastLogin = (TextView)rootView.findViewById(R.id.last_login);
        mComment = (TextView)rootView.findViewById(R.id.comment);

        mDeaths = (ViewGroup) rootView.findViewById(R.id.deaths);
        mCharacters = (ViewGroup) rootView.findViewById(R.id.other_chars);

        mMarriageTitle = (TextView) rootView.findViewById(R.id.marriage_title);
        mHouseTitle = (TextView) rootView.findViewById(R.id.house_title);
        mGuildTitle = (TextView) rootView.findViewById(R.id.guild_title);
        mFormerNamesTitle = (TextView) rootView.findViewById(R.id.former_names_title);
        mFormerWorldTitle = (TextView) rootView.findViewById(R.id.former_world_title);

        mContainerComment = (ViewGroup) rootView.findViewById(R.id.container_comment);
        mContainerDeaths = (ViewGroup)rootView.findViewById(R.id.container_deaths);
        mContainerChars = (ViewGroup) rootView.findViewById(R.id.container_chars);

        final EditText fieldSearch = (EditText)rootView.findViewById(R.id.field_search);
        fieldSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    /* Hide virtual keyboard */
                    InputMethodManager inputMethodManager =
                            (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);

                    new fetchData(getContext()).execute(v.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });

        final Button buttonSearch = (Button) rootView.findViewById(R.id.button_search);
        buttonSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Hide virtual keyboard */
                InputMethodManager inputMethodManager =
                        (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);

                new fetchData(getContext()).execute(fieldSearch.getText().toString());
            }
        });

        /* Expand/Collapse buttons listeners */
        final TextView headerComment = (TextView) rootView.findViewById(R.id.header_comment);
        headerComment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mComment.getVisibility() == View.GONE){
                    mComment.setVisibility(View.VISIBLE);
                    headerComment.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_up,0);
                }else{
                    mComment.setVisibility(View.GONE);
                    headerComment.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_down,0);
                }
            }
        });

        final TextView headerDeaths = (TextView)rootView.findViewById(R.id.header_deaths);
        headerDeaths.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mDeaths.getVisibility() == View.GONE){
                    mDeaths.setVisibility(View.VISIBLE);
                    headerDeaths.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_up,0);
                }else{
                    mDeaths.setVisibility(View.GONE);
                    headerDeaths.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_down,0);
                }
            }
        });

        final TextView headerChars = (TextView) rootView.findViewById(R.id.header_chars);
        headerChars.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCharacters.getVisibility() == View.GONE){
                    mCharacters.setVisibility(View.VISIBLE);
                    headerChars.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_up,0);
                }else{
                    mCharacters.setVisibility(View.GONE);
                    headerChars.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_down,0);
                }
            }
        });
        /* Getting player name argument */
        String playerName = getArguments().getString(Utils.ARG_PLAYER_NAME);
        /* If a player is already loaded, fill views */
        if(mPlayer != null){
            loadViews(mPlayer);
        /* If fragment was called with a name argument, load name */
        }else if(playerName != null){
            fieldSearch.setText(playerName);
            new fetchData(getContext()).execute(playerName);
        }

        /* Recovering state */
        if(savedInstanceState != null){
            String playerJson = savedInstanceState.getString("PLAYER","");
            if(!playerJson.isEmpty()){
                Gson gson = new Gson();
                mPlayer = gson.fromJson(playerJson,Player.class);
                loadViews(mPlayer);
            }
        }

        return rootView;
    }



    @Override
    public void onSaveInstanceState(Bundle outState) {
        Gson gson = new Gson();
        String playerJson = null;
        if(mPlayer != null){
            playerJson = gson.toJson(mPlayer);
        }

        outState.putString("PLAYER",playerJson);
        super.onSaveInstanceState(outState);
    }

    private class fetchData extends AsyncTask<String, Integer, Player> {
        private final Context mContext;
        fetchData(Context context){
            mContext = context;
        }

        @Override
        protected Player doInBackground(String... params) {
            if(!NetworkUtils.isConnected(mContext)){
                publishProgress(Utils.NO_NETWORK_ENABLED);
                return null;
            }

            HttpURLConnection connection;
            InputStream stream;
            try {
                connection = (HttpURLConnection)(
                        new URL("https://secure.tibia.com/community/?subtopic=characters&name="+ URLEncoder.encode(params[0],"UTF-8")))
                        .openConnection();
                connection.setRequestMethod("GET");
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.connect();

                //Reading response
                StringBuilder buffer = new StringBuilder();
                stream = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                stream.close();
                connection.disconnect();

                return Parser.parseCharacter(buffer.toString());
            }catch (SocketTimeoutException s){
                //publishProgress();
            }catch (IOException e) {
                publishProgress(Utils.COULDNT_REACH);
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress){
            if(progress[0] == Utils.COULDNT_REACH){
                Toast.makeText(getContext(),R.string.network_error,Toast.LENGTH_SHORT).show();
            }
            if(progress[0] == Utils.NO_NETWORK_ENABLED){
                Toast.makeText(getContext(),R.string.no_network_error,Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            mPlayer = null;
            mContainerLoading.setVisibility(View.VISIBLE);
            mContainerCharacter.setVisibility(View.GONE);
            mContainerComment.setVisibility(View.GONE);
            mContainerDeaths.setVisibility(View.GONE);
            mContainerChars.setVisibility(View.GONE);
            mContainerNoResults.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Player result){
            mContainerLoading.setVisibility(View.GONE);
            mPlayer = result;
            loadViews(mPlayer);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        if(toolbar != null) {
            toolbar.setTitle(getArguments().getInt(Utils.ARG_TITLE_RESOURCE));
        }
    }

    private void loadViews(final Player player){
        if(player == null){
            mContainerNoResults.setVisibility(View.VISIBLE);
            mContainerCharacter.setVisibility(View.GONE);
            mContainerComment.setVisibility(View.GONE);
            mContainerDeaths.setVisibility(View.GONE);
            mContainerChars.setVisibility(View.GONE);
            return;
        }
        mContainerNoResults.setVisibility(View.GONE);
        mContainerCharacter.setVisibility(View.VISIBLE);
        /* Name */
        mName.setText(player.getName());
        /* Summary (level & vocation) */
        mSummary.setText(getString(
                R.string.char_summary,
                player.getLevel(),
                player.getVocation().equalsIgnoreCase("none") ? "" : player.getVocation()
        ));
        /* Sex */
        if(player.getSex().equalsIgnoreCase("female")){
            mGender.setImageResource(R.drawable.ic_female);
            mGender.setContentDescription(getString(R.string.female));
        }else{
            mGender.setImageResource(R.drawable.ic_male);
            mGender.setContentDescription(getString(R.string.male));
        }
        /* Achievement points */
        mAchievements.setText(String.valueOf(player.getAchievementPoints()));
        /* Residence */
        mResidence.setText(getString(
                R.string.char_residence,
                player.getResidence(),
                player.getWorld()
        ));
        /* Marriage */
        if(player.getMarriage() != null){
            final String marriageString = player.getMarriage();
            SpannableString marriageStyled = new SpannableString(marriageString);
            marriageStyled.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View view) {
                    new fetchData(getContext()).execute(marriageString);
                }
            },0,marriageString.length(),0);
            mMarriage.setMovementMethod(LinkMovementMethod.getInstance());
            mMarriage.setText(marriageStyled);
            mMarriage.setVisibility(View.VISIBLE);
            mMarriageTitle.setVisibility(View.VISIBLE);
        }else{
            mMarriage.setVisibility(View.GONE);
            mMarriageTitle.setVisibility(View.GONE);
        }
        /* House */
        if(player.getHouse() != null){
            Log.e("afafaf","house set visible");
            mHouseTitle.setVisibility(View.VISIBLE);
            mHouse.setVisibility(View.VISIBLE);
            mHouse.setText(getString(
                    R.string.char_house,
                    player.getHouse(),
                    player.getHouseCity()
            ));
        }else{
            Log.e("afafaf","house set gone");
            mHouse.setVisibility(View.GONE);
            mHouseTitle.setVisibility(View.GONE);
        }
        /* Guild */
        if(player.getGuild() != null){
            mGuild.setVisibility(View.VISIBLE);
            mGuildTitle.setVisibility(View.VISIBLE);
            final String guildRank = player.getGuildRank();
            final String guild = player.getGuild();
            final String guildString = getString(R.string.guildcontent,guildRank,guild);
            /* Make guild name touchable */
            SpannableString guildStyled = new SpannableString(guildString);
            int startIndex = guildString.length()-guild.length();
            int endIndex = guildString.length();
            guildStyled.setSpan(new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    int in = R.anim.slide_in_right;
                    int out = R.anim.slide_out_left;
                    getFragmentManager().beginTransaction()
                            .setCustomAnimations(in,out,in,out)
                            .addToBackStack(null)
                            .replace(R.id.container,GuildFragment.searchInstance(guild))
                            .commit();
                }
            },startIndex,endIndex,0);
            mGuild.setMovementMethod(LinkMovementMethod.getInstance());
            mGuild.setText(guildStyled);
        }else{
            mGuild.setVisibility(View.GONE);
            mGuildTitle.setVisibility(View.GONE);
        }
        /* Former names */
        if(player.getFormerNames() != null){
            mFormerNamesTitle.setVisibility(View.VISIBLE);
            mFormerNames.setVisibility(View.VISIBLE);
            mFormerNames.setText(player.getFormerNames());
        }else{
            mFormerNamesTitle.setVisibility(View.GONE);
            mFormerNames.setVisibility(View.GONE);
        }
        /* Former world */
        if(player.getFormerWorld() != null){
            mFormerWorldTitle.setVisibility(View.VISIBLE);
            mFormerWorld.setVisibility(View.VISIBLE);
            mFormerWorld.setText(player.getFormerWorld());
        }else{
            mFormerWorldTitle.setVisibility(View.GONE);
            mFormerWorld.setVisibility(View.GONE);
        }
        /* Last login */
        if(player.getLastLoginString() != null) {
            mLastLogin.setText(player.getLastLoginString());
        }
        /* Comment */
        if(player.getComment() != null) {
            mComment.setText(Utils.fromHtml(player.getComment()));
            mContainerComment.setVisibility(View.VISIBLE);
        }
        /* Deaths */
        if(player.getDeathList().size() > 0) {
            mDeaths.removeAllViews();
            mContainerDeaths.setVisibility(View.VISIBLE);
            loadDeathsView(getContext(), mDeaths, player.getDeathList());
        }
        if(player.getOtherCharacters().size() > 1) {
            mCharacters.removeAllViews();
            mContainerChars.setVisibility(View.VISIBLE);
            loadCharsView(getContext(), mCharacters, player.getOtherCharacters());
        }

        /* Deletion */
        if(player.getDeletion() != null){
            mWarning.setVisibility(View.VISIBLE);
            mWarning.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast toast = Toast.makeText(getContext(),getString(R.string.deletion, player.getDeletionString()),Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
            });
        }else{
            mWarning.setVisibility(View.GONE);
        }
    }


    private void loadDeathsView(Context context, ViewGroup parent, List<Death> deaths) {
        LayoutInflater inflater =
                (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(Death death : deaths) {
            View rowView = inflater.inflate(R.layout.row_death, null);

            TextView dateView = (TextView)rowView.findViewById(R.id.death_date);
            dateView.setText(death.getDateString());

            TextView detailsView = (TextView)rowView.findViewById(R.id.death_details);
            detailsView.setText(getString(R.string.death_details,
                    death.isByPlayer() ? "Killed" : "Died",
                    death.getLevel(),
                    death.getKiller()));

            parent.addView(rowView);
        }
    }

    private void loadCharsView(Context context, ViewGroup parent, List<Player> players) {
        LayoutInflater inflater =
                (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for(Player player : players) {
            View rowView = inflater.inflate(R.layout.row_other_char, null);
            TextView charName = (TextView) rowView.findViewById(R.id.name);
            TextView charWorld = (TextView) rowView.findViewById(R.id.char_world);

            charName.setText(player.getName());
            charWorld.setText(player.getWorld());

            rowView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new fetchData(getContext()).execute(((TextView) v.findViewById(R.id.name)).getText().toString());
                }
            });
            parent.addView(rowView);
        }
    }
}
