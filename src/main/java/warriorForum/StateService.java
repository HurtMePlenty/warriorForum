package warriorForum;

import com.google.common.io.Files;
import org.apache.commons.io.Charsets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public enum StateService
{
    instance;

    File stateFile;
    final String stateFileName = "state.csv";
    final String separator = ";";

    List<User> userList = new ArrayList<User>();

    StateService()
    {
        try
        {
            stateFile = new File(stateFileName);
            if (!stateFile.exists())
            {
                stateFile.createNewFile();

                StringBuilder builder = new StringBuilder();
                builder.append("User name");
                builder.append(separator);
                builder.append("Activity");
                builder.append(separator);
                builder.append("Received message");
                builder.append("\n");
                Files.append(builder.toString(), stateFile, Charsets.UTF_8);
            } else
            {

            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public boolean isNewUser(String userName)
    {
        for (User user : userList)
        {
            if (user.getName().equals(userName))
            {
                return false;
            }
        }
        return true;
    }

    public void addUser(String name, String activity, boolean receivedMessage)
    {
        User newUser = new User(name, activity, receivedMessage);
        userList.add(newUser);

        StringBuilder builder = new StringBuilder();
        builder.append(name);
        builder.append(separator);
        builder.append(activity);
        builder.append(separator);
        builder.append(receivedMessage);
        builder.append("\n");

        try
        {
            Files.append(builder.toString(), stateFile, Charsets.UTF_8);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public static class User
    {
        private String name;
        private String activity;
        boolean receivedMessage;

        public User(String name, String activity, boolean receivedMessage)
        {
            this.name = name;
            this.activity = activity;
        }

        public String getName()
        {
            return name;
        }

        public String getActivity()
        {
            return activity;
        }

        public boolean isReceivedMessage()
        {
            return receivedMessage;
        }

    }
}
