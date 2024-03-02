package com.example.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dtos.RequestConditionsDTO;
import com.example.demo.dtos.UserInfoDTO;
import com.example.demo.services.UserInfoService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1")
public class TestController {
  // Variables de entorno
  @Value("${FRONTEND_URL}")
  private String frontendUrl;
  // Inyección de dependencias
  @Autowired
  private UserInfoService userInfoService;
  // Variables
  // private List<String> allowedOrigins;
  
  // Constructor
  // @PostConstruct
  // public void init() {
  //   this.allowedOrigins = Arrays.asList(frontendUrl);
  // }

  // Métodos principales
  @GetMapping("/find")
  public ResponseEntity<?> find(HttpServletRequest request,
      @RequestBody(required = true) RequestConditionsDTO requestConditions) throws Exception {
    
    // Validaciones
    // DataUtils.verifyAllowedOrigin(this.allowedOrigins, request.getHeader("Origin"));
    // Valida si el nombre ingresado tiene 3 o 4 partes (está en una sola cadena separada por espacios)
    if (requestConditions.getHasFourOrThreeParts()) {
      String[] nameSplit = requestConditions.getFullName().split(" ");
      if (nameSplit.length == 3) {
        requestConditions.setNames(nameSplit[0]);
        requestConditions.setFatherLastName(nameSplit[1]);
        requestConditions.setMotherLastName(nameSplit[2]);
      } else if (nameSplit.length == 4) {
        requestConditions.setNames(nameSplit[0] + " " + nameSplit[1]);
        requestConditions.setFatherLastName(nameSplit[2]);
        requestConditions.setMotherLastName(nameSplit[3]);
      } else {
        throw new RuntimeException("El nombre ingresado no es válido");
      }
    }
    // Verifica que se haya ingresado algún dato
    if (requestConditions.allDataIsInvalid()) {
      throw new RuntimeException("No se ha ingresado ningún dato");
    }
    // Formate los nombres para que coincidan con los de la BD
    requestConditions.formatNames();
    UserInfoDTO userInfo = this.userInfoService.validateDBAndSelectTypeOfSearch(requestConditions);
    
    return new ResponseEntity<>(userInfo, HttpStatus.OK);
  }

  @GetMapping("/test")
  public ResponseEntity<?> test() throws Exception {
    return new ResponseEntity<>(this.userInfoService.test(), HttpStatus.OK);
  }

}
