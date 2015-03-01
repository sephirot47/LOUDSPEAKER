package com.example.sephirot47.loudspeaker;

import android.widget.ListView;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.*;

public class FeedListView extends ListView
{
    private Context context;
    private FeedListAdapter adapter;

    public FeedListView(Context pContext)
    {
        super(pContext);
        Init(pContext);
    }

    public FeedListView(Context pContext, AttributeSet pAttrs, int pDefStyle) {
        super(pContext, pAttrs, pDefStyle);
        Init(pContext);
    }

    public FeedListView(Context pContext, AttributeSet pAttrs) {
        super(pContext, pAttrs);
        Init(pContext);
    }

    private void Init(Context pContext)
    {
        context = pContext;
        setLayoutParams(new ViewGroup.LayoutParams(480, 800));
        adapter = new FeedListAdapter(context);
        setAdapter(adapter);
    }

    public void Append(final Feed feed)
    {
        MainActivity.activity.runOnUiThread(new Runnable(){
            public void run()
            {
                adapter.AddFeed(feed);
                adapter.notifyDataSetChanged();
            }
        });
    }

    public void ScrollDown()
    {
        post( new Runnable() {
            @Override
            public void run()
            {
                setSelection(getCount() - 1);
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        super.onTouchEvent(event);
        return false;
    }

    @Override
    public void addView(View pChild)
    {
        super.addView(pChild);
    }

    @Override
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect)
    {
        return false;
    }
}
