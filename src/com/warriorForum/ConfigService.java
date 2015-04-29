package com.warriorForum;

import com.google.common.io.Files;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

enum ConfigService
{
    instance;

    final String configFileName = "config.txt";
    public int delayMiliseconds;
    public Map<String, String> activitiesMap = new HashMap();

    ConfigService()
    {
        try
        {
            File configFile = new File(configFileName);
            if (!configFile.exists())
            {
                throw new RuntimeException("no config.txt found");
            } else
            {
                List<String> configLines = Files.readLines(configFile, Charsets.UTF_8);
                StringBuilder activityBuilder = new StringBuilder();
                String currentActivity = null;
                for (String configLine : configLines)
                {
                    if (configLine.startsWith("#delay"))
                    {
                        delayMiliseconds = Integer.parseInt(configLine.split("=")[1]) * 1000;
                    }
                    if (configLine.startsWith("#activity"))
                    {
                        if (currentActivity != null)
                        {
                            activitiesMap.put(currentActivity, activityBuilder.toString());
                        }
                        String activityString = configLine.replace("#activity", "").trim();
                        currentActivity = activityString.substring(1, activityString.length() - 1);
                        activityBuilder.setLength(0);
                    } else
                    {
                        if (currentActivity != null)
                        {
                            activityBuilder.append(configLine);
                            if (activityBuilder.length() > 0)
                            {
                                activityBuilder.append("\n");
                            }
                        }
                    }
                }
                activitiesMap.put(currentActivity, activityBuilder.toString());
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    public String getMessageForActivity(String activity)
    {
        if (activitiesMap.containsKey(activity))
        {
            return activitiesMap.get(activity);
        }
        return null;
    }
}
