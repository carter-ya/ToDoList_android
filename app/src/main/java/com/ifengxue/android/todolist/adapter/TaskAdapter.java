package com.ifengxue.android.todolist.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ifengxue.android.todolist.R;
import com.ifengxue.android.todolist.adapter.TaskAdapter.TaskViewHolder;
import com.ifengxue.android.todolist.response.TaskResponse;
import java.util.List;

/**
 * Created by apple on 2017/12/5.
 */

public class TaskAdapter extends RecyclerView.Adapter<TaskViewHolder> {

  private final List<TaskResponse> tasks;

  public TaskAdapter(@NonNull List<TaskResponse> tasks) {
    this.tasks = tasks;
  }

  @Override
  public TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item_layout, parent, false);
    return new TaskViewHolder(view);
  }

  @Override
  public void onBindViewHolder(TaskViewHolder holder, int position) {
    TaskResponse task = tasks.get(position);
    holder.taskNameView.setText(task.getTitle());
  }

  @Override
  public int getItemCount() {
    return tasks.size();
  }

  static class TaskViewHolder extends RecyclerView.ViewHolder {

    TextView taskNameView;

    public TaskViewHolder(View itemView) {
      super(itemView);
      taskNameView = (TextView) itemView.findViewById(R.id.text_task_name);
    }
  }
}
