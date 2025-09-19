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
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CliLauncher {
    private static final ThreadPoolExecutor stressExecutorService = new ThreadPoolExecutor(
            0,
            Integer.MAX_VALUE,
            1L,
            TimeUnit.SECONDS,
            new SynchronousQueue<>());

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

    private static boolean checkTestTime(long testStartTime, int testTime) {
        long currentTime = System.currentTimeMillis();
        long testCostTime = currentTime - testStartTime;
        if (testCostTime >= testTime * 1000) {
            return false;
        }
        return true;
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

        try {
            if (recorderConfig.getStressTest().isEnable()) {
                // Stress test mode
                System.out.println("Starting stress test mode...");
                System.out.println("Stress test config: " + recorderConfig.getStressTest().toString());

                final long testStartTime = System.currentTimeMillis();
                Object[] lock = new Object[recorderConfig.getStressTest().getThreadNum()];
                for (int i = 0; i < recorderConfig.getStressTest().getThreadNum(); i++) {
                    lock[i] = new Object();
                }

                for (int i = 0; i < recorderConfig.getStressTest().getThreadNum(); i++) {
                    final int threadIndex = i;
                    stressExecutorService.submit(() -> {
                        String channelName = recorderConfig.getChannelName();
                        if (!recorderConfig.getStressTest().isEnableSingleChannel()) {
                            channelName = channelName + "_" + threadIndex;
                        }

                        while (checkTestTime(testStartTime, recorderConfig.getStressTest().getTestTime())) {
                            System.out.println("threadIndex:" + threadIndex + " checkTestTime:"
                                    + checkTestTime(testStartTime, recorderConfig.getStressTest().getTestTime()));
                            try {
                                String stressTestTaskId = Utils.getTaskId();
                                manager.startRecording(stressTestTaskId, recorderConfig, channelName);
                                synchronized (lock[threadIndex]) {
                                    lock[threadIndex].wait(recorderConfig.getStressTest().getOneTestTime() * 1000);
                                }
                                manager.stopRecording(stressTestTaskId, false);
                                Thread.sleep(recorderConfig.getStressTest().getSleepTime() * 1000);
                            } catch (Exception e) {
                                System.err.println(
                                        "Thread interrupted while waiting for testTaskExecutorService to complete: "
                                                + e.getMessage());
                            }
                        }
                    });
                }

                // Wait for all stress test threads to complete
                while (stressExecutorService.getActiveCount() > 0) {
                    try {
                        Thread.sleep(1000);
                        System.out.println("Active stress test threads: " + stressExecutorService.getActiveCount());
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        System.err.println("Thread interrupted while waiting for stressExecutorService to complete");
                    }
                }

                System.out.println("Stress test completed!");
            } else {
                // Normal mode
                String taskId = Utils.getTaskId();
                // start
                manager.startRecording(taskId, recorderConfig, recorderConfig.getChannelName());

                // Dedicated input listener thread: stop and exit when user enters "1"
                Thread inputThread = new Thread(() -> {
                    try {
                        java.io.BufferedReader br = new java.io.BufferedReader(
                                new java.io.InputStreamReader(System.in));
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

                // Wait for input thread to finish
                inputThread.join();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Shutdown stress test thread pool
                stressExecutorService.shutdown();
                if (!stressExecutorService.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err
                            .println("stressExecutorService did not terminate within 60 seconds, forcing shutdown...");
                    stressExecutorService.shutdownNow();
                    if (!stressExecutorService.awaitTermination(60, TimeUnit.SECONDS)) {
                        System.err.println("stressExecutorService did not terminate even after forced shutdown.");
                    }
                }
            } catch (InterruptedException e) {
                stressExecutorService.shutdownNow();
                Thread.currentThread().interrupt();
            }

            try {
                manager.destroy();
            } catch (Exception ignored) {
            }
            AgoraServiceInitializer.destroy();
        }
    }
}
