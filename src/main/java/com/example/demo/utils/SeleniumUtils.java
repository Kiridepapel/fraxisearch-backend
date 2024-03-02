package com.example.demo.utils;

import java.time.Duration;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;

@Component
public class SeleniumUtils {
  public final static Duration TIMEOUT = Duration.ofSeconds(5);
  
  // Espera hasta que el texto de un elemento con id específico cambie
  public static void waitUntilTextChanges(WebDriver driver, By locator) {
    WebDriverWait wait = new WebDriverWait(driver, SeleniumUtils.TIMEOUT);
    WebElement element = driver.findElement(locator);
    String initialText = element.getText().trim();
    wait.until(ExpectedConditions.not(ExpectedConditions.textToBe(locator, initialText)));
  }
  // WebDriverWait wait = new WebDriverWait(driver, TIMEOUT);
  // wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#nombres")));
}
