package com.example.demo.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestByFullNameDTO {
  private String fullName;
  private ConditionsDTO conditions;
}
