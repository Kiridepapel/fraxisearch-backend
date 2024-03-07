package xyz.kiridepapel.fraxisearchbackend.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestByDNIDTO {
  private String dni;
  
  public boolean dniResquestIsValid() {
    return this.dni != null && !this.dni.isEmpty() && this.dni.matches("^[0-9]{8}$");
  }
}
