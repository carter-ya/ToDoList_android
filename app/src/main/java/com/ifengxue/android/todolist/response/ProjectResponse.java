package com.ifengxue.android.todolist.response;

import lombok.Data;

@Data
public class ProjectResponse {

  private Long createdAt;
  private Long id;
  private String name;
  private Long totalFinishedTask;
  private Long totalTask;
}
