package com.example.sephirot47.loudspeaker;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class SettingsManager extends SQLiteOpenHelper
{
    public static String username = "", pass = "";

    private static final String DBName = "settingsDB";
    private static final int DBVersion = 1;
    private static final String TableName = "settings";

    public static final String  IdTag = "_id",
                                UsernameTag = "username", UsernameType = "VARCHAR(40)",
                                PasswordTag = "password", PasswordType = "VARCHAR(255)",
                                TextTag = "text",
                                TimestampTag = "timestamp";

    private static final String DropTableSQL = "DROP TABLE IF EXISTS " + TableName;

    private static final String CreateTableSQL =
            "CREATE TABLE " + TableName + " (" +
                    UsernameTag + " " + UsernameType + ", " +
                    PasswordTag + " " + PasswordType +
                    " );";

    private static SettingsManager settingsManager = null;

    public SettingsManager()
    {
        super((MainActivity.context == null ? MainService.context : MainActivity.context), DBName, null, DBVersion);
    }

    public static void Init()
    {
        if(settingsManager == null)
        {
            if (MainActivity.context != null || MainService.context != null)
            {
                if(MainActivity.context != null) settingsManager = new SettingsManager();
                else settingsManager = new SettingsManager();

                if(IsEmpty()) InsertRow(); //para que la DB tenga solo una unica row
            }
            else MainActivity.Log("FATAL ERROR: can't find any context to work with. Pene");
        }
    }

    public static boolean Created() { return settingsManager != null; }

    public static String GetUsername() { return GetStringSetting(UsernameTag); }
    public static String GetPassword() { return GetStringSetting(PasswordTag); }
    public static String GetMAC()
    {
        WifiManager wifiManager = (WifiManager) MainService.context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        return wInfo.getMacAddress().substring(3);
    }

    public static void SetUsername(String str) { SetStringSetting(UsernameTag, str); }
    public static void SetPassword(String str) { SetStringSetting(PasswordTag, str); }

    private static int GetIntSetting(String columnTag)
    {
        Init();
        int x = 0;
        SQLiteDatabase db = settingsManager.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + columnTag + " FROM " + TableName, null);
        c.moveToFirst();
        if (!c.isAfterLast()) x = c.getInt( c.getColumnIndex(columnTag) );
        c.close();
        return x;
    }

    private static double GetDoubleSetting(String columnTag)
    {
        Init();
        double x = 0.0;
        SQLiteDatabase db = settingsManager.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + columnTag + " FROM " + TableName, null);
        c.moveToFirst();
        if (!c.isAfterLast()) x = c.getDouble(c.getColumnIndex(columnTag));
        c.close();
        return x;
    }

    private static String GetStringSetting(String columnTag)
    {
        Init();
        String x = "";
        SQLiteDatabase db = settingsManager.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT " + columnTag + " FROM " + TableName, null);
        c.moveToFirst();
        if (!c.isAfterLast()) { x = c.getString( c.getColumnIndex(columnTag) ); }
        c.close();
        return x;
    }

    private static void SetStringSetting(String columnTag, String str)
    {
        Init();
        SQLiteDatabase db = settingsManager.getWritableDatabase();
        db.execSQL("UPDATE " + TableName + " SET " + columnTag + "='" + str + "'");
    }

    private static void SetIntSetting(String columnTag, int d)
    {
        Init();
        SQLiteDatabase db = settingsManager.getWritableDatabase();
        db.execSQL("UPDATE " + TableName + " SET " + columnTag + "='" + String.valueOf(d) + "'");
    }

    private static void SetDoubleSetting(String columnTag, double d)
    {
        Init();
        SQLiteDatabase db = settingsManager.getWritableDatabase();
        db.execSQL("UPDATE " + TableName + " SET " + columnTag + "='" + String.valueOf(d) + "'");
    }

    public static boolean LoggedIn()
    {
        return !GetUsername().equals("");
    }

    private static boolean IsEmpty()
    {
        SQLiteDatabase db = settingsManager.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + TableName, null);
        c.moveToFirst();
        boolean empty = c.isAfterLast();
        c.close();
        return empty;
    }

    private static void InsertRow()
    {
        SQLiteDatabase db = settingsManager.getWritableDatabase();
        db.execSQL("INSERT INTO  " + TableName + " VALUES('', '');");
    }

    private static void EraseLastSession()
    {
        SQLiteDatabase db = settingsManager.getWritableDatabase();
        db.execSQL("UPDATE " + TableName + " SET " + UsernameTag + "='' ;");
        db.execSQL("UPDATE " + TableName + " SET " + PasswordTag + "='' ;");
    }

    public static void Logout()
    {
        EraseLastSession();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CreateTableSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(DropTableSQL);
        onCreate(db);
    }
}
