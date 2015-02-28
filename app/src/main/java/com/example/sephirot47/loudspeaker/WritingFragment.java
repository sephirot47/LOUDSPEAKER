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
    Button backButton, sendButton;
    boolean firstChanged;
    private View view;

    public WritingFragment() {}

    private void Init(View v)
    {
        view = v;

        firstChanged = true;

        msgText = (EditText) view.findViewById(R.id.msgTextWriting);
        msgText.requestFocus();
        backButton = (Button) view.findViewById(R.id.backButton);
        sendButton = (Button) view.findViewById(R.id.sendButton);

        msgText.setOnKeyListener(new View.OnKeyListener()
        {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    return true;
                }
                return false;
            }
        });

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

        backButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                MainActivity.activity.SetCurrentFragment(MainActivity.FragmentFeed);
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //SEND
                InputMethodManager imm = (InputMethodManager) MainActivity.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(msgText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    public void OnEnterFragment()
    {
        FeedFragment.comingFromWriting = true;
        InputMethodManager imm = (InputMethodManager) MainActivity.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(msgText, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.writing_feed, container, false);
        Init(v);
        return v;
    }
}
