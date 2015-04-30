package com.warriorForum;

public class Main {

    public static void main(String[] args) {
        int delay = ConfigService.instance.delayMiliseconds;
        Loader.instance.initializeAndLogin();
        while (true) {
            try {
                Worker.instance.checkNewUser();
            } catch (Exception e) {
                //suppress
                LoggerService.instance.log(e.getMessage());
            }
            try {
                Thread.sleep(delay);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
