package com.example.sephirot47.loudspeaker;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import java.util.ArrayList;
import java.util.TreeMap;

public class MainService extends Service
{
    public static MainService service;
    public static Context context;

    TreeMap<Integer, Message> messages;
    public static final int ReceiveThreadDelayBG = 5000;
    public static final int ReceiveThreadDelay = 1000;

    @Override
    public void onCreate()
    {
        super.onCreate();
        context = getApplicationContext();
        service = this;

        WifiDirectBroadcastReceiver.Init();

        messages = new TreeMap<Integer, Message>(); // messages[idMsg] = text
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        super.onStartCommand(intent, flags, startId);

        Runnable receiverRunnable = new Runnable()
        {
            public void run()
            {
                while(true)
                {
                    ReceiveMessages();
                }
            }
        };
        Thread receiveThread = new Thread(receiverRunnable);
        receiveThread.start();

        return START_STICKY;
    }

    private void ReceiveMessages()
    {
        ArrayList<Message> receivedMessages = ConManager.Receive();
        boolean newMessages = false;
        for(int i = 0; i < receivedMessages.size(); ++i)
        {
            Message msg = receivedMessages.get(i);
            if(!messages.containsKey(msg.GetId()))
            {
                newMessages = true;
                messages.put(msg.GetId(), msg);
            }
        }

        if(MainActivity.activity == null || MainActivity.activity.inBackground )
        {
            if(newMessages)
            {
                NotificationMgr.ShowOrUpdate(getApplicationContext(), messages.size());
            }
        }
        else
        {
            if(MainActivity.feedFragment != null /*&& MainActivity.feedFragment.Created()*/)
            {
                MainActivity.feedFragment.OnReceiveMessages(messages);
                messages.clear();
            }
        }

        try { Thread.sleep(ReceiveThreadDelay);  } catch (InterruptedException e) {}
    }

    @Override
    public void onDestroy() { }

    public Intent getIntent()
    {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);

        if(MainActivity.activity == null) intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        else intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        return intent;
    }

    public IBinder onBind(Intent arg0)
    {
        return null;
    }
}
