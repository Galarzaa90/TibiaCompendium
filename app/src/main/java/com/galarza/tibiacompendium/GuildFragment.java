package com.galarza.tibiacompendium;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.galarza.tibiacompendium.data.Guild;
import com.galarza.tibiacompendium.data.GuildMember;
import com.galarza.tibiacompendium.data.NetworkUtils;
import com.galarza.tibiacompendium.data.Parser;
import com.galarza.tibiacompendium.data.Utils;

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

/**
 * A simple {@link Fragment} subclass.
 */
public class GuildFragment extends Fragment {

    private Guild guild = null;
    private MemberListAdapter adapter = null;

    /* Views used in the async task */
    private LinearLayout guildBox;
    private RelativeLayout boxLoading;
    private RelativeLayout boxNoResults;

    private TextView guildName;
    private GifImageView guildLogo;
    private TextView guildInfo;
    private TextView guildMembers;
    private TextView guildOnline;

    private ListView memberList;

    public static GuildFragment newInstance() {
        GuildFragment fragment = new GuildFragment();
        Bundle args = new Bundle();
        args.putInt(Utils.ARG_TITLE_RESOURCE, R.string.title_search_guild);
        fragment.setArguments(args);
        return fragment;
    }

    public static GuildFragment searchInstance(String name){
        GuildFragment fragment = newInstance();
        Bundle args = fragment.getArguments();
        args.putString(Utils.ARG_GUILD_NAME,name);
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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_guild, container, false);

        ((MainActivity)getActivity()).fragment = this;

        /* Views used in the async task */
        guildBox = (LinearLayout)rootView.findViewById(R.id.guild_box);
        boxLoading = (RelativeLayout)rootView.findViewById(R.id.loading_box);
        boxNoResults = (RelativeLayout)rootView.findViewById(R.id.no_results_box);

        guildName = (TextView)rootView.findViewById(R.id.guild_name);
        guildLogo = (GifImageView) rootView.findViewById(R.id.guild_logo);
        guildInfo = (TextView)rootView.findViewById(R.id.guild_info);
        guildMembers = (TextView)rootView.findViewById(R.id.guild_members);
        guildOnline = (TextView)rootView.findViewById(R.id.guild_members_online);

        memberList = (ListView)rootView.findViewById(R.id.member_list);

        final EditText searchField = (EditText)rootView.findViewById(R.id.search_guild);
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

        Button searchButton = (Button) rootView.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Hide virtual keyboard */
                InputMethodManager inputMethodManager =
                        (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(),0);

                new fetchData(getContext()).execute(searchField.getText().toString());
            }
        });
        /* Getting guild name argument */
        String guildName = getArguments().getString(Utils.ARG_GUILD_NAME);
        /* If a guild is already loaded, fill views */
        if(guild != null){
            loadViews(guild);
        /* If fragment was called with a guild argument, load guild */
        }else if(guildName != null){
            searchField.setText(guildName);
            new fetchData(getContext()).execute(guildName);
        }

        return rootView;
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
            guild = null;
            boxLoading.setVisibility(View.VISIBLE);
            boxNoResults.setVisibility(View.GONE);
            guildBox.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Guild result) {
            boxLoading.setVisibility(View.GONE);
            guild = result;
            loadViews(guild);
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
                guildLogo.setImageDrawable(gifDrawable);
            }else{
                guildLogo.setImageResource(R.drawable.default_guildlogo);
            }
        }
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.setTitle(getArguments().getInt(Utils.ARG_TITLE_RESOURCE));

    }

    private void loadViews(Guild guild){
        if(guild == null){
            boxNoResults.setVisibility(View.VISIBLE);
            guildBox.setVisibility(View.GONE);
            return;
        }
        boxNoResults.setVisibility(View.GONE);
        guildBox.setVisibility(View.VISIBLE);
        /* Guild logo */
        new fetchGuildLogo(guild.getLogoUrl()).execute();
        /* Guild name */
        guildName.setText(guild.getName());
        /* Guild info */
        guildInfo.setText(getString(
                R.string.guild_info,
                guild.getWorld(),
                guild.getFoundedString()
        ));
        /* Member count */
        guildMembers.setText(getResources().getQuantityString(
                R.plurals.guild_member_count,
                guild.getMemberCount(),
                guild.getMemberCount()
        ));
        /* Online member count */
        int onlineCount = guild.getOnlineCount();
        guildOnline.setText(getResources().getQuantityString(
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

    class MemberListAdapter extends ArrayAdapter<GuildMember> {
        private final Context context;
        private final List<GuildMember> objects;
        private final int layout;

        public MemberListAdapter(Context context, List<GuildMember> objects) {
            super(context, R.layout.row_member, objects);
            this.context = context;
            this.layout = R.layout.row_member;
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
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
            if (!member.isOnline()) {
                viewHolder.online.setVisibility(View.INVISIBLE);
            }else{
                viewHolder.online.setVisibility(View.GONE);
            }
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int in = R.anim.slide_in_right;
                    int out = R.anim.slide_out_left;
                    getFragmentManager().beginTransaction()
                            .setCustomAnimations(in,out,in,out)
                            .addToBackStack(null)
                            .replace(R.id.container,CharacterFragment.searchInstance(member.getName()))
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
