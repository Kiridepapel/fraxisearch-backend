package xyz.kiridepapel.fraxisearchbackend.utils;

import java.util.List;

import org.springframework.stereotype.Component;

import xyz.kiridepapel.fraxisearchbackend.exceptions.SecurityExceptions.ProtectedResource;

@Component
public class DataUtils {
  
  // Convierte la primera letra a mayúscula
  public static String firstUpper(String text) {
    return text.substring(0, 1).toUpperCase() + text.substring(1);
  }

  // ? Validations
  public static void verifyAllowedOrigin(List<String> allowedOrigins, String origin) {
    if (origin == null || !allowedOrigins.contains(origin)) {
      throw new ProtectedResource("Acceso denegado TK-001");
    }
  }
}
