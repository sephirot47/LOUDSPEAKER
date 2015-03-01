package com.example.sephirot47.loudspeaker;

import android.database.sqlite.*;
import android.database.*;
import android.content.Context;
import android.util.Log;
import java.util.ArrayList;
import java.util.Set;


public class HistoryManager extends SQLiteOpenHelper
{
    private static final String DBName = "historyDB";
    private static final int DBVersion = 1;
    private static final String TableName = "history";

    public static final String  IdTag = "_id", IdType = "INTEGER PRIMARY KEY",
                                UsernameTag = "username", UsernameType = "VARCHAR(40)",
                                TextTag = "text", TextType = "VARCHAR(140)",
                                TimestampTag = "timestamp", TimestampType = "VARCHAR(40)";

    private static HistoryManager historyManager = null;

    private HistoryManager(Context context)
    {
        super(context, DBName, null, DBVersion);
    }

    public static void Init()
    {
        if(historyManager == null)
        {
            if(MainActivity.context != null) historyManager = new HistoryManager(MainActivity.context);
            else if(MainService.context != null) historyManager = new HistoryManager(MainService.context);
            else MainActivity.Log("FATAL ERROR: can't find any context to work with.");
        }
    }
    public static boolean Created() { return historyManager != null; }

    public String GetCreateStatement()
    {
        return  "CREATE TABLE " + GetTableName() + " (" +
                IdTag + " " + IdType + ", " +
                UsernameTag + " " + UsernameType + ", " +
                TextTag + " " + TextType + ", " +
                TimestampTag + " " + TimestampType +
                " );";
    }

    public static void OnLogin()
    {
        Init();
        if( !TableExists( GetTableName() ) )
            historyManager.getWritableDatabase().execSQL(historyManager.GetCreateStatement());
    }

    public static boolean TableExists(String tableName)
    {
        Init();
        Cursor c =  historyManager.getReadableDatabase().
                    rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name=" + tableName + ";", null);
        c.moveToFirst();
        return !c.isAfterLast();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(GetCreateStatement());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + GetTableName());
        onCreate(db);
    }

    public static ArrayList<Message> GetMessages()
    {
        Init();

        ArrayList<Message> messages = new ArrayList<Message>();
        SQLiteDatabase db = historyManager.getReadableDatabase();
        String sql = "SELECT * FROM " + GetTableName() + " ORDER BY " + TimestampTag + " ASC;";
        Cursor c = db.rawQuery(sql, null);
        c.moveToFirst();
        while (!c.isAfterLast())
        {
            Message msg = new Message();
            msg.SetId( c.getInt(c.getColumnIndex(IdTag)) );
            msg.SetUsername( c.getString(c.getColumnIndex(UsernameTag)) );
            msg.SetText( c.getString(c.getColumnIndex(TextTag)) );
            msg.SetTimestamp( c.getString(c.getColumnIndex(TimestampTag)) );
            messages.add(msg);
            c.moveToNext();
        }
        c.close();
        return messages;
    }

    public static void InsertMessage(Message msg)
    {
        Init();
        if(HistoryManager.Exist(msg)) return;

        SQLiteDatabase db = historyManager.getWritableDatabase();
        String sql = "INSERT INTO " + GetTableName() + " VALUES(" +
                     "'" + msg.GetId() + "'" + ", " +
                     "'" + msg.GetUsername() + "'" + ", " +
                     "'" + msg.GetText() + "'" + ", " +
                     "'" + msg.GetTimestamp() + "'" +
                     ");";
        MainActivity.Log(sql);
        db.execSQL(sql);
    }

    public static void DeleteMessage(Message msg)
    {
        Init();
        if(!HistoryManager.Exist(msg)) return;

        SQLiteDatabase db = historyManager.getWritableDatabase();
        String sql = "DELETE FROM " + GetTableName() + " WHERE " + IdTag + " = " + msg.GetId() + ";";
        MainActivity.Log(sql);
        db.execSQL(sql);
    }

    public static boolean Exist(Message msg)
    {
        Init();

        SQLiteDatabase db = historyManager.getReadableDatabase();
        String sql = "SELECT * FROM " + GetTableName() + " WHERE " + IdTag + "='" + msg.GetId() + "'";
        Cursor c = db.rawQuery(sql, null);
        boolean exists = c.getCount() > 0;
        c.close();
        return exists;
    }

    public static String GetTableName()
    {
        return "'" + TableName + SettingsManager.GetUsername() + "'";
    }
}
