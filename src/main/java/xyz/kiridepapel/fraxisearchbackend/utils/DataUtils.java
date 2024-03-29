package xyz.kiridepapel.fraxisearchbackend.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
// import java.util.Map;

import org.springframework.stereotype.Component;

import xyz.kiridepapel.fraxisearchbackend.exceptions.SecurityExceptions.ProtectedResource;

@Component
public class DataUtils {
  public static List<String> mayusMonths = List.of(
    "enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
  );
  
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
  
  // ? Dates
  public static LocalDateTime getLocalDateTimeNow(Boolean isProduction) {
    return isProduction ? LocalDateTime.now().minusHours(5) : LocalDateTime.now();
  }

  public static Date getDateNow(Boolean isProduction) {
    return isProduction ? new Date(System.currentTimeMillis() - 18000000) : new Date();
  }
  
  public static String parseDate(String date, String formatIn, String formatOut, int yearsToModify) {
    if (date == null || date.isEmpty()) {
      return null;
    }

    DateTimeFormatter formatterIn = DateTimeFormatter.ofPattern(formatIn, new Locale("es", "ES"));
    DateTimeFormatter formatterOut = DateTimeFormatter.ofPattern(formatOut, new Locale("es", "ES"));

    LocalDate currentDate = LocalDate.parse(date, formatterIn);
    LocalDate nextChapterDate = currentDate.plusYears(yearsToModify);

    String outputDate = nextChapterDate.format(formatterOut);
    for (String month : mayusMonths) {
      if (outputDate.contains(month)) {
        outputDate = outputDate.replace(month, firstUpper(month));
      }
    }

    return outputDate;
  }
}
