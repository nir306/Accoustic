package com.vibrations.k2user.invv;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class iNVV extends AppCompatActivity {

    private Button accoutic, vibration, video;
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invv);
        final Context context = this;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null || user.isAnonymous()){
            Intent intent = new Intent(context,MainActivity.class);
            startActivity(intent);
        }
        accoutic = (Button) findViewById(R.id.button);
        vibration = (Button) findViewById(R.id.button2);
        video = (Button) findViewById(R.id.button3);


        boolean permission_flag = CheckPermissions();
        if(permission_flag != true)
        {
            Log.d("Accoutic","False");
            RequestPermissions();
        }else{
            Log.d("Accoutic", "TRUE");
        }

        //String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Log.d("Anand", "Refreshed token: " + refreshedToken);


        accoutic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Accoutic.class);
                startActivity(intent);
            }
        });

        vibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Vibration.class);
                startActivity(intent);
            }
        });

        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, Video.class);
                startActivity(intent);
            }
        });

    }

    public boolean CheckPermissions() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        return (result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED);
    }

    private void RequestPermissions() {
        ActivityCompat.requestPermissions(iNVV.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }
}
