package com.dbright.SP;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class gallarySelect extends AppCompatActivity {

    String TAG = "Testing";

    final private int GALLERY = 1;
    Button select_img, process_img;
    ImageView img_preview;
    public Bitmap bitmap = null;
    String dText;
    TextView pw;
    Boolean dpImage = false; // This means the the picture image // if selected then it is a true if not then it is a false.
    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_gallery);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        select_img = (Button) findViewById(R.id.select_img);
        process_img = (Button) findViewById(R.id.process_img);
        img_preview = (ImageView) findViewById(R.id.img_preview);
        pw = (TextView) findViewById(R.id.pw);
        select_img.setOnClickListener(
                v -> choosePhotoFromGallary()
        );

        process_img.setOnClickListener(
                v -> {
                    try{
                        if(dpImage){
                            pw.setVisibility(View.VISIBLE);
                            dText = cImage(toGrayscale(bitmap));
                            Log.e(TAG, "onCreate: "+ dText );
                            if(dText.isEmpty() || dText.equals(" ")){
                                DDisplay("Please try again");
                            }else{
                                Intent read = new Intent(this,second_frame.class);
                                read.putExtra("dText",dText);
                                startActivity(read);
                            }
                        }else DDisplay("Please select a picture to process");

                    }catch(Exception e){
                        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                            //we are connected to a network
                            pw.setVisibility(View.VISIBLE);
                            dText = cImage(toGrayscale(bitmap));
                            Log.e(TAG, "onCreate: "+ dText );
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
                }
        );
    }
    //ImageView img_preview = (ImageView) findViewById(R.id.img_preview);
    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);

    }

    @Override
    public void onBackPressed() {
        Intent m = new Intent(this, second_frame.class);
        startActivity(m);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    //cImage(bitmap);
                    //Toast.makeText(getApplicationContext(), "Image Saved!", Toast.LENGTH_SHORT).show();
                    try{
                        dpImage = true;
                        img_preview.setImageBitmap(bitmap);
                        //cImage(bitmap);
                    }catch(Exception ignored){

                    }

                    Log.e(TAG, "onActivityResult: Itz working 1");

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                }
            }

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
        }catch(Exception e){

        }

    }

}