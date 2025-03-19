package com.example.do_an.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.do_an.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class StepFragment extends Fragment {
    private String postId;

    public StepFragment() {
        // Constructor mặc định khi dùng setArguments()
    }

    public static StepFragment newInstance(String postId) {
        StepFragment fragment = new StepFragment();
        Bundle args = new Bundle();
        args.putString("postId", postId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_step, container, false);
        TextView tvSteps = view.findViewById(R.id.tv_steps);

        if (getArguments() != null) {
            postId = getArguments().getString("postId");
        }

        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Posts").child(postId);
        postRef.child("steps").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String stepsString = snapshot.getValue(String.class);
                if (stepsString != null && !stepsString.isEmpty()) {
                    tvSteps.setText(stepsString);
                } else {
                    tvSteps.setText("Không có bước làm.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                tvSteps.setText("Lỗi tải dữ liệu.");
            }
        });

        return view;
    }
}
