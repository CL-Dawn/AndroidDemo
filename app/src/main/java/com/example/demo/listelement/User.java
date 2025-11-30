package com.example.demo.listelement;

public class User {
    private int id;
    private String name;
    private boolean isFollowed;
    private String remark;
    private int avatarResId; // 本地资源ID
    private String avatarUrl; // 网络图片URL
    private boolean isSpecial;
    private boolean pendingRemoval;

    public User(int id, String name, boolean isFollowed, int avatarResId) {
        this.id = id;
        this.name = name;
        this.isFollowed = isFollowed;
        this.avatarResId = avatarResId;
        this.isSpecial = false;
        this.remark = null;
        this.pendingRemoval = false;
        this.avatarUrl = null;
    }

    public User(int id, String name, boolean isFollowed, String avatarUrl) {
        this.id = id;
        this.name = name;
        this.isFollowed = isFollowed;
        this.avatarUrl = avatarUrl;
        this.isSpecial = false;
        this.remark = null;
        this.pendingRemoval = false;
        this.avatarResId = 0;
    }

    // 从服务器用户创建本地User对象
    public static User fromServerUser(com.example.demo.model.ServerUser serverUser) {
        User user = new User(
                serverUser.getId(),
                serverUser.getName(),
                serverUser.isFollowed(),
                serverUser.getAvatarUrl()
        );
        user.setSpecial(serverUser.isSpecial());
        user.setRemark(serverUser.getRemark());
        return user;
    }

    public String getRemarkOrName() {
        if (remark != null && !remark.isEmpty()) {
            return remark;
        } else {
            return name;
        }
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isFollowed() { return isFollowed; }
    public void setFollowed(boolean followed) { isFollowed = followed; }

    public int getAvatarResId() { return avatarResId; }
    public void setAvatarResId(int avatarResId) { this.avatarResId = avatarResId; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public boolean isSpecial() { return isSpecial; }
    public void setSpecial(boolean special) { isSpecial = special; }

    public boolean isPendingRemoval() { return pendingRemoval; }
    public void setPendingRemoval(boolean pendingRemoval) { this.pendingRemoval = pendingRemoval; }
}