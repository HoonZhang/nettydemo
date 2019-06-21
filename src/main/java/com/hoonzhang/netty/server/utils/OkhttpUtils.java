package com.hoonzhang.netty.server.utils;

import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @author: HoonZhang
 * @Creation Date: 2019-06-19 19:47
 * @ModificationHistory:
 * @Link:
 */
public class OkhttpUtils {
    private static final Logger log = LoggerFactory.getLogger(OkhttpUtils.class);
    private static OkHttpClient okHttpClient = new OkHttpClient.Builder().
            readTimeout(3000, TimeUnit.MILLISECONDS)
            .build();

    public static void sendReq(Request request, Callback callback) {
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void sendReq(Request request, int timeout, Callback callback) {
        okHttpClient.newBuilder().readTimeout(timeout, TimeUnit.MILLISECONDS).build().newCall(request).enqueue(callback);
    }

    public static void main(String[] args) {
        String url1 = "http://wtest-xxmm.yy.com/nav/dating";
        Request request1 = new Request.Builder().url(url1).build();
        OkhttpUtils.sendReq(request1, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                log.info("{}", response.headers());
                response.close();
            }
        });

        String url2 = "http://wtest-xxmm.yy.com/nav/dating";
        Request request2 = new Request.Builder().url(url2).build();
        OkhttpUtils.sendReq(request2, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                log.info("{}, protocol:{}", response.headers(), response.protocol());

                response.close();
            }
        });

    }
}
