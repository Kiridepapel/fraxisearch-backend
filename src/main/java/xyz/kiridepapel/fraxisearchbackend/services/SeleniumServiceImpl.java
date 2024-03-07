package xyz.kiridepapel.fraxisearchbackend.services;

import java.net.URL;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.java.Log;
import xyz.kiridepapel.fraxisearchbackend.dtos.UserInfoDTO;
import xyz.kiridepapel.fraxisearchbackend.dtos.requests.RequestByDNIDTO;
import xyz.kiridepapel.fraxisearchbackend.dtos.requests.RequestBySingleNameDTO;
import xyz.kiridepapel.fraxisearchbackend.entity.UserInfoEntity;
import xyz.kiridepapel.fraxisearchbackend.exceptions.SecurityExceptions.NotFoundData;
import xyz.kiridepapel.fraxisearchbackend.repository.UserInfoDaoRepository;
import xyz.kiridepapel.fraxisearchbackend.services.interfaces.ISeleniumService;
import xyz.kiridepapel.fraxisearchbackend.utils.DataUtils;
import xyz.kiridepapel.fraxisearchbackend.utils.SeleniumUtils;

@Service
@Log
public class SeleniumServiceImpl implements ISeleniumService {
  // Variables de entorno
  @Value("${APP_PRODUCTION}")
  private boolean isProduction;
  @Value("${LINK_1}")
  private String link1;
  @Value("${LINK_2}")
  private String link2;
  @Value("${LINK_3}")
  private String link3;
  // Inyección de dependencias
  private final UserInfoDaoRepository userInfoDaoRepository;
  
  public SeleniumServiceImpl(UserInfoDaoRepository userInfoDaoRepository) {
    this.userInfoDaoRepository = userInfoDaoRepository;
  }

  // ? Configuración
  private ThreadLocal<RemoteWebDriver> setUp(ThreadLocal<RemoteWebDriver> driver) throws Exception {
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--start-maximized"); // Maximizar ventana
    options.addArguments("--disable-notifications"); // Desactivar notificaciones
    if (this.isProduction) {
      driver.set(new RemoteWebDriver(new URL("http://selenium-hub:4444"), options));
    } else {
      WebDriverManager.chromedriver().setup();
      driver.set(new ChromeDriver(options));
    }
    return driver;
  }

  private void closeBrowser(ThreadLocal<RemoteWebDriver> driver) {
    driver.get().quit();
    driver.remove();
  }

  // ? Armar información
  @Override
  public UserInfoDTO searchOnChromeBySingleName(RequestBySingleNameDTO requestDTO) {
    // Devuelve al usuario si lo encuentra en la BD
    Optional<UserInfoEntity> userEntity = Optional.empty();
    userEntity = this.userInfoDaoRepository.findByFullName(requestDTO.getNames(), requestDTO.getFatherLastName(), requestDTO.getMotherLastName());

    if (userEntity.isPresent()) {
      UserInfoDTO user = userEntity.get().toDTO();
      user.setIsInDataBase(true);
      return user;
    } else {
      // Controlador del navegador a usar
      ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<RemoteWebDriver>();

      try {
        // Configura y abre el navegador
        driver = this.setUp(driver);

        // ? Información básica
        UserInfoDTO user = new UserInfoDTO();
        user = this.findDNIByNames(driver.get(), requestDTO.getNames(), requestDTO.getFatherLastName(), requestDTO.getMotherLastName());
        if (user.getDni().equals("00000000")) {
          throw new NotFoundData("No se encontraron resultados");
        }

        // ? Fecha de naicmiento
        user = this.findBornDate(driver.get(), user);

        user.setIsInDataBase(false);
        return user;
      } catch (Exception e) {
        log.warning("Error en findByRequestSingleName(): " + e.getMessage());
        throw new NotFoundData(e.getMessage());
      } finally {
        // Cierra el navegador
        this.closeBrowser(driver);
      }
    }
  }

  @Override
  public UserInfoDTO searchOnChromeByDNI(RequestByDNIDTO requestDTO) {
    // Devuelve al usuario si lo encuentra en la BD
    Optional<UserInfoEntity> userEntity = Optional.empty();
    userEntity = this.userInfoDaoRepository.findByDni(requestDTO.getDni());

    if (userEntity.isPresent()) {
      UserInfoDTO user = userEntity.get().toDTO();
      user.setIsInDataBase(true);
      return user;
    } else {
      // Controlador del navegador a usar
      ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<RemoteWebDriver>();

      try {
        // Configura y abre el navegador
        driver = this.setUp(driver);

        // ? Información básica
        UserInfoDTO user = new UserInfoDTO();
        user = this.findNamesByDNI(driver.get(), requestDTO.getDni());
        if (user.getDni().equals("00000000")) {
          throw new NotFoundData("No se encontraron resultados");
        }

        // ? Fecha de naicmiento
        user = this.findBornDate(driver.get(), user);

        user.setIsInDataBase(false);
        return user;
      } catch (Exception e) {
        log.warning("Error en findByRequestDNI(): " + e.getMessage());
        throw new NotFoundData(e.getMessage());
      } finally {
        // Cierra el navegador
        this.closeBrowser(driver);
      }
    }
  }

  // ? Buscar información
  private UserInfoDTO findNamesByDNI(WebDriver driver, String value) { 
    try {
      UserInfoDTO user = new UserInfoDTO();
      // Abre la página web
      driver.get(this.link1);
      driver.manage().timeouts().implicitlyWait(SeleniumUtils.TIMEOUT);
      
      // Encuentra el campo de entrada y escribe texto en él
      WebElement inputField = driver.findElement(By.id("dni"));
      inputField.sendKeys(value);
      
      // Encuentra el botón por su texto visible y haz clic en él
      WebElement button = driver.findElement(By.xpath("//button[contains(text(),'BUSCAR')]"));
      button.click();
      // Espera que el texto de un elemento con ID "nombres" cambie
      SeleniumUtils.waitUntilTextChanges(driver, By.id("nombres"));

      // Obtiene el HTML después de hacer click
      String html = driver.getPageSource();
      Document document = Jsoup.parse(html);
      
      // Asignar valores
      user.setNames(document.select("#nombres").text());
      user.setFatherLastName(DataUtils.firstUpper(document.select("#ape_pat").text()));
      user.setMotherLastName(DataUtils.firstUpper(document.select("#ape_mat").text()));
      user.setDni(document.select("#numero").text());
      
      return user;
    } catch (Exception e) {
      log.warning("Error en findNamesByDNI(): " + e.getMessage());
      throw new NotFoundData("No se encontraron resultados");
    }
  }

  private UserInfoDTO findDNIByNames(WebDriver driver, String names, String fatherLastName, String motherLastName) {
    try {
      UserInfoDTO user = new UserInfoDTO();
      // Abre la página web
      driver.get(this.link2);  
      driver.manage().timeouts().implicitlyWait(SeleniumUtils.TIMEOUT);

      // Encuentra el campo de entrada y escribe texto en él
      WebElement inputNames = driver.findElement(By.id("nombres"));
      WebElement inputFatherLastNames = driver.findElement(By.id("ape_pat"));
      WebElement inputMotherLastNames = driver.findElement(By.id("ape_mat"));
      inputNames.sendKeys(names);
      inputFatherLastNames.sendKeys(fatherLastName);
      inputMotherLastNames.sendKeys(motherLastName);
      
      // Encuentra el botón por su texto visible y haz clic en él
      WebElement button = driver.findElement(By.xpath("//button[contains(text(),'BUSCAR')]"));
      button.click();
      // Espera que el texto de un elemento con ID "numero2" cambie
      SeleniumUtils.waitUntilTextChanges(driver, By.id("numero2"));
      
      // Obtiene el HTML después de hacer click
      String html = driver.getPageSource();
      Document document = Jsoup.parse(html);
      
      // Asignar valores
      user.setNames(names);
      user.setFatherLastName(fatherLastName);
      user.setMotherLastName(motherLastName);
      user.setDni(document.select("#numero2").text().trim());

      return user;
    } catch (Exception e) {
      log.warning("Error en findDNIByNames(): " + e.getMessage());
      throw new NotFoundData("No se encontraron resultados");
    }
  }

  private UserInfoDTO findBornDate(WebDriver driver, UserInfoDTO user) {
    try {
      // Abre la página web
      driver.get(this.link3);
      driver.manage().timeouts().implicitlyWait(SeleniumUtils.TIMEOUT);
      
      // Encuentra el campo de entrada y escribe texto en él
      // Encuentra el campo de entrada y escribe texto en él
      WebElement inputNames = driver.findElement(By.id("nombres"));
      WebElement inputFatherLastNames = driver.findElement(By.id("ape_pat"));
      WebElement inputMotherLastNames = driver.findElement(By.id("ape_mat"));
      inputNames.sendKeys(user.getNames());
      inputFatherLastNames.sendKeys(user.getFatherLastName());
      inputMotherLastNames.sendKeys(user.getMotherLastName());  
      
      // Encuentra el botón por su texto visible y haz clic en él
      WebElement button = driver.findElement(By.xpath("//button[contains(text(),'BUSCAR')]"));
      button.click();
      // Espera que el texto de un elemento con ID "fecha_cumpleanios" cambie
      SeleniumUtils.waitUntilTextChanges(driver, By.id("fecha_cumpleanios"));

      // Obtiene el HTML después de hacer click
      String html = driver.getPageSource();
      Document document = Jsoup.parse(html);

      String bornDate = document.select("#fecha_cumpleanios").text().split("cumple años el")[1].trim();
      bornDate = DataUtils.parseDate(bornDate, "dd/MM/yyyy", "dd 'de' MMMM 'de' yyyy", 0);
      
      // Asignar valores
      user.setBornDate(bornDate);
      
      return user;
    } catch (Exception e) {
      log.warning("Error en findBirthDate(): " + e.getMessage());
      throw new NotFoundData("No se encontraron resultados");
    }
  }
}
