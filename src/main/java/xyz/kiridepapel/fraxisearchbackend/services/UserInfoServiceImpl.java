package xyz.kiridepapel.fraxisearchbackend.services;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import xyz.kiridepapel.fraxisearchbackend.dtos.UserInfoDTO;
import xyz.kiridepapel.fraxisearchbackend.dtos.requests.RequestByDNIDTO;
import xyz.kiridepapel.fraxisearchbackend.dtos.requests.RequestBySingleNameDTO;
import xyz.kiridepapel.fraxisearchbackend.repository.UserInfoDaoRepository;
import xyz.kiridepapel.fraxisearchbackend.services.interfaces.ISeleniumService;
import xyz.kiridepapel.fraxisearchbackend.services.interfaces.IUserInfoService;

@Service
public class UserInfoServiceImpl implements IUserInfoService {
  // Variables de entorno
  @Value("${APP_PRODUCTION}")
  private boolean isProduction;
  // Inyección de dependencias
  private final UserInfoDaoRepository userInfoDaoRepository;
  private final ISeleniumService ISeleniumService;
  
  public UserInfoServiceImpl(UserInfoDaoRepository userInfoDaoRepository, ISeleniumService ISeleniumService) {
    this.userInfoDaoRepository = userInfoDaoRepository;
    this.ISeleniumService = ISeleniumService;
  }
  
  @Override
  public UserInfoDTO searchBySingleName(RequestBySingleNameDTO requestDTO) {
    requestDTO.upperNames();
    UserInfoDTO user = this.ISeleniumService.searchOnChromeBySingleName(requestDTO);
    
    if (user.getIsInDataBase() == false) {
      // continua la busqueda de datos

      // Guarda al usuario en la BD
      this.userInfoDaoRepository.save(user.toEntity());
    }

    user.setIsInDataBase(null);
    user.formatNames();
    return user;
  }

  @Override
  public UserInfoDTO searchByDNI(RequestByDNIDTO requestDTO) {
    UserInfoDTO user = this.ISeleniumService.searchOnChromeByDNI(requestDTO);

    if (user.getIsInDataBase() == false) {
      // continua la busqueda de datos

      // Guarda al usuario en la BD
      this.userInfoDaoRepository.save(user.toEntity());
    }

    user.setIsInDataBase(null);
    user.formatNames();
    return user;
  }

  public UserInfoDTO calculateAge(UserInfoDTO user) {
    return null;
  }
}
