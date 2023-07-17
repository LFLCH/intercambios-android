package com.example.intercambios;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class BottomSheetDialogSelector extends BottomSheetDialogFragment
{
    private int contentId;
    static Toast toast;
    public BottomSheetDialogSelector(int contentId){
        this.contentId = contentId;
    }
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(this.contentId, container, false);
//
//            EditText messageEditor = view.findViewById(R.id.messageEdition);
//
//
//            FloatingActionButton galleryButton =  view.findViewById(R.id.galleryIcon);
//            FloatingActionButton cameraButton =  view.findViewById(R.id.cameraIcon);
//
//
//            cameraButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(toast!=null) toast.cancel();
//                    toast = Toast.makeText(view.getContext(),"Camera Clicked On",Toast.LENGTH_SHORT);
//                    toast.show();
//
//                }
//            });
//
//
//            galleryButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(toast!=null)toast.cancel();
//                    toast =Toast.makeText(view.getContext(),"Gallery Clicked On",Toast.LENGTH_SHORT);
//                    toast.show();
//                }
//            });

            return view;
        }
}
