package com.example.do_an.adapter;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.do_an.fragments.IngredientFragment;
import com.example.do_an.fragments.StepFragment;

import java.util.ArrayList;

public class RecipeAdapter extends FragmentStateAdapter {
    private String postId;

    public RecipeAdapter(@NonNull FragmentActivity fragmentActivity, String postId) {
        super(fragmentActivity);
        this.postId = postId;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            Log.d("RecipeAdapter", "Đang tạo IngredientFragment");
            return IngredientFragment.newInstance(postId);
        } else {
            Log.d("RecipeAdapter", "Đang tạo StepFragment");
            return StepFragment.newInstance(postId);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}


