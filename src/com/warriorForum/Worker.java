package com.warriorForum;


import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;

import java.io.File;

public enum Worker
{
    instance;


    public void checkNewUser()
    {
        WebDriver webDriver = Loader.instance.driver();
        if (webDriver.getCurrentUrl().equals("http://www.warriorforum.com/") || webDriver.getCurrentUrl().equals("http://www.warriorforum.com"))
        {
            webDriver.navigate().refresh();
        } else
        {
            webDriver.get("http://www.warriorforum.com/");
        }
        WebElement newUserAnchor = webDriver.findElement(By.cssSelector("#collapseobj_forumhome_stats div.smallfont a[href^=\"http://www.warriorforum.com/members/\"]"));
        String userName = newUserAnchor.getText();
        if (StateService.instance.isNewUser(userName))
        {
            String userUrl = newUserAnchor.getAttribute("href");
            webDriver.get(userUrl);
            WebElement lastActivityAnchor;
            try
            {
                lastActivityAnchor = webDriver.findElement(By.cssSelector("#activity_info a"));
            }
            catch (NoSuchElementException e)
            {
                return;
            }
            String activityText = lastActivityAnchor.getText();
            String message = ConfigService.instance.getMessageForActivity(activityText);
            if (message != null)
            {
                WebElement messageBox = webDriver.findElement(By.cssSelector("#vB_Editor_QR_textarea"));
                messageBox.sendKeys(message);
                WebElement sendMessageBtn = webDriver.findElement(By.cssSelector("#qr_submit"));
                sendMessageBtn.click();
                StateService.instance.addUser(userName, activityText, true);
            } else
            {
                StateService.instance.addUser(userName, activityText, true);
            }
        }
    }

}
