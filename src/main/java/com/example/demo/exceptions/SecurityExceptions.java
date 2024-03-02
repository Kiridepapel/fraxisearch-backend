package com.example.demo.exceptions;

public class SecurityExceptions {
  public static class ProtectedResource extends RuntimeException {
    public ProtectedResource(String message) {
      super(message);
    }
  }
  public static class NotFoundData extends RuntimeException {
    public NotFoundData(String message) {
      super(message);
    }
  }
}
