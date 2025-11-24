package com.example.demo.fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.demo.R;
import com.example.demo.adapter.UserAdapter;
import com.example.demo.listelement.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FollowFragment extends Fragment {

    private static final String TAG="FollowFragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private int myfollownum;
    private TextView myfollows;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follow, container, false);
        initView(view);
        initData();
        setupRecyclerView();
        refreshMyFollow();
        return view;
    }
    private void initView(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = view.findViewById(R.id.recycler_view);
        myfollows=view.findViewById(R.id.myfollows);

        // 下拉刷新监听
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);
    }
    private void initData() {
        // 初始化数据
        userList = new ArrayList<>();
        String[] names = {"test1", "what", "why", "how", "error???",
                "warning", "never", "be", "important"};
        int[] avatarResIds = {
                R.drawable.avator_1,
                R.drawable.avator_2,
                R.drawable.avator_3,
                R.drawable.avator_4,
                R.drawable.avator_5,
                R.drawable.avator_6,
                R.drawable.avator_7,
                R.drawable.avator_8,
                R.drawable.avator_9,
                R.drawable.avator_10,
                R.drawable.avator_11,
                R.drawable.avator_12,
                R.drawable.avator_13,
                R.drawable.avator_14,
                R.drawable.avator_15,
                R.drawable.avator_16,
                R.drawable.avator_17,
                R.drawable.avator_18,
                R.drawable.avator_19,
                R.drawable.avator_20

        };
        for (int i = 0; i < names.length; i++) {
            int avatarIndex = i % avatarResIds.length;
            userList.add(new User(names[i], true, avatarResIds[avatarIndex]));
        }
        myfollownum=userList.size();
        sortUserList();
    }
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new UserAdapter(userList);

        userAdapter.setOnItemClickListener(new UserAdapter.OnItemClickListener() {
            @Override
            public void onFollowClick(int position, User user) {
                boolean newFollowState = !user.isFollowed();
                user.setFollowed(newFollowState);
                if(newFollowState){
                    myfollownum++;
                    user.setPendingRemoval(false);
                }else{
                    myfollownum--;
                    user.setSpecial(false);
                    user.setPendingRemoval(true);
                }
                refreshMyFollow();
                sortUserList();
                userAdapter.notifyItemChanged(position);

            }

            @Override
            public void onUnfollowClick(int position, User user) {
                user.setFollowed(false);
                myfollownum--;
                user.setSpecial(false);
                user.setPendingRemoval(true);
                userAdapter.notifyItemChanged(position);
                refreshMyFollow();
            }

            @Override
            public void onSpecialClick(int position, User user, boolean isSpecial) {
                user.setSpecial(isSpecial);
                sortUserList();
                userAdapter.notifyDataSetChanged();
            }
        });

        recyclerView.setAdapter(userAdapter);
    }
    private void sortUserList() {
        Collections.sort(userList, (user1, user2) -> {

            if (user1.isSpecial() && !user2.isSpecial()) {
                return -1;
            } else if (!user1.isSpecial() && user2.isSpecial()) {
                return 1;
            } else {
                return user1.getName().compareTo(user2.getName());
            }
        });
    }
    private void refreshData() {
        Log.d(TAG, "开始下拉刷新");
        List<User> usersToRemove = new ArrayList<>();
        for (User user : userList) {
            if (user.isPendingRemoval()) {
                usersToRemove.add(user);
            }
        }
        userList.removeAll(usersToRemove);
        sortUserList();
        userAdapter.updateData(userList);
        swipeRefreshLayout.setRefreshing(false);
        refreshMyFollow();
    }
    private void refreshMyFollow(){
        String myfollowsText="我的关注（" + myfollownum + "人）";
        myfollows.setText(myfollowsText);
    }
}
