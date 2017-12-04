package com.ifengxue.android.todolist.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.alibaba.fastjson.JSON;
import com.ifengxue.android.todolist.R;
import com.ifengxue.android.todolist.ToDoListContext;
import com.ifengxue.android.todolist.ToDoListContext.UrlContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RenameProjectActivity extends AppCompatActivity implements View.OnClickListener {

  private static final String TAG = "RenameProjectActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_add_project);
    EditText projectInput = ((EditText) findViewById(R.id.text_input_project_name));
    projectInput.setText(getIntent().getStringExtra("name"));
    findViewById(R.id.button_cancel).setOnClickListener(this);
    findViewById(R.id.button_ok).setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.button_cancel:
        finish();
        break;
      case R.id.button_ok:
        onOkButton();
        break;
    }
  }

  private void onOkButton() {
    final String originalProjectName = getIntent().getStringExtra("name");
    final EditText projectInput = (EditText) findViewById(R.id.text_input_project_name);
    final String changeName = projectInput.getText().toString();
    if (originalProjectName.equals(changeName)) {
      finish();
      return;
    }
    if (TextUtils.isEmpty(changeName)) {
      AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
      builder.setTitle("提示");
      builder.setMessage("标题还没填写");
      builder.setCancelable(false);
      builder.create().show();
      return;
    }
    Map<String, String> map = new HashMap<>();
    map.put("name", changeName);
    Call call = ToDoListContext.HTTP_CLIENT
        .newCall(
            new Request.Builder().url(UrlContext.getProjectRenameUrl(getIntent().getLongExtra("projectId", 0L))).post(
                RequestBody.create(ToDoListContext.MEDIA_TYPE_JSON, JSON.toJSONString(map))).build());
    call.enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        Log.e(TAG, "onFailure: " + e.getLocalizedMessage(), e);
      }

      @Override
      public void onResponse(Call call, Response response) throws IOException {
        runOnUiThread(new Runnable() {
          @Override
          public void run() {
            Intent intent = new Intent();
            intent.putExtra("name", changeName);
            setResult(ProjectActivity.RENAME_PROJECT_REQUEST_CODE, intent);
            finish();
          }
        });
      }
    });
  }
}
