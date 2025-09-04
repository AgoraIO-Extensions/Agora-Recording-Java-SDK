package io.agora.example.recording.cli;

import com.google.gson.Gson;
import io.agora.example.recording.agora.AgoraServiceInitializer;
import io.agora.example.recording.agora.RecorderConfig;
import io.agora.example.recording.agora.RecordingManager;
import io.agora.example.recording.utils.Utils;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class CliLauncher {
    private static void printUsageAndExit() {
        System.out.println(
                "Usage: java -jar target/agora-example.jar --mode=cli --configFileName=<name.json> [--channel=<name>] [--port=<port>] ");
        System.out.println(
                "       or: java -cp target/agora-example.jar io.agora.example.recording.cli.CliLauncher --configFileName=<name.json> [--channel=<name>]");
        System.exit(1);
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> map = new HashMap<>();
        for (String arg : args) {
            if (arg.startsWith("--") && arg.contains("=")) {
                int idx = arg.indexOf('=');
                String key = arg.substring(2, idx);
                String val = arg.substring(idx + 1);
                map.put(key, val);
            } else if (arg.startsWith("--")) {
                map.put(arg.substring(2), "true");
            }
        }
        return map;
    }

    public static void main(String[] args) {
        Map<String, String> options = parseArgs(args);
        String configFileName = options.getOrDefault("configFileName", "");
        if (configFileName.isEmpty()) {
            printUsageAndExit();
            return;
        }

        String resourcesDir = "src/main/resources/";
        String configPath = Files.exists(Paths.get(configFileName)) ? configFileName : resourcesDir + configFileName;
        String json;
        try {
            json = Utils.readFile(configPath);
        } catch (Exception e) {
            System.err.println("Failed to read config file: " + configPath + ", error: " + e.getMessage());
            System.exit(2);
            return;
        }

        RecorderConfig recorderConfig = new Gson().fromJson(json, RecorderConfig.class);
        if (recorderConfig == null) {
            System.err.println("Invalid config: " + configPath);
            System.exit(3);
            return;
        }

        String[] keys = Utils.readAppIdAndToken(".keys");
        if (keys != null && keys.length == 2
                && !io.agora.recording.utils.Utils.isNullOrEmpty(keys[0])) {
            recorderConfig.setAppId(keys[0]);
            recorderConfig.setToken(keys[1]);

        } else {
            System.err.println("WARN: Could not load AppId/Token from .keys for taskId: ");
        }

        if (options.containsKey("channel") && options.get("channel") != null && !options.get("channel").isEmpty()) {
            recorderConfig.setChannelName(options.get("channel"));
        }

        // init service
        AgoraServiceInitializer.initService(recorderConfig);
        RecordingManager manager = new RecordingManager();

        String taskId = Utils.getTaskId();
        try {
            // start
            manager.startRecording(taskId, recorderConfig, recorderConfig.getChannelName());

            // Dedicated input listener thread: stop and exit when user enters "1"
            Thread inputThread = new Thread(() -> {
                try {
                    java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(System.in));
                    System.out.println("Enter 1 to stop recording and exit:");
                    String line;
                    while ((line = br.readLine()) != null) {
                        if ("1".equals(line.trim())) {
                            System.out.println("Stop command received. Stopping...");
                            try {
                                manager.stopRecording(taskId, false);
                            } catch (Exception ignore) {
                            }
                            break;
                        } else {
                            System.out.println("Unknown command: " + line + ". Enter 1 to stop.");
                        }
                    }
                } catch (Exception ignore) {
                }
            }, "Cli-Input-Listener");
            inputThread.setDaemon(false);
            inputThread.start();

            // 等待输入线程结束
            inputThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                manager.destroy();
            } catch (Exception ignored) {
            }
            AgoraServiceInitializer.destroy();
        }
    }
}
