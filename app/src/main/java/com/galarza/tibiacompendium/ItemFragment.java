package com.galarza.tibiacompendium;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;

import com.galarza.tibiacompendium.data.Item;
import com.galarza.tibiacompendium.data.TibiaDatabase;
import com.galarza.tibiacompendium.data.Utils;

import java.util.List;


public class ItemFragment extends Fragment {

    private TibiaDatabase db;

    private LinearLayout headerBox;
    private TextView header;

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
            return db.getItemsByCategory(params[0]);
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
            LayoutInflater inflater =
                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(layout, null);
            Item item = objects.get(position);

            TextView name = (TextView)rowView.findViewById(R.id.name);
            name.setText(item.getName());

            Bitmap bitmap = BitmapFactory.decodeByteArray(item.getImage(),0,item.getImage().length);
            ImageView image = (ImageView)rowView.findViewById(R.id.image);
            image.setImageBitmap(bitmap);

            return rowView;
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
                        new fetchData(context).execute(categoryName[index]);
                        header.setText(categoryTitles[index]);
                        headerBox.setVisibility(View.VISIBLE);
                        categoryLayout.setVisibility(View.GONE);
                    }
                });

                parent.addView(view);
            }
        }

    }

}
