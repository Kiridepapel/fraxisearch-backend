package xyz.kiridepapel.fraxisearchbackend.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestByFullNameDTO {
  private String fullName;
}
