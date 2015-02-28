package com.example.sephirot47.loudspeaker;

import android.app.Fragment;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.audiofx.BassBoost;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.view.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeMap;

public class InitFragment extends Fragment
{
    private boolean created = false;

    public InitFragment()
    {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        new Thread(
                new Runnable()
                {
                    public void run()
                    {
                        ConManager.Init();
                        while(true)
                        {
                            if(!ConManager.Created()) ConManager.Init();
                            if(!SettingsManager.Created()) SettingsManager.Init();
                            if(!WifiDirectBroadcastReceiver.Created()) WifiDirectBroadcastReceiver.Init();

                            boolean everythingInited = ConManager.Created() && SettingsManager.Created() &&
                                                       WifiDirectBroadcastReceiver.Created();
                            if(everythingInited)
                            {
                                try { Thread.sleep(1000); } catch (InterruptedException e) { e.printStackTrace(); }

                                if(SettingsManager.LoggedIn())
                                {
                                    MainActivity.activity.SetCurrentFragment(MainActivity.FragmentFeed);
                                }
                                else
                                {
                                    MainActivity.activity.SetCurrentFragment(MainActivity.FragmentRegister);
                                }
                                break;
                            }
                            else
                            {
                                if(!ConManager.Created()) MainActivity.Log("ConManager");
                                if(!SettingsManager.Created()) MainActivity.Log("SettingsManager");
                            }
                            try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }
                        }
                    }
                }
        ).start();
    }

    @Override
    public void onResume()
    {
        super.onResume();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view =  inflater.inflate(R.layout.fragment_init, container, false);
        created = true;

        return view;
    }

}
