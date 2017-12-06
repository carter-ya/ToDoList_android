package com.ifengxue.android.todolist.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ifengxue.android.todolist.R;
import com.ifengxue.android.todolist.ToDoListContext;
import com.ifengxue.android.todolist.ToDoListContext.UrlContext;
import com.ifengxue.android.todolist.adapter.TaskAdapter;
import com.ifengxue.android.todolist.response.TaskResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request.Builder;
import okhttp3.Response;

public class TaskActivity extends AppCompatActivity {

  private static final String TAG = "TaskActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_task);
    final RecyclerView taskView = (RecyclerView) findViewById(R.id.task_recycler_view);
    LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    taskView.setLayoutManager(layoutManager);
    refreshTask();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_task, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int menuId = item.getItemId();
    if (menuId == R.id.action_refresh) {
      refreshTask();
    }
    return true;
  }

  public void refreshTask() {
    ProgressDialog.Builder builder = new ProgressDialog.Builder(this);
    builder.setTitle("提示");
    builder.setMessage("加载中，请稍后");
    builder.setCancelable(false);
    final AlertDialog dialog = builder.show();
    final RecyclerView taskView = (RecyclerView) findViewById(R.id.task_recycler_view);
    Intent intent = getIntent();
    long projectId = intent.getLongExtra("projectId", 0L);
    long parentId = intent.getLongExtra("parentId", 0L);
    Call call = ToDoListContext.HTTP_CLIENT
        .newCall(new Builder().url(UrlContext.getTasksUrl(projectId, parentId)).build());
    call.enqueue(new Callback() {
      @Override
      public void onFailure(Call call, final IOException e) {
        Log.e(TAG, "onFailure: " + e.getLocalizedMessage(), e);
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            dialog.dismiss();
            Toast.makeText(TaskActivity.this, "加载失败:" + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
          }
        });
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        JSONArray jary = JSON.parseObject(response.body().string()).getJSONArray("data");
        int size = jary.size();
        final List<TaskResponse> tasks = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
          tasks.add(jary.getObject(i, TaskResponse.class));
        }
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            taskView.setAdapter(new TaskAdapter(tasks));
            taskView.getAdapter().notifyDataSetChanged();
            dialog.dismiss();
          }
        });
      }
    });
  }
}
