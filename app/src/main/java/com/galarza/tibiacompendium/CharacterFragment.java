package com.galarza.tibiacompendium;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

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
    private TextView mFormerNamesTitle;
    private TextView mFormerNames;
    private TextView mFormerWorldTitle;
    private TextView mFormerWorld;
    private TextView mLastLogin;
    private TextView mAccountStatus;

    private TextView mComment;
    private ViewGroup mDeaths;
    private ViewGroup mCharacters;

    private ViewGroup mContainerComment;
    private ViewGroup mContainerDeaths;
    private ViewGroup mContainerChars;

    private Set<String> history;
    SharedPreferences preferences;

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

        preferences = ((MainActivity) getActivity()).preferences;
        history = preferences.getStringSet(Utils.PREFS_CHARACTER_HISTORY, new HashSet<String>());

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
        mGuild = (TextView)rootView.findViewById(R.id.guild);
        mFormerNames = (TextView)rootView.findViewById(R.id.former_names);
        mFormerWorld = (TextView)rootView.findViewById(R.id.former_world);
        mLastLogin = (TextView)rootView.findViewById(R.id.last_login);
        mAccountStatus = (TextView)rootView.findViewById(R.id.account_status);
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

        final AutoCompleteTextView fieldSearch = (AutoCompleteTextView) rootView.findViewById(R.id.field_search);
        fieldSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    /* Hide virtual keyboard */
                    InputMethodManager inputMethodManager =
                            (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);

                    fetchCharacter(v.getText().toString().trim());
                    return true;
                }
                return false;
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_1, history.toArray(new String[history.size()]));
        fieldSearch.setAdapter(adapter);


        final Button buttonSearch = (Button) rootView.findViewById(R.id.button_search);
        buttonSearch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Hide virtual keyboard */
                InputMethodManager inputMethodManager =
                        (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);

                fetchCharacter(fieldSearch.getText().toString());
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
        Bundle arguments = getArguments();
        String playerName = null;
        if(arguments != null) {
            playerName = getArguments().getString(Utils.ARG_PLAYER_NAME);
        }
        /* If a player is already loaded, fill views */
        if(mPlayer != null){
            loadViews(mPlayer);
        /* If fragment was called with a name argument, load name */
        }else if(playerName != null){
            fieldSearch.setText(playerName);
            fetchCharacter(playerName);
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


    private void fetchCharacter(String name){
        if(!NetworkUtils.isConnected(getContext())){
            Toast.makeText(getContext(),R.string.no_network_error,Toast.LENGTH_SHORT).show();
            return;
        }

        Request request;
        try {
            request = new Request.Builder()
                    .url("https://secure.tibia.com/community/?subtopic=characters&name="+ URLEncoder.encode(name,"UTF-8"))
                    .build();
        } catch (UnsupportedEncodingException e) {
            return;
        }

        mPlayer = null;
        mContainerLoading.setVisibility(View.VISIBLE);
        mContainerCharacter.setVisibility(View.GONE);
        mContainerComment.setVisibility(View.GONE);
        mContainerDeaths.setVisibility(View.GONE);
        mContainerChars.setVisibility(View.GONE);
        mContainerNoResults.setVisibility(View.GONE);

        MainActivity.client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mContainerLoading.setVisibility(View.GONE);
                        Toast.makeText(getContext(),R.string.network_error,Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!response.isSuccessful()){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(),R.string.network_error,Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                final Player player = Parser.parseCharacter(response.body().string());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mContainerLoading.setVisibility(View.GONE);
                        mPlayer = player;
                        loadViews(mPlayer);
                    }
                });
            }
        });


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        if(toolbar != null) {
            toolbar.setTitle(R.string.title_search_char);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(Utils.PREFS_CHARACTER_HISTORY, history);
        editor.apply();
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

        history.add(player.getName());

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
                    fetchCharacter(marriageString);
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
                    GuildFragment fragment = new GuildFragment();
                    Bundle arguments = new Bundle();
                    arguments.putString(Utils.ARG_GUILD_NAME,guild);
                    fragment.setArguments(arguments);

                    getFragmentManager().beginTransaction()
                            .setCustomAnimations(in,out,in,out)
                            .addToBackStack(null)
                            .replace(R.id.container,fragment)
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

        if(player.isPremium()){
            mAccountStatus.setText(R.string.premium_account);
        }else{
            mAccountStatus.setText(R.string.free_account);
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
                    fetchCharacter(((TextView) v.findViewById(R.id.name)).getText().toString());
                }
            });
            parent.addView(rowView);
        }
    }
}
