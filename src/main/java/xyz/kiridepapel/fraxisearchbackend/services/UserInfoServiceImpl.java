package xyz.kiridepapel.fraxisearchbackend.services;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import xyz.kiridepapel.fraxisearchbackend.dtos.UserInfoDTO;
import xyz.kiridepapel.fraxisearchbackend.dtos.requests.RequestByDNIDTO;
import xyz.kiridepapel.fraxisearchbackend.dtos.requests.RequestBySingleNameDTO;
import xyz.kiridepapel.fraxisearchbackend.repository.UserInfoDaoRepository;
import xyz.kiridepapel.fraxisearchbackend.services.interfaces.ISeleniumService;
import xyz.kiridepapel.fraxisearchbackend.services.interfaces.IUserInfoService;
import xyz.kiridepapel.fraxisearchbackend.utils.DataUtils;

@Service
public class UserInfoServiceImpl implements IUserInfoService {
  // Variables de entorno
  @Value("${APP_PRODUCTION}")
  private boolean isProduction;
  // Variables
  public static boolean forceSearch = false;
  // Inyección de dependencias
  private final UserInfoDaoRepository userInfoDaoRepository;
  private final ISeleniumService ISeleniumService;
  // Constructor
  public UserInfoServiceImpl(UserInfoDaoRepository userInfoDaoRepository, ISeleniumService ISeleniumService) {
    this.userInfoDaoRepository = userInfoDaoRepository;
    this.ISeleniumService = ISeleniumService;
  }
  
  @Override
  public UserInfoDTO searchBySingleName(RequestBySingleNameDTO requestDTO) {
    requestDTO.upperNames();
    UserInfoDTO user = this.ISeleniumService.searchOnChromeBySingleName(requestDTO);
    // Busca la información de la misma forma ambas formas de búsqueda
    return this.searchBothInfo(user);
  }

  @Override
  public UserInfoDTO searchByDNI(RequestByDNIDTO requestDTO) {
    UserInfoDTO user = this.ISeleniumService.searchOnChromeByDNI(requestDTO);
    // Busca la información de la misma forma en ambos métodos
    return this.searchBothInfo(user);
  }

  private UserInfoDTO searchBothInfo(UserInfoDTO user) {
    if (user.getIsInDataBase() == false) {
      // * Fecha de cumpleaños y edad
      user = this.calculateBirthDateAndAge(user);

      // * Formato de la Fecha de nacimiento
      user.setBornDate(DataUtils.parseDate(user.getBornDate(), "dd/MM/yyyy", "dd 'de' MMMM 'de' yyyy", 0));

      // Guarda al usuario en la BD
      this.userInfoDaoRepository.save(user.toEntity());
    }

    user.setIsInDataBase(null);
    user.formatNames();
    return user;
  }

  private UserInfoDTO calculateBirthDateAndAge(UserInfoDTO user) {
    LocalDate actualDate = DataUtils.getLocalDateTimeNow(this.isProduction).toLocalDate();
    LocalDate bornDate = LocalDate.parse(user.getBornDate(), DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("es", "ES")));
    
    boolean alreadyTurnedYears = false;
    if (actualDate.getMonthValue() > bornDate.getMonthValue()) {
      alreadyTurnedYears = true;
    } else if (actualDate.getMonthValue() == bornDate.getMonthValue() && actualDate.getDayOfMonth() >= bornDate.getDayOfMonth()) {
      alreadyTurnedYears = true;
    }

    Integer ageForBirthDate = actualDate.getYear() - bornDate.getYear();
    Integer calculatedAge = actualDate.getYear() - bornDate.getYear();

    if (alreadyTurnedYears == true) {
      ageForBirthDate++;
    } else {
      calculatedAge--;
    }

    String birthDate = DataUtils.parseDate(user.getBornDate(), "dd/MM/yyyy", "dd 'de' MMMM 'de' yyyy", ageForBirthDate);
    Integer age = Integer.parseInt(String.format("%02d", calculatedAge));

    user.setNextBirthDate(birthDate);
    user.setAge(age);

    return user;
  }
}
