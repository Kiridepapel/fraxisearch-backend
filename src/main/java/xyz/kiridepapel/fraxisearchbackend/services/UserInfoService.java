package xyz.kiridepapel.fraxisearchbackend.services;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import xyz.kiridepapel.fraxisearchbackend.dtos.UserInfoDTO;
import xyz.kiridepapel.fraxisearchbackend.dtos.requests.ConditionsDTO;
import xyz.kiridepapel.fraxisearchbackend.dtos.requests.RequestByDNIDTO;
import xyz.kiridepapel.fraxisearchbackend.dtos.requests.RequestBySingleNameDTO;
import xyz.kiridepapel.fraxisearchbackend.entity.UserInfoEntity;
import xyz.kiridepapel.fraxisearchbackend.exceptions.SecurityExceptions.NotFoundData;
import xyz.kiridepapel.fraxisearchbackend.repository.UserInfoRepository;
import xyz.kiridepapel.fraxisearchbackend.utils.DataUtils;
import xyz.kiridepapel.fraxisearchbackend.utils.SeleniumUtils;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.java.Log;

@Service
@Log
public class UserInfoService {
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
  @Autowired
  private UserInfoRepository userInfoRepository;


  // ? Prueba
  public String test() throws Exception {
    ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<RemoteWebDriver>();
    driver = this.setUp(driver);
    driver.get().get("https://jkanime.net/boku-no-kokoro-no-yabai-yatsu/2/");
    driver.get().manage().timeouts().implicitlyWait(SeleniumUtils.TIMEOUT);

    // // Images of chapters
    // List<String> dataSetbgList = new ArrayList<>();
    // List<WebElement> elements = driver.get().findElements(By.cssSelector(".homemini"));

    // for (WebElement element : elements) {
    //   dataSetbgList.add(element.getAttribute("data-setbg"));
    // }

    // Body
    WebElement bodyElement = driver.get().findElement(By.tagName("body"));
    String bodyHtml = bodyElement.getAttribute("innerHTML");
    bodyHtml = bodyHtml.replaceAll("(?s)<script[^>]*>.*?</script>", "");
    bodyHtml = bodyHtml.replaceAll("(?s)<style[^>]*>.*?</style>", "");
    Document document = Jsoup.parse(bodyHtml);

    // for (String dataSetbg : dataSetbgList) {
    //   log.info("data-setbg: " + dataSetbg);
    // }
    // log.info("--------------------");
    log.info("Contenido del body: " + document);

    this.closeBrowser(driver);
    return "Ok";
  }

  public String test2() throws IOException {
    Document doc = Jsoup.connect("https://jkanime.org/boku-no-kokoro-no-yabai-yatsu").get();
    // Elimina las etiquetas <script> del documento
    Elements scripts = doc.select("script");
    scripts.remove();
    // Elimina las etiquetas <style> del documento
    Elements styles = doc.select("style");
    styles.remove();
    log.info("doc: " + doc);
    return "Ok";
  }

  public String login() throws Exception {
    // Datos
    String email = "email";
    String password = "password";

    // Controlador del navegador a usar
    ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<RemoteWebDriver>();
    driver = this.setUp(driver);
    driver.get().get("https://login.microsoftonline.com");
    driver.get().manage().timeouts().implicitlyWait(SeleniumUtils.TIMEOUT);
    WebDriverWait wait = new WebDriverWait(driver.get(), Duration.ofSeconds(10));

    // Encontrar el campo de correo electrónico y escribir tu correo
    WebElement emailField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("i0116")));
    emailField.sendKeys(email);

    // Hacer clic en el botón "Siguiente"
    WebElement nextButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("idSIButton9")));
    nextButton.click();

    // Esperar a que se cargue la página y encontrar el campo de contraseña
    WebElement passwordField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("i0118")));
    passwordField.sendKeys(password);

    // Hacer clic en el botón "Iniciar sesión"
    WebElement signInButton = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("idSIButton9")));
    signInButton.click();

    Thread.sleep(10000);

    this.closeBrowser(driver);

    return "";
  }

  public String doc() throws Exception {
    ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<RemoteWebDriver>();
    driver = this.setUp(driver);
    driver.get().get("https://jkanime.net/saijaku-tamer-wa-gomi-hiroi-no-tabi-wo-hajimemashita/8");
    driver.get().manage().timeouts().implicitlyWait(SeleniumUtils.TIMEOUT);

    try {
      String html = driver.get().getPageSource();
      Document document = Jsoup.parse(html);

      log.info("hola: " + document);

      return "";
    } catch (Exception e) {
      log.warning(e.getMessage());
      throw new RuntimeException(e);
    } finally {
      this.closeBrowser(driver);
    }
  }

  // ? Métodos de configuración
  public ThreadLocal<RemoteWebDriver> setUp(ThreadLocal<RemoteWebDriver> driver) throws Exception {
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

  public void closeBrowser(ThreadLocal<RemoteWebDriver> driver) {
    driver.get().quit();
    driver.remove();
  }

  // ? Métodos para armar la información con la que se va a trabajar aplicando condiciones
  public UserInfoDTO searchByRequestSingleName(RequestBySingleNameDTO requestDTO) {
    // * Buscar en la base de datos
    Optional<UserInfoEntity> userEntity = Optional.empty();
    userEntity = this.userInfoRepository.findByFullName(requestDTO.getNames(), requestDTO.getFatherLastName(), requestDTO.getMotherLastName());

    if (userEntity.isPresent()) {
      // * Sí se encuentra en la BD: Devuelve el usuario
      return this.constructResponse(userEntity.get().toDTO(), requestDTO.getConditions());
    } else {
      // * No se encuentra en la BD
      try {
        // Controlador del navegador a usar
        ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<RemoteWebDriver>();
        driver = this.setUp(driver);

        // * Busca su información básica
        // Arma el usuario
        UserInfoDTO user = new UserInfoDTO();
        user = this.findDNIByNames(driver.get(), requestDTO.getNames(), requestDTO.getFatherLastName(), requestDTO.getMotherLastName());
        // Verifica si se encontró el usuario
        if (user.getDni().equals("00000000")) {
          throw new NotFoundData("No se encontraron resultados");
        }

        // * Busca su información adicional para ambos casos
        user = this.searchExtraInfo(driver.get(), user, requestDTO.getConditions());

        // * Cierra el navegador
        this.closeBrowser(driver);

        // * Retorna el usuario
        return user;
      } catch (Exception e) {
        log.warning("Error en findByRequestSingleName(): " + e.getMessage());
        throw new NotFoundData(e.getMessage());
      }
    }
  }

  public UserInfoDTO searchByRequestDNI(RequestByDNIDTO requestDTO) {
    // * Buscar en la base de datos
    Optional<UserInfoEntity> userEntity = Optional.empty();
    userEntity = this.userInfoRepository.findByDni(requestDTO.getDni());

    if (userEntity.isPresent()) {
      // * Sí se encuentra en la BD: Devuelve el usuario
      return this.constructResponse(userEntity.get().toDTO(), requestDTO.getConditions());
    } else {
      // * No se encuentra en la BD
      try {
        // Controlador del navegador a usar
        ThreadLocal<RemoteWebDriver> driver = new ThreadLocal<RemoteWebDriver>();
        driver = this.setUp(driver);

        // * Busca su información básica
        // Arma el usuario
        UserInfoDTO user = new UserInfoDTO();
        user = this.findNamesByDNI(driver.get(), requestDTO.getDni());
        // Verifica si se encontró el usuario
        if (user.getDni().equals("00000000")) {
          throw new NotFoundData("No se encontraron resultados");
        }

        // * Busca su información adicional para ambos casos
        user = this.searchExtraInfo(driver.get(), user, requestDTO.getConditions());

        // * Cierra el navegador
        this.closeBrowser(driver);

        // * Retorna el usuario
        return user;
      } catch (Exception e) {
        log.warning("Error en findByRequestDNI(): " + e.getMessage());
        throw new NotFoundData(e.getMessage());
      }
    }
  }

  private UserInfoDTO searchExtraInfo(WebDriver driver, UserInfoDTO user, ConditionsDTO conditions) {
    // * Busca su cumpleaños
    user = this.findBirthDate(driver, user);
    
    // * Formatea los nombres y guarda en la BD
    user.formatNames();
    this.userInfoRepository.save(user.toEntity());
  
    // * Retorna el usuario con la infomación solicitada
    return this.constructResponse(user, conditions);
  }

  // ? Métodos de búsqueda
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

  private UserInfoDTO findBirthDate(WebDriver driver, UserInfoDTO user) {
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
      
      // Asignar valores
      user.setBirthDate(document.select("#fecha_cumpleanios").text().split("cumple años el")[1].trim());
      
      return user;
    } catch (Exception e) {
      log.warning("Error en findBirthDate(): " + e.getMessage());
      throw new NotFoundData("No se encontraron resultados");
    }
  }

  // ? Métodos de respuesta
  private UserInfoDTO constructResponse(UserInfoDTO user, ConditionsDTO conditions) {
    // Personal info
    if (conditions != null)  {
      if (conditions.getShowPersonalInfo() == false){
        user.setDni(null);
        user.setBirthDate(null);
        user.setPhone(null);
        user.setCountry(null);
        user.setAddress(null);
        user.setEmails(null);
      }
      // Profesional info
      if (conditions.getShowProfesionalInfo() == false) {
        user.setLinkedin(null);
        user.setGithub(null);
      }
      // Social medias
      if (conditions.getShowSocialMedias() == false) {
        user.setInstagram(null);
        user.setTikTok(null);
        user.setFacebook(null);
        user.setTwitter(null);
        user.setYouTube(null);
        user.setTwitch(null);
      }
    }

    return user;
  }
}
