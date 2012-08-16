
package com.aokp.swagpapers;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class NavigationBarCategoryAdapater implements SpinnerAdapter {

    Context mContext;
    ArrayList<WallpaperCategory> mCategories;

    public NavigationBarCategoryAdapater(Context c, ArrayList<WallpaperCategory> cats) {
        mContext = c;
        WallpaperCategory allCats = new WallpaperCategory(cats.size() + "", "<All>");
        for (WallpaperCategory cat : cats) {
            for (Wallpaper wall : cat.getWallpapers()) {
                allCats.addWallpaper(wall);
            }
        }
        cats.add(0, allCats);

        mCategories = cats;
    }
    
    public ArrayList<WallpaperCategory> getCategories() {
        return mCategories;
    }

    @Override
    public int getCount() {
        return mCategories.size();
    }

    @Override
    public Object getItem(int position) {
        return mCategories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(
                R.layout.navigation_bar_category_spinner, parent, false);
        TextView catTitle = (TextView) v.findViewById(R.id.category);
        catTitle.setText(mCategories.get(position).getName());

        return v;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return mCategories.isEmpty();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(
                R.layout.navigation_bar_category_spinner, parent, false);
        TextView catTitle = (TextView) v.findViewById(R.id.category);
        catTitle.setText(mCategories.get(position).getName());

        TextView desc = (TextView) v.findViewById(R.id.category_description);
        desc.setVisibility(View.GONE);

        return v;
    }

}
