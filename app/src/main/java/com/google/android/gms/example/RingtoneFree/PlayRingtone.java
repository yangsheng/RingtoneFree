package com.google.android.gms.example.RingtoneFree;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PlayRingtone extends AppCompatActivity implements AbsListView.OnScrollListener{

    private boolean lastItemVisibleFlag = false;
    private int page = 0;
    private final int OFFSET = 20;
    private ProgressBar progressBar;
    private boolean mLockListView = false;
    private ListView listView;
    private ProductListAdapter adapter;

    private List<Product> mProductList;
    private MediaPlayer mediaPlayer;

    private static final String PREF_KEY_LAUNCH_TIMES = "android_rate_app_launch_times";
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ringtonelist);

//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//            String value = extras.getString("rate_flag");
//            if (value == "true")
//                rate_flag = true ;
//            //The key argument here must match that used in the other activity
//        }

        //Add Admob Banner
        MobileAds.initialize(this, "ca-app-pub-2464025963119043~3354768417");

        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-2464025963119043/5168335548");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        listView = (ListView)findViewById(R.id.listview_product);
        mProductList =  new ArrayList<>();
        progressBar = (ProgressBar) findViewById(R.id.progressbar);

        mediaPlayer = new MediaPlayer();
        //Add sample data for list
        //Can Get data from DB

        mProductList.add(new Product(1, "1"));
        mProductList.add(new Product(1, "2"));
        mProductList.add(new Product(1, "3"));
        mProductList.add(new Product(1, "4"));
        mProductList.add(new Product(1, "5"));
        mProductList.add(new Product(1, "6"));
        mProductList.add(new Product(1, "7"));
        mProductList.add(new Product(1, "8"));
        mProductList.add(new Product(1, "9"));
        mProductList.add(new Product(1, "10"));
        mProductList.add(new Product(1, "11"));
        mProductList.add(new Product(1, "12"));
        mProductList.add(new Product(1, "13"));

        //add Adapter
        adapter =  new ProductListAdapter(getApplicationContext(), mProductList);
        listView.setAdapter(adapter);

        progressBar.setVisibility(View.GONE);

        listView.setOnScrollListener(this);
        getItem();

        // when you click the items, make code here
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(),"this is test", Toast.LENGTH_LONG).show();

                if(mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
//                    try {
//                        mediaPlayer.prepare();
//                    }catch (IOException e){
//                        e.printStackTrace();
//                    }
                }
                Uri uri = Uri.parse("http://104.248.46.168/alarm/");
                String url = uri.toString() + "alarm(" + id + ").mp3"; // your URL here
                Log.d("This is Listview Click", "catch exception");

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                try{
                    mediaPlayer.setDataSource(url);
                    mediaPlayer.prepare(); // might take long! (for buffering, etc)
                }catch (IOException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"No More Files",Toast.LENGTH_LONG).show();
                }

                mediaPlayer.start();
            }
        });

    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {

        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag && mLockListView == false) {

            progressBar.setVisibility(View.VISIBLE);
            getItem();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
    }

    private void getItem(){

        mLockListView = true;

        for(int i = 0; i < 20; i++){
            int label = ((page * OFFSET) + i +14);
            String label_index = String.valueOf(label);
            mProductList.add(new Product(1, label_index));
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                page++;
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                mLockListView = false;
            }
        },1000);
    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        mediaPlayer.release();
//    }


    public void withRatingBar() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        builder.setTitle("Rate This App!");
        View dialogLayout = inflater.inflate(R.layout.alert_ratingbar_layout, null);
        final RatingBar ratingBar = dialogLayout.findViewById(R.id.ratingBar);
        builder.setView(dialogLayout);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Toast.makeText(getApplicationContext(), "Rating is " + ratingBar.getRating(), Toast.LENGTH_SHORT).show();
                // if user taps 1,2,3 stars, go to email writer
                if(ratingBar.getRating()<= 3.0){
                    sendEmail();

                }
                //if user taps 4,5 stars, go to Play Store
                else {
                    openMarket();
                }
            }
        });
        builder.setNegativeButton("NOT NOW", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }


//      This is Show Rate me alertDialog
    @Override
    protected void onStart(){
        super.onStart();
        setLaunchTimes(this);

        if(getLaunchTimes(this)== 3 ){
            withRatingBar();
        }

//       initRateApp(this, 0, 2, 2, "Rate Us~~", "Rate This App", "OK", "Cancel", "maybe");

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        mediaPlayer.release();
    }


    private static SharedPreferences.Editor getPreferencesEditor(Context context) {
        return getPreferences(context).edit();
    }

    private static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
    }

    private static void setLaunchTimes(Context context) {
        SharedPreferences.Editor editor = getPreferencesEditor(context);
        editor.putInt(PREF_KEY_LAUNCH_TIMES, getLaunchTimes(context) + 1);
        editor.apply();
    }

    private static int getLaunchTimes(Context context) {
        return getPreferences(context).getInt(PREF_KEY_LAUNCH_TIMES, 0);
    }

    private void openMarket() {
        final String appPackageName = this.getPackageName();
        try {
            this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    private void sendEmail() {
        final Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("text/email");
        //emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{supportEmail});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App Report (" + this.getPackageName() + ")");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
        this.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

}

