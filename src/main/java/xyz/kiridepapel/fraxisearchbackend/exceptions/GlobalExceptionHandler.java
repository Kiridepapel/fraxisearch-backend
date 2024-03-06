package xyz.kiridepapel.fraxisearchbackend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import xyz.kiridepapel.fraxisearchbackend.dtos.ResponseDTO;
import xyz.kiridepapel.fraxisearchbackend.exceptions.SecurityExceptions.NotFoundData;
import xyz.kiridepapel.fraxisearchbackend.exceptions.SecurityExceptions.ProtectedResource;

@ControllerAdvice
public class GlobalExceptionHandler {
  // Security Exceptions
  @ExceptionHandler(ProtectedResource.class)
  public ResponseEntity<?> handleProtectedResource(ProtectedResource ex) {
    ResponseDTO response = new ResponseDTO(ex.getMessage(), 401);
    return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
  }

  // Data
  @ExceptionHandler(NotFoundData.class)
  public ResponseEntity<?> handleNotFoundData(NotFoundData ex) {
    ResponseDTO response = new ResponseDTO(ex.getMessage(), 401);
    return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
  }
  
}
