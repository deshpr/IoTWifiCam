package com.example.rahul.wificam;

import android.os.Environment;
import android.support.annotation.MainThread;
import android.view.SurfaceView;
import android.view.SurfaceHolder;
import android.hardware.Camera;
import android.content.Context;
import android.util.*;
import android.hardware.Camera.PreviewCallback;
import android.graphics.Rect;
import  android.util.Base64;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import android.graphics.ImageFormat;
import android.graphics.YuvImage;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;


/**
 * Created by Rahul on 3/4/2016.
 */
public class AndroidCameraView extends SurfaceView  implements SurfaceHolder.Callback {


    public SurfaceHolder myHolder;
    public Camera myCamera;

public InetAddress  serverAddress;


    public String  encodedImageToSend = "";

    public AndroidCameraView(Context context, Camera  camera){
        super(context);
        this.myCamera = camera;
        Log.d(MainActivity.TAG, "the camera is = " + camera.toString());
        this.myHolder =getHolder();
        this.myHolder.addCallback(this);
        this.myHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        try{

        }catch(Exception ex){
            Log.d(MainActivity.TAG, ex.toString());
        }


    }

    public void surfaceCreated(SurfaceHolder holder){
        if(myCamera == null){
            Log.d(MainActivity.TAG, "You passed a null camera");
            return;
        }
        try{
            myCamera.setPreviewDisplay(holder);
            Log.d(MainActivity.TAG, "Starting the preview");
            myCamera.startPreview();
            Log.d(MainActivity.TAG, "Preview has started");
        }catch(java.io.IOException ex){
            Log.d(MainActivity.TAG, ex.toString());
        }
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder){

    }




    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h){
        if(this.myCamera == null)
            return;
        if(this.myHolder.getSurface() == null){
            return;
        }
        try{
            this.myCamera.stopPreview();


        }catch(Exception ex){

        }
        try{

            this.myCamera.setPreviewDisplay(this.myHolder);
            this.myCamera.startPreview();

        }catch(Exception ex){
            Log.d(MainActivity.TAG, "Exception in AndroidView before start preview");
            Log.d(MainActivity.TAG, ex.toString());
        }



        this.myCamera.setPreviewCallback(new PreviewCallback(){
            @Override
            public void onPreviewFrame(byte[] data, Camera camera){
                Camera.Parameters  parameters =  myCamera.getParameters();
//                int  previewHeight = 480;
//                int previewWidth = 640;
//               parameters.setPreviewSize(previewWidth, previewHeight);
//                myCamera.setParameters(parameters);
                int height = parameters.getPreviewSize().height;
                int width = parameters.getPreviewSize().width;
              //  Log.d(MainActivity.TAG, "Width = " + width);
              //  Log.d(MainActivity.TAG, "Height = " + height);
                YuvImage  yuvImage = new YuvImage(data, ImageFormat.NV21, width, height, null);

                Rect rectangle = new Rect(0,0,width, height);

//                Bitmap image = BitmapFactory.decodeByteArray(data,0, data.length);


                File mediaFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if(mediaFile == null){
                    Log.d(MainActivity.TAG, "Could not make the media file, see storage");
                }
                try{

                    FileOutputStream  fos = new FileOutputStream(mediaFile, false);// overwrite the file.
           //         boolean written = image.compress(Bitmap.CompressFormat.JPEG,20, fos);
                    boolean writeData = yuvImage.compressToJpeg(rectangle,80, fos);
                    fos.close();
                    BufferedInputStream buf = new BufferedInputStream(new FileInputStream(mediaFile));
                    byte[] bytes  = new byte[(int)mediaFile.length()];
                    buf.read(bytes, 0, bytes.length);
                    buf.close();
          //          Log.d(MainActivity.TAG, String.valueOf(bytes.length));
                 //   Log.d(MainActivity.TAG, bytes.toString());
                    String result= Base64.encodeToString(bytes, Base64.NO_WRAP);
                    encodedImageToSend =  result;
//                    Log.d(MainActivity.TAG,  result);
                    // now read all the bytes from the image.
            //        Log.d(MainActivity.TAG, "Saveed the file successfully");

                     new Thread(new ClientThread()).start();
                }catch(Exception ex){
                  Log.d(MainActivity.TAG, "Could not save the file");
                  Log.d(MainActivity.TAG, ex.getMessage());
                }
            }
        });
    }




public static int MEDIA_TYPE_IMAGE = 1;
    public File getOutputMediaFile(int fileType){
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory(), "WifiCam");
        if(!mediaStorageDir.exists()){
            if(!mediaStorageDir.mkdirs()){
                Log.d(MainActivity.TAG, "Could not make the directory");
                return null;
            }
        }
        File mediaFile;
        if(fileType == MEDIA_TYPE_IMAGE){
            String path = mediaStorageDir.getPath() + File.separator + "IMGrocksFinal.jpg";
            mediaFile = new File(path);
           // Log.d(MainActivity.TAG, "File path = " +  path);
            if(mediaFile.exists()){
       //         Log.d(MainActivity.TAG, "The file exists");
                mediaFile.delete();
         //       Log.d(MainActivity.TAG, "The file is deleted");
                mediaFile = new File(path);
            }

        }
        else{
            return null;
        }
        return mediaFile;
    }
    public static  String SERVER_ADDR = "192.168.1.3";

    public static Socket clientSocket = null;

    public static  int PORT = 1069;
    public java.io.PrintWriter  out;

    public String makeStringToSend(int sizeLength, String stringToSend){
        //  first 16 bytes(characters) represent the size of the image to  send.
        StringBuffer buffer = new StringBuffer();
        int sizeStringLength = String.valueOf(sizeLength).length();

        for(int i= 0; i < (16-sizeStringLength); i++ ){
            buffer.append(" ");
        }
        buffer.append(String.valueOf(sizeLength));
        buffer.append(".");
        buffer.append(stringToSend);
        return buffer.toString();
    }


    boolean setConnection = false;
    int count = 0;
   class ClientThread  implements Runnable {
        @Override
        public void run() {
            try{

                if(!setConnection){
                    serverAddress =  InetAddress.getByName(SERVER_ADDR);
                    clientSocket = new Socket(serverAddress, PORT);
                //    out =  new java.io.PrintWriter(clientSocket.getOutputStream(), true);
                    Log.d(MainActivity.TAG, "Created the connection man!");
                    setConnection  =  true;
                    out =  new java.io.PrintWriter(clientSocket.getOutputStream(), true);
                }
                ++count;
                if(count%2 == 0){

                    Log.d(MainActivity.TAG, "Sending the string now...");
                    String toSend =   encodedImageToSend;
                    int length = toSend.length();
                    Log.d(MainActivity.TAG, "Sending a size of = " + length);
          //            out.println(toSend);
                    String result = makeStringToSend(length, toSend);
               //               Log.d(MainActivity.TAG, "Sending this string");
                    Log.d(MainActivity.TAG, "Sending the string = " + result);
                    out.println(result);
                 //   out.flush();
   //                 out.close();
                }
        //        out.close();

       //         clientSocket.close();
//                out.println("This is text from me....");
                //      logTextView.setText("the thread has sent text!");
            }

            catch(Exception ex){
                Log.d(MainActivity.TAG, "Exception inside the thread");
                Log.d(MainActivity.TAG, ex.toString());
            }
        }
    }
}


