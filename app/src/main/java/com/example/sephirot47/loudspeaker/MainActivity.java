package com.example.sephirot47.loudspeaker;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.pm.ActivityInfo;
import android.content.Intent;
import android.content.Context;
import android.media.audiofx.BassBoost;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.widget.Toast;

import java.lang.reflect.Method;

public class MainActivity extends FragmentActivity
{
    public static MainActivity activity;
    public static Context context;
    public static boolean inBackground = false;

    public static final String DefaultMessage = "Write something here...";
    public static final String LogTag = "com.example.sephirot47.loudspeaker";

    public static final int FragmentRegister = 1,
                            FragmentFeed = 2,
                            FragmentWriting = 3;

    public static int currentFragment = FragmentRegister;

    public static FeedFragment feedFragment = null;
    public static RegisterFragment registerFragment = null;
    public static WritingFragment writingFragment = null;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        activity = this;
        context = getApplicationContext();
        setContentView(R.layout.main_activity);

        //NO LANDSCAPE MODE
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        feedFragment = new FeedFragment();
        registerFragment = new RegisterFragment();
        writingFragment = new WritingFragment();

        fragmentManager = getFragmentManager();
        FragmentTransaction trans = fragmentManager.beginTransaction();
        trans.add(R.id.fragmentContainer, feedFragment);
        trans.add(R.id.fragmentContainer, registerFragment);
        trans.add(R.id.fragmentContainer, writingFragment);
        trans.commit();

        if(SettingsManager.LoggedIn()) SetCurrentFragment(FragmentFeed);
        else SetCurrentFragment(FragmentRegister);

        startService(new Intent(this, MainService.class)); //Por si no se habia iniciado :p
        NotificationMgr.Clear(this);
    }

    public void SetCurrentFragment(final int fragmentId)
    {
        runOnUiThread( new Runnable() { public void run()
        {
            FragmentTransaction trans = fragmentManager.beginTransaction();
            trans.hide(feedFragment);
            trans.hide(registerFragment);
            trans.hide(writingFragment);

            if (fragmentId == FragmentFeed) { trans.show(feedFragment); feedFragment.OnEnterFragment(); }
            else if (fragmentId == FragmentRegister) { trans.show(registerFragment); registerFragment.OnEnterFragment(); }
            else if (fragmentId == FragmentWriting) { trans.show(writingFragment); writingFragment.OnEnterFragment(); }
            trans.commit();

            currentFragment = fragmentId;
        }});
    }

    public static void Log(String text)
    {
        Log.d(LogTag, text);
    }

    public static void Toast(final String text)
    {
        activity.runOnUiThread( new Runnable(){ public void run() { Toast.makeText(context, text, Toast.LENGTH_SHORT).show(); } });
    }

    @Override
    public void onResume()
    {
        super.onResume();
        inBackground = false;
        if(!SettingsManager.GetUsername().equals(""))
            SetCurrentFragment(FragmentFeed);
        else

        WifiDirectBroadcastReceiver.Register();
        NotificationMgr.Clear(this.getApplicationContext());
    }

    public int GetCurrentFragment()
    {
        return currentFragment;
    }

    @Override
    public void onPause()
    {
        super.onPause();
        inBackground = true;
        WifiDirectBroadcastReceiver.Unregister();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        activity = null;
        inBackground = true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) { }

    @Override
    public void onBackPressed()
    {
        if(currentFragment == FragmentWriting)
            SetCurrentFragment(FragmentFeed);
    }
}
