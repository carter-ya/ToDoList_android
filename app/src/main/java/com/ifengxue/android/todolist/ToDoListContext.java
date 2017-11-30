package com.ifengxue.android.todolist;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ifengxue.android.todolist.response.UserResponse;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Created by 刘克峰 on 2017-11-26.
 */
public final class ToDoListContext {
  public static String token;
  public static UserResponse user;
  public static MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf8");
  public static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
    @Override
    public Response intercept(Chain chain) throws IOException {
      Request request = chain.request();
      if (token != null) {
        request = request.newBuilder().header("Token", token).build();
      }
      return chain.proceed(request);
    }
  }).addInterceptor(new Interceptor() {
    @Override
    public Response intercept(Chain chain) throws IOException {
      Response response = chain.proceed(chain.request());
      if (response.code() != 200) {
        throw new IOException("invalid response code:" + response.code() + ", response body:" + response.body().string());
      }
      String responseString = response.body().string();
      JSONObject jsonObject = JSON.parseObject(responseString);
      // 失败
      if (jsonObject.getString("status").equals("error")) {
        throw new IOException("错误码:" + jsonObject.getString("errorCode") + "，错误提示:" + jsonObject.getString("message"));
      }
      return response.newBuilder().body(ResponseBody.create(MEDIA_TYPE_JSON, responseString)).build();
    }
  }).build();

  public static class UrlContext {
    public static String baseUrl = "http://192.168.10.57:8080";

    public static String getLoginUrl() {
      return baseUrl + "/v1/users/login";
    }

    public static String getUserInfoUrl() {
      return baseUrl + "/v1/users/current";
    }

    public static String getProjectsUrl() {
      return baseUrl + "/v1/projects/";
    }
  }
}
