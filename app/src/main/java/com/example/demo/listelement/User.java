package com.example.demo.listelement;

public class User {
    private String name;
    private boolean isFollowed;
    private String remark;
    private int avatarResId;
    private boolean isSpecial;
    private boolean pendingRemoval;

    public User(String name,boolean isFollowed,int avatarResId){
        this.name=name;
        this.isFollowed=isFollowed;
        this.avatarResId=avatarResId;
        this.isSpecial = false;
        this.remark=null;
        this.pendingRemoval=false;
    }
    public User(String name,boolean isFollowed,int avatarResId,String remark){
        this.name=name;
        this.isFollowed=isFollowed;
        this.avatarResId=avatarResId;
        this.isSpecial = false;
        this.remark=remark;
        this.pendingRemoval=false;
    }
    public String getRemarkOrName(){
        if(remark==null)return name;
        else return  remark;
    }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isFollowed() { return isFollowed; }
    public void setFollowed(boolean followed) { isFollowed = followed; }
    public int getAvatarResId() {return avatarResId;}
    public void setAvatarResId(int avatarResId) {this.avatarResId = avatarResId;}
    public String getRemark() {return remark;}
    public void setRemark(String remark) {this.remark = remark;}
    public boolean isSpecial() {return isSpecial;}
    public void setSpecial(boolean special) {isSpecial = special;}

    public boolean isPendingRemoval() {return pendingRemoval;}

    public void setPendingRemoval(boolean pendingRemoval) {this.pendingRemoval = pendingRemoval;}
}
