package com.example.rahul.wificam;

import android.app.Activity;
import android.os.Bundle;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.view.Menu;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;


/**
 * Created by Rahul on 3/6/2016.
 */
public class DetialsActivity extends AppCompatActivity {


    public static String IPAddressKey = "IP";
    public static String PortNumberKey = "PORT";
    public static String CameraChoiceKey = "CAMERACHOICE";// default is back.
    public  enum CameraChoices{

        FRONT("FRONT"), BACK("BACK");
        private String value;
        private CameraChoices(String value){
            this.value = value;
        }
    }

    private EditText serverIpAddressEditText;
    private EditText serverPortNumberEditText;
    private Button  startStreaming;
    public ListView cameraChoiceListView;
    public int  selectedCameraOption;

    @Override
    public  boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


     @Override
    public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.layout_entry);
            serverIpAddressEditText = (EditText)this.findViewById(R.id.serverIpAddress);
            serverPortNumberEditText = (EditText)this.findViewById(R.id.serverPortNumber);

            String[] choices = new String[]{"FRONT", "BACK"};
            final ArrayAdapter<String>  cameraChoiceAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,
                            android.R.id.text1, choices);
            cameraChoiceListView = (ListView)this.findViewById(R.id.camera_choice_list_view);
            cameraChoiceListView.setAdapter(cameraChoiceAdapter);
            cameraChoiceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String cameraOptionSelected = (String)cameraChoiceListView.getItemAtPosition(position);
                    if(cameraOptionSelected == CameraChoices.FRONT.value){
                        selectedCameraOption = 1;

                    }
                    else{
                        selectedCameraOption = 0;
                    }
                }
            });

            startStreaming = (Button)this.findViewById(R.id.startStreamingBtn);
            startStreaming.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                    Intent streamingVideoIntent = new Intent(getBaseContext(), MainActivity.class);
                    streamingVideoIntent.putExtra(IPAddressKey, serverIpAddressEditText.getText());
                    streamingVideoIntent.putExtra(PortNumberKey, Integer.parseInt(serverPortNumberEditText.getText().toString()));
                    streamingVideoIntent.putExtra(CameraChoiceKey, selectedCameraOption);
                    startActivity(streamingVideoIntent);
                    finish();   // finish this activity.

                }
            });

         String[] cameraOptions = new String[]{"Front", "Back"};

     }

}
