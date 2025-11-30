package com.example.demo.fragment;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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

import com.example.demo.ImageLoader;
import com.example.demo.R;
import com.example.demo.adapter.UserAdapter;
import com.example.demo.listelement.User;
import com.example.demo.network.MockInterceptor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class FollowFragment extends Fragment {

    private static final String TAG = "FollowFragment";
    private static final String MOCK_API_URL = "https://mock.api.com/api/";
    private static final int PAGE_SIZE = 10;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> userList;
    private int myfollownum;
    private TextView myfollows;
    private OkHttpClient okHttpClient;
    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean hasMore = true;
    private int initialFollowCount = 0;
    // 内存优化相关
    private Handler uiHandler = new Handler(Looper.getMainLooper());
    private LinearLayoutManager layoutManager;
    private int lastVisibleItemPosition = -1;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_follow, container, false);
        initView(view);
        initData();
        setupRecyclerView();
        loadMoreData();
        return view;
    }

    private void initView(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = view.findViewById(R.id.recycler_view);
        myfollows = view.findViewById(R.id.myfollows);

        // 初始化 OkHttpClient 并添加日志拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);

        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new MockInterceptor())
                .addInterceptor(loggingInterceptor)
                .build();

        // 下拉刷新监听
        swipeRefreshLayout.setOnRefreshListener(this::refreshData);

        // 上拉加载更多
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                    if (!isLoading && hasMore) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && firstVisibleItemPosition >= 0
                                && totalItemCount >= PAGE_SIZE) {
                            loadMoreData();
                        }
                    }
                }
            }
        });
    }

    private void initData() {
        userList = new ArrayList<>();
        myfollownum = 0;
        refreshMyFollow();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //性能优化
        recyclerView.setHasFixedSize(false);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        userAdapter = new UserAdapter(userList);

        userAdapter.setOnItemClickListener(new UserAdapter.OnItemClickListener() {
            @Override
            public void onFollowClick(int position, User user) {
                updateFollowStatus(user, !user.isFollowed(), position);
            }
            @Override
            public void onUnfollowClick(int position, User user) {
                updateFollowStatus(user, false, position);
            }
            @Override
            public void onSpecialClick(int position, User user, boolean isSpecial) {
                updateSpecialStatus(user, isSpecial, position);
            }
        });

        recyclerView.setAdapter(userAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (layoutManager != null) {
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                    int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                    // 预加载头像
                    if (lastVisibleItemPosition != -1) {
                        userAdapter.preloadAvatars(firstVisibleItemPosition, lastVisibleItemPosition);
                    }
                    if (!isLoading && hasMore) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && firstVisibleItemPosition >= 0
                                && totalItemCount >= PAGE_SIZE) {
                            loadMoreData();
                        }
                    }
                }
            }
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 根据滚动状态调整图片加载策略
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        // 停止滚动时，可以加载更高质量的图片
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        // 拖动时，优先保证流畅度
                        break;
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        // 惯性滚动时，保持当前策略
                        break;
                }
            }
        });
    }

    private void loadMoreData() {
        if (isLoading || !hasMore) return;

        isLoading = true;
        Log.d(TAG, "加载第 " + currentPage + " 页数据");

        String url = MOCK_API_URL + "users?page=" + currentPage + "&limit=" + PAGE_SIZE;
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, "网络请求失败: " + e.getMessage());
                new Handler(Looper.getMainLooper()).post(() -> {
                    isLoading = false;
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code " + response);
                }
                final String responseData = response.body().string();
                Log.d(TAG, "收到响应，数据长度: " + responseData.length());
                new Handler(Looper.getMainLooper()).post(() -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(responseData);

                        if (!jsonResponse.getBoolean("success")) {
                            Log.e(TAG, "API返回失败: " + jsonResponse.getString("message"));
                            return;
                        }
                        JSONObject data = jsonResponse.getJSONObject("data");
                        // 只在第一页时获取总关注数
                        if (currentPage == 1) {
                            initialFollowCount = data.getInt("total_follow_count");
                            if (myfollownum == 0) {
                                myfollownum = initialFollowCount;
                            }
                            refreshMyFollow();
                        }
                        List<User> newUsers = parseUsersFromResponse(data);

                        if (newUsers.isEmpty()) {
                            hasMore = false;
                            Log.d(TAG, "没有更多数据了");
                        } else {
                            int startPosition = userList.size();
                            userList.addAll(newUsers);

                            sortUserList();

                            if (startPosition == 0) {
                                userAdapter.notifyDataSetChanged();
                            } else {
                                userAdapter.notifyItemRangeInserted(startPosition, newUsers.size());
                            }

                            currentPage++;
                            Log.d(TAG, "成功加载 " + newUsers.size() + " 个用户，当前总计: " + userList.size());
                        }

                    } catch (JSONException e) {
                        Log.e(TAG, "JSON解析错误: " + e.getMessage());
                    } finally {
                        isLoading = false;
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    }
                });
            }
        });
    }

    private List<User> parseUsersFromResponse(JSONObject data) throws JSONException {
        List<User> users = new ArrayList<>();

        JSONArray usersArray = data.getJSONArray("users");

        // 检查是否还有更多数据
        int currentPage = data.getInt("current_page");
        int totalPages = data.getInt("total_pages");
        hasMore = currentPage < totalPages;

        // 预定义的本地头像资源
        int[] avatarResIds = {
                R.drawable.avator_1, R.drawable.avator_2, R.drawable.avator_3,
                R.drawable.avator_4, R.drawable.avator_5, R.drawable.avator_6,
                R.drawable.avator_7, R.drawable.avator_8, R.drawable.avator_9,
                R.drawable.avator_10, R.drawable.avator_11, R.drawable.avator_12,
                R.drawable.avator_13, R.drawable.avator_14, R.drawable.avator_15,
                R.drawable.avator_16, R.drawable.avator_17, R.drawable.avator_18,
                R.drawable.avator_19, R.drawable.avator_20
        };

        for (int i = 0; i < usersArray.length(); i++) {
            JSONObject userJson = usersArray.getJSONObject(i);

            int id = userJson.getInt("id");
            String name = userJson.getString("name");
            boolean isFollowed = userJson.getBoolean("is_followed");
            boolean isSpecial = userJson.getBoolean("is_special");
            String remark = userJson.isNull("remark") ? null : userJson.getString("remark");

            // 创建用户对象 - 使用本地头像资源
            int avatarIndex = (this.currentPage * PAGE_SIZE + i) % avatarResIds.length;
            User user = new User(id, name, isFollowed, avatarResIds[avatarIndex]);

            user.setRemark(remark);
            user.setSpecial(isSpecial);
            users.add(user);
        }

        return users;
    }

    private void updateFollowStatus(User user, boolean follow, int position) {
        user.setFollowed(follow);
        if (follow) {
            myfollownum++;
            user.setPendingRemoval(false);
        } else {
            myfollownum--;
            user.setSpecial(false);
            user.setPendingRemoval(true);
        }

        refreshMyFollow();
        sortUserList();
        userAdapter.notifyItemChanged(position);

        Log.d(TAG, "更新关注状态: " + user.getName() + " - " + (follow ? "已关注" : "取消关注"));
    }

    private void updateSpecialStatus(User user, boolean isSpecial, int position) {
        user.setSpecial(isSpecial);
        sortUserList();
        userAdapter.notifyDataSetChanged();

        Log.d(TAG, "更新特别关注状态: " + user.getName() + " - " + (isSpecial ? "设为特别关注" : "取消特别关注"));
    }

    private void refreshData() {
        Log.d(TAG, "开始下拉刷新");

        // 移除标记为待删除的用户
        List<User> usersToRemove = new ArrayList<>();
        for (User user : userList) {
            if (user.isPendingRemoval()) {
                usersToRemove.add(user);
            }
        }
        userList.removeAll(usersToRemove);

        // 重置分页状态
        currentPage = 1;
        hasMore = true;
        userList.clear();

        if (getContext() != null) {
            ImageLoader.clearMemoryCache(getContext());
        }
        // 重新加载数据（关注数会在第一页加载时从服务器重新获取）
        loadMoreData();
    }
    private void sortUserList() {
        Collections.sort(userList, (user1, user2) -> {
            if (user1.isSpecial() && !user2.isSpecial()) {
                return -1;
            } else if (!user1.isSpecial() && user2.isSpecial()) {
                return 1;
            } else {
                int id1 =user1.getId();
                int id2 = user2.getId();
                return Integer.compare(id1, id2);
            }
        });
    }

    private void refreshMyFollow() {
        String myfollowsText = "我的关注（" + myfollownum + "人）";
        myfollows.setText(myfollowsText);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (okHttpClient != null) {
            okHttpClient.dispatcher().cancelAll();
        }
    }
}