package com.ifengxue.android.todolist.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.ifengxue.android.todolist.R;
import com.ifengxue.android.todolist.ToDoListContext;
import com.ifengxue.android.todolist.ToDoListContext.UrlContext;
import com.ifengxue.android.todolist.activity.ProjectActivity;
import com.ifengxue.android.todolist.activity.ProjectInfoActivity;
import com.ifengxue.android.todolist.activity.RenameProjectActivity;
import com.ifengxue.android.todolist.response.ProjectResponse;
import java.io.IOException;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by 刘克峰 on 2017-11-26.
 */
public class ProjectAdapter extends ArrayAdapter<ProjectResponse> {

  private static final String TAG = "ProjectAdapter";
  private int resourceId;

  public ProjectAdapter(@NonNull Context context,
      int textViewResourceId,
      @NonNull List<ProjectResponse> objects) {
    super(context, textViewResourceId, objects);
    this.resourceId = textViewResourceId;
  }

  @NonNull
  @Override
  public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
    final ProjectResponse project = getItem(position);
    View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
    TextView idView = (TextView) view.findViewById(R.id.project_id);
    idView.setText(position + 1 + "");
    idView.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getContext(), ProjectInfoActivity.class);
        intent.putExtra("projectId", project.getId());
        getContext().startActivity(intent);
      }
    });

    TextView textProjectName = (TextView) view.findViewById(R.id.project_name);
    textProjectName.setText(project.getName());
    textProjectName.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(getContext(), RenameProjectActivity.class);
        intent.putExtra("name", project.getName());
        intent.putExtra("projectId", project.getId());
        ((ProjectActivity) getContext()).startActivityForResult(intent, ProjectActivity.RENAME_PROJECT_REQUEST_CODE);
      }
    });

    ((TextView) view.findViewById(R.id.project_total_task)).setText(project.getTotalTask().toString());
    ((TextView) view.findViewById(R.id.project_total_finish_task)).setText(project.getTotalFinishedTask().toString());
    view.findViewById(R.id.button_delete_project).setOnClickListener(new View.OnClickListener() {

      @Override
      public void onClick(View view) {
        Call call = ToDoListContext.HTTP_CLIENT
            .newCall(new Request.Builder().url(UrlContext.getProjectDeleteUrl(project.getId()))
                .post(RequestBody.create(ToDoListContext.MEDIA_TYPE_JSON, "{}")).build());
        call.enqueue(new Callback() {
          @Override
          public void onFailure(Call call, IOException e) {
            Log.e(TAG, "onFailure: " + e.getLocalizedMessage(), e);
          }

          @Override
          public void onResponse(Call call, Response response) throws IOException {
            final ProjectActivity projectActivity = ((ProjectActivity) getContext());
            projectActivity.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                projectActivity.refreshProject();
                Toast.makeText(getContext(), "删除项目 " + project.getName() + " 成功", Toast.LENGTH_SHORT).show();
              }
            });
          }
        });
      }
    });
    return view;
  }
}
