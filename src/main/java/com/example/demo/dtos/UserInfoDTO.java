package com.example.demo.dtos;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.demo.entity.UserInfoEntity;
import com.example.demo.utils.DataUtils;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoDTO {
  private String names;
  private String fatherLastName;
  private String motherLastName;
  // Personal info
  private String dni;
  private String birthDate;
  private String phone;
  private String country;
  private String address;
  private List<String> emails;
  // Profesional info
  private String linkedin;
  private String github;
  // Social medias
  private String instagram;
  private String tikTok;
  private String facebook;
  private String twitter;
  private String youTube;
  private String twitch;
  
  public void formatNames() {
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

  public UserInfoEntity toEntity() {
    return UserInfoEntity.builder()
      .names(this.names)
      .fatherLastName(this.fatherLastName)
      .motherLastName(this.motherLastName)
      // Personal info
      .dni(this.dni)
      .birthDate(this.birthDate)
      .phone(this.phone)
      .country(this.country)
      .address(this.address)
      .emails(this.emails)
      // Profesional info
      .linkedin(this.linkedin)
      .github(this.github)
      // Social medias
      .instagram(this.instagram)
      .tikTok(this.tikTok)
      .facebook(this.facebook)
      .twitter(this.twitter)
      .youTube(this.youTube)
      .twitch(this.twitch)
      .build();
  }
}
