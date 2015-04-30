package com.warriorForum;

import com.google.common.io.Files;
import org.apache.commons.io.Charsets;

import java.io.File;

enum LoggerService {
    instance;

    final String logFileName = "log.txt";
    File logFile;

    LoggerService() {
        try {
            logFile = new File(logFileName);
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void log(String message) {
        try
        {
            Files.append(message + "/n", logFile, Charsets.UTF_8);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
}
