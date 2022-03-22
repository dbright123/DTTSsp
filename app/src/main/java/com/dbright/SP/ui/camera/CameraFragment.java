package com.dbright.SP.ui.camera;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dbright.SP.CameraSelect;
import com.dbright.SP.databinding.FragmentSlideshowBinding;
import com.dbright.SP.second_frame;

public class CameraFragment extends Fragment {

    private FragmentSlideshowBinding binding;
    private static final String TAG = "Testing";
    second_frame sf = new second_frame();

    final private int GALLERY = 1, CAMERA = 2;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Intent ci = new Intent(getActivity(),CameraSelect.class);
        startActivity(ci);
        CameraViewModel cameraViewModel = new ViewModelProvider(this).get(CameraViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();



        return root;
    }


}