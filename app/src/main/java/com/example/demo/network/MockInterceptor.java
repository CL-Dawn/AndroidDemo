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
    private static final String[] USER_NAMES = {
            "Android开发", "测试用户1", "测试用户2", "产品经理", "程序员",
            "大学生A", "大学生B", "大学生C", "大学生D", "大学生E",
            "大学生F", "大学生G", "大学生H", "大学生I", "大学生J",
            "大学生K", "大学生L", "大学生M", "大学生N", "大学生O",
            "大学生P", "大学生Q", "大学生R", "大学生S", "大学生T",
            "大学生U", "大学生V", "大学生W", "大学生X", "大学生Y",
            "大学生Z", "大学生AA", "大学生BB", "大学生CC", "大学生DD",
            "大学生EE", "大学生FF", "大学生GG", "大学生HH", "大学生II",
            "大学生JJ", "大学生KK", "大学生LL", "大学生MM", "大学生NN",
            "大学生OO", "大学生PP", "大学生QQ", "大学生RR", "大学生SS",
            "大学生TT", "大学生UU", "大学生VV", "大学生WW", "大学生XX",
            "大学生YY", "大学生ZZ", "大学生AAA", "大学生BBB", "大学生CCC",
            "大学生DDD", "大学生EEE", "大学生FFF", "大学生GGG", "大学生HHH",
            "大学生III", "大学生JJJ", "大学生KKK", "大学生LLL", "大学生MMM",
            "大学生NNN", "大学生OOO", "大学生PPP", "大学生QQQ", "大学生RRR",
            "大学生SSS", "大学生TTT", "大学生UUU", "大学生VVV", "大学生WWW",
            "大学生XXX", "大学生YYY", "大学生ZZZ", "大学生AAAA", "大学生BBBB",
            "大学生CCCC", "大学生DDDD", "大学生EEEE", "大学生FFFF", "大学生GGGG",
            "大学生HHHH", "大学生IIII", "大学生JJJJ", "大学生KKKK", "大学生LLLL",
            "大学生MMMM", "大学生NNNN", "大学生OOOO", "大学生PPPP", "大学生QQQQ",
            "大学生RRRR", "大学生SSSS", "大学生TTTT", "大学生UUUU", "大学生VVVV",
            "大学生WWWW", "大学生XXXX", "大学生YYYY", "大学生ZZZZ", "前端开发",
            "全栈工程师", "设计师", "后端开发", "iOS开发", "张三", "李四",
            "王五", "赵六", "钱七", "孙八", "周九", "吴十", "开发人员"
    };

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
        int totalUsers = USER_NAMES.length;

        for (int i = 0; i < limit; i++) {
            int userIndex = startIndex + i;
            if (userIndex >= totalUsers) {
                break;
            }

            int userId = userIndex + 1;
            String name = USER_NAMES[userIndex];
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

            if (i < limit - 1 && userIndex < totalUsers - 1) {
                jsonBuilder.append(",");
            }
            jsonBuilder.append("\n");
        }

        jsonBuilder.append("    ],\n")
                .append("    \"total\": ").append(totalUsers).append(",\n")
                .append("    \"current_page\": ").append(page).append(",\n")
                .append("    \"total_pages\": ").append((int) Math.ceil((double) totalUsers / limit)).append("\n")
                .append("  }\n")
                .append("}");

        return jsonBuilder.toString();
    }
}