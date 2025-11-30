package com.example.demo.model;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserResponse {
    @SerializedName("success")
    private boolean success;
    @SerializedName("data")
    private UserData data;
    @SerializedName("message")
    private String message;
    public boolean isSuccess() {
        return success;
    }
    public UserData getData() {
        return data;
    }
    public String getMessage() {
        return message;
    }
    public static class UserData {
        @SerializedName("users")
        private List<ServerUser> users;
        @SerializedName("total")
        private int total;
        @SerializedName("current_page")
        private int currentPage;
        @SerializedName("total_pages")
        private int totalPages;
        public List<ServerUser> getUsers() {
            return users;
        }
        public int getTotal() {
            return total;
        }
        public int getCurrentPage() {
            return currentPage;
        }
        public int getTotalPages() {
            return totalPages;
        }
    }
}