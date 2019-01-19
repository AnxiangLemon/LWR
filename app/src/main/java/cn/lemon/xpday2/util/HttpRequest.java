package cn.lemon.xpday2.util;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class HttpRequest {
    //调试版接口
    private String url ;

    public HttpRequest(String url) {
        this.url = url;
    }


    //get方法获取数据
    public String getData() {
        Response response;
        String strResponse;

        //准备网络请求
        OkHttpClient client = OkHttpUtils.getInstance().getOkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        try {
            response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return "网络请求失败";
            }
            ResponseBody body = response.body();
            if (body == null) {
                return "网络请求返回body为空";
            }
            strResponse = body.string();
            //解析返回的JSON字符串
        } catch (IOException e) {
            e.printStackTrace();
            return "网络请求异常" + e.getMessage();
        }
        return strResponse;
    }

}
