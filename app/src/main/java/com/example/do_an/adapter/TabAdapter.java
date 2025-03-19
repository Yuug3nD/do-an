package com.example.do_an.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.do_an.fragments.saveFragment;
import com.example.do_an.fragments.shareFragment;

public class TabAdapter extends FragmentStateAdapter {

    public TabAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new shareFragment();
            case 1:
                return new saveFragment();
            default:
                return new shareFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
