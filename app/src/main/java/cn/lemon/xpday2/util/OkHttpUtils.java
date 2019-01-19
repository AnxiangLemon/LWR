package cn.lemon.xpday2.util;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class OkHttpUtils {
    private static final long DEFAULT_READ_TIMEOUT_MILLIS = 15 * 1000;
    private static final long DEFAULT_WRITE_TIMEOUT_MILLIS = 20 * 1000;
    private static final long DEFAULT_CONNECT_TIMEOUT_MILLIS = 20 * 1000;
    private static volatile OkHttpUtils sInstance;
    private OkHttpClient mOkHttpClient;

    private OkHttpUtils() {
        mOkHttpClient = new OkHttpClient.Builder()
                .readTimeout(DEFAULT_READ_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .writeTimeout(DEFAULT_WRITE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .connectTimeout(DEFAULT_CONNECT_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)
                .build();
    }

    public static OkHttpUtils getInstance() {
        if (sInstance == null) {
            synchronized (OkHttpUtils.class) {
                if (sInstance == null) {
                    sInstance = new OkHttpUtils();
                }
            }
        }
        return sInstance;
    }

    //单例获取OkHttpClient实例
    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

}
