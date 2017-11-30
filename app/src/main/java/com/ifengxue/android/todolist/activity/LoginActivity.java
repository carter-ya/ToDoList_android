package com.ifengxue.android.todolist.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ifengxue.android.todolist.R;
import com.ifengxue.android.todolist.ToDoListContext;
import com.ifengxue.android.todolist.ToDoListContext.UrlContext;
import com.ifengxue.android.todolist.response.UserResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.ifengxue.android.todolist.ToDoListContext.HTTP_CLIENT;
import static com.ifengxue.android.todolist.ToDoListContext.MEDIA_TYPE_JSON;

/**
 * 登陆活动
 */
public class LoginActivity extends AppCompatActivity {

  private static final String TAG = "LoginActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    final EditText textInputAccount = (EditText) findViewById(R.id.text_input_account);
    final EditText textInputPassword = (EditText) findViewById(R.id.text_input_password);
    findViewById(R.id.button_login).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setTitle("提示");
        progressDialog.setMessage("登录中，请稍后");
        progressDialog.setCancelable(false);

        final String account = textInputAccount.getText().toString();
        String password = textInputPassword.getText().toString();
        Map<String, String> map = new HashMap<>();
        map.put("account", account);
        map.put("password", password);
        Request request = new Request.Builder().url(UrlContext.getLoginUrl())
            .post(RequestBody.create(MEDIA_TYPE_JSON, JSON.toJSONString(map))).build();
        Call call = HTTP_CLIENT.newCall(request);
        call.enqueue(new Callback() {
          @Override
          public void onFailure(Call call, final IOException e) {
            Log.e(TAG, e.getLocalizedMessage(), e);
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
              }
            });
          }

          @Override
          public void onResponse(Call call, Response response) throws IOException {
            JSONObject jsonObject = JSON.parseObject(response.body().string());
            String token = jsonObject.getString("data");
            ToDoListContext.token = token;
            Log.i(TAG, "用户 " + account + " 登陆成功, token:" + token);
            Request userInfoRequest = new Request.Builder()
                .url(UrlContext.getUserInfoUrl())
                .header("Accept", MEDIA_TYPE_JSON.toString())
                .build();
            Call userInfoCall = HTTP_CLIENT.newCall(userInfoRequest);
            userInfoCall.enqueue(new Callback() {
              @Override
              public void onFailure(Call call, final IOException ex) {
                Log.e(TAG, ex.getLocalizedMessage(), ex);
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    Toast.makeText(LoginActivity.this, ex.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                  }
                });
              }

              @Override
              public void onResponse(Call call, Response response) throws IOException {
                JSONObject userInfoJSON = JSON.parseObject(response.body().string());
                final UserResponse userResponse = userInfoJSON.getObject("data", UserResponse.class);
                Log.i(TAG, "获取用户信息成功:" + JSON.toJSONString(userResponse));
                runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                    Toast.makeText(LoginActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();
                    ToDoListContext.user = userResponse;
                    progressDialog.dismiss();
                    Intent intent = new Intent(LoginActivity.this, ProjectActivity.class);
                    startActivity(intent);
                  }
                });
              }
            });
          }
        });
        progressDialog.show();
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    return super.onOptionsItemSelected(item);
  }
}
