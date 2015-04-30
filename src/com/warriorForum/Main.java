package com.warriorForum;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {

        Logger logger = Logger.getLogger("");
        logger.setLevel (Level.OFF);


        int delay = ConfigService.instance.delayMiliseconds;
        Loader.instance.initializeAndLogin();
        while (true) {
            try {
                Worker.instance.checkNewUser();
            } catch (Exception e) {
                //suppress
                LoggerService.instance.log(e.toString());
            }
            try {
                Thread.sleep(delay);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
