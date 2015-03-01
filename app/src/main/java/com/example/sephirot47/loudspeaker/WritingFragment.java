package com.example.sephirot47.loudspeaker;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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

public class WritingFragment extends Fragment
{
    EditText msgText;
    ImageButton backButton, sendButton;
    boolean firstChanged;

    private View view;

    public WritingFragment() {}

    private void Init(View v)
    {
        view = v;

        firstChanged = true;

        msgText = (EditText) view.findViewById(R.id.msgTextWriting);
        backButton = (ImageButton) view.findViewById(R.id.backButton);
        sendButton = (ImageButton) view.findViewById(R.id.sendButton);

        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MainActivity.activity.SetCurrentFragment(MainActivity.FragmentFeed);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendMessage();
                InputMethodManager imm = (InputMethodManager) MainActivity.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(msgText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    public void SendMessage()
    {
        if(msgText.getText().toString().equals(MainActivity.DefaultMessage) ||
           msgText.getText().toString().equals("")) return;

        Message msg = new Message();
        msg.SetText(msgText.getText().toString());
        ConManager.SendMessage(msg);

        msgText.setText("");
        msgText.clearFocus();

        InputMethodManager imm = (InputMethodManager) MainActivity.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(msgText.getWindowToken(), 0);

        MainActivity.activity.SetCurrentFragment(MainActivity.FragmentFeed);
    }

    public void OnEnterFragment()
    {
        FeedFragment.comingFromWriting = true;

        new Thread(new Runnable(){public void run(){

            try { Thread.sleep(200); } catch (InterruptedException e) { e.printStackTrace(); }

            MainActivity.activity.runOnUiThread(new Runnable(){public void run(){
                msgText.requestFocus();
            }});

            InputMethodManager imm = (InputMethodManager) MainActivity.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(msgText.getWindowToken(), 0);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }}).start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.writing_feed, container, false);
        Init(v);
        return v;
    }
}
