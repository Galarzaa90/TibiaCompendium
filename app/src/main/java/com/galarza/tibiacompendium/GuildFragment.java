package com.galarza.tibiacompendium;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.galarza.tibiacompendium.data.Guild;
import com.galarza.tibiacompendium.data.GuildMember;
import com.galarza.tibiacompendium.data.Parser;

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
public class GuildFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";

    private LinearLayout guildInfo;
    private RelativeLayout boxLoading;
    private RelativeLayout boxNoResults;

    private TextView guildName;
    private TextView guildMembers;
    private TextView guildOnline;

    ListView memberList;

    EditText test;

    public static GuildFragment newInstance() {
        GuildFragment fragment = new GuildFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, 3);
        fragment.setArguments(args);
        return fragment;
    }

    public GuildFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_guild, container, false);

        guildInfo = (LinearLayout)rootView.findViewById(R.id.guild_box);
        boxLoading = (RelativeLayout)rootView.findViewById(R.id.loading_box);
        boxNoResults = (RelativeLayout)rootView.findViewById(R.id.no_results_box);

        guildName = (TextView)rootView.findViewById(R.id.guild_name);
        guildMembers = (TextView)rootView.findViewById(R.id.guild_members);
        guildOnline = (TextView)rootView.findViewById(R.id.guild_members_online);

        memberList = (ListView)rootView.findViewById(R.id.member_list);

        final EditText searchField = (EditText)rootView.findViewById(R.id.search_guild);

        Button searchButton = (Button) rootView.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new fetchData().execute(searchField.getText().toString());
            }
        });

        return rootView;
    }

    private class fetchData extends AsyncTask<String,Integer,Guild>{
        @Override
        protected Guild doInBackground(String... params) {
            HttpURLConnection connection;
            InputStream stream;
            try {
                connection = (HttpURLConnection)(
                        new URL("https://secure.tibia.com/community/?subtopic=guilds&page=view&GuildName="+ URLEncoder.encode(params[0],"UTF-8")))
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
                return Parser.parseGuild(buffer.toString());
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
            boxLoading.setVisibility(View.VISIBLE);
            boxNoResults.setVisibility(View.GONE);
            guildInfo.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Guild guild) {
            boxLoading.setVisibility(View.GONE);
            if(guild == null){
                boxNoResults.setVisibility(View.VISIBLE);
                return;
            }
            guildName.setText(guild.getName());
            guildMembers.setText(getString(R.string.guild_member_count,guild.getMemberCount()));
            int onlineCount = guild.getOnlineCount();
            guildOnline.setText(getResources().getQuantityString(R.plurals.guild_members_online,onlineCount,onlineCount));
            guildInfo.setVisibility(View.VISIBLE);
            memberList.setAdapter(new MemberListAdapter(getContext(),R.layout.row_member, guild.getMemberList()));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        super.onAttach(activity);

    }

    class MemberListAdapter extends ArrayAdapter<GuildMember>{
        private final Context context;
        private final List<GuildMember> objects;
        private final int layout;

        public MemberListAdapter(Context context, int resource, List<GuildMember> objects) {
            super(context, resource, objects);
            this.context = context;
            this.layout = resource;
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater =
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(layout, null);
            GuildMember member = objects.get(position);

            TextView rank = (TextView)rowView.findViewById(R.id.rank);
            rank.setText(member.getRank());
            rank.setVisibility(View.VISIBLE);

            TextView name = (TextView)rowView.findViewById(R.id.name);
            name.setText(member.getName());

            if(!member.getTitle().isEmpty()) {
                TextView title = (TextView) rowView.findViewById(R.id.title);
                title.setText(getString(R.string.member_title, member.getTitle()));
            }

            TextView summary = (TextView)rowView.findViewById(R.id.summary);
            summary.setText(getString(R.string.char_summary,member.getLevel(),member.getVocation()));

            TextView joined = (TextView)rowView.findViewById(R.id.joined);
            joined.setText(member.getJoined());

            if(!member.isOnline()){
                ImageView online = (ImageView)rowView.findViewById(R.id.online);
                online.setVisibility(View.INVISIBLE);
            }

            return rowView;
        }

    }

}
