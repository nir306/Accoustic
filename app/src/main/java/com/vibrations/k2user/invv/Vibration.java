package com.vibrations.k2user.invv;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Vibration extends AppCompatActivity implements SensorEventListener {

    //private static final android.util.Log Log =;
    private TextView xText, yText, zText;
    private Button btnRec,btnStp,btnUpld;
    private Sensor mySensor;
    private SensorManager SM;
    private FileWriter writer;
    private int STORAGE_PERMISSION_CODE = 1;
    // private int REQUEST_CODE = 9999;
    boolean isRunning;
    final String TAG = "SensorLog";
    private File outRnmFile, folder ;
    private String fileParNm;
    private FirebaseStorage storage;
    private StorageReference storageRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vibration);

        isRunning = false;

        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();

        xText = (TextView)findViewById(R.id.xText);
        yText = (TextView)findViewById(R.id.yText);
        zText = (TextView)findViewById(R.id.zText);

        btnStp = (Button)findViewById(R.id.BtnStp);
        //btnExt = (Button) findViewById(R.id.BtnExt);
        btnRec = (Button)findViewById(R.id.BtnRec);
        btnUpld =(Button)findViewById(R.id.BtnUpld);

        SM= (SensorManager)getSystemService(SENSOR_SERVICE);

        mySensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        final EditText fileName = new EditText(Vibration.this);

        checkPermissions();

        btnRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnRec.setEnabled(false);
                btnStp.setEnabled(true);
                Log.d(TAG, "Writing to " + getStorageDir());

                SM.registerListener(Vibration.this,mySensor, SensorManager.SENSOR_DELAY_GAME);


                try {
                    writer = new FileWriter(new File(getStorageDir(), new StringBuilder().append("_OutFile").append(".csv").toString()));
                    fileParNm = new StringBuilder().append("_OutFile").append(".csv").toString();
                    //writer = new FileWriter(new File(getStorageDir(), "Sensor_" + System.currentTimeMillis() + ".csv"));
                    writer.write(String.format("TimeStamp, X, Y, Z, RMS\n"));

                } catch (IOException e) {
                    e.printStackTrace();
                }

                isRunning = true;

            }
        });

        btnStp.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(final View view) {
                btnRec.setEnabled(true);
                btnStp.setEnabled(false);
                SM.flush(Vibration.this);
                SM.unregisterListener(Vibration.this);

                new AlertDialog.Builder(Vibration.this)
                        .setTitle("SAVE FILE")
                        .setMessage("Save the Recorded File?")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);


                                new AlertDialog.Builder(Vibration.this)
                                        .setTitle("FILE NAME")
                                        .setMessage("Give The File Name")
                                        .setView(fileName)
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                try {
                                                    writer.close();
                                                    File outFile = new File(getStorageDir(),fileParNm);
                                                    outRnmFile = new File(getStorageDir(),new StringBuilder().append(fileName.getText()).append(fileParNm).toString());
                                                    outFile.renameTo(outRnmFile);
                                                    outFile.getClass();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }

                                                Toast.makeText(Vibration.this, "File Saved Successfully", Toast.LENGTH_SHORT).show();
                                                if(fileName.getParent()!=null)
                                                    ((ViewGroup)fileName.getParent()).removeView(fileName); // <- fix
                                                //alert.addView(fileName);
                                            }
                                        }).create().show();

                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                try {
                                    writer.flush();
                                    File outFile = new File(getStorageDir(),fileParNm);
                                    outFile.delete();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        })
                        .create().show();
            }
        });



        btnUpld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fileChooser();
            }
        });

    }

    public void fileChooser(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        intent.setType("*/*");

        startActivityForResult(intent, 10);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Toast.makeText(getApplicationContext(), "line no 266", Toast.LENGTH_SHORT).show();

        if (resultCode == RESULT_OK && requestCode == 10 && data !=null && data.getData() !=null ) {

            try {
                Uri file = data.getData();
                File actualFile = new File(file.getPath());
                //Uri file = Uri.fromFile(file2);
                final ProgressDialog progressDialog = new ProgressDialog(this);
                progressDialog.setTitle("Uploading...");
                progressDialog.show();
                StorageReference riversRef = storageRef.child("vibration/"+actualFile.getName());
                UploadTask uploadTask = riversRef.putFile(file);
                //Toast.makeText(getApplicationContext(), "Uploading Vibration File", Toast.LENGTH_LONG).show();
                btnUpld.setEnabled(false);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                        Log.v("Failed", "not uploaded");

                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.v("passed", "uploaded");
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Upload success", Toast.LENGTH_LONG).show();
                        btnUpld.setEnabled(true);
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress =(100.0 * taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage(((int) progress)+" % Uploaded...");
                    }
                });

            } catch (Exception e) {
            }

        }

    }

    private File getStorageDir() {

        folder= new File(Environment.getExternalStorageDirectory().toString()+"/Vibrometer");
        if(!folder.exists())
        {
            folder.mkdir();
        }

        return folder;
    }

    public void checkPermissions()
    {
        if (ContextCompat.checkSelfPermission(Vibration.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            //Toast.makeText(MainActivity.this,"Permisson Already Available",Toast.LENGTH_SHORT).show();
        }
        else
        {
            requestStoragePermission();
        }
    }

    private void requestStoragePermission()
    {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE))
        {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("Permission is needed For Storing the file")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(Vibration.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }
        else
        {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_CODE)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                //Toast.makeText(this,"Recording Started",Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        double x = event.values[0];
        double y = event.values[1];
        double z = event.values[2];
        double rms = Math.sqrt((Math.pow(x,2))+ (Math.pow(y,2))+ (Math.pow(z,2)));
        xText.setText("X: " + x);
        yText.setText("Y: " + y);
        zText.setText("Z: " + z);
        //rmsText.setText("RMS: " + rms);
        if(isRunning) {
            try {
                writer.write(String.format("%d, %f, %f,%f, %f\n", event.timestamp, x, y, z,rms));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //nothing
    }
}

