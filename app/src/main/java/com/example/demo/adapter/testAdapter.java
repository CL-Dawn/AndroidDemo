package com.example.demo.adapter;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.demo.fragment.FansFragment;
import com.example.demo.fragment.FollowFragment;
import com.example.demo.fragment.FriendFragment;
import com.example.demo.fragment.MutualFragment;

public class testAdapter extends FragmentStateAdapter {
    // 页面数量（对应“互关/关注/粉丝/朋友”4个页面）
    private static final int PAGE_COUNT = 4;
    private static final String TAG = "TestAdapter";
    private Fragment[] fragments = new Fragment[PAGE_COUNT];
    public testAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);

    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (fragments[position] != null) {
            return fragments[position];
        }
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new MutualFragment();
                break;
            case 1:
                fragment = new FollowFragment();
                break;
            case 2:
                fragment = new FansFragment();
                break;
            case 3:
                fragment = new FriendFragment();
                break;
            default:
                fragment = new MutualFragment();
                break;
        }

        return fragment;
    }

    @Override
    public int getItemCount() {
        return PAGE_COUNT;
    }

}
