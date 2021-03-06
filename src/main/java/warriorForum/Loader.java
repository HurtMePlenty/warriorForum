package warriorForum;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public enum Loader {
    instance;

    //private static String login = "CharliePX";
    //private static String pass = "123123cha";

    //private static String login = "qqq2";
    //private static String pass = "q1w2e3";

    private WebDriver driver;

    public void initializeAndLogin() {
        if (driver != null) {
            driver.close();
        }

        DesiredCapabilities cap = DesiredCapabilities.phantomjs();
        cap.setCapability("phantomjs.binary.path", "phantomjs");

        List<String> cliArgsCap = new ArrayList<String>();
        cliArgsCap.add("--web-security=false");
        cliArgsCap.add("--ssl-protocol=any");
        cliArgsCap.add("--ignore-ssl-errors=yes");
        cliArgsCap.add("--webdriver-loglevel=NONE");
        if (!StringUtils.isEmpty(ConfigService.instance.proxy)) {
            String proxy = ConfigService.instance.proxy;
            Proxy p = new Proxy();
            p.setHttpProxy(proxy).setFtpProxy(proxy).setSslProxy(proxy);
            cap.setCapability(CapabilityType.PROXY, p);
        }
        String[] args = cliArgsCap.toArray(new String[0]);
        cap.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, args);

        //cap.setCapability("acceptSslCerts", "true");
        //cap.setCapability("webStorageEnabled", "true");
        try {
            driver = new PhantomJSDriver(cap);
            Date now = new Date();
            driver.get("http://www.warriorforum.com/?t=" + now.getTime());
            driver.manage().deleteAllCookies();

            WebElement loginBox = driver.findElement(By.id("navbar_username"));
            WebElement passBox = driver.findElement(By.id("navbar_password"));

            Thread.sleep(500);
            loginBox.sendKeys(ConfigService.instance.login);
            Thread.sleep(500);
            passBox.sendKeys(ConfigService.instance.password);
            Thread.sleep(500);
            WebElement loginBtn = driver.findElement(By.cssSelector("input[value=\"Log in\"]"));
            loginBtn.click();
            //Thread.sleep(500);
            //makeScreenShot(driver);
            Thread.sleep(500);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //makeScreenShot(driver);
    }

    public WebDriver driver() {
        return driver;
    }

    public void makeScreenShot(WebDriver driver) {
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(scrFile, new File("screenshot.png"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
