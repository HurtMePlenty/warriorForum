package warriorForum;

import com.google.common.io.Files;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

enum ConfigService {
    instance;

    final String configFileName = "config.txt";
    public int delayMiliseconds;


    public String login;
    public String password;
    public String messageTitle;
    public String message;
    public String proxy;

    ConfigService() {
        try {
            File configFile = new File(configFileName);
            if (!configFile.exists()) {
                throw new RuntimeException("no config.txt found");
            } else {
                List<String> configLines = Files.readLines(configFile, Charsets.UTF_8);
                StringBuilder messageBuilder = new StringBuilder();

                for (String configLine : configLines) {
                    if (configLine.startsWith("#login")) {
                        login = configLine.split("=")[1];
                    } else if (configLine.startsWith("#proxy")) {
                        proxy = configLine.split("=")[1];
                    } else if (configLine.startsWith("#password")) {
                        password = configLine.split("=")[1];
                    } else if (configLine.startsWith("#delay")) {
                        delayMiliseconds = Integer.parseInt(configLine.split("=")[1]) * 1000;
                    } else if (configLine.startsWith("#title")) {
                        messageTitle = configLine.replace("#title", "").trim();
                    } else if (configLine.startsWith("#message")) {

                    } else {
                        messageBuilder.append(configLine);
                        if (messageBuilder.length() > 0) {
                            messageBuilder.append("\n");
                        }
                    }
                }

                message = messageBuilder.toString();

                if (StringUtils.isEmpty(login) || StringUtils.isEmpty(password)) {
                    throw new RuntimeException("Login and passsword should be specified in the config.txt file");
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
