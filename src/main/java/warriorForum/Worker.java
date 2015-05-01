package warriorForum;


import jdk.nashorn.internal.runtime.regexp.joni.Config;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.swing.plaf.nimbus.State;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public enum Worker {
    instance;


    public void checkNewUser() {
        WebDriver webDriver = Loader.instance.driver();

        sendMessageForUser(webDriver);

        if (webDriver.getCurrentUrl().equals("http://www.warriorforum.com/") || webDriver.getCurrentUrl().equals("http://www.warriorforum.com")) {
            webDriver.navigate().refresh();
        } else {
            webDriver.get("http://www.warriorforum.com/");
        }

        WebElement newUserAnchor = webDriver.findElement(By.cssSelector("#collapseobj_forumhome_stats div.smallfont a[href^=\"http://www.warriorforum.com/members/\"]"));
        String userName = newUserAnchor.getText();

        if (StateService.instance.isNewUser(userName)) {
            System.out.println(String.format("Last user - %s. Checking activity.", userName));
            String userUrl = newUserAnchor.getAttribute("href");
            webDriver.get(userUrl);
            WebElement lastActivityAnchor;
            try {
                lastActivityAnchor = webDriver.findElement(By.cssSelector("#activity_info a"));
            } catch (NoSuchElementException e) {

                System.out.println(String.format("No activity found for - %s", userName));
                //check if we are still logged in
                try {
                    webDriver.findElement(By.cssSelector("input[value=\"Log in\"]"));
                    System.out.println("Looks like got logged out. Trying to relogin.");
                    //well, looks like we were logged out
                    Loader.instance.initializeAndLogin();
                    checkNewUser();
                } catch (NoSuchElementException ex) {
                    //cool, no login btn
                }
                StateService.instance.addUser(userName, null, userUrl, false);
                return;
            }
            String activityText = lastActivityAnchor.getText();
            StateService.instance.addUser(userName, activityText, userUrl, false);


        } else {
            System.out.println(String.format("Last user - %s. We have already worked with this user. Skip it.", userName));
        }
    }

    private void sendMessageForUser(WebDriver webDriver) {

        StateService.User user = StateService.instance.getNextUserForMessage();
        if (user == null) {
            return;
        }

        System.out.println(String.format("Sending a message for user - %s", user.name));

        WebElement privateMsgLink = webDriver.findElement(By.cssSelector("#messaging_list a[href^=\"http://www.warriorforum.com/private.php\"]"));
        Date now = new Date();
        String url = privateMsgLink.getAttribute("href");
        url += "&vticks=" + now.getTime();

        webDriver.get(url);
        if (webDriver.getPageSource().contains("You may only post 5 messages every 60 minutes.")) {
            System.out.println("You may only post 5 messages every 60 minutes.");
            return;
        }
        try {
            Thread.sleep(1000);
            WebElement titleBox = webDriver.findElement(By.cssSelector("table.fieldset input.bginput"));
            titleBox.sendKeys(ConfigService.instance.messageTitle);
            Thread.sleep(1000);
            WebElement messageBox = webDriver.findElement(By.cssSelector("#vB_Editor_001_textarea"));
            messageBox.sendKeys(ConfigService.instance.message);
            Thread.sleep(1000);
            WebElement sendMessageBtn = webDriver.findElement(By.cssSelector("#vB_Editor_001_save"));
            sendMessageBtn.click();
            Thread.sleep(1000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        user.receivedMessage = true;

        System.out.println(String.format("Sent a message for user - %s", user.name));

    }


}
