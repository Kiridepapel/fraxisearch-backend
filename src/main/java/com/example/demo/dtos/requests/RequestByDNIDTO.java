package com.example.demo.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestByDNIDTO {
  private String dni;
  private ConditionsDTO conditions;
  
  public boolean dniResquestIsValid() {
    return this.dni != null && !this.dni.isEmpty() && this.dni.matches("^[0-9]{8}$");
  }
}
