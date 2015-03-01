package com.example.sephirot47.loudspeaker;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.Visibility;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.content.Context;
import android.view.*;

import java.util.ArrayList;

class FeedListAdapter extends BaseAdapter
{
    Context context;
    public View view;
    private ArrayList<Feed> feeds;

    public FeedListAdapter(Context context)
    {
        this.context = context;
        feeds = new ArrayList<Feed>();
        view = null;
    }

    public void AddFeed(Feed f)
    {
        feeds.add(f);
        notifyDataSetChanged();
    }
    public void DeleteFeed(final Feed f)
    {
        int x = 0;
        for(int i = 0; i < feeds.size(); ++i)
        {
            if(feeds.get(i).msg.GetId() == f.msg.GetId())
            {
                feeds.get(i).view.setVisibility(View.INVISIBLE);
                x = i;
                break;
            }
        }
        final int indexOfDeleted = x;
        final View row = feeds.get(indexOfDeleted).view;
        row.setAlpha(0.0f);

        final int initialHeight = row.getHeight();
        Animation animation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                super.applyTransformation(interpolatedTime, t);
                int newHeight = (int) (initialHeight * (1 - interpolatedTime));
                if (newHeight > 0)
                {
                    row.getLayoutParams().height = newHeight;
                    row.requestLayout();
                    row.setAlpha(0.0f);
                }
            }
        };
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation)
            {
                row.getLayoutParams().height = initialHeight;
                row.requestLayout();
                HistoryManager.DeleteMessage(feeds.get(indexOfDeleted).msg);
                feeds.remove(indexOfDeleted);
                notifyDataSetChanged();
            }
        });
        animation.setDuration(400);
        row.startAnimation(animation);
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
        view = feeds.get(position).GetView();
        if (convertView == null) convertView = view;
        return view;
    }
}