package com.dbright.SP.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.dbright.SP.R;
import com.dbright.SP.databinding.FragmentHomeBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final String TAG = "Testing";
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        final EditText et1 = binding.et1;
        final ImageButton reset = binding.reset;

        final String[] text = {""};
        final String[][] aText = {{}};
        final int[] d_count = {0};


        reset.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // select_img 1 was clicked!
                        et1.setText("");
                        Snackbar.make(v, "Done resetting", Snackbar.LENGTH_LONG) // still on target monitoring
                                .setAction("Action", null).show();
                    }
                }
        );


        homeViewModel.getText().observe(getViewLifecycleOwner(), s -> {
            textView.setText(R.string.msg1);
            new Thread(
                    new Runnable() {
                        @SuppressLint("SetTextI18n")
                        @Override
                        public void run() {
                            while(true){
                                if(et1.getText().length() > 0){
                                    for(int x = 0; x < aText[0].length; x++){
                                        if (!aText[0][x].isEmpty() && !aText[0][x].equals("") && !aText[0][x].equals(" ")) d_count[0]++;
                                    }
                                    text[0] = String.valueOf(et1.getText());
                                    aText[0] = text[0].split(" ", -1);
                                    textView.setText(d_count[0] + " Words");
                                }else{
                                    textView.setText(R.string.msg1);
                                }
                                d_count[0] = 0;
                            }

                        }
                    }
            ).start();
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }





}