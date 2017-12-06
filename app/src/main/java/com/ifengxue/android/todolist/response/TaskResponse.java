package com.ifengxue.android.todolist.response;

import lombok.Data;

@Data
public class TaskResponse {

  private Long createdAt;
  private Long endedAt;
  private Boolean empty;
  private Long id;
  private Long parentId;
  private Integer priority;
  private Long projectId;
  private Long startedAt;
  private String title;
  private String state;
}
