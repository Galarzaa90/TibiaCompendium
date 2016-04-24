package com.galarza.tibiacompendium;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import com.squareup.picasso.Picasso;

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

    private Guild guild = null;
    private MemberListAdapter adapter = null;

    private LinearLayout guildBox;
    private RelativeLayout boxLoading;
    private RelativeLayout boxNoResults;

    private TextView guildName;
    private ImageView guildLogo;
    private TextView guildInfo;
    private TextView guildMembers;
    private TextView guildOnline;

    ListView memberList;

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
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_guild, container, false);

        guildBox = (LinearLayout)rootView.findViewById(R.id.guild_box);
        boxLoading = (RelativeLayout)rootView.findViewById(R.id.loading_box);
        boxNoResults = (RelativeLayout)rootView.findViewById(R.id.no_results_box);

        guildName = (TextView)rootView.findViewById(R.id.guild_name);
        guildLogo = (ImageView)rootView.findViewById(R.id.guild_logo);
        guildInfo = (TextView)rootView.findViewById(R.id.guild_info);
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
            guild = null;
            boxLoading.setVisibility(View.VISIBLE);
            boxNoResults.setVisibility(View.GONE);
            guildBox.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Guild result) {
            boxLoading.setVisibility(View.GONE);
            if(result == null){
                boxNoResults.setVisibility(View.VISIBLE);
                return;
            }
            guild = result;
            if(result.getLogoUrl() != null) {
                Picasso.with(getContext()).load(result.getLogoUrl()).into(guildLogo);
            }
            guildName.setText(result.getName());
            guildInfo.setText(getString(R.string.guild_info,result.getWorld(),result.getFoundedString()));
            guildMembers.setText(getString(R.string.guild_member_count,result.getMemberCount()));
            int onlineCount = result.getOnlineCount();
            guildOnline.setText(getResources().getQuantityString(R.plurals.guild_members_online,onlineCount,onlineCount));
            guildBox.setVisibility(View.VISIBLE);
            adapter = new MemberListAdapter(getContext(),R.layout.row_member, result.getMemberList());
            memberList.setAdapter(adapter);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
        super.onAttach(activity);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.guild,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.by_name:
                if(guild != null) {
                    guild.sortByName();
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.by_rank:
                if(guild != null) {
                    guild.sortByRank();
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.by_level:
                if(guild != null) {
                    guild.sortByLevel();
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.by_vocation:
                if(guild != null) {
                    guild.sortByVocation();
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.by_joined:
                if(guild != null) {
                    guild.sortByJoined();
                    adapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
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
            ViewHolder viewHolder;
            if(convertView == null) {
                LayoutInflater inflater =
                        (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                viewHolder = new ViewHolder();

                convertView = inflater.inflate(layout, parent, false);

                viewHolder.rank = (TextView) convertView.findViewById(R.id.rank);
                viewHolder.name = (TextView) convertView.findViewById(R.id.name);
                viewHolder.title = (TextView) convertView.findViewById(R.id.title);
                viewHolder.summary = (TextView) convertView.findViewById(R.id.summary);
                viewHolder.joined = (TextView) convertView.findViewById(R.id.joined);
                viewHolder.online = (ImageView) convertView.findViewById(R.id.online);

                convertView.setTag(viewHolder);

            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }

            GuildMember member = objects.get(position);
            viewHolder.rank.setText(member.getRank());
            viewHolder.rank.setVisibility(View.VISIBLE);
            viewHolder.name.setText(member.getName());
            if (!member.getTitle().isEmpty()) {
                viewHolder.title.setText(getString(R.string.member_title, member.getTitle()));
            }else{
                viewHolder.title.setText("");
            }
            viewHolder.summary.setText(getString(R.string.char_summary, member.getLevel(), member.getVocation()));
            viewHolder.joined.setText(member.getJoinedString());
            if (!member.isOnline()) {
                viewHolder.online.setVisibility(View.INVISIBLE);
            }else{
                viewHolder.online.setVisibility(View.GONE);
            }
            return convertView;
        }

        private class ViewHolder{
            TextView rank;
            TextView name;
            TextView title;
            TextView summary;
            TextView joined;
            ImageView online;
        }

    }

}
