package xyz.kiridepapel.fraxisearchbackend.services.interfaces;

import xyz.kiridepapel.fraxisearchbackend.dtos.UserInfoDTO;
import xyz.kiridepapel.fraxisearchbackend.dtos.requests.RequestByDNIDTO;
import xyz.kiridepapel.fraxisearchbackend.dtos.requests.RequestBySingleNameDTO;

public interface ISeleniumService {
  public UserInfoDTO searchOnChromeBySingleName(RequestBySingleNameDTO requestDTO);
  public UserInfoDTO searchOnChromeByDNI(RequestByDNIDTO requestDTO);
}
