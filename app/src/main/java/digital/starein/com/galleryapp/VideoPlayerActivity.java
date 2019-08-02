package digital.starein.com.galleryapp;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class VideoPlayerActivity extends AppCompatActivity implements View.OnClickListener{
    public static ArrayList<String> videoLocations;
    public static int currentVideoIndex=0;
    // MediaController mediaController;
    ImageView lpButton,lockButton,playButton,prevButton,nextButton;
    SeekBar seekBar;
    VideoView videoView;
    Boolean isHidden,isLocked,isDisturbed,isPlayin,isPortrait;
    LinearLayout bottomControls,topControls;
    public static int stopPos,maxLimit;
    TextView textView,totalVideoTime,currentVideoTime;String videoLoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        lpButton=findViewById(R.id.landscape_portrait);
        lockButton=findViewById(R.id.lock_button);
        playButton=findViewById(R.id.play_button_small);
        prevButton=findViewById(R.id.prev_button);
        nextButton=findViewById(R.id.next_button);
        textView=findViewById(R.id.video_name_text);
        totalVideoTime=findViewById(R.id.total_video_time);
        currentVideoTime=findViewById(R.id.current_video_time);

        seekBar=findViewById(R.id.seekbar_in_videoPlayer);
        bottomControls=findViewById(R.id.down_controls);
        topControls=findViewById(R.id.top_controls);
        videoView=findViewById(R.id.videoView_in_player);

        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        nextButton.setOnClickListener(this);
        prevButton.setOnClickListener(this);
        lpButton.setOnClickListener(this);
        playButton.setOnClickListener(this);


        isPlayin=true;
        isHidden=false;
        isLocked=false;
        isDisturbed=false;

        prepareTheVideoMan();
        autoHide();
    }


    void prepareTheVideoMan(){


        SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String temp= saved_values.getString("isPortrait","f");
        if (temp.equals("f"))
            checkVideoOrientation();
        else isPortrait = temp.equalsIgnoreCase("p");

        videoLoc=videoLocations.get(currentVideoIndex);
        videoView.setVideoPath(videoLoc);
        videoView.setOnClickListener(this);
        lockButton.setOnClickListener(this);
        textView.setText(videoLoc.substring(videoLoc.lastIndexOf("/")+1));

        if (isPlayin)
            playButton.setImageResource(R.drawable.ic_pause_black_24dp);
        else
            playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);

        if (isLocked)
            hideControls(true);

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                seekBar.setMax(mp.getDuration()/1000);

                SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                int locc = saved_values.getInt("locc", 0);

                if (isPlayin)
                    videoView.start();
                videoView.seekTo(locc);



                int millis=videoView.getDuration();

                String hms;
                if (millis>3600000)
                    hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
                else
                    hms = String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                            TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));

                totalVideoTime.setText(hms);
                updateTimes();

            }
        });
        // mediaController.setAnchorView(videoView);
        // videoView.setMediaController(mediaController);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    videoView.seekTo(progress*1000);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            }
        });
    }

    void checkVideoOrientation(){
        MediaPlayer mpp=new MediaPlayer();

        try {
            mpp.setDataSource(videoLocations.get(currentVideoIndex));
            mpp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        int videoHeight=mpp.getVideoHeight();
        int videoWidth=mpp.getVideoWidth();

        if (videoHeight>videoWidth) {
            isPortrait = true;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else {
            isPortrait = false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId()==R.id.prev_button) {
            if(currentVideoIndex>0) {
                currentVideoIndex--;
                prepareTheVideoMan();
            }
            else
                Toast.makeText(getApplicationContext(),"First Video in Album", Toast.LENGTH_SHORT).show();
        }
        if (v.getId()==R.id.next_button) {
            if (currentVideoIndex<maxLimit-1) {
                currentVideoIndex++;
                prepareTheVideoMan();
            }
            else
                Toast.makeText(getApplicationContext(),"Last Video in Album",Toast.LENGTH_SHORT).show();
        }
        if (v.getId()==R.id.lock_button){
            if (!isLocked) {
                isLocked = true;
                lockButton.setImageResource(R.drawable.ic_lock_outline_black_24dp);
                hideControls(true);
            }
            else {
                isLocked = false;
                lockButton.setImageResource(R.drawable.ic_lock_open_black_24dp);
                showControls(true);
            }
        }
        if (v.getId()==R.id.landscape_portrait) {

            SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            SharedPreferences.Editor editor=saved_values.edit();

            if (!isPortrait) {
                editor.putString("isPortrait","p");
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                isPortrait = true;
            } else {
                editor.putString("isPortrait","l");
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                isPortrait = false;
            }
            editor.apply();
        }
        if (v.getId()==R.id.play_button_small){
            if(videoView.isPlaying()){
                isPlayin=false;
                videoView.pause();
                playButton.setImageResource(R.drawable.ic_play_arrow_black_24dp);
            }

            else{
                videoView.start();
                isPlayin=true;
                playButton.setImageResource(R.drawable.ic_pause_black_24dp);
            }
        }
        if (v.getId()==R.id.videoView_in_player){
            isDisturbed=true;
            if (!isLocked) {
                if ((!isHidden)&&(videoView.isPlaying())) {
                    hideControls(false);
                }
                else {
                    showControls(false);
                    autoHide();
                }
            }
        }
    }

    void hideControls(final Boolean flag){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                bottomControls.animate().translationY(500).setDuration(200);
                topControls.animate().translationY(-500).setDuration(200);
                lpButton.setVisibility(View.INVISIBLE);
                if (!flag)
                    lockButton.setVisibility(View.INVISIBLE);
                //playButton.animate().alpha(0);

                playButton.setVisibility(View.INVISIBLE);
                nextButton.setVisibility(View.INVISIBLE);
                prevButton.setVisibility(View.INVISIBLE);
                isHidden = true;

                autoHide();
            }
        }, 100);
    }

    void autoHide(){
        new CountDownTimer(4000,4000){

            @Override
            public void onTick(long millisUntilFinished) {}

            @Override
            public void onFinish() {
                if ((!isDisturbed)&&(!isHidden)&&(videoView.isPlaying()))
                    hideControls(false);
                else
                    isDisturbed=false;
            }
        }.start();
    }
    void showControls(final Boolean flag){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                isHidden = false;
                bottomControls.animate().translationY(0).setDuration(500);
                topControls.animate().translationY(0).setDuration(500);
                lpButton.setVisibility(View.VISIBLE);
                if (!flag)
                    lockButton.setVisibility(View.VISIBLE);
                playButton.animate().alpha(1);

                playButton.setVisibility(View.VISIBLE);
                prevButton.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.VISIBLE);
            }
        }, 100);
    }

    void updateTimes()
    {
        final Handler mHandler = new Handler();
//Make sure you update Seekbar on UI thread
        this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(videoView != null){
                    int millis = videoView.getCurrentPosition() ;

                    String hms;
                    if (millis>3600000)
                        hms = String.format("%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
                    else
                        hms = String.format("%02d:%02d",
                                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));

                    currentVideoTime.setText(hms);
                    seekBar.setProgress(millis/1000);
                }
                mHandler.postDelayed(this, 10);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor=saved_values.edit();
        editor.putInt("locc",videoView.getCurrentPosition());

        Log.d("myTest",String.valueOf(isPlayin));
        editor.apply();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation== Configuration.ORIENTATION_LANDSCAPE)
            Log.d("myTest","changed");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences saved_values = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor=saved_values.edit();
        editor.putInt("locc",0);
        editor.apply();
    }
}
