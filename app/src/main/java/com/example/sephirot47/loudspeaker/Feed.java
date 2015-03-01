package com.example.sephirot47.loudspeaker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.View;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
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
    public View view;
    private TextView usernameTextView, textView, timestampText;
    private ImageButton reloudButton;
    public static final String ReloudURL = ConManager.BaseURL + "/reloud";
    public Message msg;
    private ImageView userImage;
    private static LayoutInflater inflater=null;

    public Feed(final Message msg)
    {
        inflater = (LayoutInflater) MainActivity.activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.feed_template, null);

        usernameTextView = (TextView) view.findViewById(R.id.usernameTextView);
        textView = (TextView) view.findViewById(R.id.textView);
        timestampText = (TextView) view.findViewById(R.id.timestampText);
        reloudButton = (ImageButton) view.findViewById(R.id.reloudButton);
        userImage = (ImageView) view.findViewById(R.id.userImage);

        if(msg.GetUsername().charAt(0) == 'j') userImage.setImageResource(R.drawable.face1);
        else if(msg.GetUsername().charAt(0) == 'A') userImage.setImageResource(R.drawable.face2);
        else if(msg.GetUsername().charAt(0) == 'v') userImage.setImageResource(R.drawable.face3);
        else userImage.setImageResource(R.drawable.face1);

        RoundPalanca(userImage);

        this.msg = msg;

        if(msg.GetUsername().equals(SettingsManager.GetUsername()))
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

        final Feed fthis = this;

        view.setOnTouchListener(
            new View.OnTouchListener() {
            private int padding = 0;
            private int initialx = 0;
            private int currentx = 0;

            public boolean onTouch(View v, MotionEvent event)
            {
                int action = event.getAction() & MotionEvent.ACTION_MASK;
                if ( action == MotionEvent.ACTION_DOWN)
                {
                    padding = 0;
                    initialx = (int) event.getX();
                    currentx = (int) event.getX();
                }
                if ( action == MotionEvent.ACTION_MOVE )
                {
                    currentx = (int) event.getX();
                    padding = currentx - initialx;
                    if(v.getParent() != null && padding > 5)
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                }

                if ( action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL)
                {
                    padding = 0;
                    initialx = 0;
                    currentx = 0;
                    view.setAlpha( 1.0f );
                    if(v.getParent() != null)
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                }
                    if(padding > view.getWidth() * 0.7f)
                    {
                        ((FeedListAdapter) FeedFragment.feedListView.getAdapter()).DeleteFeed(fthis);
                    }
                    else if (padding >= 0)
                    {
                        v.setPadding(padding, v.getPaddingTop(), -padding, v.getPaddingBottom());
                        view.setAlpha( 1.0f - (float)(view.getPaddingLeft())/(float)(view.getWidth()) );
                    }

                return true;
            }
        });
    }

    public void RoundPalanca(ImageView iv)
    {
        Bitmap bitmap = ((BitmapDrawable) iv.getDrawable()).getBitmap();
        Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        paint.setStrokeWidth(5);
        Canvas c = new Canvas(circleBitmap);
        c.drawCircle(bitmap.getWidth()/2, bitmap.getHeight()/2, bitmap.getWidth()/2, paint);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        paint.setAntiAlias(true);
        paint.setShader(shader);
        c.drawCircle(bitmap.getWidth()/2, bitmap.getHeight()/2, bitmap.getWidth()/2-2, paint);
        iv.setImageBitmap(circleBitmap);
    }

    public View GetView()
    {
        textView.setText( msg.GetText().toString() );
        usernameTextView.setText( Html.fromHtml(msg.GetUsername()).toString() );

        String timestamp = msg.GetTimestamp();
        int indexOfDot = timestamp.indexOf(".");
        if(indexOfDot >= 0) timestamp = timestamp.substring(0, indexOfDot);
        timestampText.setText( timestamp );

        return view;
    }

    public void Reloud()
    {
        try {
            JSONArray jsonArray = new JSONArray();
            ArrayList<String> macList = WifiDirectBroadcastReceiver.GetMACList();
            for (int i = 0; i < macList.size(); i++)
                jsonArray.put(macList.get(i));

            //jsonArray.put(SettingsManager.GetMAC()); //aqui no añadimos la localMAC

            JSONObject obj = new JSONObject();
            obj.put(HistoryManager.IdTag, msg.GetId());
            obj.put("macs", jsonArray);
            MainActivity.Log("RELOUD SEND: " + obj.toString());

            ConManager.SendPost(ReloudURL, obj,
                    Class.forName("com.example.sephirot47.loudspeaker.Feed").getDeclaredMethod("OnReloudSuccess", String.class, JSONObject.class),
                    Class.forName("com.example.sephirot47.loudspeaker.Feed").getDeclaredMethod("OnReloudFailed", String.class, JSONObject.class));
        }
        catch (JSONException e) { e.printStackTrace(); }
        catch (ClassNotFoundException e) { e.printStackTrace(); }
        catch (NoSuchMethodException e) { e.printStackTrace(); }
        MainActivity.Log("ASDASSAD");
    }

    public static void OnReloudSuccess(String response, JSONObject jsonObj){ MainActivity.Log("RELOUD RESP: " + response);}
    public static void OnReloudFailed(String response, JSONObject jsonObj){}
}
