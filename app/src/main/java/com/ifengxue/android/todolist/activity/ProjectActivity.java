package com.ifengxue.android.todolist.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ifengxue.android.todolist.R;
import com.ifengxue.android.todolist.ToDoListContext;
import com.ifengxue.android.todolist.ToDoListContext.UrlContext;
import com.ifengxue.android.todolist.adapter.ProjectAdapter;
import com.ifengxue.android.todolist.response.ProjectResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class ProjectActivity extends AppCompatActivity {

  private static final String TAG = "ProjectActivity";
  public static final int ADD_PROJECT_REQUEST_CODE = 1;
  public static final int RENAME_PROJECT_REQUEST_CODE = 2;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_project);
    // 加载数据
    refreshProject();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_project, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_add) {
      Intent intent = new Intent(ProjectActivity.this, AddProjectActivity.class);
      startActivityForResult(intent, ADD_PROJECT_REQUEST_CODE);
      return true;
    }
    if (id == R.id.action_refresh) {
      refreshProject();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    switch (requestCode) {
      case ADD_PROJECT_REQUEST_CODE:
        refreshProject();
        break;
      case RENAME_PROJECT_REQUEST_CODE:
        refreshProject();
        break;
    }
  }

  private void refreshProject() {
    final ListView projectView = (ListView) findViewById(R.id.list_project);
    final ProgressDialog progressDialog = new ProgressDialog(ProjectActivity.this);
    progressDialog.setTitle("提示");
    progressDialog.setMessage("加载数据中，请稍后");
    progressDialog.show();
    progressDialog.setCancelable(false);
    final Request request = new Request.Builder().url(UrlContext.getProjectsUrl()).build();
    Call call = ToDoListContext.HTTP_CLIENT.newCall(request);
    call.enqueue(new Callback() {
      @Override
      public void onFailure(Call call, final IOException e) {
        Log.e(TAG, e.getLocalizedMessage(), e);
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            Toast.makeText(ProjectActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
          }
        });
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        JSONObject jsonObject = JSON.parseObject(response.body().string());
        JSONArray ary = jsonObject.getJSONArray("data");
        int size = ary.size();
        final List<ProjectResponse> projects = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
          projects.add(ary.getObject(i, ProjectResponse.class));
        }
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            projectView
                .setAdapter(new ProjectAdapter(ProjectActivity.this, R.layout.list_project_layout, projects));
            progressDialog.dismiss();
          }
        });
      }
    });
    progressDialog.show();
  }
}
