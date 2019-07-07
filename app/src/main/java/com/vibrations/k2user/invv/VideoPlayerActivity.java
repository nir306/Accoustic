 package com.vibrations.k2user.invv;

 import android.os.Bundle;
 import android.os.Handler;
 import android.support.v7.app.AppCompatActivity;
 import android.view.View;
 import android.widget.ImageView;
 import android.widget.SeekBar;
 import android.widget.VideoView;

 public class VideoPlayerActivity extends AppCompatActivity {

    VideoView videoView;
    ImageView imageView;
    SeekBar seekBar;
    String str_video_url;
    boolean isPlay = false;
    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        init();


    }

     private void init()
     {
         videoView = (VideoView)findViewById(R.id.videoView);
         imageView =  (ImageView)findViewById(R.id.toggleButton);
         seekBar = (SeekBar)findViewById(R.id.seekBar);
         str_video_url = getIntent().getStringExtra("video");
         videoView.setVideoPath(str_video_url);
         handler = new Handler();
         videoView.start();
         isPlay = true;
         imageView.setImageResource(R.drawable.pausebutton);
         updateseekbar();


     }

     private void updateseekbar()
     {
       handler.postDelayed(updateTimeTask,100);
     }

     public Runnable updateTimeTask = new Runnable() {
         @Override
         public void run() {
             seekBar.setProgress(videoView.getCurrentPosition());
             seekBar.setMax(videoView.getDuration());
             handler.postDelayed(this,100);

             seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                 @Override
                 public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                 }

                 @Override
                 public void onStartTrackingTouch(SeekBar seekBar) {
                     handler.removeCallbacks(updateTimeTask);

                 }

                 @Override
                 public void onStopTrackingTouch(SeekBar seekBar) {
                     handler.removeCallbacks(updateTimeTask);
                     videoView.seekTo(seekBar.getProgress());
                     updateseekbar();



                 }
             });
         }
     };

    public void toggle_method(View v)
    {
        if(isPlay)
        {
            videoView.pause();
            isPlay = false;
            imageView.setImageResource(R.drawable.playbutton);

        }

        else if(isPlay == false)
        {
            videoView.start();
            updateseekbar();
            isPlay = true;
            imageView.setImageResource(R.drawable.pausebutton);

        }
    }

 }
