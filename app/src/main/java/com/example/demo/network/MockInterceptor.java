package com.example.demo.network;
import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MockInterceptor implements Interceptor {
    private static final String TAG = "StableMockInterceptor";

    private static final int INITIAL_FOLLOW_COUNT = 1000;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        String url = request.url().toString();

        Log.d(TAG, "拦截请求: " + url);

        if (url.contains("/api/users") || url.contains("/users")) {
            return createStableMockUsersResponse(request);
        }

        return chain.proceed(request);
    }

    private Response createStableMockUsersResponse(Request request) {
        try {
            int page = 1;
            int limit = 10;

            String query = request.url().query();
            if (query != null) {
                if (query.contains("page=")) {
                    String[] params = query.split("&");
                    for (String param : params) {
                        if (param.startsWith("page=")) {
                            page = Integer.parseInt(param.substring(5));
                        } else if (param.startsWith("limit=")) {
                            limit = Integer.parseInt(param.substring(6));
                        }
                    }
                }
            }

            String mockResponse = generateStableMockUsersJson(page, limit);

            return new Response.Builder()
                    .code(200)
                    .message("OK")
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .body(ResponseBody.create(MediaType.parse("application/json"), mockResponse))
                    .addHeader("content-type", "application/json")
                    .build();

        } catch (Exception e) {
            Log.e(TAG, "创建模拟响应失败", e);
            return new Response.Builder()
                    .code(500)
                    .message("Internal Server Error")
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .body(ResponseBody.create(MediaType.parse("application/json"), "{\"error\":\"Mock data generation failed\"}"))
                    .build();
        }
    }

    private String generateStableMockUsersJson(int page, int limit) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\n")
                .append("  \"success\": true,\n")
                .append("  \"message\": \"成功\",\n")
                .append("  \"data\": {\n")
                .append("    \"total_follow_count\": ").append(INITIAL_FOLLOW_COUNT).append(",\n")
                .append("    \"users\": [\n");

        int startIndex = (page - 1) * limit;

        for (int i = 0; i < limit; i++) {
            int userIndex = startIndex + i;
            if (userIndex >= INITIAL_FOLLOW_COUNT) {
                break;
            }

            int userId = userIndex + 1;
            String name ="user"+userId;
            boolean isFollowed = true;
            boolean isSpecial = false;
            String remark = null;

            jsonBuilder.append("      {\n")
                    .append("        \"id\": ").append(userId).append(",\n") // 直接输出整数，不加引号
                    .append("        \"name\": \"").append(name).append("\",\n")
                    .append("        \"is_followed\": ").append(isFollowed).append(",\n")
                    .append("        \"is_special\": ").append(isSpecial).append(",\n")
                    .append("        \"remark\": ").append(remark != null ? "\"" + remark + "\"" : "null").append("\n")
                    .append("      }");

            if (i < limit - 1 && userIndex < INITIAL_FOLLOW_COUNT - 1) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append("\n");
        }

        jsonBuilder.append("    ],\n")
                .append("    \"total\": ").append(INITIAL_FOLLOW_COUNT).append(",\n")
                .append("    \"current_page\": ").append(page).append(",\n")
                .append("    \"total_pages\": ").append((int) Math.ceil((double) INITIAL_FOLLOW_COUNT / limit)).append("\n")
                .append("  }\n")
                .append("}");

        return jsonBuilder.toString();
    }
}