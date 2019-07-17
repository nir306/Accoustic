package com.vibrations.k2user.invv;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;

import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;


public class Video extends AppCompatActivity {

    private Button mRecordBtn, mPlayBtn, mSelectBtn;
    private static final int videoPlayReqCode = 100;
    private static final int selectVideoREQ = 200;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerviewLayoutManager;
    ArrayList<VideoModel> arrayListVideos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video);
        final Context context = this;

        mRecordBtn = (Button) findViewById(R.id.recordButton);
        mSelectBtn = (Button) findViewById(R.id.selectButton);

        mRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playVideoIntent = new Intent();
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                playVideoIntent.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
//                File  mediaFile = new File(Environment.getExternalStorageDirectory()
//                        .getAbsolutePath() + "/myvideo.mp4");
//
//
//
//                Uri videouri = Uri.fromFile(mediaFile);
//                playVideoIntent.putExtra(MediaStore.EXTRA_OUTPUT, videouri);
                startActivityForResult(playVideoIntent,videoPlayReqCode);
            }
        });

        mSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Recyclerview.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode==videoPlayReqCode && resultCode==RESULT_OK){
            Uri videoUri = data.getData();
            //mVideoView.setVideoURI(videoUri);
        }
    }
}