package io.agora.recording.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import io.agora.recording.test.utils.SampleLogger;
import io.agora.recording.test.utils.Utils;

public class RecorderConfigManager {
    public static RecorderConfig parseArgs(String[] args) {
        SampleLogger.info("parseArgs args:" + Arrays.toString(args));
        if (args == null || args.length == 0) {
            return null;
        }

        Map<String, String> params = new HashMap<>();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("-")) {
                String key = arg.substring(1);
                if (i + 1 < args.length) {
                    String value = args[i + 1];
                    params.put(key, value);
                    i++;
                }
            }
        }

        Gson gson = new Gson();
        RecorderConfig config = gson.fromJson(io.agora.recording.utils.Utils.readFile(params.get("config")),
                RecorderConfig.class);

        String[] keys = Utils.readAppIdAndToken(".keys");
        if (keys != null && keys.length == 2 && !io.agora.recording.utils.Utils.isNullOrEmpty(keys[0])
                && !io.agora.recording.utils.Utils.isNullOrEmpty(keys[1])) {
            config.setAppId(keys[0]);
            config.setToken(keys[1]);
        }
        return config;
    }
}
