package com.galarza.tibiacompendium;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.galarza.tibiacompendium.data.Guild;
import com.galarza.tibiacompendium.data.GuildMember;
import com.galarza.tibiacompendium.data.NetworkUtils;
import com.galarza.tibiacompendium.data.Parser;
import com.galarza.tibiacompendium.data.Utils;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class GuildFragment extends Fragment {

    private Guild mGuild = null;
    private MemberListAdapter adapter = null;

    /* Views used in the async task */
    private ViewGroup containerGuild;
    private ViewGroup containerLoading;
    private ViewGroup containerNoResults;

    private TextView mName;
    private GifImageView mLogo;
    private TextView mInfo;
    private TextView mMembersCount;
    private TextView mOnlineCount;

    private ListView memberList;

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

        ((MainActivity)getActivity()).fragment = this;

        /* Views used in the async task */
        containerGuild = (ViewGroup)rootView.findViewById(R.id.guild_box);
        containerLoading = (ViewGroup) rootView.findViewById(R.id.container_loading);
        containerNoResults = (ViewGroup) rootView.findViewById(R.id.container_no_results);

        mName = (TextView)rootView.findViewById(R.id.name);
        mLogo = (GifImageView) rootView.findViewById(R.id.guild_logo);
        mInfo = (TextView)rootView.findViewById(R.id.info);
        mMembersCount = (TextView)rootView.findViewById(R.id.members);
        mOnlineCount = (TextView)rootView.findViewById(R.id.members_online);

        memberList = (ListView)rootView.findViewById(R.id.member_list);

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

        Button buttonSearch = (Button) rootView.findViewById(R.id.button_search);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Hide virtual keyboard */
                InputMethodManager inputMethodManager =
                        (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);

                new fetchData(getContext()).execute(fieldSearch.getText().toString());
            }
        });
        /* Getting guild name argument */
        Bundle arguments = getArguments();
        String guildName = null;
        if(arguments != null){
            guildName = getArguments().getString(Utils.ARG_GUILD_NAME);
        }
        /* If a guild is already loaded, fill views */
        if(mGuild != null){
            loadViews(mGuild);
        /* If fragment was called with a guild argument, load guild */
        }else if(guildName != null){
            fieldSearch.setText(guildName);
            new fetchData(getContext()).execute(guildName);
        }

        /* Recovering state */
        if(savedInstanceState != null){
            String guildJson = savedInstanceState.getString("GUILD","");
            if(!guildJson.isEmpty()){
                Gson gson = new Gson();
                mGuild = gson.fromJson(guildJson,Guild.class);
                loadViews(mGuild);
            }
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Gson gson = new Gson();
        String guildJson = null;
        if(mGuild != null){
            guildJson = gson.toJson(mGuild);
        }

        outState.putString("GUILD",guildJson);
        super.onSaveInstanceState(outState);
    }

    private class fetchData extends AsyncTask<String,Integer,Guild>{
        private Context mContext;
        fetchData(Context context){
            mContext = context;
        }

        @Override
        protected Guild doInBackground(String... params) {
            if(!NetworkUtils.isConnected(mContext)){
                publishProgress(Utils.NO_NETWORK_ENABLED);
                return null;
            }

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
            mGuild = null;
            containerLoading.setVisibility(View.VISIBLE);
            containerNoResults.setVisibility(View.GONE);
            containerGuild.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Guild result) {
            containerLoading.setVisibility(View.GONE);
            mGuild = result;
            loadViews(mGuild);
        }
    }
    
    private class fetchGuildLogo extends AsyncTask<String,Integer,GifDrawable>{

        private final String logoUrl;

        public fetchGuildLogo(String logoUrl) {
            this.logoUrl = logoUrl;
        }

        @Override
        protected GifDrawable doInBackground(String... strings) {
            try {
                URLConnection urlConnection = new URL(logoUrl).openConnection();
                urlConnection.connect();
                final int contentLength = urlConnection.getContentLength();
                ByteBuffer buffer = ByteBuffer.allocateDirect(contentLength);
                ReadableByteChannel channel = Channels.newChannel(urlConnection.getInputStream());
                while (buffer.remaining() > 0){
                    channel.read(buffer);
                }
                channel.close();
                return new GifDrawable(buffer);

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(GifDrawable gifDrawable) {
            if (gifDrawable != null){
                mLogo.setImageDrawable(gifDrawable);
            }else{
                mLogo.setImageResource(R.drawable.default_guildlogo);
            }
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        if(toolbar != null) {
            toolbar.setTitle(R.string.title_search_guild);
        }

    }

    private void loadViews(Guild guild){
        if(guild == null){
            containerNoResults.setVisibility(View.VISIBLE);
            containerGuild.setVisibility(View.GONE);
            return;
        }
        containerNoResults.setVisibility(View.GONE);
        containerGuild.setVisibility(View.VISIBLE);
        /* Guild logo */
        new fetchGuildLogo(guild.getLogoUrl()).execute();
        /* Guild name */
        mName.setText(guild.getName());
        /* Guild info */
        mInfo.setText(getString(
                R.string.guild_info,
                guild.getWorld(),
                guild.getFoundedString()
        ));
        /* Member count */
        mMembersCount.setText(getResources().getQuantityString(
                R.plurals.guild_member_count,
                guild.getMemberCount(),
                guild.getMemberCount()
        ));
        /* Online member count */
        int onlineCount = guild.getOnlineCount();
        mOnlineCount.setText(getResources().getQuantityString(
                R.plurals.guild_members_online,
                onlineCount,
                onlineCount));
        /* Guild member list */
        adapter = new MemberListAdapter(getContext(), guild.getMemberList());
        memberList.setAdapter(adapter);
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
                if(mGuild != null) {
                    mGuild.sortByName();
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.by_rank:
                if(mGuild != null) {
                    mGuild.sortByRank();
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.by_level:
                if(mGuild != null) {
                    mGuild.sortByLevel();
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.by_vocation:
                if(mGuild != null) {
                    mGuild.sortByVocation();
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.by_joined:
                if(mGuild != null) {
                    mGuild.sortByJoined();
                    adapter.notifyDataSetChanged();
                }
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class MemberListAdapter extends ArrayAdapter<GuildMember> {
        private final Context context;
        private final List<GuildMember> objects;
        private final int layout;

        MemberListAdapter(Context context, List<GuildMember> objects) {
            super(context, R.layout.row_member, objects);
            this.context = context;
            this.layout = R.layout.row_member;
            this.objects = objects;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            final ViewHolder viewHolder;
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

            final GuildMember member = objects.get(position);
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
            if (member.isOnline()) {
                viewHolder.online.setVisibility(View.VISIBLE);
            }else{
                viewHolder.online.setVisibility(View.GONE);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int in = R.anim.slide_in_right;
                    int out = R.anim.slide_out_left;
                    CharacterFragment fragment = new CharacterFragment();
                    Bundle arguments = new Bundle();
                    arguments.putString(Utils.ARG_PLAYER_NAME,member.getName());
                    fragment.setArguments(arguments);
                    getFragmentManager().beginTransaction()
                            .setCustomAnimations(in,out,in,out)
                            .addToBackStack(null)
                            .replace(R.id.container, fragment)
                            .commit();
                }
            });
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
