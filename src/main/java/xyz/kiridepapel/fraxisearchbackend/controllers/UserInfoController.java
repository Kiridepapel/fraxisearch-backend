package xyz.kiridepapel.fraxisearchbackend.controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import xyz.kiridepapel.fraxisearchbackend.dtos.UserInfoDTO;
import xyz.kiridepapel.fraxisearchbackend.dtos.requests.RequestByDNIDTO;
import xyz.kiridepapel.fraxisearchbackend.dtos.requests.RequestByFullNameDTO;
import xyz.kiridepapel.fraxisearchbackend.dtos.requests.RequestBySingleNameDTO;
import xyz.kiridepapel.fraxisearchbackend.exceptions.SecurityExceptions.BadNames;
import xyz.kiridepapel.fraxisearchbackend.services.interfaces.ISeleniumService;
import xyz.kiridepapel.fraxisearchbackend.services.interfaces.IUserInfoService;
import jakarta.annotation.PostConstruct;
import lombok.extern.java.Log;

@RestController
@RequestMapping("/fraxisearch/api/v1")
@SuppressWarnings("unused")
@Log
public class UserInfoController {
  // Variables de entorno
  @Value("${FRONTEND_URL}")
  private String frontendUrl;
  // Variables
  private List<String> allowedOrigins;
  // Inyección de dependencias
  private final IUserInfoService IUserInfoService;

  public UserInfoController(IUserInfoService IUserInfoService) {
    this.IUserInfoService = IUserInfoService;
  }
  
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

    // Objeto de búsqueda
    RequestBySingleNameDTO requestDTO = new RequestBySingleNameDTO();
    String[] nameSplit = request.getFullName().split(" ");
    
    // Apellidos
    requestDTO.setFatherLastName(nameSplit[nameSplit.length - 2]);
    requestDTO.setMotherLastName(nameSplit[nameSplit.length - 1]);

    // Nombres
    if (nameSplit.length >= 3 && nameSplit.length <= 5) {
      String names = "";
      for (int i = 0; i <= nameSplit.length - 3; i++) {
        names += nameSplit[i] + " ";
      }
      requestDTO.setNames(names.trim());
    } else {
      throw new BadNames("El nombre ingresado no es válido, prueba otra opción de búsqueda");
    }
    
    // Verifica que se haya ingresado algún dato
    if (!requestDTO.namesRequestIsValid()) {
      throw new BadNames("No se ha ingresado ningún dato");
    }

    // Formatea los nombres para que coincidan con los de la BD
    UserInfoDTO user = this.IUserInfoService.searchBySingleName(requestDTO);

    return new ResponseEntity<>(user, HttpStatus.OK);
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
    requestDTO.upperNames();
    UserInfoDTO user = this.IUserInfoService.searchBySingleName(requestDTO);

    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @GetMapping("/find-by-dni")
  public ResponseEntity<?> findByDNI(@RequestBody(required = true) RequestByDNIDTO requestDTO) throws Exception {
    // Validaciones
    // DataUtils.verifyAllowedOrigin(this.allowedOrigins, request.getHeader("Origin"));

    // Verifica que se haya ingresado algún dato
    if (!requestDTO.dniResquestIsValid()) {
      throw new RuntimeException("No se ha ingresado ningún dato");
    }

    UserInfoDTO user = this.IUserInfoService.searchByDNI(requestDTO);

    return new ResponseEntity<>(user, HttpStatus.OK);
  }
}
