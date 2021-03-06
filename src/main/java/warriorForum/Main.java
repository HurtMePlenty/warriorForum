package warriorForum;

public class Main {

    public static void main(String[] args) {
        System.out.println("---------- Initializing web driver ---------");
        int delay = ConfigService.instance.delayMiliseconds;
        Loader.instance.initializeAndLogin();
        System.out.println("---------- Started ---------");
        while (true) {
            try {
                Worker.instance.checkNewUser();
                StateService.instance.renderResult();
            } catch (Exception e) {
                //suppress
                e.printStackTrace();
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
