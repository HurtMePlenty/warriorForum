package com.warriorForum;

public class Main
{

    public static void main(String[] args)
    {
        int delay = ConfigService.instance.delayMiliseconds;
        Loader.instance.initializeAndLogin();
        while (true)
        {
            Worker.instance.checkNewUser();
            try
            {
                Thread.sleep(delay);
            }
            catch (Exception e)
            {
                throw new RuntimeException(e);
            }
        }
    }
}
