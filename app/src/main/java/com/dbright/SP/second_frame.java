package com.dbright.SP;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.dbright.SP.databinding.ActivitySecondFrameBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.vorlonsoft.android.rate.AppRate;
import com.vorlonsoft.android.rate.StoreType;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

public class second_frame extends AppCompatActivity {
    String TAG = "About us";
    private AppBarConfiguration mAppBarConfiguration;
    public TextToSpeech text_to_speech;
    boolean ttsIsInitialized = false;
    String dText = " ";

    // Variable ontop
    @SuppressLint("SourceLockedOrientationActivity")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.dbright.SP.databinding.ActivitySecondFrameBinding binding = ActivitySecondFrameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Configuration config = getResources().getConfiguration();
        try {
            Class configClass = config.getClass();
            configClass.getField("SEM_DESKTOP_MODE_ENABLED").getInt(configClass);
            configClass.getField("semDesktopModeEnabled").getInt(config);// Samsung DeX mode enabled
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
            // Device does not support Samsung DeX
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        try {
            Bundle bundle = getIntent().getExtras();
            dText = bundle.getString("dText");
            Log.e(TAG, "onCreate: Information has been gotten" + "\n "+ dText);
        }catch(Exception ignored){

        }

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); // to help fix FileUriExposedException error
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();

        // considering how many days for payment

        askRatings();

        // ending ///
        AppRate.with(this)
                .setStoreType(StoreType.GOOGLEPLAY) //default is GOOGLEPLAY (Google Play), other options are
                //           AMAZON (Amazon Appstore) and
                //           SAMSUNG (Samsung Galaxy Apps)
                .setInstallDays((byte) 0) // default 10, 0 means install day
                .setLaunchTimes((byte) 3) // default 10
                .setRemindInterval((byte) 2) // default 1
                .setRemindLaunchTimes((byte) 2) // default 1 (each launch)
                .setShowLaterButton(true) // default true
                .setDebug(false) // default false
                //Java 8+: .setOnClickButtonListener(which -> Log.d(MainActivity.class.getName(), Byte.toString(which)))
                .setOnClickButtonListener(which -> Log.d(second_frame.class.getName(), Byte.toString(which)))
                .monitor();

        if (AppRate.with(this).getStoreType() == StoreType.GOOGLEPLAY) {
            //Check that Google Play is available
            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) != ConnectionResult.SERVICE_MISSING) {
                // Show a dialog if meets conditions
                AppRate.showRateDialogIfMeetsConditions(this);
            }
        } else {
            // Show a dialog if meets conditions
            AppRate.showRateDialogIfMeetsConditions(this);
        }
        // end-->>



        setSupportActionBar(binding.appBarSecondFrame.toolbar);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA},
                1);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);


        text_to_speech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS){
                int result = text_to_speech.setLanguage(Locale.UK);

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                    Log.e("TTS", "Language not supported");
                    DDisplay("Language not supported");
                } else {
                    ttsIsInitialized = true; // flag tts as initialized
                }
            } else {
                Log.e("TTS", "Failed");
            }

        });
        try{
            EditText et1 = (EditText) findViewById(R.id.et1);
            ImageButton stop = (ImageButton) findViewById(R.id.stop);

            et1.setText(dText);
            stop.setOnClickListener(
                    view -> {
                        Snackbar.make(view, "Reading stopped", Snackbar.LENGTH_LONG) // still on target monitoring
                                .setAction("Action", null).show();
                        fab.setImageResource(R.drawable.play_button);
                        if(text_to_speech.isSpeaking()) text_to_speech.stop();

                    }
            );

            final boolean[] d_permit = {true};

            binding.appBarSecondFrame.fab.setOnClickListener(view -> {
                if(d_permit[0]){
                    fab.setImageResource(R.drawable.pause);// This part plays sound


                    Snackbar.make(view, "Reading started with "+ text_to_speech.getLanguage()+ " language", Snackbar.LENGTH_LONG) // still on target monitoring
                            .setAction("Action", null).show();

                    d_permit[0] = false;
                    if(et1.getText().toString().isEmpty() || et1.getText().toString().equals(" ")){
                        Snackbar.make(view, "Please enter a text to read", Snackbar.LENGTH_LONG) // still on target monitoring
                                .setAction("Action", null).show();
                        d_permit[0] = true;
                        fab.setImageResource(R.drawable.play_button);
                    }else{
                        try{
                            new Thread(
                                    ()->DTalk(et1.getText().toString())
                            ).start();


                        }catch (Exception e){
                            Log.e(TAG, "onClick: Not working "+ e);
                        }

                    }

                    // media plays
                }else{
                    if(text_to_speech.isSpeaking()){
                        try {
                            text_to_speech.wait();
                        } catch (InterruptedException e) {
                            DDisplay("Unable to pause voice");
                        }
                    }
                    fab.setImageResource(R.drawable.play_button);// This part pauses sounds
                    Snackbar.make(view, "Reading pause", Snackbar.LENGTH_LONG) // still on target monitoring
                            .setAction("Action", null).show();
                    d_permit[0] = true;


                }


            });
        }catch(Exception e){
            binding.appBarSecondFrame.fab.setOnClickListener(view -> {
                fab.setImageResource(R.drawable.play_button);// This part pauses sounds
                Snackbar.make(view, "Reading pause", Snackbar.LENGTH_LONG) // still on target monitoring
                        .setAction("Action", null).show();
            });
        }

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_second_frame);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        // Testing intents
        //show_Notification();// Still working on this


        //ending intents

        //TextView trial = (TextView) findViewById(R.id.trialVersion);
        File dexpired = new File(getApplicationContext().getFilesDir(), "free.db");
        if (dexpired.exists()) {
            StringBuilder text = new StringBuilder();

            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(dexpired));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            String line = "";

            while (true) {
                try {
                    assert br != null;
                    if ((line = br.readLine()) == null) break;
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                text.append(line);
                //text.append('\n');
            }
            try {
                br.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            Log.e(TAG, "onStart: "+text);
            //DDisplay(String.valueOf(text));

            //Checking if user's has paid for application
            // next target for 2morrow///
            String kkey = "2i000luv11u22";
            MainActivity ma = new MainActivity();
            String dckey[] = kkey.split("");
            String dgdi[] = ma.getDeviceID().split("");
            String ffkey = "";
            for(int dc = 0; dc < dckey.length; dc ++){
                //creating security shit!!!!
                ffkey = ffkey + dckey[dc] + dgdi[dc];
            }
            if(String.valueOf(text).equals(ffkey)){
                Log.e(TAG, "onCreate: Payment successfully done");
                DDisplay("Thank you very much for paying for the application");
            }else{
                String[] a1 = String.valueOf(text).split("-",-1);


                MainActivity main = new MainActivity();

                String cdate = main.getCurrentDateAndTime();
                String dInfo = main.getDeviceID();

                String[] a2 = cdate.split("/",-1);

                String[] a3 = a1[1].split("/", -1);

                // This is where we will run comparison check
                //DDisplay(a3[2]);
                int pday = Integer.parseInt(a3[2]);
                int pmonth = Integer.parseInt(a3[1]);
                int pyear = Integer.parseInt(a3[0]);
                int cday = Integer.parseInt(a2[2]);
                int cmonth = Integer.parseInt(a2[1]);
                int cyear = Integer.parseInt(a2[0]);
                float dkey = Float.parseFloat(a1[0]);

                //DDisplay(String.valueOf(cday));
                if(cday == pday){
                    DDisplay("Hello user "+ dInfo);
                }
                else if(cday > pday){
                    //DDisplay("The current value is " + String.valueOf(ckey(dkey, dInfo)));
                    modkey(ckey(dkey, dInfo),a1[1], dexpired);// attain difficulty currently on this line
                    //DDisplay("A day is gone !!!!");

                }else{
                    if(cmonth > pmonth){
                        //DDisplay("The current value is " + String.valueOf(ckey(dkey, dInfo)));
                        DDisplay("Hello user "+ dInfo + " Welcome back!!!!");
                        modkey(ckey(dkey, dInfo),a1[1], dexpired);
                    }
                    else{
                        if(cyear > pyear){
                            //DDisplay("The current value is " + String.valueOf(ckey(dkey, dInfo)));
                            DDisplay("Hello user "+ dInfo + " Welcome back!!!, I have really missed you");
                            modkey(ckey(dkey, dInfo),a1[1], dexpired);
                        }else{
                            if(dkey == 0){
                                DDisplay("Please purchase the main DTTS application ");
                                DDisplay("on either play store, Samsung store, or any android store");
                                Intent pOption = new Intent(this,pay_option.class);
                                startActivity(pOption);// please wait
                                // to be continued on the purchase method
                            }else{
                                DDisplay("Something is wrong with the application");
                                DDisplay("resetting application");
                                try{
                                    f = Float.parseFloat(dInfo);
                                    f = 2 * (f/22112000);
                                    modkey(f,ma.getCurrentDateAndTime(), dexpired);
                                    //DDisplay("Our value of ID is "+ f);
                                }catch(Exception e){
                                    DDisplay(String.valueOf(e));
                                }// This is where i stopped
                            }

                        }
                    }
                }

            }


        }else{
            DDisplay("Please reinstall application or permit application to read");
        }

    }
    // notification panel
    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)

    public void show_Notification(){

        Intent intent=new Intent(getApplicationContext(),second_frame.class);

        String CHANNEL_ID="DTTS";
        NotificationChannel notificationChannel=new NotificationChannel(CHANNEL_ID,"Payment notice",NotificationManager.IMPORTANCE_HIGH);
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),1,intent,0);
        Notification notification=new Notification.Builder(getApplicationContext(),CHANNEL_ID)
                .setContentText("I am so sorry but your trial is finished, you might need to buy the application")
                .setContentTitle("payment notice")
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.sym_action_chat,"Payment notice",pendingIntent)
                .setChannelId(CHANNEL_ID)
                .setSmallIcon(R.drawable.dtts_logo)
                .build();

        NotificationManager notificationManager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(notificationChannel);
        notificationManager.notify(1,notification);


    }
    //end notification

    public float f = 0;

    @SuppressLint("SetTextI18n")
    public float ckey(float dkey, String dInfo){

        int x = 0;
        for(x = 10; x >= 0 ; x --){
            try{
                f = Float.parseFloat(dInfo);
                f = x * (f/22112000);
                //DDisplay("Our value of ID is "+ f);
            }catch(Exception e){
                DDisplay(String.valueOf(e));
            }

            if(f == dkey){ // This is to know the current day remaining
                DDisplay(String.valueOf(x)+"days remaining !!!");
                f = (x - 1) * (f/22112000);
                break;
            }else{
                DDisplay("Please the application license key is fake, kindly purchase the application for a new license key");
                Intent pOption = new Intent(this,pay_option.class);
                startActivity(pOption);// please wait
                // Here is where i redirect for payment of application
                break;
            }
            //DDisplay(" Doesn't match ");
        }

        return f;
    }

    public void modkey(float f, String date, File dexpired){
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(dexpired);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            if (stream != null) {

                String mkey = String.valueOf(f + "-" + date);
                stream.write(mkey.getBytes());
            }
        } catch (IOException e) {
            DDisplay(String.valueOf(e));
        } finally {
            try {
                assert stream != null;
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart(){
        super.onStart();
        Log.e(TAG, "onStart: Itz have started");

    }
    @Override
    public void onRestart(){
        super.onRestart();
        Log.e(TAG, "onRestart: Itz have started");
        DDisplay("Welcome back !!");
    }

    @Override
    public void onPause(){
        super.onPause();
        if(text_to_speech.isSpeaking()) text_to_speech.stop();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NotNull String[] permissions, @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1)
            if (grantResults.length <= 0
                    || grantResults[0] != PackageManager.PERMISSION_GRANTED)
                DDisplay("Permission denied to read your External storage");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.second_frame, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_second_frame);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
    public int dcount1 = 1;
    @Override
    public void onBackPressed() {
        if(dcount1 >= 2){
            dcount1 = 1;
            this.moveTaskToBack(true);
        }else {
            dcount1++;
            DDisplay("Click back again to quit application");
            askRatings();
        }
    }

    public void DDisplay(String text) {
        try{
            Context context = getApplicationContext();

            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }catch(Exception ignored){

        }

    }


    public void dcont(MenuItem item) {
        setContentView(R.layout.activity_main2);
        DDisplay("More about the DTTS application");
    }

    public void DTalk(final String text) {
        if (ttsIsInitialized){
            text_to_speech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }else{
            Log.e(TAG, "DTalk: Something went wrong while converting text to speech");
        }
        text_to_speech.setOnUtteranceProgressListener(
                new UtteranceProgressListener() {
                    @Override
                    public void onStart(String s) {
                        DDisplay("Please wait !!!!!");
                        Log.e(TAG, "onStart: itz started" );
                    }

                    @Override
                    public void onDone(String s) {
                        DDisplay("Done processing");
                        Log.e(TAG, "onDone: Itx done processing");
                    }

                    @Override
                    public void onError(String s) {

                        DDisplay("Please contact the developer at micheal@dbright.org for fix and update");
                        Log.e(TAG, "onError: Please check over here");
                    }
                }
        );
        text_to_speech.setOnUtteranceCompletedListener(
                s -> {
                    Log.e(TAG, "onUtteranceCompleted: Done speaking!!!");
                    DDisplay("Done speaking");
                }
        );


    }


    void askRatings() {
        ReviewManager manager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = manager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();
                com.google.android.play.core.tasks.Task<Void> flow = manager.launchReviewFlow(this, reviewInfo);
                flow.addOnCompleteListener(task2 -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                    Log.e(TAG, "askRatings: The askRating function is done");
                });
            } else {
                // There was some problem, continue regardless of the result.
                Log.e(TAG, "askRatings: They is an unknown error detected");
            }
        });
    }



}