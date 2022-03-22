package com.dbright.SP;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class pay_option extends AppCompatActivity {
    WebView webView;
    private ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> uploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;
    public TextToSpeech text_to_speech;
    boolean ttsIsInitialized = false;
    String dText = " ";
    Handler h1 = new Handler();
    @SuppressLint({"SetJavaScriptEnabled", "ObsoleteSdkInt"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_pay_option);
        setContentView(R.layout.payment_web);
        //
        text_to_speech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS){
                int result = text_to_speech.setLanguage(Locale.US);

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

        //
        webView = findViewById(R.id.paymentOpt);
        //webView = new WebView( context );
        WebSettings webSetting = webView.getSettings();
        webSetting.setBuiltInZoomControls(false);
        webView.getSettings().setAppCacheMaxSize( 5 * 1024 * 1024 ); // 5MB
        webView.getSettings().setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
        webView.getSettings().setAllowFileAccess( true );
        webView.getSettings().setAppCacheEnabled( true );
        webView.getSettings().setJavaScriptEnabled( true );
        webSetting.setMediaPlaybackRequiresUserGesture(false);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        webSetting.setAllowContentAccess(true);
        webSetting.setLoadsImagesAutomatically(true);
        webSetting.getDatabaseEnabled();
        webSetting.getDomStorageEnabled();
        webSetting.setAllowFileAccessFromFileURLs(true);

        webView.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT );
        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");

        if (Build.VERSION.SDK_INT < 18) {
            //speed webview
            webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
        }
        webView.loadUrl("file:///android_asset/index.html");
        webView.setWebChromeClient(new WebChromeClient(){
            // For 3.0+ Devices (Start)
            // onActivityResult attached before constructor
            protected void openFileChooser(ValueCallback uploadMsg, String acceptType)
            {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Browser"), FILECHOOSER_RESULTCODE);
            }


            // For Lollipop 5.0+ Devices
            public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams)
            {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }

                uploadMessage = filePathCallback;

                Intent intent = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    intent = fileChooserParams.createIntent();
                }
                try
                {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e)
                {
                    uploadMessage = null;
                    DDisplay("Cannot open file chooser");
                    return false;
                }
                return true;
            }

            //For Android 4.1 only
            protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture)
            {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "File Browser"), FILECHOOSER_RESULTCODE);
            }

            protected void openFileChooser(ValueCallback<Uri> uploadMsg)
            {
                mUploadMessage = uploadMsg;
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("image/*");
                startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
            }

        });
        webView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Toast.makeText(getApplicationContext(),"Loading",Toast.LENGTH_SHORT).show();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Toast.makeText(getApplicationContext(),"Please you might need to pay to continue to use the application", Toast.LENGTH_SHORT).show();
                super.onPageFinished(view, url);
            }


        });
    }
    String key = "2i000luv11u22";
    MainActivity ma = new MainActivity();
    String ckey[] = key.split("");
    String gdi[] = ma.getDeviceID().split("");
    String fkey = "";
    public class WebAppInterface {
        Context mContext;
        /** Instantiate the interface and set the context */
        WebAppInterface(Context c) {
            mContext = c;
        }
        Boolean paid = false;
        pay_option tts = new pay_option();
        /** Show a toast from the web page */
        @JavascriptInterface
        public void showToast(String toast) {

            if(toast.equals(key)){

                paid = true;

                for(int dc = 0; dc < ckey.length; dc ++){
                    //
                    fkey = fkey + ckey[dc] + gdi[dc];

                }
                DDisplay(fkey);
                DDisplay("Thank you for purchasing the application");
                Log.e(TAG, "private: "+fkey );
                String dkey = String.valueOf(fkey);
                try{
                    //This part of code, is to show when the application has expired
                    File dexpired = new File(getApplicationContext().getFilesDir(), "free.db");
                    FileOutputStream stream = null;
                    try {
                        stream = new FileOutputStream(dexpired);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        if (stream != null) {
                            //DDisplay("Hello user " + ma.getDeviceID());

                            stream.write(dkey.getBytes());
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
                        //setContentView(R.layout.activity_main2);
                    }

                }catch(Exception e){
                    Log.e(TAG, "showToast: "+ e );
                }
                new Thread(
                        new Runnable() {
                            @Override
                            public void run() {
                                Intent snd_f = new Intent(pay_option.this, MainActivity.class);
                                startActivity(snd_f);
                            }
                        }
                ).start();

            }else{
                DDisplay("Please try again");
            }
            Log.e("Testing", "Itz working");
        }

        @JavascriptInterface
        public void Speak(String text){
            DDisplay("Reading");
            Log.e(TAG, "Speak: "+ text );
            DTalk(text);
        }

    }

    public void FDTTS(){
        Intent dtts = new Intent(pay_option.this, FDTTS.class);
        startActivity(dtts);
    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            // Use MainActivity.RESULT_OK if you're implementing WebView inside Fragment
            // Use RESULT_OK only if you're implementing WebView inside an Activity
            Uri result = intent == null || resultCode != MainActivity.RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else
            Toast.makeText(this.getApplicationContext(), "Failed to Upload Image", Toast.LENGTH_LONG).show();
    }

    public void DTalk(final String text) {
        if (ttsIsInitialized){
            text_to_speech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }else{
            Log.e(TAG, "DTalk: Something went wrong while converting text to speech");
        }
    }
    public void DDisplay(String text) {
        try{
            Context context = getApplicationContext();

            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }catch(Exception ignored){ }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void reg_paid(){

    };

}