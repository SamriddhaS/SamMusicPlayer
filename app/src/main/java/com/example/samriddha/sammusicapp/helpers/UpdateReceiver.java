package com.example.samriddha.sammusicapp.helpers;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.widget.RemoteViews;

import com.example.samriddha.sammusicapp.MainActivity;
import com.example.samriddha.sammusicapp.R;
import com.example.samriddha.sammusicapp.SongService;

import static android.content.Context.AUDIO_SERVICE;

public class UpdateReceiver extends AppWidgetProvider {

    public static final String ACTION_PLAY = "com.example.samriddha.sammusicapp.ACTION_PLAY";
    public static final String ACTION_NEXT = "com.example.samriddha.sammusicapp.ACTION_NEXT";
    public static final String ACTION_PREV = "com.example.samriddha.sammusicapp.ACTION_PREV";
    public static final String ACTION_CLOSE = "com.example.samriddha.sammusicapp.ACTION_CLOSE";
    public static boolean staticBool;
    private static final String WIDGET_CLICK = "WIDGET_CLICK";

    @Override
    public void onUpdate(Context mContext, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        //super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int widgetId : appWidgetIds) {
            // create some random data
            RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                    R.layout.widget_layout);
            remoteViews.setTextViewText(R.id.songTitle, String.valueOf("Hi...Don't think too much. \n This is just a test widget!"));

            // Register an onClickListener
            Intent intent = new Intent(mContext, UpdateReceiver.class);

            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext,
                    0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.songTitle, pendingIntent);
            appWidgetManager.updateAppWidget(widgetId, remoteViews);

        }
    }

//    public void  update(Context mContext, AppWidgetManager appWidgetManager, ComponentName thisWidget, int[] appWidgetIds){
//
//
//    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        boolean isPlaying = SongService.isPlaying();
        switch (action) {
            case ACTION_PLAY:
                if(isPlaying){
                    PlaybackManager.isManuallyPaused = true;
                }
                PlaybackManager.playPauseEvent(false, isPlaying, true, SongService.getCurrPos());
                break;
            case ACTION_NEXT:
                PlaybackManager.playNext(MainActivity.suffelflag);
                break;
            case ACTION_PREV:
                PlaybackManager.playPrev(MainActivity.suffelflag);
                break;
            case ACTION_CLOSE:
                NotificationHandler.getInstance(context).onServiceDestroy();
                PlaybackManager.stopService();
                MainActivity.mainActivity.finish();
                break;
            case AppWidgetManager.ACTION_APPWIDGET_UPDATE:
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context.getApplicationContext());
                ComponentName thisWidget = new ComponentName(context.getApplicationContext(), UpdateReceiver.class);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
                if (appWidgetIds != null && appWidgetIds.length > 0) {
//                    update(context, appWidgetManager, thisWidget, appWidgetIds);
                }
                break;
            case Intent.ACTION_HEADSET_PLUG:
                //noinspection deprecaRandom Playervition
                if (context != null && !staticBool && isPlaying)
                    if (!((AudioManager) context.getSystemService(AUDIO_SERVICE)).isWiredHeadsetOn()) {
                        PlaybackManager.playPauseEvent(true, true, false, SongService.getCurrPos());
                    }
                staticBool = false;
                break;
        }
    }

}
