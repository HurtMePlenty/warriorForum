package com.warriorForum;


import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public enum Worker {
    instance;

    private String lastUser;
    private String lastUserActivity;


    public void checkNewUser() {
        WebDriver webDriver = Loader.instance.driver();
        if (webDriver.getCurrentUrl().equals("http://www.warriorforum.com/") || webDriver.getCurrentUrl().equals("http://www.warriorforum.com")) {
            webDriver.navigate().refresh();
        } else {
            webDriver.get("http://www.warriorforum.com/");
        }


        WebElement newUserAnchor = webDriver.findElement(By.cssSelector("#collapseobj_forumhome_stats div.smallfont a[href^=\"http://www.warriorforum.com/members/\"]"));
        String userName = newUserAnchor.getText();

        //if last user is null that means that it is first check or previous user was sent a message
        if (!StringUtils.isEmpty(lastUser) && !lastUser.equals(userName)) {
            StateService.instance.addUser(lastUser, lastUserActivity, false);
        }

        lastUser = userName;


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

                return;
            }
            String activityText = lastActivityAnchor.getText();
            lastUserActivity = activityText;
            String message = ConfigService.instance.getMessageForActivity(activityText);
            System.out.println(String.format("Username - %s   UserActivity - %s", userName, activityText));
            if (message != null) {
                System.out.println(String.format("Sending a message for user - %s", userName));
                WebElement messageBox = webDriver.findElement(By.cssSelector("#vB_Editor_QR_textarea"));
                messageBox.sendKeys(message);
                WebElement sendMessageBtn = webDriver.findElement(By.cssSelector("#qr_submit"));
                sendMessageBtn.click();
                StateService.instance.addUser(userName, activityText, true);
                lastUser = null;
                lastUserActivity = null;
            }
        } else {
            System.out.println(String.format("Last user - %s. We have already worked with this user. Skip it.", userName));
        }
    }

}
