package com.example.demo.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import java.util.concurrent.TimeUnit;

public class HttpClient {
    private static final long CONNECT_TIMEOUT = 15;
    private static final long READ_TIMEOUT = 15;
    private static final long WRITE_TIMEOUT = 15;
    private static OkHttpClient instance;
    public static OkHttpClient getInstance() {
        if (instance == null) {
            synchronized (HttpClient.class) {
                if (instance == null) {
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY); // 打印日志

                    instance = new OkHttpClient.Builder()
                            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                            .addInterceptor(logging)
                            .build();
                }
            }
        }
        return instance;
    }
}