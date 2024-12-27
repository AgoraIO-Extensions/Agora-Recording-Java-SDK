package io.agora.recording.test;

import java.util.Scanner;
import java.util.concurrent.CountDownLatch;

import io.agora.recording.test.utils.SampleLogger;
import io.agora.recording.test.utils.Utils;

public class RecordingApplication {
    private static final CountDownLatch leaveLatch = new CountDownLatch(1);
    private static final RecordingManager recordingManager = new RecordingManager();

    public static void main(String[] args) {
        try {
            RecorderConfig config = RecorderConfigManager.parseArgs(args);
            SampleLogger.info("Recording config: " + config);

            AgoraServiceInitializer.initService(config);

            String taskId = Utils.getTaskId();
            recordingManager.startRecording(taskId, config);

            // Start a new thread to listen for console input
            new Thread(() -> {
                Scanner scanner = new Scanner(System.in);
                while (true) {
                    String input = scanner.nextLine();
                    if ("1".equals(input)) {
                        leaveLatch.countDown();
                        break;
                    }
                }
                scanner.close();
            }).start();

            leaveLatch.await();

            recordingManager.stopRecording(taskId, false);

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
