package warriorForum;


import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public enum Worker {
    instance;

    private String lastUser;
    private String lastUserActivity;

    private List<User> queue = new ArrayList<User>();

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
                User user = new User(userName, userUrl, activityText, message);
                queue.add(user);
                System.out.println(String.format("Username - %s added to queue, queue size - %d", userName, queue.size()));
                lastUser = null;
                lastUserActivity = null;
            }
        } else {
            System.out.println(String.format("Last user - %s. We have already worked with this user. Skip it.", userName));
        }
    }

    private void sendMessageForUser(WebDriver webDriver) {
        if (queue.size() > 0) {
            User user = queue.get(0);

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
                messageBox.sendKeys(user.message);
                Thread.sleep(1000);
                WebElement sendMessageBtn = webDriver.findElement(By.cssSelector("#vB_Editor_001_save"));
                sendMessageBtn.click();
                Thread.sleep(1000);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            StateService.instance.addUser(user.name, user.activity, true);
            System.out.println(String.format("Sent a message for user - %s, queue size - %d", user.name, queue.size()));
            queue.remove(user);
        }
    }

    class User {
        public String name;
        public String profileUrl;
        public String activity;
        public String message;

        public User(String name, String profileUrl, String activity, String message) {
            this.name = name;
            this.profileUrl = profileUrl;
            this.activity = activity;
            this.message = message;
        }
    }

}
