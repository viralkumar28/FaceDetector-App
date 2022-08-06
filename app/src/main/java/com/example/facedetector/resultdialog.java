package com.example.facedetector;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class resultdialog extends DialogFragment {
    Button OK;
    TextView RESULT;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_resultdialog, container, false);
        String resultText = "";

        OK = view.findViewById(R.id.ok);
        RESULT = view.findViewById(R.id.result);

        Bundle bundle = getArguments();
        resultText = bundle.getString(FaceDetection.RESULT_TEXT);
        RESULT.setText(resultText);

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }
}