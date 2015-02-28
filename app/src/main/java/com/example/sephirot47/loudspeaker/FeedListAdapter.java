package com.example.sephirot47.loudspeaker;

import android.widget.BaseAdapter;
import android.widget.TextView;
import android.content.Context;
import android.view.*;

import java.util.ArrayList;

class FeedListAdapter extends BaseAdapter
{
    Context context;
    private ArrayList<Feed> feeds;

    public FeedListAdapter(Context context)
    {
        this.context = context;
        feeds = new ArrayList<Feed>();
    }

    public void AddFeed(Feed f)
    {
        feeds.add(f);
    }

    public void Clear()
    {
        feeds.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return feeds.size();
    }

    @Override
    public Object getItem(int position)
    {
        return feeds.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = feeds.get(position).GetView();
        if (convertView == null) convertView = v;
        return v;
    }
}