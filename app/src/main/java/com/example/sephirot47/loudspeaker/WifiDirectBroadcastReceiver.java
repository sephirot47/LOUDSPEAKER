package com.example.sephirot47.loudspeaker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.net.wifi.p2p.WifiP2pManager;

import java.util.ArrayList;
import java.util.Iterator;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {

    private static ArrayList<String> macs;
    private static IntentFilter mIntentFilter = null;
    private static BroadcastReceiver mReceiver = null;
    private static WifiP2pManager mManager = null;
    private static Channel mChannel = null;
    private static PeerListListener peerListListener;

    private static final int REFRESH_RATE = 10 * 1000;

    public static void Init()
    {
        if(mReceiver == null && MainActivity.activity != null)
        {
            mManager = (WifiP2pManager) MainActivity.activity.getSystemService(Context.WIFI_P2P_SERVICE);
            mChannel = mManager.initialize(MainActivity.activity.getApplicationContext(),
                                           MainActivity.activity.getMainLooper(), null);

            macs = new ArrayList<String>();
            peerListListener = new PeerListListener()
            {
                @Override
                public void onPeersAvailable(WifiP2pDeviceList peers)
                {
                    macs.clear();
                    Iterator i = peers.getDeviceList().iterator();
                    Context ctx = MainActivity.activity == null ? MainService.context : MainActivity.context;
                    while(i.hasNext())
                    {
                        WifiP2pDevice dev = ((WifiP2pDevice)i.next());
                        MainActivity.Log(dev.deviceAddress.substring(3));
                        macs.add(dev.deviceAddress.substring(3));
                    }

                    if(MainActivity.feedFragment != null)
                    {
                        MainActivity.feedFragment.SetPeersText(String.valueOf(macs.size()));
                    }
                }
            };

            mReceiver = new WifiDirectBroadcastReceiver();

            mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

            new Thread(new Runnable(){public void run(){
                while(true)
                {
                    UpdateMACsList();
                    try { Thread.sleep(REFRESH_RATE); } catch (InterruptedException e) { e.printStackTrace(); }
                }
            }}).start();
        }
    }

    public static void Register()
    {
        if(MainActivity.activity != null)
            MainActivity.activity.registerReceiver(mReceiver, mIntentFilter);
    }

    public static boolean Created()
    {
        return mReceiver != null;
    }

    public static void Unregister()
    {
        if(MainActivity.activity != null)
            MainActivity.activity.registerReceiver(mReceiver, mIntentFilter);
    }

    public static synchronized ArrayList<String> GetMACList()
    {
        return macs;
    }

    public static synchronized void UpdateMACsList()
    {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess()
            {
                if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION))
                {
                    if (mManager != null)
                    {
                        mManager.requestPeers(mChannel, peerListListener);
                    }
                }
            }

            @Override
            public void onFailure(int reasonCode)
            {
                MainActivity.Log("FAILED PEERING: " + reasonCode);
            }
        });

    }

    public WifiDirectBroadcastReceiver() {}

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }
    }
}