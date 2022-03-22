package com.dbright.SP;

import static com.google.android.play.core.install.model.ActivityResult.RESULT_IN_APP_UPDATE_FAILED;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

// This application is on free trial
public class MainActivity extends AppCompatActivity {
    String TAG = "Testing something";
    ImageView ml;


    // To enable auto updating
    private AppUpdateManager mAppUpdateManager;
    private int RC_APP_UPDATE = 999;
    private int inAppUpdateType;
    private com.google.android.play.core.tasks.Task<AppUpdateInfo> appUpdateInfoTask;
    private InstallStateUpdatedListener installStateUpdatedListener;
    // Ending variable


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //coding
        // Creates instance of the manager.
        mAppUpdateManager = AppUpdateManagerFactory.create(this);
        // Returns an intent object that you use to check for an update.
        appUpdateInfoTask = mAppUpdateManager.getAppUpdateInfo();
        //lambda operation used for below listener
        //For flexible update
        installStateUpdatedListener = installState -> {
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate();
            }
        };
        mAppUpdateManager.registerListener(installStateUpdatedListener);

        //coding
        Log.e(TAG, "onCreate: Ok now you can start project");
        Configuration config = getResources().getConfiguration();
        try {
            Class configClass = config.getClass();
            configClass.getField("SEM_DESKTOP_MODE_ENABLED").getInt(configClass);
            configClass.getField("semDesktopModeEnabled").getInt(config);// Samsung DeX mode enabled
        } catch (NoSuchFieldException | IllegalAccessException | IllegalArgumentException e) {
            // Device does not support Samsung DeX
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
    public String getDeviceID(){
        String devIDShort = "45" + //we make this look like a valid IMEI
                Build.BOARD.length()%10+ Build.BRAND.length()%10 +
                Build.CPU_ABI.length()%10 + Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 + Build.HOST.length()%10 +
                Build.ID.length()%10 + Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 + Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 + Build.TYPE.length()%10 +
                Build.USER.length()%10 ; //13 digits

        return  devIDShort;
    }
    public String getCurrentDateAndTime(){
        Date c = Calendar.getInstance().getTime();
        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        String formattedDate = simpleDateFormat.format(c);

        return formattedDate;
    }
    // to enable auto update

    // to enable auto update
    @Override
    protected void onStart() {
        super.onStart();
        //To enable auto updating

        //For Immediate
        inAppUpdateType = AppUpdateType.IMMEDIATE; //1
        inAppUpdate();

        //To enable auto updating
        ml = findViewById(R.id.main_logo);
        Handler h1 = new Handler();
        float f = 0;

        try{
            f = Float.parseFloat(getDeviceID());
            f = 10 * (f/22112000);
            //DDisplay("Our value of ID is "+ f);
        }catch(Exception e){
            DDisplay(String.valueOf(e));
        }
        float finalF = f;
        new Thread(
                () -> {
                    fadeOutAndHideImage(ml);

                    try {
                        Thread.sleep(5000);
                        Log.e(TAG, "run: changing layout");
                        h1.post(() -> {
                            File file = new File(getApplicationContext().getFilesDir(), "active.db");
                            if (file.exists()) {
                                Intent it1 = new Intent(MainActivity.this, second_frame.class);
                                try {
                                    startActivity(it1);
                                } catch (Exception e) {
                                    Log.e(TAG, "Itz like sth went wrong but here is the error " + e);
                                }
                            } else {
                                //Nothing
                                FileOutputStream stream = null;
                                try {
                                    stream = new FileOutputStream(file);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    if (stream != null) {
                                        stream.write("About US have been read by user's".getBytes());
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                } finally {
                                    try {
                                        assert stream != null;
                                        stream.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    //setContentView(R.layout.activity_main2);
                                }

                            }

                            //This part of code, is to show when the application has expired
                            File dexpired = new File(getApplicationContext().getFilesDir(), "free.db");
                            if (dexpired.exists()) {
                                Intent it1 = new Intent(MainActivity.this, second_frame.class);
                                try {
                                    startActivity(it1);
                                    // This means that 7 days freemium have been given
                                } catch (Exception e) {
                                    Log.e(TAG, "Itz like sth went wrong but here is the error " + e);
                                }
                            } else {
                                //Nothing
                                FileOutputStream stream = null;
                                try {
                                    stream = new FileOutputStream(dexpired);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    if (stream != null) {
                                        DDisplay("Hello user " + getDeviceID());
                                        String dkey = String.valueOf(finalF) + "-" + getCurrentDateAndTime();
                                        stream.write(dkey.getBytes());
                                    }
                                } catch (IOException e) {
                                    DDisplay(String.valueOf(e));
                                } finally {
                                    try {
                                        assert stream != null;
                                        stream.close();
                                        DDisplay("I can see you are new to the application, if you have any question");
                                        DDisplay("based on the usage of the application feel free ");
                                        DDisplay("to check the tutorial video on how to use the application on youtube");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    setContentView(R.layout.activity_main2);
                                }

                            }


                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
        ).start();


    }

    private void fadeOutAndHideImage(final ImageView img) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(3000);

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationEnd(Animation animation) {
                img.setVisibility(View.GONE);
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationStart(Animation animation) {
            }
        });

        img.startAnimation(fadeOut);
    }

    public void dcont(View v) {
        Intent it1 = new Intent(this, second_frame.class);
        try {
            startActivity(it1);
        } catch (Exception e) {
            Log.e(TAG, "dcont: Itz like sth went wrong but here is the error " + e);
        }

    }
    //newly added
    @Override
    protected void onResume() {
        try {
            mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
                if (appUpdateInfo.updateAvailability() ==
                        UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                    // If an in-app update is already running, resume the update.
                    try {
                        mAppUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                inAppUpdateType,
                                this,
                                RC_APP_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            });


            mAppUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
                //For flexible update
                if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                    popupSnackbarForCompleteUpdate();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_APP_UPDATE) {
            //when user clicks update button
            if (resultCode == RESULT_OK) {
                Toast.makeText(MainActivity.this, "App download starts...", Toast.LENGTH_LONG).show();
            } else if (resultCode != RESULT_CANCELED) {
                //if you want to request the update again just call checkUpdate()
                Toast.makeText(MainActivity.this, "App download canceled.", Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_IN_APP_UPDATE_FAILED) {
                Toast.makeText(MainActivity.this, "App download failed.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void inAppUpdate() {

        try {
            // Checks that the platform will allow the specified type of update.
            appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
                @Override
                public void onSuccess(AppUpdateInfo appUpdateInfo) {
                    if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                            // For a flexible update, use AppUpdateType.FLEXIBLE
                            && appUpdateInfo.isUpdateTypeAllowed(inAppUpdateType)) {
                        // Request the update.

                        try {
                            mAppUpdateManager.startUpdateFlowForResult(
                                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                    appUpdateInfo,
                                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                    inAppUpdateType,
                                    // The current activity making the update request.
                                    MainActivity.this,
                                    // Include a request code to later monitor this update request.
                                    RC_APP_UPDATE);
                        } catch (IntentSender.SendIntentException ignored) {

                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void popupSnackbarForCompleteUpdate() {
        try {
            Snackbar snackbar =
                    Snackbar.make(
                            findViewById(R.id.important),
                            "An update has just been downloaded.\nRestart to update",
                            Snackbar.LENGTH_INDEFINITE);

            snackbar.setAction("INSTALL", view -> {
                if (mAppUpdateManager != null){
                    mAppUpdateManager.completeUpdate();
                }
            });
            //snackbar.setActionTextColor(getResources().getColor(R.color.install_color));
            snackbar.show();

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {
        mAppUpdateManager.unregisterListener(installStateUpdatedListener);
        super.onDestroy();
    }
    //end
}