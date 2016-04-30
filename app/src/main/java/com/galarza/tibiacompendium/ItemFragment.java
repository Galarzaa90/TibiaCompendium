package com.galarza.tibiacompendium;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.galarza.tibiacompendium.data.Item;
import com.galarza.tibiacompendium.data.ItemDrop;
import com.galarza.tibiacompendium.data.TibiaDatabase;
import com.galarza.tibiacompendium.data.Utils;

import java.util.List;


public class ItemFragment extends Fragment {

    private TibiaDatabase db;

    private LinearLayout headerBox;
    private TextView header;

    private ScrollView itemBox;
    private TextView itemName;
    private TextView itemLook;

    private LinearLayout itemDrops;

    private LinearLayout itemDropsBox;

    private ListView itemList;
    private GridLayout categoryLayout;

    public static ItemFragment newInstance() {
        ItemFragment fragment = new ItemFragment();
        Bundle args = new Bundle();
        args.putInt(Utils.ARG_TITLE_RESOURCE, R.string.title_items);
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
        View rootView =  inflater.inflate(R.layout.fragment_item, container, false);

        ((MainActivity)getActivity()).fragment = this;

        categoryLayout = (GridLayout) rootView.findViewById(R.id.category_container);
        new CategoryAdapter(getContext()).populateView(categoryLayout);

        headerBox = (LinearLayout)rootView.findViewById(R.id.category_header_box);
        headerBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                headerBox.setVisibility(View.GONE);
                categoryLayout.setVisibility(View.VISIBLE);
                itemList.setVisibility(View.GONE);
            }
        });
        header = (TextView)rootView.findViewById(R.id.category_header);

        itemList = (ListView)rootView.findViewById(R.id.category_results);

        itemBox = (ScrollView)rootView.findViewById(R.id.item_box);
        itemName = (TextView)rootView.findViewById(R.id.item_name);
        itemName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                itemBox.setVisibility(View.GONE);
                headerBox.setVisibility(View.VISIBLE);
                itemList.setVisibility(View.VISIBLE);
            }
        });
        itemLook = (TextView)rootView.findViewById(R.id.item_look);
        itemDrops = (LinearLayout)rootView.findViewById(R.id.item_drops);

        itemDropsBox = (LinearLayout)rootView.findViewById(R.id.drops_box);
        final TextView itemDropsHeader = (TextView)rootView.findViewById(R.id.drops_header) ;
        itemDropsBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemDrops.getVisibility() == View.GONE){
                    itemDrops.setVisibility(View.VISIBLE);
                    itemDropsHeader.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_up,0);
                }else{
                    itemDrops.setVisibility(View.GONE);
                    itemDropsHeader.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_down,0);
                }
            }
        });


        db = new TibiaDatabase(getContext());

        return rootView;
    }

    private class fetchData extends AsyncTask<String, Integer, List<Item>> {
        private Context mContext;
        public fetchData(Context context){
            mContext = context;
        }

        @Override
        protected List<Item> doInBackground(String... params) {
            return db.getItemsByCategory(params[0],params[1]);
        }

        protected void onProgressUpdate(Integer... progress){

        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(List<Item> items){
            ItemListAdapter adapter = new ItemListAdapter(mContext,items);
            itemList.setAdapter(adapter);
            itemList.setVisibility(View.VISIBLE);
        }
    }

    private class fetchItem extends AsyncTask<String, Integer, Item> {
        private Context mContext;
        public fetchItem(Context context){
            mContext = context;
        }

        @Override
        protected Item doInBackground(String... params) {
            if (params[0] == null){
                return null;
            }
            return db.getItem(params[0]);
        }

        protected void onProgressUpdate(Integer... progress){

        }

        @Override
        protected void onPreExecute() {
            itemBox.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Item item){
            itemBox.setVisibility(View.VISIBLE);
            itemList.setVisibility(View.GONE);
            headerBox.setVisibility(View.GONE);
            itemDrops.setVisibility(View.GONE);

            itemName.setText(item.getName());
            itemLook.setText(item.getLookText());

            if(item.getDropCount() > 0) {
                itemDropsBox.setVisibility(View.VISIBLE);
                DropsAdapter adapter = new DropsAdapter(mContext, item.getDroppedBy());
                adapter.populateView(itemDrops);
            }else{
                itemDropsBox.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        ((MainActivity) context).onSectionAttached(
                getArguments().getInt(Utils.ARG_TITLE_RESOURCE));
        super.onAttach(context);

    }

    private class ItemListAdapter extends ArrayAdapter<Item>{
        private final Context context;
        private final List<Item> objects;
        private final int layout;

        public ItemListAdapter(Context context, List<Item> objects) {
            super(context, R.layout.row_item, objects);
            this.context = context;
            this.layout = R.layout.row_item;
            this.objects = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder viewHolder;
            if(convertView == null){
                LayoutInflater inflater =
                        (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                viewHolder = new ViewHolder();

                convertView = inflater.inflate(layout,null);

                viewHolder.name = (TextView)convertView.findViewById(R.id.item_name);
                viewHolder.image = (ImageView)convertView.findViewById(R.id.image);

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }

            final Item item = objects.get(position);

            viewHolder.name.setText(item.getName());
            viewHolder.image.setImageBitmap(item.getImage());

            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new fetchItem(context).execute(item.getName());
                }
            });

            return convertView;
        }

        private class ViewHolder{
            TextView name;
            ImageView image;
        }
    }

    private class CategoryAdapter{
        private final Context context;
        private final int layout = R.layout.category_item;

        public CategoryAdapter(Context context){
            this.context = context;
        }

        public void populateView(GridLayout parent){
            LayoutInflater inflater =
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            parent.removeAllViews();
            final String[] categoryTitles = getResources().getStringArray(R.array.categories_titles);
            final String[] categorySort = getResources().getStringArray(R.array.categories_sort);
            final TypedArray categoryDrawables = getResources().obtainTypedArray(R.array.categories_drawables);
            final String[] categoryName = getResources().getStringArray(R.array.categories_name);
            for(int position = 0; position < categoryTitles.length; position++){
                View view = inflater.inflate(layout,parent,false);
                TextView categoryView = (TextView)view.findViewById(R.id.category);

                categoryView.setText(categoryTitles[position]);
                categoryView.setCompoundDrawablesRelativeWithIntrinsicBounds(categoryDrawables.getDrawable(position),null,null,null);
                final int index = position;
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new fetchData(context).execute(categoryName[index],categorySort[index]);
                        header.setText(categoryTitles[index]);
                        headerBox.setVisibility(View.VISIBLE);
                        categoryLayout.setVisibility(View.GONE);
                    }
                });

                parent.addView(view);
            }
        }

    }

    class DropsAdapter{
        private final Context context;
        private final List<ItemDrop> objects;
        static private final int layout = R.layout.row_itemdrop;

        public DropsAdapter(Context context, List<ItemDrop> objects) {
            this.context = context;
            this.objects = objects;
        }

        public void populateView(ViewGroup parent) {
            LayoutInflater inflater =
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            parent.removeAllViews();
            for(ItemDrop itemDrop : objects) {
                View rowView = inflater.inflate(layout,null);
                TextView name = (TextView) rowView.findViewById(R.id.name);
                TextView chance = (TextView) rowView.findViewById(R.id.chance);

                name.setText(itemDrop.getCreature());
                BitmapDrawable drawable = new BitmapDrawable(getResources(),itemDrop.getImage());
                name.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable,null,null,null);
                if(itemDrop.getChance() > 0) {
                    chance.setText(getString(R.string.chance, itemDrop.getChance()));
                }else{
                    chance.setText("?");
                }

                parent.addView(rowView);
            }
        }
    }

}
