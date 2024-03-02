package com.example.demo.controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.UserInfoDTO;
import com.example.demo.dtos.requests.RequestByDNIDTO;
import com.example.demo.dtos.requests.RequestByFullNameDTO;
import com.example.demo.dtos.requests.RequestBySingleNameDTO;
import com.example.demo.services.UserInfoService;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/api/v1")
@SuppressWarnings("unused")
public class UserInfoController {
  // Variables de entorno
  @Value("${FRONTEND_URL}")
  private String frontendUrl;
  // Inyección de dependencias
  @Autowired
  private UserInfoService userInfoService;
  // Variables
  private List<String> allowedOrigins;
  
  // Constructor
  @PostConstruct
  public void init() {
    this.allowedOrigins = Arrays.asList(frontendUrl);
  }
  
  // Métodos principales
  @GetMapping("/find-by-full-name")
  public ResponseEntity<?> findByNames(@RequestBody(required = true) RequestByFullNameDTO request) throws Exception {
    // Validaciones
    // DataUtils.verifyAllowedOrigin(this.allowedOrigins, request.getHeader("Origin"));

    // Arma el objeto general de búsqueda por nombres apellidos
    RequestBySingleNameDTO requestDTO = new RequestBySingleNameDTO();
    String[] nameSplit = request.getFullName().split(" ");
    if (nameSplit.length == 3) {
      requestDTO.setNames(nameSplit[0]);
      requestDTO.setFatherLastName(nameSplit[1]);
      requestDTO.setMotherLastName(nameSplit[2]);
    } else if (nameSplit.length == 4) {
      requestDTO.setNames(nameSplit[0] + " " + nameSplit[1]);
      requestDTO.setFatherLastName(nameSplit[2]);
      requestDTO.setMotherLastName(nameSplit[3]);
    } else {
      throw new RuntimeException("El nombre ingresado no es válido");
    }
    
    // Verifica que se haya ingresado algún dato
    if (!requestDTO.namesRequestIsValid()) {
      throw new RuntimeException("No se ha ingresado ningún dato");
    }
    // Formatea los nombres para que coincidan con los de la BD
    requestDTO.formatNames();

    UserInfoDTO userInfo = this.userInfoService.searchByRequestSingleName(requestDTO);
    return new ResponseEntity<>(userInfo, HttpStatus.OK);
  }

  @GetMapping("/find-by-single-name")
  public ResponseEntity<?> findByNames(@RequestBody(required = true) RequestBySingleNameDTO requestDTO) throws Exception {
    // Validaciones
    // DataUtils.verifyAllowedOrigin(this.allowedOrigins, request.getHeader("Origin"));
    
    // Verifica que se haya ingresado algún dato
    if (!requestDTO.namesRequestIsValid()) {
      throw new RuntimeException("No se ha ingresado ningún dato");
    }
    // Formatea los nombres para que coincidan con los de la BD
    requestDTO.formatNames();

    UserInfoDTO userInfo = this.userInfoService.searchByRequestSingleName(requestDTO);
    return new ResponseEntity<>(userInfo, HttpStatus.OK);
  }

  @GetMapping("/find-by-dni")
  public ResponseEntity<?> findByDNI(@RequestBody(required = true) RequestByDNIDTO requestDTO) throws Exception {
    // Validaciones
    // DataUtils.verifyAllowedOrigin(this.allowedOrigins, request.getHeader("Origin"));

    // Verifica que se haya ingresado algún dato
    if (!requestDTO.dniResquestIsValid()) {
      throw new RuntimeException("No se ha ingresado ningún dato");
    }

    UserInfoDTO userInfo = this.userInfoService.searchByRequestDNI(requestDTO);
    return new ResponseEntity<>(userInfo, HttpStatus.OK);
  }

  // Métodos de prueba
  @GetMapping("/test")
  public ResponseEntity<?> test() throws Exception {
    return new ResponseEntity<>(this.userInfoService.test(), HttpStatus.OK);
  }

}
