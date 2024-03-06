package xyz.kiridepapel.fraxisearchbackend.dtos.requests;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import xyz.kiridepapel.fraxisearchbackend.utils.DataUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestBySingleNameDTO {
  private String names;
  private String fatherLastName;
  private String motherLastName;
  private ConditionsDTO conditions;
  
  public boolean namesRequestIsValid() {
    return this.names != null && !this.names.isEmpty() && this.names.matches("^[a-zA-Z]+( [a-zA-Z]+)*$") &&
      this.names.length() >= 3 && this.names.length() <= 50 &&
      this.fatherLastName != null && !this.fatherLastName.isEmpty() && this.fatherLastName.matches("^[a-zA-Z]+( [a-zA-Z]+)*$") &&
      this.fatherLastName.length() >= 3 && this.fatherLastName.length() <= 50 &&
      this.motherLastName != null && !this.motherLastName.isEmpty() && this.motherLastName.matches("^[a-zA-Z]+( [a-zA-Z]+)*$") &&
      this.motherLastName.length() >= 3 && this.motherLastName.length() <= 50;
  }
  
  public void formatNames() {
    if (this.names != null && this.fatherLastName != null && this.motherLastName != null &&
        !this.names.isEmpty() && !this.fatherLastName.isEmpty() && !this.motherLastName.isEmpty()) {
      // Convierte los nombres a minúsculas
      this.names = this.names.toLowerCase();
      this.fatherLastName = this.fatherLastName.toLowerCase();
      this.motherLastName = this.motherLastName.toLowerCase();

      // Crea un patrón para buscar palabras que no sean "de", "la", "el", "los", "las", "y", "del" y que sean letras
      Pattern pattern = Pattern.compile("\\b(d|c)\\b|\\b(?!(?:de|la|el|los|las|y|del)\\b)\\p{L}");
      // Instancia matchers que buscarán los segmentos que coincidan con el patrón
      Matcher matcherNames = pattern.matcher(this.names);
      Matcher matcherFatherLastName = pattern.matcher(this.fatherLastName);
      Matcher matcherMotherLastName = pattern.matcher(this.motherLastName);
      StringBuilder sbNames = new StringBuilder();
      StringBuilder sbFatherLastName = new StringBuilder();
      StringBuilder sbMotherLastName = new StringBuilder();

      // Itera sobre los segmentos encontrados y los reemplaza por el mismo segmento en mayúsculas
      while (matcherNames.find()) {
        matcherNames.appendReplacement(sbNames, matcherNames.group().toUpperCase());
      }
      while (matcherFatherLastName.find()) {
        matcherFatherLastName.appendReplacement(sbFatherLastName, matcherFatherLastName.group().toUpperCase());
      }
      while (matcherMotherLastName.find()) {
        matcherMotherLastName.appendReplacement(sbMotherLastName, matcherMotherLastName.group().toUpperCase());
      }

      // Agrega el segmento fallido al final del StringBuilder
      matcherNames.appendTail(sbNames);
      matcherFatherLastName.appendTail(sbFatherLastName);
      matcherMotherLastName.appendTail(sbMotherLastName);
    
      // Asigna los valores modificados
      this.names = DataUtils.firstUpper(sbNames.toString());
      this.fatherLastName = DataUtils.firstUpper(sbFatherLastName.toString());
      this.motherLastName = DataUtils.firstUpper(sbMotherLastName.toString());
    }
  }
}
