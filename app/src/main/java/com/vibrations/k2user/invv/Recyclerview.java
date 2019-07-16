package com.vibrations.k2user.invv;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import java.util.ArrayList;

public class Recyclerview extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerviewLayoutManager;
    ArrayList<VideoModel> arrayListVideos;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycle_view);
        init();
    }

    private void init()
    {
        recyclerView = (RecyclerView)findViewById(R.id.recyclerviewVideo);
        //int num_columns = 2;
        recyclerviewLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(recyclerviewLayoutManager);


        arrayListVideos = new ArrayList<>();
        fetchVideosFromGallery();
    }

    private void fetchVideosFromGallery()
    {
        Uri uri;
        Cursor cursor;
        int column_index_data,column_index_folder_name,column_id,thum;

        String absolutePathImage = null;

        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;



        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Thumbnails.DATA};

        String orderBy = MediaStore.Images.Media.DATE_TAKEN;

        cursor = getApplicationContext().getContentResolver().query(uri,projection,null,null,orderBy +" DESC");

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.BUCKET_DISPLAY_NAME);
        column_id = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID);
        thum = cursor.getColumnIndexOrThrow(MediaStore.Video.Thumbnails.DATA);

        while(cursor.moveToNext())
        {
            absolutePathImage = cursor.getString(column_index_data);

            VideoModel videoModel = new VideoModel();

            videoModel.setBoolean_selected(false);
            videoModel.setStr_path(absolutePathImage);
            videoModel.setStr_thunb(cursor.getString(thum));

            arrayListVideos.add(videoModel);

        }

        VideoAdapter videoAdapter = new VideoAdapter(getApplicationContext(),arrayListVideos,Recyclerview.this);
        recyclerView.setAdapter(videoAdapter);
    }
}
