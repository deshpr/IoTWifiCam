package com.example.rahul.wificam;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.service.media.CameraPrewarmService;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.hardware.Camera.PictureCallback;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.view.Menu;

import java.io.File;
import java.io.FileOutputStream;
import  java.net.*;
import java.net.InetAddress;
import android.util.Log;
import android.hardware.Camera;
import android.widget.FrameLayout;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    public static String TAG=  "CAMERAAPP";

    public static  String SERVER_ADDR = "";
    public static  int PORT =  1069;    // a default port.

    public AndroidCameraView myCameraPreview;

    public Camera currentCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Intent  launchedBy = getIntent();
        SERVER_ADDR = launchedBy.getStringExtra(DetialsActivity.IPAddressKey);
        PORT  = launchedBy.getIntExtra(DetialsActivity.PortNumberKey, 1069);
        int selectedCameraOption =  launchedBy.getIntExtra(DetialsActivity.CameraChoiceKey, 0);

        currentCamera = getCameraInstance(selectedCameraOption);
        Log.d(TAG, currentCamera.toString());
        FrameLayout  previewLayout = (FrameLayout)this.findViewById(R.id.camera_preview);
        myCameraPreview = new AndroidCameraView(this, currentCamera);
        previewLayout.addView(myCameraPreview);
            }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public static Camera getCameraInstance(int selectedCameraOption){
        Camera camera = null;
        // open the camera.
        try{
            camera = Camera.open(selectedCameraOption);

        }catch(Exception ex){
            Log.d(TAG, "There was an exception when opening the camera");
        }
        Log.d(TAG, "Created the camera  :) ");

        return camera;
    }
}
