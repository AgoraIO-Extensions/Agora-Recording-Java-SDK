package io.agora.recording.example.test;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import io.agora.recording.example.AgoraServiceInitializer;
import io.agora.recording.example.RecorderConfigManager;
import io.agora.recording.example.RecordingManager;
import io.agora.recording.example.utils.SampleLogger;

public class StressLoopRecordingTest {
    private static final RecordingManager recordingManager = new RecordingManager();
    private static String taskId;

    public static void main(String[] args) {
        try {
            Object lock = new Object();
            RecorderConfigManager.parseArgs(args);
            SampleLogger.info("Recording config: " + RecorderConfigManager.getConfig());

            AgoraServiceInitializer.initService(RecorderConfigManager.getConfig());

            ExecutorService executorService = Executors.newFixedThreadPool(2);
            AtomicBoolean isStopped = new AtomicBoolean(false);

            executorService.submit(() -> {
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    String input = scanner.nextLine();
                    if ("1".equals(input)) {
                        isStopped.set(true);
                        synchronized (lock) {
                            lock.notifyAll();
                        }
                        break;
                    } else if ("9".equals(input)) {
                        SampleLogger.info("程序即将终止");
                        System.exit(1);
                    }
                }
                scanner.close();
            });

            while (!isStopped.get()) {
                taskId = io.agora.recording.example.utils.Utils.getTaskId();
                executorService.submit(() -> {
                    recordingManager.startRecording(taskId, RecorderConfigManager.getConfig());
                });
                synchronized (lock) {
                    lock.wait(RecorderConfigManager.getSleepTime() * 1000);
                }
                recordingManager.stopRecording(taskId, false);
                Thread.sleep(2 * 1000);
            }

        } catch (Exception e) {
            SampleLogger.error("Recording failed");
        } finally {
            recordingManager.destroy();
            AgoraServiceInitializer.destroy();

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                SampleLogger.error("exit failed");
            }
            System.exit(0);
        }
    }
}
