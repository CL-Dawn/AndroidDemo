package com.example.demo.model;

import com.google.gson.annotations.SerializedName;
public class ServerUser {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("avatar_url")
    private String avatarUrl;
    @SerializedName("is_followed")
    private boolean isFollowed;
    @SerializedName("is_special")
    private boolean isSpecial;
    @SerializedName("remark")
    private String remark;
    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getAvatarUrl() { return avatarUrl; }
    public boolean isFollowed() { return isFollowed; }
    public boolean isSpecial() { return isSpecial; }
    public String getRemark() { return remark; }
}