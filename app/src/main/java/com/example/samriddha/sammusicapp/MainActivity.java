package com.example.samriddha.sammusicapp;

import android.Manifest;
import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.samriddha.sammusicapp.helpers.AllSongListAdapter;
import com.example.samriddha.sammusicapp.helpers.BitmapPalette;
import com.example.samriddha.sammusicapp.helpers.NotificationHandler;
import com.example.samriddha.sammusicapp.helpers.PlayPauseView;
import com.example.samriddha.sammusicapp.helpers.PlaybackManager;
import com.example.samriddha.sammusicapp.SongService;

import java.util.Calendar;
import java.util.HashMap;

import static com.example.samriddha.sammusicapp.helpers.BitmapPalette.blurredBitmap;
import static com.example.samriddha.sammusicapp.helpers.BitmapPalette.mediumBitmap;
import static com.example.samriddha.sammusicapp.helpers.BitmapPalette.smallBitmap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

private static Context mContext;
public  static Activity mainActivity ;

public static final String SONG_TITLE = "songTitle";
public static final String DISPLAY_NAME = "displayName";
public static final String SONG_ID = "songID";
public static final String SONG_PATH = "songPath";
public static final String ALBUM_NAME = "albumName";
public static final String ARTIST_NAME = "artistName";
public static final String SONG_DURATION = "songDuration";
public static final String SONG_POS = "songPosInList";
public static final String SONG_PROGRESS = "songProgress";
private Toolbar toolbar;
private ListView lv_songslist;
private AllSongListAdapter mAllSongsListAdapter;
private com.sothree.slidinguppanel.SlidingUpPanelLayout mLayout;
private ImageView songAlbumbg, img_bottom_slideone, img_bottom_slidetwo,
        imgbtn_backward, imgbtn_forward, ivListBG;
private PlayPauseView btn_playpause, btn_playpausePanel;
private TextView txt_timeprogress, txt_timetotal, txt_playesongname,
        txt_songartistname, txt_playesongname_slidetoptwo, txt_songartistname_slidetoptwo;

private RelativeLayout slidepanelchildtwo_topviewone, slidepanelchildtwo_topviewtwo;
private LinearLayout llBottomLayout;
private boolean isExpand = false;
private SharedPreferences sharedPref;
private PlaybackManager playbackManager;
private SeekBar seekBar;
        Thread thread;
        ViewPager pager;
        //ViewPagerAdapter adapter;
        //SlidingTabLayout tabs;
        CharSequence Titles[] = {"All Songs", "Playlists", "Favourites"};
        int Numboftabs = 3;
public static boolean shouldContinue = false;
private static boolean sleepflag = true ;
private static CountDownTimer counttimer = null ;
public static boolean suffelflag = false ;

@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.activity_main);

//        adapter = new ViewPagerAdapter(getSupportFragmentManager(), Titles, Numboftabs);
//
//        // Assigning ViewPager View and setting the adapter
//        pager = (ViewPager) findViewById(R.id.pager);
//        pager.setAdapter(adapter);
//
//        // Assiging the Sliding Tab Layout View
//        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
//        tabs.setDistributeEvenly(true); // To make the Tabs Fixed set this true, This makes the tabs Space Evenly in Available width
//
//        // Setting Custom Color for the Scroll bar indicator of the Tab View
//        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
//            @Override
//            public int getIndicatorColor(int position) {
//                return getResources().getColor(R.color.tabsScrollColor);
//            }
//        });
//
//        // Setting the ViewPager For the SlidingTabsLayout
//        tabs.setViewPager(pager);
        init();
        toolbarStatusBar();
        initListeners();
        initPlaybackManager();


}

private void initPlaybackManager() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        playbackManager = PlaybackManager.getInstance(mContext);
        } else {
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        requestPermissions(new String[]{
        Manifest.permission.READ_EXTERNAL_STORAGE
        }, 0);
        else playbackManager = PlaybackManager.getInstance(mContext);
        }
        }

public void toolbarStatusBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);


        }

private void init() {
        lv_songslist = (ListView) findViewById(R.id.recycler_allSongs);
        mLayout = (com.sothree.slidinguppanel.SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        songAlbumbg = (ImageView) findViewById(R.id.image_songAlbumbg_mid);
        ivListBG = (ImageView) findViewById(R.id.iv_lvBG);
        img_bottom_slideone = (ImageView) findViewById(R.id.img_bottom_slideone);
        img_bottom_slidetwo = (ImageView) findViewById(R.id.img_bottom_slidetwo);

        llBottomLayout = (LinearLayout) findViewById(R.id.ll_bottom);
        txt_timeprogress = (TextView) findViewById(R.id.slidepanel_time_progress);
        txt_timetotal = (TextView) findViewById(R.id.slidepanel_time_total);
        imgbtn_backward = (ImageView) findViewById(R.id.btn_backward);
        imgbtn_forward = (ImageView) findViewById(R.id.btn_forward);

        btn_playpause = (PlayPauseView) findViewById(R.id.btn_play);
        btn_playpausePanel = (PlayPauseView) findViewById(R.id.bottombar_play);
        seekBar = (SeekBar) findViewById(R.id.seekbar);
        seekBar.setEnabled(false);
        btn_playpausePanel.Pause();
        btn_playpause.Pause();

        TypedValue typedvaluecoloraccent = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, typedvaluecoloraccent, true);

        txt_playesongname = (TextView) findViewById(R.id.txt_playesongname);
        txt_songartistname = (TextView) findViewById(R.id.txt_songartistname);
        txt_playesongname_slidetoptwo = (TextView) findViewById(R.id.txt_playesongname_slidetoptwo);
        txt_songartistname_slidetoptwo = (TextView) findViewById(R.id.txt_songartistname_slidetoptwo);

        slidepanelchildtwo_topviewone = (RelativeLayout) findViewById(R.id.slidepanelchildtwo_topviewone);
        slidepanelchildtwo_topviewtwo = (RelativeLayout) findViewById(R.id.slidepanelchildtwo_topviewtwo);

        slidepanelchildtwo_topviewone.setVisibility(View.VISIBLE);
        slidepanelchildtwo_topviewtwo.setVisibility(View.INVISIBLE);

        mainActivity = this ;

}

private void initListeners() {
        btn_playpausePanel.setOnClickListener(this);
        btn_playpause.setOnClickListener(this);
        imgbtn_backward.setOnClickListener(this);
        imgbtn_forward.setOnClickListener(this);

        slidepanelchildtwo_topviewone.setOnClickListener(new View.OnClickListener() {

@Override
public void onClick(View v) {
        mLayout.setPanelState(com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.EXPANDED);
        }
        });

        slidepanelchildtwo_topviewtwo.setOnClickListener(new View.OnClickListener() {

@Override
public void onClick(View v) {
        mLayout.setPanelState(com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
        });

        mLayout.addPanelSlideListener(new com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener() {
                @Override
                public void onPanelSlide(View view, float v) {

                        if (v == 0.0f) {
                                isExpand = false;
                                slidepanelchildtwo_topviewone.setVisibility(View.VISIBLE);
                                slidepanelchildtwo_topviewtwo.setVisibility(View.INVISIBLE);
                        } else if (v > 0.0f && v < 1.0f) {
                                if (isExpand) {
//                        slidepanelchildtwo_topviewone.setAlpha(1.0f);
//                        slidepanelchildtwo_topviewtwo.setAlpha(1.0f -
//                                slideOffset);
                                        slidepanelchildtwo_topviewone.setVisibility(View.VISIBLE);
                                        slidepanelchildtwo_topviewtwo.setVisibility(View.INVISIBLE);
                                } else {
//                        slidepanelchildtwo_topviewone.setAlpha(1.0f -
//                                slideOffset);
//                        slidepanelchildtwo_topviewtwo.setAlpha(1.0f);
                                        slidepanelchildtwo_topviewone.setVisibility(View.INVISIBLE);
                                        slidepanelchildtwo_topviewtwo.setVisibility(View.VISIBLE);
                                }
                        } else {
                                isExpand = true;
                                slidepanelchildtwo_topviewone.setVisibility(View.INVISIBLE);
                                slidepanelchildtwo_topviewtwo.setVisibility(View.VISIBLE);
                        }


                }

                @Override
                public void onPanelStateChanged(View view, com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState panelState, com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState panelState1) {

                }
        });


        lv_songslist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
@Override
public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String, String> hashMap = PlaybackManager.songsList.get(position);
//                loadSongInfo(hashMap, true);
        PlaybackManager.playSong(hashMap);
        btn_playpause.Play();
        btn_playpausePanel.Play();
        }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
@Override
public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser) {
        shouldContinue = false;
        PlaybackManager.playPauseEvent(false, false, false, progress);
        }
        }

@Override
public void onStartTrackingTouch(SeekBar seekBar) {

        }

@Override
public void onStopTrackingTouch(SeekBar seekBar) {
        PlaybackManager.showNotif(false);
        }
        });
        }

private Handler seekHandler = new Handler();
        Runnable runnable = new Runnable() {
@Override
public void run() {
        int currSeekPos = SongService.getCurrPos();
        int max = seekBar.getMax();
        if (currSeekPos > max) {
        currSeekPos = 0;
        }
        if (shouldContinue) {
        txt_timeprogress.setText(calculateDuration(currSeekPos));
        seekBar.setProgress(currSeekPos);
        seekHandler.postDelayed(runnable, 1000);
        }
        }
        };

public void setSeekProgress() {
        seekHandler.removeCallbacks(runnable);
        shouldContinue = true;
//        if (duration != seekBar.getMax())
//            seekBar.setMax(duration);
        if (!btn_playpause.isPlay()) {
        runOnUiThread(new Runnable() {
@Override
public void run() {
        setPlayPauseView(true);
        }
        });
        }
        runOnUiThread(runnable);
        }

public void setAllSongs() {
        mAllSongsListAdapter = new AllSongListAdapter(mContext, PlaybackManager.songsList);
        lv_songslist.setAdapter(mAllSongsListAdapter);
        }

@Override
public void onClick(View v) {

        switch (v.getId()) {
        case R.id.bottombar_play:
        if (PlaybackManager.playPauseEvent(false, SongService.isPlaying(), true, seekBar.getProgress())) {
//                    btn_playpause.Play();
//                    btn_playpausePanel.Play();
//                    thread = new Thread(runnable);
//                    thread.start();
        } else {
        PlaybackManager.isManuallyPaused = true;
        btn_playpause.Pause();
        btn_playpausePanel.Pause();
        shouldContinue = false;
        }
        break;

        case R.id.btn_play:
        if (PlaybackManager.playPauseEvent(false, SongService.isPlaying(), true, seekBar.getProgress())) {
//                    btn_playpause.Play();
//                    btn_playpausePanel.Play();
        } else {
        PlaybackManager.isManuallyPaused = true;
        btn_playpause.Pause();
        btn_playpausePanel.Pause();
        shouldContinue = false;
        }
        break;

        case R.id.btn_forward:
        playbackManager.playNext(suffelflag);
        break;

        case R.id.btn_backward:
        playbackManager.playPrev(suffelflag);
        break;

default:
        break;
        }

        }

public void loadSongInfo(HashMap<String, String> songDetail, boolean seeking) {
        String title = songDetail.get(SONG_TITLE);
        String artist = songDetail.get(ARTIST_NAME);
        String path = songDetail.get(SONG_PATH);
        int milliSecDuration = Integer.parseInt(songDetail.get(SONG_DURATION));
        String progress = songDetail.get(SONG_PROGRESS);
        int milliSecProgress = Integer.parseInt(progress == null ? "0" : progress);
        if (txt_playesongname != null) {
        txt_playesongname.setText(title);
        txt_playesongname_slidetoptwo.setText(title);
        txt_songartistname.setText(artist);
        txt_songartistname_slidetoptwo.setText(artist);
        txt_timetotal.setText(calculateDuration(milliSecDuration));
        seekBar.setMax(milliSecDuration);
        txt_timeprogress.setText(calculateDuration(milliSecProgress));
        seekBar.setEnabled(true);
        if (seeking) {
        setSeekProgress();
        } else {
        seekBar.setProgress(milliSecProgress);
        }
        }
        try {
        BitmapPalette.getColorsFromBitmap(mContext, path, false);
        } catch (RuntimeException e) {
        e.printStackTrace();
        }
//        updateProgress(songsManager);
        }

public void setBitmapColors() {
        if (songAlbumbg != null) {
        if (mediumBitmap != null) {
        ivListBG.setImageBitmap(blurredBitmap);
        songAlbumbg.setImageBitmap(mediumBitmap);
        img_bottom_slideone.setImageBitmap(smallBitmap);
        img_bottom_slidetwo.setImageBitmap(smallBitmap);
        toolbar.setBackgroundColor(BitmapPalette.darkVibrantRGBColor);
        toolbar.setTitleTextColor(BitmapPalette.darkVibrantTitleTextColor);
        slidepanelchildtwo_topviewone.setBackgroundColor(BitmapPalette.darkVibrantRGBColor);
        slidepanelchildtwo_topviewtwo.setBackgroundColor(BitmapPalette.darkVibrantRGBColor);
        txt_playesongname.setTextColor(BitmapPalette.darkVibrantBodyTextColor);
        txt_playesongname_slidetoptwo.setTextColor(BitmapPalette.darkVibrantBodyTextColor);
        txt_songartistname.setTextColor(BitmapPalette.darkVibrantBodyTextColor);
        txt_songartistname_slidetoptwo.setTextColor(BitmapPalette.darkVibrantBodyTextColor);
        txt_timetotal.setTextColor(BitmapPalette.darkVibrantBodyTextColor);
        txt_timeprogress.setTextColor(BitmapPalette.darkVibrantBodyTextColor);
        llBottomLayout.setBackgroundColor(BitmapPalette.darkVibrantRGBColor);
        } else {
        songAlbumbg.setImageResource(R.drawable.random);
        img_bottom_slideone.setImageResource(R.drawable.random);
        img_bottom_slidetwo.setImageResource(R.drawable.random);
        }
        }
        }


public String calculateDuration(int millisec) {
        long sec = millisec / 1000;
        return sec != 0 ? String.format("%d:%02d", sec / 60, sec % 60) : "0:00";
        }


@Override
public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
        case 0:
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        playbackManager = PlaybackManager.getInstance(mContext);
        break;
        }
        }

@Override
protected void onPause() {
        super.onPause();
        }

@Override
protected void onResume() {
        super.onResume();
        if (playbackManager != null) {
        loadSongInfo(PlaybackManager.getPlayingSongPref(), SongService.isPlaying());
        setPlayPauseView(SongService.isPlaying());
        } else initPlaybackManager();
        }

@Override
public void onBackPressed() {
        if (mLayout.getPanelState() == com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.EXPANDED) {
        mLayout.setPanelState(com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else if (SongService.isPlaying()) {
        super.onBackPressed();
        } else {
        super.onBackPressed();
        PlaybackManager.stopService();
        finish();
        }
        }

@Override
protected void onDestroy() {
        if(PlaybackManager.isServiceRunning && !SongService.isPlaying())
        PlaybackManager.stopService();
        super.onDestroy();
        }

public void setPlayPauseView(boolean isPlaying) {

        if (btn_playpause != null)
        if (isPlaying) {
        btn_playpause.Play();
        btn_playpausePanel.Play();
        } else {
        btn_playpause.Pause();
        btn_playpausePanel.Pause();
        shouldContinue = false;
        }
        }

@Override
public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);

        }

public void callCountDownTimer(boolean state,int minute){

    //init timer for auto sleep
    int countDownInterval = 1000;

    if (state) {
            counttimer = new CountDownTimer(minute * 60 * 1000, countDownInterval) {

            public void onFinish() {

                NotificationHandler.getInstance(mContext).onServiceDestroy();
                PlaybackManager.stopService();
                finish();
                sleepflag = true ;
            }

            public void onTick(long millisUntilFinished) {

            }};
            counttimer.start();
    }else {

        counttimer.cancel();
    }

        }

@Override
public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()){

                case R.id.sleeptimer_id :
                        if (sleepflag){

                                final Calendar mcurrentTime = Calendar.getInstance();
                                final int minute = mcurrentTime.get(Calendar.MINUTE);
                                TimePickerDialog mTimePicker;
                                mTimePicker = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                                        callCountDownTimer(sleepflag,selectedMinute);
                                        Toast.makeText(mContext, "Sleep Timer On. Music Will auto Off After "+selectedMinute+" Min", Toast.LENGTH_SHORT).show();
                                        item.setTitle("Sleep Timer Off");
                                        sleepflag = false ;


                                    }
                                },0, minute, true);//Yes 24 hour time
                                mTimePicker.setTitle("Select Sleep Time(In Minute's)");
                                mTimePicker.show();

                        }else {
                                callCountDownTimer(sleepflag,0);
                                Toast.makeText(mContext, "Sleep Timer Off.", Toast.LENGTH_SHORT).show();
                                item.setTitle("Sleep Timer On");
                                sleepflag = true ;
                        }
                        break;
                case R.id.suffel_id :

                        if (suffelflag) {
                                suffelflag = false;
                                Toast.makeText(mContext, "Music Suffel Off", Toast.LENGTH_SHORT).show();
                                item.setTitle("Suffel Music On");
                        }else {

                                suffelflag = true ;
                                Toast.makeText(mContext, "Music Suffel On", Toast.LENGTH_SHORT).show();
                                item.setTitle("Suffel Music Off");
                        }

                        break;


        }

        return super.onOptionsItemSelected(item);
        }
}

