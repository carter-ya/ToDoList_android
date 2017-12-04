package com.ifengxue.android.todolist.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.alibaba.fastjson.JSON;
import com.ifengxue.android.todolist.R;
import com.ifengxue.android.todolist.ToDoListContext;
import com.ifengxue.android.todolist.ToDoListContext.UrlContext;
import com.ifengxue.android.todolist.response.ProjectResponse;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class ProjectInfoActivity extends Activity {

  private static final String TAG = "ProjectInfoActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_project_info);
    long projectId = getIntent().getLongExtra("projectId", 0L);
    if (projectId <= 0L) {
      finish();
      return;
    }
    final TextView textProjectId = (TextView) findViewById(R.id.text_project_id);
    final TextView textProjectName = (TextView) findViewById(R.id.text_project_name);
    final TextView textProjectTask = (TextView) findViewById(R.id.text_project_task);
    final TextView textProjectCreatedAt = (TextView) findViewById(R.id.text_project_created_at);
    Call call = ToDoListContext.HTTP_CLIENT
        .newCall(
            new Request.Builder().url(UrlContext.getProjectInfoUrl(projectId)).get().build());
    call.enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        Log.e(TAG, "onFailure: " + e.getLocalizedMessage(), e);
        finish();
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        final ProjectResponse projectResponse = JSON.parseObject(response.body().string())
            .getObject("data", ProjectResponse.class);
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            textProjectId.setText(projectResponse.getId() + "");
            textProjectName.setText(projectResponse.getName());
            textProjectTask.setText(projectResponse.getTotalFinishedTask() + "/" + projectResponse.getTotalTask());
            String createdAt = DateUtils
                .formatDateTime(ProjectInfoActivity.this, projectResponse.getCreatedAt(), DateUtils.FORMAT_ABBREV_ALL);
            textProjectCreatedAt.setText(createdAt);
          }
        });
      }
    });
    findViewById(R.id.button_ok).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        finish();
      }
    });
  }

}
