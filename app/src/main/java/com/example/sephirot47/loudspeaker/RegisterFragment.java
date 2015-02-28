package com.example.sephirot47.loudspeaker;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.view.*;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;

public class RegisterFragment extends Fragment
{
    private static Button loginButton, registerButton;
    private static EditText userText, passText;

    private View view;

    public RegisterFragment()
    {

    }

    private void Init(View v)
    {
        view = v;

        loginButton = (Button) view.findViewById(R.id.loginButton);
        registerButton = (Button) view.findViewById(R.id.registerButton);
        userText = (EditText) view.findViewById(R.id.userRegisterText);
        passText = (EditText) view.findViewById(R.id.passwordRegisterText);

        loginButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LoginUser();
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                RegisterUser();
            }
        });

        userText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                InputMethodManager imm = (InputMethodManager) MainActivity.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(userText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        passText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                InputMethodManager imm = (InputMethodManager) MainActivity.activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(passText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    public void RegisterUser()
    {
        MainActivity.activity.SetCurrentFragment(MainActivity.FragmentFeed);
    }

    public void LoginUser()
    {
        MainActivity.activity.SetCurrentFragment(MainActivity.FragmentFeed);
    }

    public void OnEnterFragment()
    {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View v = inflater.inflate(R.layout.fragment_register, container, false);
        Init(v);
        return v;
    }
}
