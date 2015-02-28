package com.example.sephirot47.loudspeaker;

import android.content.Context;
import android.graphics.Color;
import android.view.ViewGroup;
import android.view.View;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.text.Html;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Feed
{
    private View view;
    private TextView usernameTextView, textView, timestampText;
    private ImageButton reloudButton;
    private Message msg;
    private static LayoutInflater inflater=null;

    public Feed(final Message msg)
    {
        inflater = (LayoutInflater) MainActivity.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.feed_template, null);

        usernameTextView = (TextView) view.findViewById(R.id.usernameTextView);
        textView = (TextView) view.findViewById(R.id.textView);
        timestampText = (TextView) view.findViewById(R.id.timestampText);
        reloudButton = (ImageButton) view.findViewById(R.id.reloudButton);

        this.msg = msg;

        if(msg.GetUsername().equals(SettingsManager.username))
        {
            reloudButton.setVisibility(View.GONE);
        }

        reloudButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Reloud();
            }
        });
    }

    public View GetView()
    {
        textView.setText( Html.fromHtml(msg.GetText()).toString() );
        usernameTextView.setText( Html.fromHtml(msg.GetUsername()).toString() );

        String timestamp = msg.GetTimestamp();
        int indexOfDot = timestamp.indexOf(".");
        if(indexOfDot >= 0) timestamp = timestamp.substring(0, indexOfDot);
        timestampText.setText( timestamp );

        return view;
    }

    public void Reloud()
    {
    }
}
