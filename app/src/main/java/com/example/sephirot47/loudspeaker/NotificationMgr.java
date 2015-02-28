package com.example.sephirot47.loudspeaker;

import android.content.Intent;
import android.app.PendingIntent;
import android.support.v4.app.TaskStackBuilder;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat.Builder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

public abstract class NotificationMgr
{
    private static int notificationId = 47;

    private static final int Priority = 5;

    private static final String Title = "Loudspeaker";
    private static final String MessageFirst = "You have ";
    private static final String MessageLastOne = " new message.";
    private static final String MessageLast = " new messages.";

    public static void ShowOrUpdate(Context context, int numMessages)
    {
        NotificationCompat.Builder notBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setPriority(Priority)
                        .setContentTitle(Title)
                        .setContentText(MessageFirst + numMessages + (numMessages == 1 ? MessageLastOne : MessageLast))
                        .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS);

        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notBuilder.setSound(uri);

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingInt = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notBuilder.setContentIntent(pendingInt);
        NotificationManager notManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notManager.notify(notificationId, notBuilder.build());
    }

    public static void Clear(Context context)
    {
        NotificationManager notManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notManager.cancel(notificationId);
    }
}
