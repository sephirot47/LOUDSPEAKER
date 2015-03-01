package com.example.sephirot47.loudspeaker;

import android.content.Context;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.io.ByteArrayInputStream;
import java.util.*;
import android.os.NetworkOnMainThreadException;
import java.io.IOException;
import java.io.*;
import android.os.StrictMode;
import android.util.JsonReader;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.HttpResponse;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.lang.Math.sqrt;

public class ConManager
{
    public static final String BaseURL = "http://loudspeaker-api.herokuapp.com";

    private static final String ReceiveURL = BaseURL + "/messages";
    private static final String SendURL = BaseURL + "/messages";
    private static final String GetNickURL = BaseURL + "/getNick";
    private static final String CleanURL = BaseURL + "/mec";

    private static ConManager ConManager = null;

    private ConManager(Context context)
    {
        super();
        if (android.os.Build.VERSION.SDK_INT > 9) //Per solucionar una excepcio al fer send!
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    public static void Init()
    {
        if(ConManager == null)
        {
            if(MainActivity.context != null) ConManager = new ConManager(MainActivity.context);
            else if(MainService.context != null) ConManager = new ConManager(MainService.context);
            else MainActivity.Log("FATAL ERROR: can't find any context to work with. Pene");
        }
    }
    public static boolean Created() { return ConManager != null; }

    public static void SendPost(final String url, final JSONObject jsonObj, final Method OnResponse, final Method OnFail)
    {
        Runnable runnable = new Runnable()  { public void run() {

            Init();
            boolean good = false;
            String responseText = "";
            HttpEntity entity = null;
            try
            {
                HttpPost httpPost = new HttpPost(url);
                httpPost.addHeader("Accept","application/json");
                httpPost.setEntity(new ByteArrayEntity(jsonObj.toString().getBytes("UTF-8")));
                DefaultHttpClient httpClientSend = new DefaultHttpClient();
                HttpResponse response = httpClientSend.execute(httpPost);
                if(response != null)
                {
                    entity = response.getEntity();
                    responseText = EntityUtils.toString(entity, "UTF-8");
                    good = true;
                }
            }
            catch (ClientProtocolException e1) { e1.printStackTrace(); }
            catch (IOException e1) { e1.printStackTrace(); }
            catch(NetworkOnMainThreadException e) { e.printStackTrace(); }

            if(entity != null) try { entity.consumeContent(); } catch (IOException e){}

            try
            {
                if(good) OnResponse.invoke(null, responseText, jsonObj);
                else OnFail.invoke(null, responseText, jsonObj);
            }
            catch (IllegalAccessException e) { e.printStackTrace(); }
            catch (InvocationTargetException e) {  e.printStackTrace(); }
        } };

        new Thread(runnable).start();
    }

    public static void SendPut(final String url, final JSONObject jsonObj, final Method OnResponse, final Method OnFail)
    {
        Runnable runnable = new Runnable()  { public void run() {

            Init();
            boolean good = false;
            String responseText = "";
            HttpEntity entity = null;
            try
            {
                HttpPut httpPut = new HttpPut(url);
                httpPut.addHeader("Accept","application/json");
                httpPut.setEntity(new ByteArrayEntity(jsonObj.toString().getBytes("UTF-8")));
                DefaultHttpClient httpClientSend = new DefaultHttpClient();
                HttpResponse response = httpClientSend.execute(httpPut);
                if(response != null)
                {
                    entity = response.getEntity();
                    responseText = EntityUtils.toString(entity, "UTF-8");
                    good = true;
                }
            }
            catch (ClientProtocolException e1) { e1.printStackTrace(); }
            catch (IOException e1) { e1.printStackTrace(); }
            catch(NetworkOnMainThreadException e) { e.printStackTrace(); }

            if(entity != null) try { entity.consumeContent(); } catch (IOException e){}

            try
            {
                if(good) OnResponse.invoke(null, responseText, jsonObj);
                else OnFail.invoke(null, responseText, jsonObj);
            }
            catch (IllegalAccessException e) { e.printStackTrace(); }
            catch (InvocationTargetException e) {  e.printStackTrace(); }
        } };

        new Thread(runnable).start();
    }

    public static void SendGet(final String url, final String[] parNames, final String[] parValues, final Method OnResponse, final Method OnFail)
    {
        Runnable runnable = new Runnable()  { public void run() {

        Init();

        String responseText = "";
        String newURL = url;
        for(int i = 0; i < parNames.length; ++i)
        {
            if(i != 0) newURL += "&"; else newURL += "?";
            newURL += parNames[i] + "=" + parValues[i];
        }
        MainActivity.Log("SendGet: " + newURL);
        HttpEntity entity = null;
        boolean good = false;
        try
        {
            HttpGet httpGet = new HttpGet(newURL);
            httpGet.addHeader("Accept","application/json");
            DefaultHttpClient httpClientReceive = new DefaultHttpClient();
            HttpResponse response = httpClientReceive.execute(httpGet);

            if(response != null)
            {
                entity = response.getEntity();
                responseText = EntityUtils.toString(entity, "UTF-8");
                good = true;
            }
        }
        catch (IOException e) { e.printStackTrace(); }

        if(entity != null) try { entity.consumeContent(); } catch (IOException e) { e.printStackTrace(); }

        try
        {
            if(good) if(OnResponse != null) OnResponse.invoke(null, responseText);
            else if(OnFail != null) OnFail.invoke(null, responseText);
        }
        catch (IllegalAccessException | InvocationTargetException e) { e.printStackTrace(); }
        } };

        new Thread(runnable).start();
    }


    public static void OnMessageCorrectlySent(String response, JSONObject obj)
    {
        if(response.equals("\"OK\""))
        {
            Receive();
        }
    }

    public static void OnMessageFailedSent(String response, JSONObject obj)
    {
    }

    public static synchronized void SendMessage(final Message msg)
    {
        Init();
        if(!SettingsManager.LoggedIn() /*||
           WifiDirectBroadcastReceiver.GetMACList().isEmpty()*/) return;

        msg.SetUsername(SettingsManager.GetUsername());

        if(msg.ReadyToBeSent())
        {
            JSONObject jsonObj = msg.GetJSONObject();
            JSONArray jsonArray = new JSONArray();
            ArrayList<String> macList = WifiDirectBroadcastReceiver.GetMACList();
            if(macList == null) return;

            for (int i = 0; i < macList.size(); i++)
                jsonArray.put(macList.get(i));
            jsonArray.put(SettingsManager.GetMAC());

            try { jsonObj.put("macs", jsonArray); }
            catch (JSONException e) { e.printStackTrace(); }

            try
            {
                ConManager.SendPost(SendURL, jsonObj,
                                    Class.forName("com.example.sephirot47.loudspeaker.ConManager").getDeclaredMethod("OnMessageCorrectlySent", String.class, JSONObject.class),
                                    Class.forName("com.example.sephirot47.loudspeaker.ConManager").getDeclaredMethod("OnMessageFailedSent", String.class, JSONObject.class));
            }
            catch (NoSuchMethodException e) { e.printStackTrace(); }
            catch (ClassNotFoundException e1) { e1.printStackTrace(); }
        }
        else MainActivity.Toast("Message not ready to be sent ( nick or text = '' ? )");
    }

    public static synchronized ArrayList<Message> Receive()
    {
        Init();

        ArrayList<Message> messages = new ArrayList<Message>();
        if(SettingsManager.GetUsername().equals("")) return messages;

        JsonReader jsonReader = null;
        InputStream in = null;
        HttpEntity entity = null;
        try
        {
            DefaultHttpClient httpClientReceive = new DefaultHttpClient();
            String url = ReceiveURL + "?mac=" + SettingsManager.GetMAC();

            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("Accept","application/json");
            HttpResponse response = httpClientReceive.execute(httpGet);
            if(response != null)
            {
                entity = response.getEntity();
                String responseText = EntityUtils.toString(entity, "UTF-8");

                if(!responseText.equals("\"KO\""))
                {
                    in = new ByteArrayInputStream(responseText.getBytes());

                    jsonReader = new JsonReader(new InputStreamReader(in, "UTF-8"));
                    jsonReader.beginArray();
                    while(jsonReader.hasNext())
                    {
                        Message msg = new Message(jsonReader);
                        messages.add(msg);
                    }
                    jsonReader.endArray();
                }
            }
        }
        catch (IOException e)
        {
            MainActivity.Log("Error trying create connection to receive: " + e.getMessage());
            e.printStackTrace();
        }

        if(jsonReader != null) try { jsonReader.close(); } catch (IOException e){}
        if(in != null) try { in.close(); } catch (IOException e){}
        if(entity != null) try { entity.consumeContent(); } catch (IOException e){}

        return messages;
    }
}
