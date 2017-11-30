package com.ifengxue.android.todolist.response;

import lombok.Data;

@Data
public class UserResponse {
  private Long createdAt;
  private String email;
  private String icon;
  private Long id;
  private String nickname;
  private String phone;
}
