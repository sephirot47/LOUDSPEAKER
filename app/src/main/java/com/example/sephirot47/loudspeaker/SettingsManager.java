package com.example.sephirot47.loudspeaker;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class SettingsManager
{
    public static String username = "", pass = "";

    public static final String  IdTag = "_id",
                                UsernameTag = "username",
                                PasswordTag = "password",
                                TextTag = "text",
                                TimestampTag = "timestamp";

    private static boolean created;

    private SettingsManager(){}

    public static void Init()
    {
        created = true;
    }

    public static String GetMAC()
    {
        WifiManager wifiManager = (WifiManager) MainActivity.context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        return wInfo.getMacAddress().substring(3);
    }

    public static boolean Created() { return created; }
    public static boolean LoggedIn() { return !username.equals(""); }
}
