package com.dbright.SP.ui.gallery;

import static android.app.Activity.RESULT_CANCELED;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.dbright.SP.R;
import com.dbright.SP.databinding.FragmentGalleryBinding;
import com.dbright.SP.gallarySelect;
import com.dbright.SP.second_frame;

import java.io.IOException;

public class GalleryFragment extends Fragment{

    private static final String TAG = "Testing";
    private FragmentGalleryBinding binding;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);
        Intent ig = new Intent(getActivity(), gallarySelect.class);
        startActivity(ig);
        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        return root;
    }




}