package warriorForum;

import com.google.common.io.Files;
import org.apache.commons.io.Charsets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public enum StateService {
    instance;

    File stateFile;
    final String stateFileName = "state.csv";
    final String separator = ";";

    List<User> userList = new ArrayList<User>();

    StateService() {
        try {
            stateFile = new File(stateFileName);
            if (!stateFile.exists()) {
                stateFile.createNewFile();
            } else {

                List<String> dataLines = Files.readLines(stateFile, Charsets.UTF_8);
                for (String line : dataLines) {
                    if (dataLines.indexOf(line) == 0) {
                        continue;
                    }

                    String[] parts = line.split(separator);
                    boolean receivedMessage = Boolean.parseBoolean(parts[3]);
                    User user = new User(parts[0], parts[1], parts[2], receivedMessage);
                    userList.add(user);
                }

            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isNewUser(String userName) {
        for (User user : userList) {
            if (user.name.equals(userName)) {
                return false;
            }
        }
        return true;
    }

    public User getNextUserForMessage(){
        for(User user: userList){
            if(!user.receivedMessage){
                return user;
            }
        }
        return null;
    }

    public void addUser(String name, String activity, String profileUrl, boolean receivedMessage) {
        User newUser = new User(name, activity, profileUrl, receivedMessage);
        userList.add(newUser);
    }

    public void renderResult() {
        StringBuilder builder = new StringBuilder();

        builder.append("User name");
        builder.append(separator);
        builder.append("Activity");
        builder.append(separator);
        builder.append("Profile Url");
        builder.append(separator);
        builder.append("Received message");
        builder.append("\n");

        for (User user : userList) {
            builder.append(user.name);
            builder.append(separator);
            builder.append(user.activity.replace(separator, "-"));
            builder.append(separator);
            builder.append(user.profileUrl);
            builder.append(separator);
            builder.append(user.receivedMessage);
            builder.append("\n");
        }

        try {
            Files.write(builder.toString(), stateFile, Charsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static class User {
        public String name;
        public String activity;
        public String profileUrl;
        boolean receivedMessage;

        public User(String name, String activity, String profileUrl, boolean receivedMessage) {
            this.name = name;
            this.activity = activity;
            this.profileUrl = profileUrl;
            this.receivedMessage = receivedMessage;
        }


    }
}
