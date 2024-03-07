package xyz.kiridepapel.fraxisearchbackend.services.interfaces;

import xyz.kiridepapel.fraxisearchbackend.dtos.UserInfoDTO;
import xyz.kiridepapel.fraxisearchbackend.dtos.requests.RequestByDNIDTO;
import xyz.kiridepapel.fraxisearchbackend.dtos.requests.RequestBySingleNameDTO;

public interface IUserInfoService {
  public UserInfoDTO searchBySingleName(RequestBySingleNameDTO requestDTO);
  public UserInfoDTO searchByDNI(RequestByDNIDTO requestDTO);
}
