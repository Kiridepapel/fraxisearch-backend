package com.example.demo.services;

import java.time.Duration;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dtos.RequestConditionsDTO;
import com.example.demo.dtos.UserInfoDTO;
import com.example.demo.dtos.RequestConditionsDTO.ConditionsDTO;
import com.example.demo.entity.UserInfoEntity;
import com.example.demo.repository.UserInfoRepository;
import com.example.demo.utils.DataUtils;

import io.github.bonigarcia.wdm.WebDriverManager;
import lombok.extern.java.Log;

@Service
// @SuppressWarnings("unused")
@Log
public class UserInfoService {
  @Autowired
  private UserInfoRepository userInfoRepository;

  public void test() {
    // * Controlador del navegador a usar
    WebDriverManager.chromedriver().setup();
    WebDriver driver = new ChromeDriver();

    try {
      driver.get("https://jkanime.net/saijaku-tamer-wa-gomi-hiroi-no-tabi-wo-hajimemashita/8");

      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".player_conte")));
      // Thread.sleep(2000);
      // Obtiene el HTML después de hacer click
      String html = driver.getPageSource();
      Document document = Jsoup.parse(html);

      log.info("document: " + document);

    } catch (Exception e) {
      log.warning(e.getMessage());
      throw new RuntimeException(e);
    } finally {
      driver.quit();
    }
  }

  // Validar que el usuario no exista en la base de datos y seleccionar el tipo de búsqueda
  public UserInfoDTO validateDBAndSelectTypeOfSearch(RequestConditionsDTO request) {
    // * Variables
    Optional<UserInfoEntity> userEntity = Optional.empty();
    UserInfoDTO user = new UserInfoDTO();

    // * Buscar en la base de datos (si encuentra al usuario en la BD, lo retorna)
    if (request.namesRequestIsValid()) {
      userEntity = this.userInfoRepository.findByFullName(request.getNames(), request.getFatherLastName(), request.getMotherLastName());
    } else if (request.dniResquestIsValid()) {
      userEntity = this.userInfoRepository.findByDni(request.getDni());
    } else {
      throw new RuntimeException("Los datos de búsqueda no son válidos");
    }

    // * Valida si el usuario fue encontrado en la BD (si no, lo busca y lo guarda)
    if (userEntity.isPresent()) {
      return this.constructResponse(userEntity.get().toDTO(), request.getConditions());
    } else {
      // * Controlador del navegador a usar
      WebDriverManager.chromedriver().setup();
      WebDriver driver = new ChromeDriver();
      
      try {
        // * Recopila: Información básica
        if (request.namesRequestIsValid()) {
          user = this.findDNIByNames(driver, request.getNames(), request.getFatherLastName(), request.getMotherLastName());
        } else if (request.dniResquestIsValid()) {
          user = this.findNamesByDNI(driver, request.getDni());
        }
        user.formatNames(); // Le da formato a los nombres
        userEntity = Optional.of(user.toEntity());

        // * Recopila: Cumpleaños
        user = this.findBirthDate(driver, user);

        // * Guardar
        this.userInfoRepository.save(user.toEntity());
        // * Retorna
        return this.constructResponse(user, request.getConditions());
      } catch (Exception e) {
        log.warning(e.getMessage());
        throw new RuntimeException(e);
      } finally {
        driver.quit(); // Cierra el navegador
      }
    }
  }

  // Métodos de búsqueda
  private UserInfoDTO findNamesByDNI(WebDriver driver, String value) { 
    try {
      UserInfoDTO user = new UserInfoDTO();
      // Abre la página web
      driver.get("https://el-dni.com/index.php");
      
      // Encuentra el campo de entrada y escribe texto en él
      WebElement inputField = driver.findElement(By.id("dni"));
      inputField.sendKeys(value);
      
      // Encuentra el botón por su texto visible y haz clic en él
      WebElement button = driver.findElement(By.xpath("//button[contains(text(),'BUSCAR')]"));
      button.click();
      // Espera a que la página haya terminado de cargar el primer elemento a usar con un tiempo máximo de 5 segundos
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#nombres")));
      Thread.sleep(1500);
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
      log.warning(e.getMessage());
        throw new RuntimeException(e);
    }
  }

  private UserInfoDTO findDNIByNames(WebDriver driver, String names, String fatherLastName, String motherLastName) {
    try {
      UserInfoDTO user = new UserInfoDTO();
      // Abre la página web
      driver.get("https://el-dni.com/buscar-dni-por-nombre.php");  
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
      // Espera a que la página haya terminado de cargar el primer elemento a usar con un tiempo máximo de 5 segundos
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#numero2")));
      Thread.sleep(1500);
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
      log.warning(e.getMessage());
      throw new RuntimeException(e);
    }
  }

  private UserInfoDTO findBirthDate(WebDriver driver, UserInfoDTO user) {
    try {
      // Abre la página web
      driver.get("https://el-dni.com/buscar-cumpleanios-por-nombres.php");
      
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
      // Espera a que la página haya terminado de cargar el primer elemento a usar con un tiempo máximo de 5 segundos
      WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#nombres")));
      Thread.sleep(1500);
      // Obtiene el HTML después de hacer click
      String html = driver.getPageSource();
      Document document = Jsoup.parse(html);
      
      // Asignar valores
      user.setBirthDate(document.select("#fecha_cumpleanios").text().split("cumple años el")[1].trim());
      
      return user;
    } catch (Exception e) {
      log.warning(e.getMessage());
        throw new RuntimeException(e);
    }
  }

  // Métodos de respuesta
  private UserInfoDTO constructResponse(UserInfoDTO user, ConditionsDTO conditions) {
    // Personal info
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

    return user;
  }
}
