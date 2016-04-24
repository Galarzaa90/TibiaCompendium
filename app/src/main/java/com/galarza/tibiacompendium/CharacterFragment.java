package com.galarza.tibiacompendium;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.galarza.tibiacompendium.data.Death;
import com.galarza.tibiacompendium.data.Parser;
import com.galarza.tibiacompendium.data.Player;

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
    private static final String ARG_SECTION_NUMBER = "section_number";

    private EditText searchField;
    private LinearLayout characterInfo;
    private RelativeLayout boxLoading;
    private RelativeLayout boxNoResults;

    private ImageView characterGender;
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
    private LinearLayout boxComment;
    private LinearLayout boxDeaths;
    private LinearLayout boxChars;

    public static CharacterFragment newInstance() {
        CharacterFragment fragment = new CharacterFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, 2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_character, container, false);

        characterInfo = (LinearLayout) rootView.findViewById(R.id.character_box);
        boxLoading = (RelativeLayout)rootView.findViewById(R.id.loading_box);
        boxNoResults = (RelativeLayout)rootView.findViewById(R.id.no_results_box);

        characterGender = (ImageView)rootView.findViewById(R.id.char_gender);
        characterName = (TextView)rootView.findViewById(R.id.char_name);
        characterSummary = (TextView)rootView.findViewById(R.id.char_summary);
        characterResidence = (TextView)rootView.findViewById(R.id.char_residence);
        characterHouse = (TextView)rootView.findViewById(R.id.char_house);
        characterAchievements = (TextView)rootView.findViewById(R.id.char_achievements);
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
        boxComment = (LinearLayout)rootView.findViewById(R.id.comment_box);
        boxDeaths = (LinearLayout)rootView.findViewById(R.id.deaths_box);
        boxChars = (LinearLayout) rootView.findViewById(R.id.chars_box);

        searchField = (EditText)rootView.findViewById(R.id.search_char);
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    new fetchData().execute(v.getText().toString());
                    return true;
                }
                return false;
            }
        });
        Button searchButton = (Button) rootView.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new fetchData().execute(searchField.getText().toString());
            }
        });
        /* Expand/Collapse buttons listeners */
        final ImageView commentToggleIcon = (ImageView)rootView.findViewById(R.id.comment_toggle_icon);
        LinearLayout commentHeader = (LinearLayout)rootView.findViewById(R.id.comment_header);
        commentHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(characterComment.getVisibility() == View.GONE){
                    characterComment.setVisibility(View.VISIBLE);
                    commentToggleIcon.setImageResource(R.drawable.ic_arrow_drop_up);
                }else{
                    characterComment.setVisibility(View.GONE);
                    commentToggleIcon.setImageResource(R.drawable.ic_arrow_drop_down);
                }
            }
        });

        final ImageView deathToggleIcon = (ImageView)rootView.findViewById(R.id.death_toggle_icon);
        LinearLayout deathsHeader = (LinearLayout)rootView.findViewById(R.id.deaths_header);
        deathsHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(characterDeaths.getVisibility() == View.GONE){
                    characterDeaths.setVisibility(View.VISIBLE);
                    deathToggleIcon.setImageResource(R.drawable.ic_arrow_drop_up);
                }else{
                    characterDeaths.setVisibility(View.GONE);
                    deathToggleIcon.setImageResource(R.drawable.ic_arrow_drop_down);
                }
            }
        });

        final ImageView charsToggleIcon = (ImageView)rootView.findViewById(R.id.chars_toggle_icon);
        LinearLayout charsHeader = (LinearLayout)rootView.findViewById(R.id.chars_header);
        charsHeader.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(otherCharacters.getVisibility() == View.GONE){
                    otherCharacters.setVisibility(View.VISIBLE);
                    charsToggleIcon.setImageResource(R.drawable.ic_arrow_drop_up);
                }else{
                    otherCharacters.setVisibility(View.GONE);
                    charsToggleIcon.setImageResource(R.drawable.ic_arrow_drop_down);
                }
            }
        });

        return rootView;
    }

    private class fetchData extends AsyncTask<String, Integer, Player> {

        @Override
        protected Player doInBackground(String... params) {
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
                    buffer.append(line).append("\r\n");
                }

                stream.close();
                connection.disconnect();

                return Parser.parseCharacter(buffer.toString());
            }catch (SocketTimeoutException s){
                //publishProgress();
            }catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress){

        }

        @Override
        protected void onPreExecute() {
            characterInfo.setVisibility(View.GONE);
            boxComment.setVisibility(View.GONE);
            boxDeaths.setVisibility(View.GONE);
            boxChars.setVisibility(View.GONE);
            boxLoading.setVisibility(View.VISIBLE);
            boxNoResults.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Player result){
            if(result != null){
                if(result.getSex().equalsIgnoreCase("female")){
                    characterGender.setImageResource(R.drawable.ic_female);
                    characterGender.setContentDescription(getString(R.string.female));
                }else{
                    characterGender.setImageResource(R.drawable.ic_male);
                    characterGender.setContentDescription(getString(R.string.male));
                }
                characterName.setText(result.getName());
                characterSummary.setText(getString(
                        R.string.char_summary,
                        result.getLevel(),
                        result.getVocation().equalsIgnoreCase("none") ? "" : result.getVocation())
                );
                characterResidence.setText(getString(R.string.char_residence,result.getResidence(),result.getWorld()));

                if(result.getHouse() != null){
                    boxHouse.setVisibility(View.VISIBLE);
                    characterHouse.setText(result.getHouse());
                    characterHouse.setText(getString(R.string.char_house,result.getHouse(),result.getHouseCity()));
                }else{
                    boxHouse.setVisibility(View.GONE);
                }

                if(result.getGuild() != null){
                    boxGuild.setVisibility(View.VISIBLE);
                    characterGuild.setText(getString(R.string.guildcontent,result.getGuildRank(),result.getGuild()));
                }else{
                    boxGuild.setVisibility(View.GONE);
                }

                if(result.getFormerNames() != null){
                    boxFormerName.setVisibility(View.VISIBLE);
                    characterFormerNames.setText(result.getFormerNames());
                }else{
                    boxFormerName.setVisibility(View.GONE);
                }

                if(result.getFormerWorld() != null){
                    boxFormerWorld.setVisibility(View.VISIBLE);
                    characterFormerWorld.setText(result.getFormerWorld());
                }else{
                    boxFormerWorld.setVisibility(View.GONE);
                }
                if(result.getLastLogin() != null) {
                    characterLastLogin.setText(result.getLastLogin().toString());
                }

                characterAchievements.setText(String.valueOf(result.getAchievementPoints()));

                if(result.getComment() != null){
                    characterComment.setText(Html.fromHtml(result.getComment()));
                    boxComment.setVisibility(View.VISIBLE);
                }
                characterInfo.setVisibility(View.VISIBLE);

                DeathListAdapter deathListAdapter = new DeathListAdapter(getContext(), result.getDeathList());
                if(result.getDeathList().size() > 0) {
                    characterDeaths.removeAllViews();
                    boxDeaths.setVisibility(View.VISIBLE);
                    for(int i = 0; i < deathListAdapter.getCount(); i++){
                        View item = deathListAdapter.getView(i,null,null);
                        characterDeaths.addView(item);
                    }
                }else{
                    boxDeaths.setVisibility(View.GONE);
                }

                CharsListAdapter charListAdapter = new CharsListAdapter(getContext(), result.getOtherCharacters());
                if(result.getOtherCharacters().size() > 1) {
                    otherCharacters.removeAllViews();
                    boxChars.setVisibility(View.VISIBLE);
                    for(int i = 0; i < charListAdapter.getCount(); i++){
                        View item = charListAdapter.getView(i,null,null);
                        otherCharacters.addView(item);
                    }
                }else{
                    boxChars.setVisibility(View.GONE);
                }

            }else{
                boxNoResults.setVisibility(View.VISIBLE);
            }
            boxLoading.setVisibility(View.GONE);

        }
    }

    @Override
    public void onAttach(Context context) {
        ((MainActivity) context).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        super.onAttach(context);

    }

    class DeathListAdapter extends ArrayAdapter<Death> {
        private final Context context;
        private final List<Death> objects;
        private final int layout;

        public DeathListAdapter(Context context, List<Death> objects) {
            super(context, R.layout.row_death, objects);
            this.context = context;
            this.layout = R.layout.row_death;
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater =
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(layout, null);
            Death death = objects.get(position);

            TextView dateView = (TextView)rowView.findViewById(R.id.death_date);
            dateView.setText(death.getDate().toString());

            TextView detailsView = (TextView)rowView.findViewById(R.id.death_details);
            detailsView.setText(getString(R.string.death_details,
                    death.isByPlayer() ? "Killed" : "Died",
                    death.getLevel(),
                    death.getKiller()));
            return rowView;
        }
    }

    class CharsListAdapter extends ArrayAdapter<Player> {
        private final Context context;
        private final List<Player> objects;
        private final int layout;

        public CharsListAdapter(Context context, List<Player> objects) {
            super(context, R.layout.row_other_char, objects);
            this.context = context;
            this.layout = R.layout.row_other_char;
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater =
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(layout, null);
            Player player = objects.get(position);

            TextView charName= (TextView)rowView.findViewById(R.id.char_name);
            charName.setText(player.getName());

            TextView charWorld = (TextView)rowView.findViewById(R.id.char_world);
            charWorld.setText(player.getWorld());

            rowView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new fetchData().execute(((TextView)v.findViewById(R.id.char_name)).getText().toString());
                }
            });

            return rowView;
        }
    }

}
