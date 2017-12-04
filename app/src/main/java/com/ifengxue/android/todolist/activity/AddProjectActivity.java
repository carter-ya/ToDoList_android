package com.ifengxue.android.todolist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.ifengxue.android.todolist.R;
import com.ifengxue.android.todolist.ToDoListContext;
import com.ifengxue.android.todolist.ToDoListContext.UrlContext;
import com.ifengxue.android.todolist.response.ProjectResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddProjectActivity extends AppCompatActivity {

  private static final String TAG = "AddProjectActivity";
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_project);
    final EditText projectInput = (EditText) findViewById(R.id.text_input_project_name);
    findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View v) {
        finish();
      }
    });
    findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        final String projectName = projectInput.getText().toString();
        if (TextUtils.isEmpty(projectName)) {
          Toast.makeText(AddProjectActivity.this, "项目名称不合法", Toast.LENGTH_SHORT).show();
          projectInput.setFocusable(true);
          return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("name", projectName);
        String body = JSON.toJSONString(map);
        Call call = ToDoListContext.HTTP_CLIENT.newCall(new Request.Builder().url(UrlContext.getProjectAddUrl()).post(
            RequestBody.create(ToDoListContext.MEDIA_TYPE_JSON, body)).build());
        call.enqueue(new Callback() {
          @Override
          public void onFailure(Call call, IOException e) {
            Log.e(TAG, "onFailure: " + e.getLocalizedMessage(), e);
          }

          @Override
          public void onResponse(Call call, Response response) throws IOException {
            final ProjectResponse projectResponse = JSON.parseObject(response.body().string())
                .getObject("data", ProjectResponse.class);
            runOnUiThread(new Runnable() {
              @Override
              public void run() {
                Intent intent = new Intent();
                intent.putExtra("project", JSON.toJSONString(projectResponse));
                setResult(ProjectActivity.ADD_PROJECT_REQUEST_CODE, intent);
                finish();
              }
            });
          }
        });
      }
    });
  }
}
