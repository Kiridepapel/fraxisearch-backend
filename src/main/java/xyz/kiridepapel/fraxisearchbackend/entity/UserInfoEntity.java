package xyz.kiridepapel.fraxisearchbackend.entity;

import java.util.List;

import xyz.kiridepapel.fraxisearchbackend.dtos.UserInfoDTO;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_info")
public class UserInfoEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
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

  public UserInfoDTO toDTO() {
    return UserInfoDTO.builder()
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

  public boolean isAllDataInDB() {
    return this.id != null
      && this.names != null
      && this.fatherLastName != null
      && this.motherLastName != null
      // Personal info
      && this.dni != null
      && this.birthDate != null
      && this.phone != null
      && this.country != null
      && this.address != null
      && !this.emails.isEmpty()
      // Profesional info
      && this.linkedin != null
      && this.github != null
      // Social medias
      && this.instagram != null
      && this.tikTok != null
      && this.facebook != null
      && this.twitter != null
      && this.youTube != null
      && this.twitch != null;
  }

  public void update(UserInfoDTO user) {
    this.names = user.getNames();
    this.fatherLastName = user.getFatherLastName();
    this.motherLastName = user.getMotherLastName();
    // Personal info
    this.dni = user.getDni();
    this.birthDate = user.getBirthDate();
    this.phone = user.getPhone();
    this.country = user.getCountry();
    this.address = user.getAddress();
    this.emails = user.getEmails();
    // Profesional info
    this.linkedin = user.getLinkedin();
    this.github = user.getGithub();
    // Social medias
    this.instagram = user.getInstagram();
    this.tikTok = user.getTikTok();
    this.facebook = user.getFacebook();
    this.twitter = user.getTwitter();
    this.youTube = user.getYouTube();
    this.twitch = user.getTwitch();
  }
}
