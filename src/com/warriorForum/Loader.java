package com.warriorForum;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public enum Loader {
    instance;

    private static String login = "CharliePX";
    private static String pass = "123123cha";

    private WebDriver driver;

    public void initializeAndLogin() {
        DesiredCapabilities cap = DesiredCapabilities.phantomjs();
        driver = new PhantomJSDriver(cap);
        driver.get("http://www.warriorforum.com/");
        WebElement loginBox = driver.findElement(By.id("navbar_username"));
        WebElement passBox = driver.findElement(By.id("navbar_password"));
        loginBox.sendKeys(login);
        passBox.sendKeys(pass);
        WebElement loginBtn = driver.findElement(By.cssSelector("input[value=\"Log in\"]"));
        loginBtn.click();
    }

    public WebDriver driver() {
        return driver;
    }
}
