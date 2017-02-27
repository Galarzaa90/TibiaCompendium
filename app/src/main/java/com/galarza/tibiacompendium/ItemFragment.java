package com.galarza.tibiacompendium;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FilterQueryProvider;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.galarza.tibiacompendium.data.Item;
import com.galarza.tibiacompendium.data.ItemDrop;
import com.galarza.tibiacompendium.data.NpcOffer;
import com.galarza.tibiacompendium.data.TibiaDatabase;
import com.galarza.tibiacompendium.data.Utils;

import java.io.IOException;
import java.util.List;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;


public class ItemFragment extends Fragment {

    private TibiaDatabase db;

    private ViewGroup headerBox;
    private TextView header;

    private ScrollView itemBox;
    private TextView itemName;
    private TextView itemLook;

    private LinearLayout itemBuyers;
    private LinearLayout itemSellers;
    private LinearLayout itemDrops;

    private ViewGroup buyersBox;
    private ViewGroup sellersBox;
    private ViewGroup itemDropsBox;

    private ListView itemList;
    private RecyclerView categoryView;

    private AutoCompleteTextView searchField;

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

        db = new TibiaDatabase(getContext());

        searchField = (AutoCompleteTextView)rootView.findViewById(R.id.search_item);

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(
                getContext(),
                android.R.layout.simple_list_item_1,
                null,
                new String[]{"title"},
                new int[]{android.R.id.text1},
                0);

        cursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                if (constraint != null) {
                    return db.getItemList(constraint.toString());
                }else{
                    return null;
                }

            }
        });
        cursorAdapter.setCursorToStringConverter(new SimpleCursorAdapter.CursorToStringConverter() {
            @Override
            public CharSequence convertToString(Cursor cursor) {
                final int index = cursor.getColumnIndexOrThrow("title");
                return cursor.getString(index);
            }
        });

        searchField.setAdapter(cursorAdapter);

        Button searchButton = (Button) rootView.findViewById(R.id.button_search);
        searchButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                new fetchItem(getContext()).execute(searchField.getText().toString().trim());
            }
        });

        categoryView = (RecyclerView) rootView.findViewById(R.id.category_view);
        GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(),2);

        CategoryAdapter adapter = new CategoryAdapter(getActivity());
        categoryView.setLayoutManager(mLayoutManager);
        categoryView.setAdapter(adapter);

        headerBox = (ViewGroup) rootView.findViewById(R.id.category_header_box);
        headerBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                headerBox.setVisibility(View.GONE);
                categoryView.setVisibility(View.VISIBLE);
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

        itemBuyers = (LinearLayout)rootView.findViewById(R.id.item_buyers);
        itemSellers = (LinearLayout)rootView.findViewById(R.id.item_sellers);
        itemDrops = (LinearLayout)rootView.findViewById(R.id.item_drops);

        buyersBox = (ViewGroup) rootView.findViewById(R.id.buyers_box);
        final TextView buyersHeader = (TextView)rootView.findViewById(R.id.buyers_header) ;
        buyersBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemBuyers.getVisibility() == View.GONE){
                    itemBuyers.setVisibility(View.VISIBLE);
                    buyersHeader.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_up,0);
                }else{
                    itemBuyers.setVisibility(View.GONE);
                    buyersHeader.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_down,0);
                }
            }
        });

        sellersBox = (ViewGroup) rootView.findViewById(R.id.sellers_box);
        final TextView sellersHeader = (TextView)rootView.findViewById(R.id.sellers_header) ;
        sellersBox.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemSellers.getVisibility() == View.GONE){
                    itemSellers.setVisibility(View.VISIBLE);
                    sellersHeader.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_up,0);
                }else{
                    itemSellers.setVisibility(View.GONE);
                    sellersHeader.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.ic_arrow_drop_down,0);
                }
            }
        });

        itemDropsBox = (ViewGroup) rootView.findViewById(R.id.drops_box);
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




        return rootView;
    }


    private class fetchData extends AsyncTask<String, Integer, List<Item>> {
        private final Context mContext;
        fetchData(Context context){
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
        private final Context mContext;
        fetchItem(Context context){
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
            itemBuyers.setVisibility(View.GONE);
            itemSellers.setVisibility(View.GONE);
            itemDrops.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Item item){
            itemBox.setVisibility(View.VISIBLE);
            itemList.setVisibility(View.GONE);
            headerBox.setVisibility(View.GONE);

            itemName.setText(item.getTitle());
            itemLook.setText(item.getLookText());

            if(item.getBuyersCount() > 0){
                buyersBox.setVisibility(View.VISIBLE);
                loadOffersView(mContext,itemBuyers,item.getBuyers());
            }else{
                buyersBox.setVisibility(View.GONE);
            }
            if(item.getSellersCount() > 0){
                sellersBox.setVisibility(View.VISIBLE);
                loadOffersView(mContext,itemSellers,item.getSellers());
            }else{
                sellersBox.setVisibility(View.GONE);
            }

            if(item.getDropCount() > 0) {
                itemDropsBox.setVisibility(View.VISIBLE);
                loadDropsView(mContext,itemDrops,item.getDroppedBy());
            }else{
                itemDropsBox.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        if(toolbar != null) {
            toolbar.setTitle(R.string.title_items);
        }

    }

    private class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder>{
        private final Context mContext;
        private final String[] categoryTitles;
        private final String[] categorySort;
        private final TypedArray categoryDrawables;
        private final String[] categoryNames;

        CategoryAdapter(Context context){
            categoryTitles = getResources().getStringArray(R.array.categories_titles);
            categorySort = getResources().getStringArray(R.array.categories_sort);
            categoryDrawables = getResources().obtainTypedArray(R.array.categories_drawables);
            categoryNames = getResources().getStringArray(R.array.categories_name);
            mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.category_item,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.category.setText(categoryTitles[position]);

            GifDrawable icon;
            try{
                icon = new GifDrawable(getResources(),categoryDrawables.getResourceId(position,0));
            } catch (IOException e){
                icon = null;
                e.printStackTrace();
            }
            holder.category.setCompoundDrawablesRelativeWithIntrinsicBounds(icon,null,null,null);

            final int index = position;
            holder.viewGroup.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new fetchData(mContext).execute(categoryNames[index],categorySort[index]);
                    header.setText(categoryTitles[index]);
                    headerBox.setVisibility(View.VISIBLE);
                    categoryView.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public int getItemCount() {
            return categoryTitles.length;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView category;
            final ViewGroup viewGroup;
            ViewHolder(View itemView) {
                super(itemView);
                viewGroup = (ViewGroup) itemView.findViewById(R.id.card);
                category = (TextView)itemView.findViewById(R.id.category);
            }
        }
    }


    private class ItemListAdapter extends ArrayAdapter<Item>{
        private final Context context;
        private final List<Item> objects;
        private final int layout;

        ItemListAdapter(Context context, List<Item> objects) {
            super(context, R.layout.row_item, objects);
            this.context = context;
            this.layout = R.layout.row_item;
            this.objects = objects;
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            final ViewHolder viewHolder;
            if(convertView == null){
                LayoutInflater inflater =
                        (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                viewHolder = new ViewHolder();

                convertView = inflater.inflate(layout,null);

                viewHolder.name = (TextView)convertView.findViewById(R.id.item_name);
                viewHolder.image = (GifImageView)convertView.findViewById(R.id.image);

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }

            final Item item = objects.get(position);

            Log.e("name",item.getName());
            Log.e("title",item.getTitle());

            viewHolder.name.setText(item.getTitle());
            GifDrawable gifFromBytes = null;
            try {
                gifFromBytes = new GifDrawable(item.getImage());
            } catch (IOException e) {
                e.printStackTrace();
            }
            viewHolder.image.setImageDrawable(gifFromBytes);


            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new fetchItem(context).execute(item.getTitle());
                }
            });

            return convertView;
        }

        private class ViewHolder{
            TextView name;
            GifImageView image;
        }
    }

    private void loadDropsView(Context context, ViewGroup parent, List<ItemDrop> itemDrops) {
        parent.removeAllViews();
        LayoutInflater inflater =
                (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (ItemDrop itemDrop : itemDrops) {
            View rowView = inflater.inflate(R.layout.row_itemdrop, null);

            TextView name = (TextView) rowView.findViewById(R.id.name);
            TextView chance = (TextView) rowView.findViewById(R.id.chance);

            name.setText(itemDrop.getCreature());
            GifDrawable gifFromBytes;
            try {
                gifFromBytes = new GifDrawable(itemDrop.getImage());
            } catch (IOException e) {
                gifFromBytes = null;
                e.printStackTrace();
            }
            name.setCompoundDrawablesRelativeWithIntrinsicBounds(gifFromBytes, null, null, null);

            if (itemDrop.getChance() > 0) {
                chance.setText(getString(R.string.chance, itemDrop.getChance()));
            } else {
                chance.setText("?");
            }

            parent.addView(rowView);
        }
    }

    private void loadOffersView(Context context, ViewGroup parent, List<NpcOffer> offers) {
        parent.removeAllViews();
        LayoutInflater inflater =
                (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (NpcOffer offer : offers) {
            View rowView = inflater.inflate(R.layout.row_npcoffer, null);

            TextView name = (TextView) rowView.findViewById(R.id.name);
            TextView city = (TextView) rowView.findViewById(R.id.city);
            TextView price = (TextView) rowView.findViewById(R.id.price);

            name.setText(offer.getNpc());
            city.setText(getString(R.string.city,offer.getCity()));
            price.setText(getString(R.string.price,offer.getValue()));

            parent.addView(rowView);
        }
    }

}
