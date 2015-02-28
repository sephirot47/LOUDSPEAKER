package com.example.sephirot47.loudspeaker;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.view.*;
import android.view.inputmethod.*;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;

public class FeedFragment extends Fragment
{
    EditText msgText;
    private static TextView peersAroundText;
    private static FeedListView feedListView;
    private View view;

    private static HashSet<Integer> idMessagesInFeed;
    private static ArrayList<Message> newMessages;

    private boolean created = false;

    public static boolean comingFromWriting;

    public FeedFragment()
    {
        super();
        comingFromWriting = false;
        idMessagesInFeed = new HashSet<Integer>();
        newMessages = new ArrayList<Message>();
    }

    private void Init(View v)
    {
        view = v;

        peersAroundText = (TextView) view.findViewById(R.id.peersAroundText);
        msgText = (EditText) view.findViewById(R.id.msgText);
        feedListView = (FeedListView) view.findViewById(R.id.feedListView);

        msgText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MainActivity.activity.SetCurrentFragment(MainActivity.FragmentWriting);
                InputMethodManager imm = (InputMethodManager) MainActivity.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(msgText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        msgText.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                if (hasFocus && !comingFromWriting)
                {
                    comingFromWriting = true;
                    msgText.clearFocus();
                    MainActivity.activity.SetCurrentFragment(MainActivity.FragmentWriting);
                    InputMethodManager imm = (InputMethodManager) MainActivity.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(msgText, InputMethodManager.SHOW_IMPLICIT);
                }
            }
        });

        created = true;
    }

    public void OnEnterFragment()
    {
        new Thread( new Runnable(){ public void run(){

            try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }
            if(MainActivity.activity.GetCurrentFragment() != MainActivity.FragmentWriting)
                comingFromWriting = false;

        } } ).start();

        msgText.clearFocus();
        InputMethodManager imm = (InputMethodManager) MainActivity.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(msgText.getWindowToken(), 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_feed, container, false);
        Init(v);
        return v;
    }

    public static void OnLogout()
    {
        ((FeedListAdapter) feedListView.getAdapter()).Clear();
        idMessagesInFeed.clear();
        newMessages.clear();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    public static void SetPeersText(String txt)
    {
        peersAroundText.setText(txt);
    }

    public synchronized void AppendNewMessages()
    {
        if(!Created()) return;
        boolean newMessageToBeAppend = false;
        for(Message msg : newMessages)
        {
            if(!idMessagesInFeed.contains(msg.GetId()))
            {
                newMessageToBeAppend = true;
                idMessagesInFeed.add(msg.GetId());
                feedListView.Append(new Feed(msg));
            }
        }
        newMessages.clear();
        if(newMessageToBeAppend) ScrollDownFeedList();
    }

    public synchronized void OnReceiveMessages(TreeMap<Integer, Message> messages)
    {
        if(!Created()) return;
        for(Message msg : messages.values())
        {
            if(!idMessagesInFeed.contains(msg.GetId()))
            {
                MainActivity.Log("Adding received message...");
                newMessages.add(msg);
            }
        }

        MainActivity.activity.runOnUiThread( new Runnable(){ public void run(){ AppendNewMessages(); } } );
    }

    public void ScrollDownFeedList()
    {
        feedListView.ScrollDown();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        AppendNewMessages();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    public boolean Created()
    {
        return created;
    }

    public void Update()
    {
        ScrollDownFeedList();
    }
}
