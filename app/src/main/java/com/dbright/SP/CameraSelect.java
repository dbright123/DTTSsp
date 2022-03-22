package com.dbright.SP;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraSelect extends AppCompatActivity {

    String TAG = "Testing";

    final private int CAMERA = 2;
    Bitmap bitmap;
    ImageView img_preview;
    ImageButton scan_img;
    TextView pw ;
    String dText;
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_slideshow);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        img_preview = (ImageView) findViewById(R.id.img_preview);
        scan_img = (ImageButton) findViewById(R.id.scan_img);
        pw = (TextView) findViewById(R.id.pw);
        scan_img.setOnClickListener(
                v -> {
                    try{
                        takePhotoFromCamera();

                    }catch(Exception ignored){

                    }

                }
        );
    }

    @Override
    public void onBackPressed() {
        Intent m = new Intent(this, second_frame.class);
        startActivity(m);
    }
    public void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == CAMERA) {
            bitmap = (Bitmap) data.getExtras().get("data");
            try{

                img_preview.setImageBitmap(bitmap);
                dText = cImage(toGrayscale(bitmap));
                pw.setVisibility(View.VISIBLE);
                Log.e(TAG, "onActivityResult: Text are "+ dText );


                Log.e(TAG, "onActivityResult: " + dText);

                if(dText.isEmpty() || dText.equals(" ")){
                    DDisplay("Please try again");
                }else{
                    Intent read = new Intent(this,second_frame.class);
                    read.putExtra("dText",dText);
                    startActivity(read);
                }

            }catch(Exception ignored){
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                    //we are connected to a network
                    takePhotoFromCamera();
                    pw.setVisibility(View.VISIBLE);

                    Log.e(TAG, "onActivityResult: " + dText);

                    if(dText.isEmpty() || dText.equals(" ")){
                        DDisplay("Please try again");
                    }else{
                        Intent read = new Intent(this,second_frame.class);
                        read.putExtra("dText",dText);
                        startActivity(read);
                    }

                }else{
                    DDisplay("Please check your internet connection");
                }
            }

            Log.e(TAG, "onActivityResult: Itx working 2");
        }
    }
    public String cImage(Bitmap bitmap){
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();


        Frame imageFrame = new Frame.Builder()

                .setBitmap(bitmap)                 // your image bitmap
                .build();

        StringBuilder imageText = new StringBuilder();


        SparseArray<TextBlock> textBlocks = textRecognizer.detect(imageFrame);

        for (int i = 0; i < textBlocks.size(); i++) {
            TextBlock textBlock = textBlocks.get(textBlocks.keyAt(i));
            imageText.append(textBlock.getValue());
            imageText.append("\n");
            // return string
        }
        return imageText.toString();

    }

    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }


    //ending new coding
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

    public void DDisplay(String text) {
        try{
            Context context = getApplicationContext();

            int duration = Toast.LENGTH_LONG;

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }catch(Exception ignored){

        }

    }

}