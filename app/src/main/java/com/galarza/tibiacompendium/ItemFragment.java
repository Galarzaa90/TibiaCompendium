package com.galarza.tibiacompendium;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
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
import pl.droidsonroids.gif.GifTextView;


public class ItemFragment extends Fragment {

    private TibiaDatabase db;

    private LinearLayout headerBox;
    private TextView header;

    private ScrollView itemBox;
    private TextView itemName;
    private TextView itemLook;

    private LinearLayout itemBuyers;
    private LinearLayout itemSellers;
    private LinearLayout itemDrops;

    private LinearLayout buyersBox;
    private LinearLayout sellersBox;
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
        loadCategoryLayout(getContext(),categoryLayout);

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
        itemBuyers = (LinearLayout)rootView.findViewById(R.id.item_buyers);
        itemSellers = (LinearLayout)rootView.findViewById(R.id.item_sellers);
        itemDrops = (LinearLayout)rootView.findViewById(R.id.item_drops);

        buyersBox = (LinearLayout)rootView.findViewById(R.id.buyers_box);
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
        sellersBox = (LinearLayout)rootView.findViewById(R.id.sellers_box);
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
            itemBuyers.setVisibility(View.GONE);
            itemSellers.setVisibility(View.GONE);
            itemDrops.setVisibility(View.GONE);
        }

        @Override
        protected void onPostExecute(Item item){
            itemBox.setVisibility(View.VISIBLE);
            itemList.setVisibility(View.GONE);
            headerBox.setVisibility(View.GONE);

            itemName.setText(item.getName());
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
        //toolbar.setTitle(R.string.title_items);

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
                viewHolder.image = (GifImageView)convertView.findViewById(R.id.image);

                convertView.setTag(viewHolder);
            }else{
                viewHolder = (ViewHolder)convertView.getTag();
            }

            final Item item = objects.get(position);

            viewHolder.name.setText(item.getName());
            //viewHolder.image.setImageBitmap(item.getImage());
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
                    new fetchItem(context).execute(item.getName());
                }
            });

            return convertView;
        }

        private class ViewHolder{
            TextView name;
            GifImageView image;
        }
    }

    public void loadCategoryLayout(Context context, GridLayout parent){
        final Context fContext = context;
        LayoutInflater inflater =
                (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        parent.removeAllViews();
        final String[] categoryTitles = getResources().getStringArray(R.array.categories_titles);
        final String[] categorySort = getResources().getStringArray(R.array.categories_sort);
        final TypedArray categoryDrawables = getResources().obtainTypedArray(R.array.categories_drawables);
        final String[] categoryName = getResources().getStringArray(R.array.categories_name);

        int screenWidth = getScreenWidth();
        int cellWidth = screenWidth / 2;
        GridLayout.LayoutParams p;
        for(int position = 0; position < categoryTitles.length; position++){
            View view = inflater.inflate(R.layout.category_item,parent,false);
            GifTextView categoryView = (GifTextView) view.findViewById(R.id.category);

            categoryView.setText(categoryTitles[position]);
            GifDrawable icon = null;
            try {
                 icon = new GifDrawable(getResources(),categoryDrawables.getResourceId(position,0));
            } catch (IOException e) {
                e.printStackTrace();
            }
            categoryView.setCompoundDrawablesRelativeWithIntrinsicBounds(icon,null,null,null);
            final int index = position;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new fetchData(fContext).execute(categoryName[index],categorySort[index]);
                    header.setText(categoryTitles[index]);
                    headerBox.setVisibility(View.VISIBLE);
                    categoryLayout.setVisibility(View.GONE);
                }
            });
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
                p = (GridLayout.LayoutParams) view.getLayoutParams();
                p.width = cellWidth;
            }


            parent.addView(view);
        }
        categoryDrawables.recycle();
    }

    private int getScreenWidth(){
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return  size.x;
    }

    public void loadDropsView(Context context, ViewGroup parent, List<ItemDrop> itemDrops) {
        parent.removeAllViews();
        LayoutInflater inflater =
                (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (ItemDrop itemDrop : itemDrops) {
            View rowView = inflater.inflate(R.layout.row_itemdrop, null);

            TextView name = (TextView) rowView.findViewById(R.id.name);
            TextView chance = (TextView) rowView.findViewById(R.id.chance);

            name.setText(itemDrop.getCreature());
            BitmapDrawable drawable = new BitmapDrawable(getResources(), itemDrop.getImage());
            name.setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, null, null, null);
            if (itemDrop.getChance() > 0) {
                chance.setText(getString(R.string.chance, itemDrop.getChance()));
            } else {
                chance.setText("?");
            }

            parent.addView(rowView);
        }
    }

    public void loadOffersView(Context context, ViewGroup parent, List<NpcOffer> offers) {
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
