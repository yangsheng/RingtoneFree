
package com.google.android.gms.example.RingtoneFree;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    String File_Name ;
    String Save_Path;

    ProgressBar loadingBar;
    DownloadThread dThread;

    //if 0=Ringtone, 1=Notification, 2=Alarm
    public int ringtone_type = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Intent intent = getIntent();

        int file_id = intent.getExtras().getInt("File_Id");
        File_Name = "alarm(" + file_id + ").mp3";

        ImageView bun_back = (ImageView) findViewById(R.id.btn_back);
        ImageView img1 = (ImageView) findViewById(R.id.img_set_ringtone);
        ImageView img2 = (ImageView) findViewById(R.id.img_set_notification);
        ImageView img3 = (ImageView) findViewById(R.id.img_set_alarm);

        RingtoneManager ringtoneManager;

        bun_back.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(SettingsActivity.this,PlayRingtone.class);
                intent.putExtra("rate_flag","true");
                SettingsActivity.this.startActivity(intent);
            }
        });

        img1.setOnClickListener(this);

        img2.setOnClickListener(this);

        img3.setOnClickListener(this);

        loadingBar = (ProgressBar) findViewById(R.id.loading);

        // Set the download path to theSD Card.
        String ext = Environment.getExternalStorageState();
        if (ext.equals(Environment.MEDIA_MOUNTED)) {
            Save_Path = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + "/Ringtones/Downloads";
        }else{
            Toast.makeText(this, "NO SD card",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        // TODO Auto-generated method stub
        if (view.getId() == R.id.img_set_ringtone)
            ringtone_type = 0;
        else if (view.getId() == R.id.img_set_notification)
            ringtone_type = 1;
        else if (view.getId() == R.id.img_set_alarm)
            ringtone_type = 2;
        if (view.getId() != R.id.btn_back) {
            File dir = new File(Save_Path);
            // if folder does not exist, make new folder
            if (!dir.exists()) {
                Toast.makeText(this,"No SD card~", Toast.LENGTH_LONG).show();
                dir.mkdir();
            }

            // Check download folder, if the same file exists
            // if not, download it , if yes, call setRingtone function
            if (new File(Save_Path + "/" + File_Name).exists() == false) {
                loadingBar.setVisibility(View.VISIBLE);
                Toast.makeText(this,"Downloading...", Toast.LENGTH_LONG).show();
//                dThread = new DownloadThread(fileURL + "/" + File_Name,
//                        Save_Path + "/" + File_Name);
                dThread = new DownloadThread("http://104.248.46.168/alarm/" + File_Name, Save_Path+"/" + File_Name);
                dThread.start();
            }else{
                mAfterDown.sendEmptyMessage(0);
            }
        }
    }

    // Download Thread
    class DownloadThread extends Thread {
        String ServerUrl;
        String LocalPath;

        DownloadThread(String serverPath, String localPath) {
            ServerUrl = serverPath;
            LocalPath = localPath;
        }

        @Override
        public void run() {
            URL imgurl;
            int Read;
            try {
                imgurl = new URL(ServerUrl);
                HttpURLConnection conn = (HttpURLConnection) imgurl
                        .openConnection();
                int len = conn.getContentLength();
                byte[] tmpByte = new byte[len];

                InputStream is = conn.getInputStream();
                File file = new File(LocalPath);
                FileOutputStream fos = new FileOutputStream(file);
                for (;;) {
                    Read = is.read(tmpByte);
                    if (Read <= 0) {
                        break;
                    }
                    fos.write(tmpByte, 0, Read);
                }
                is.close();
                fos.close();
                conn.disconnect();

            } catch (MalformedURLException e) {
                Log.e("ERROR1", e.getMessage());
            } catch (IOException e) {
                Log.e("ERROR2", e.getMessage());
                e.printStackTrace();
            }
            mAfterDown.sendEmptyMessage(0);
        }
    }

    Handler mAfterDown = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            //Toast.makeText(getApplicationContext(), "Finished Downloading.",Toast.LENGTH_LONG).show();
            loadingBar.setVisibility(View.GONE);
            Uri m_path = Uri.parse(Save_Path + "/" + File_Name);
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(Settings.System.canWrite(getApplicationContext())){
                        if(ringtone_type == 0){
                            RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(),RingtoneManager.TYPE_RINGTONE, m_path);
                        }else if(ringtone_type == 1){
                            RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(),RingtoneManager.TYPE_NOTIFICATION, m_path);
                        }else {
                            RingtoneManager.setActualDefaultRingtoneUri(getApplicationContext(),RingtoneManager.TYPE_ALARM, m_path);
                        }
                    }else{
                        Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
                Toast.makeText(getApplicationContext(), "Ringtone setted.",Toast.LENGTH_LONG).show();
            } catch (Throwable t) {
                Log.d("This is ", "catch exception");
            }
        }

    };


}

