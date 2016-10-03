package com.galarza.tibiacompendium;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.galarza.tibiacompendium.data.Death;
import com.galarza.tibiacompendium.data.NetworkUtils;
import com.galarza.tibiacompendium.data.Parser;
import com.galarza.tibiacompendium.data.Player;
import com.galarza.tibiacompendium.data.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CharacterFragment extends Fragment {
    private Player player = null;

    /* Views used in the async task */
    private ViewGroup characterInfo;
    private RelativeLayout boxLoading;
    private RelativeLayout boxNoResults;

    private ImageView characterGender;
    private ImageView characterWarning;
    private TextView characterName;
    private TextView characterSummary;
    private TextView characterResidence;
    private TextView characterHouse;
    private TextView characterGuild;
    private TextView characterAchievements;
    private TextView characterFormerNames;
    private TextView characterFormerWorld;
    private TextView characterLastLogin;
    private TextView characterComment;

    private LinearLayout characterDeaths;
    private LinearLayout otherCharacters;

    private LinearLayout boxHouse;
    private LinearLayout boxGuild;
    private LinearLayout boxFormerName;
    private LinearLayout boxFormerWorld;

    private ViewGroup boxComment;
    private ViewGroup boxDeaths;
    private ViewGroup boxChars;

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
        characterInfo = (ViewGroup) rootView.findViewById(R.id.character_box);
        boxLoading = (RelativeLayout)rootView.findViewById(R.id.loading_box);
        boxNoResults = (RelativeLayout)rootView.findViewById(R.id.no_results_box);

        characterGender = (ImageView)rootView.findViewById(R.id.gender);
        characterWarning = (ImageView)rootView.findViewById(R.id.warning);
        characterName = (TextView)rootView.findViewById(R.id.name);
        characterSummary = (TextView)rootView.findViewById(R.id.summary);
        characterResidence = (TextView)rootView.findViewById(R.id.residence);
        characterHouse = (TextView)rootView.findViewById(R.id.house);
        characterAchievements = (TextView)rootView.findViewById(R.id.achievements);
        characterGuild = (TextView)rootView.findViewById(R.id.char_guild);
        characterFormerNames = (TextView)rootView.findViewById(R.id.char_former_names);
        characterFormerWorld = (TextView)rootView.findViewById(R.id.char_former_world);
        characterLastLogin = (TextView)rootView.findViewById(R.id.char_last_login);
        characterComment = (TextView)rootView.findViewById(R.id.char_comment);

        characterDeaths = (LinearLayout) rootView.findViewById(R.id.char_deaths);
        otherCharacters = (LinearLayout) rootView.findViewById(R.id.other_chars);

        boxHouse = (LinearLayout)rootView.findViewById(R.id.box_house);
        boxGuild = (LinearLayout)rootView.findViewById(R.id.box_guild);
        boxFormerName = (LinearLayout)rootView.findViewById(R.id.box_former_name);
        boxFormerWorld = (LinearLayout)rootView.findViewById(R.id.box_former_world);

        boxComment = (ViewGroup) rootView.findViewById(R.id.comment_box);
        boxDeaths = (ViewGroup)rootView.findViewById(R.id.deaths_box);
        boxChars = (ViewGroup) rootView.findViewById(R.id.chars_box);

        final EditText searchField = (EditText)rootView.findViewById(R.id.search_char);
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
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

        final Button searchButton = (Button) rootView.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Hide virtual keyboard */
                InputMethodManager inputMethodManager =
                        (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);

                new fetchData(getContext()).execute(searchField.getText().toString());
            }
        });

        /* Expand/Collapse buttons listeners */
        final TextView commentHeader = (TextView) rootView.findViewById(R.id.comment_header);
        commentHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(characterComment.getVisibility() == View.GONE){
                    characterComment.setVisibility(View.VISIBLE);
                    commentHeader.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_up,0);
                }else{
                    characterComment.setVisibility(View.GONE);
                    commentHeader.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_down,0);
                }
            }
        });

        final TextView deathsHeader = (TextView)rootView.findViewById(R.id.deaths_header);
        deathsHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(characterDeaths.getVisibility() == View.GONE){
                    characterDeaths.setVisibility(View.VISIBLE);
                    deathsHeader.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_up,0);
                }else{
                    characterDeaths.setVisibility(View.GONE);
                    deathsHeader.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_down,0);
                }
            }
        });

        final TextView charsHeader = (TextView) rootView.findViewById(R.id.chars_header);
        charsHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(otherCharacters.getVisibility() == View.GONE){
                    otherCharacters.setVisibility(View.VISIBLE);
                    charsHeader.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_up,0);
                }else{
                    otherCharacters.setVisibility(View.GONE);
                    charsHeader.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_down,0);
                }
            }
        });
        /* Getting player name argument */
        String playerName = getArguments().getString(Utils.ARG_PLAYER_NAME);
        /* If a player is already loaded, fill views */
        if(player != null){
            loadViews(player);
        /* If fragment was called with a name argument, load name */
        }else if(playerName != null){
            searchField.setText(playerName);
            new fetchData(getContext()).execute(playerName);
        }

        return rootView;
    }

    private class fetchData extends AsyncTask<String, Integer, Player> {
        private Context mContext;
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
            player = null;
            boxLoading.setVisibility(View.VISIBLE);
            characterInfo.setVisibility(View.GONE);
            boxComment.setVisibility(View.GONE);
            boxDeaths.setVisibility(View.GONE);
            boxChars.setVisibility(View.GONE);
            boxNoResults.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Player result){
            boxLoading.setVisibility(View.GONE);
            player = result;
            loadViews(player);
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
            boxNoResults.setVisibility(View.VISIBLE);
            characterInfo.setVisibility(View.GONE);
            boxComment.setVisibility(View.GONE);
            boxDeaths.setVisibility(View.GONE);
            boxChars.setVisibility(View.GONE);
            return;
        }
        boxNoResults.setVisibility(View.GONE);
        characterInfo.setVisibility(View.VISIBLE);
        /* Name */
        characterName.setText(player.getName());
        /* Summary (level & vocation) */
        characterSummary.setText(getString(
                R.string.char_summary,
                player.getLevel(),
                player.getVocation().equalsIgnoreCase("none") ? "" : player.getVocation()
        ));
        /* Sex */
        if(player.getSex().equalsIgnoreCase("female")){
            characterGender.setImageResource(R.drawable.ic_female);
            characterGender.setContentDescription(getString(R.string.female));
        }else{
            characterGender.setImageResource(R.drawable.ic_male);
            characterGender.setContentDescription(getString(R.string.male));
        }
        /* Achievement points */
        characterAchievements.setText(String.valueOf(player.getAchievementPoints()));
        /* Residence */
        characterResidence.setText(getString(
                R.string.char_residence,
                player.getResidence(),
                player.getWorld()
        ));
        /* House */
        if(player.getHouse() != null){
            boxHouse.setVisibility(View.VISIBLE);
            characterHouse.setText(getString(
                    R.string.char_house,
                    player.getHouse(),
                    player.getHouseCity()
            ));
        }else{
            boxHouse.setVisibility(View.GONE);
        }
        /* Guild */
        if(player.getGuild() != null){
            boxGuild.setVisibility(View.VISIBLE);
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
            characterGuild.setMovementMethod(LinkMovementMethod.getInstance());
            characterGuild.setText(guildStyled);
        }else{
            boxGuild.setVisibility(View.GONE);
        }
        /* Former names */
        if(player.getFormerNames() != null){
            boxFormerName.setVisibility(View.VISIBLE);
            characterFormerNames.setText(player.getFormerNames());
        }else{
            boxFormerName.setVisibility(View.GONE);
        }
        /* Former world */
        if(player.getFormerWorld() != null){
            boxFormerWorld.setVisibility(View.VISIBLE);
            characterFormerWorld.setText(player.getFormerWorld());
        }else{
            boxFormerWorld.setVisibility(View.GONE);
        }
        /* Last login */
        if(player.getLastLoginString() != null) {
            characterLastLogin.setText(player.getLastLoginString());
        }
        /* Comment */
        if(player.getComment() != null) {
            characterComment.setText(Html.fromHtml(player.getComment()));
            boxComment.setVisibility(View.VISIBLE);
        }
        /* Deaths */
        if(player.getDeathList().size() > 0) {
            characterDeaths.removeAllViews();
            boxDeaths.setVisibility(View.VISIBLE);
            loadDeathsView(getContext(),characterDeaths, player.getDeathList());
        }
        if(player.getOtherCharacters().size() > 1) {
            otherCharacters.removeAllViews();
            boxChars.setVisibility(View.VISIBLE);
            loadCharsView(getContext(),otherCharacters, player.getOtherCharacters());
        }

        /* Deletion */
        if(player.getDeletion() != null){
            characterWarning.setVisibility(View.VISIBLE);
            characterWarning.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast toast = Toast.makeText(getContext(),getString(R.string.deletion, player.getDeletionString()),Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER,0,0);
                    toast.show();
                }
            });
        }else{
            characterWarning.setVisibility(View.GONE);
        }
    }


    void loadDeathsView(Context context,ViewGroup parent, List<Death> deaths) {
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

    void loadCharsView(Context context,ViewGroup parent, List<Player> players) {
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
