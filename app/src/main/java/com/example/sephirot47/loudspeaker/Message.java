package com.example.sephirot47.loudspeaker;

import android.util.JsonReader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import android.util.Log;

public class Message
{
    private int id;
    private String username, text;
    private String timestamp;

    public Message()
    {
        id = -1;
        username = text = "";
        timestamp = "";
    }

    //Create the message from json
    public Message(JsonReader reader)
    {
        this();
        try
        {
            reader.beginObject();
            while (reader.hasNext())
            {
                String name = reader.nextName();
                if (name.equals(SettingsManager.IdTag)) SetId(reader.nextInt());
                else if (name.equals(SettingsManager.UsernameTag)) SetUsername(reader.nextString());
                else if (name.equals(SettingsManager.TextTag)) SetText(reader.nextString());
                else if (name.equals(SettingsManager.TimestampTag))
                {
                    String timestamp = reader.nextString();
                    SetTimestamp(timestamp.substring(timestamp.indexOf(" ")));
                }
                else reader.skipValue();
            }
            reader.endObject();
        }
        catch (IOException ioexc)
        {
            MainActivity.Log("IOException trying to create message from JSON.");
        }
    }

    public Message(JSONObject obj)
    {
        this();
        try {
            SetId(obj.getInt(SettingsManager.IdTag));
            SetUsername(obj.getString(SettingsManager.UsernameTag));
            SetText(obj.getString(SettingsManager.TextTag));
            SetTimestamp(obj.getString(SettingsManager.TimestampTag));
        }
        catch (JSONException e) { e.printStackTrace(); }
    }

    public JSONObject GetJSONObject()
    {
        JSONObject jo = new JSONObject();
        try
        {
            jo.put(SettingsManager.IdTag, -1);
            jo.put(SettingsManager.UsernameTag, GetUsername());
            jo.put("nick", GetUsername());
            jo.put(SettingsManager.TextTag, GetText());
            jo.put(SettingsManager.TimestampTag, GetTimestamp());
        }
        catch(JSONException joexc)
        {
            MainActivity.Log("IOException trying to Get JSON object from message.");
        }
        return jo;
    }

    public boolean ReadyToBeSent()
    {
        return !username.equals("") && !text.equals("");
    }

    public boolean IsNull()  {  return id == -1;  }

    public void Log()
    {
        if(IsNull())
        {
            MainActivity.Log("Null message");
        }
        else
        {
            MainActivity.Log(SettingsManager.IdTag + ": " + id);
            MainActivity.Log(SettingsManager.UsernameTag + ": " + username);
            MainActivity.Log(SettingsManager.TextTag + ": " +  text);
        }
    }

    public int GetId() { return id; }
    public String GetUsername() { return username; }
    public String GetText() { return text; }
    public String GetTimestamp() { return timestamp; }

    public void SetId(int id) { this.id = id; }
    public void SetUsername(String username) { this.username = StringEscapeUtils.escape(username); }
    public void SetText(String text) { this.text = StringEscapeUtils.escape(text); }
    public void SetTimestamp(String timestamp) { this.timestamp = timestamp; }

}
