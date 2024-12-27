package io.agora.recording.test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RecordingManager {
    private final Map<String, RecordingSession> activeRecordings = new HashMap<>();
    private final ThreadPoolExecutor taskExecutorService;

    public RecordingManager() {
        this.taskExecutorService = new ThreadPoolExecutor(
                0, Integer.MAX_VALUE, 1L, TimeUnit.SECONDS, new SynchronousQueue<>());
    }

    public void startRecording(String taskId, RecorderConfig config) {
        RecordingSession session = new RecordingSession(taskId, config, taskExecutorService);
        activeRecordings.put(taskId, session);
        taskExecutorService.submit(() -> session.joinChannel());
    }

    public void stopRecording(String taskId, boolean async) {
        RecordingSession session = activeRecordings.get(taskId);
        if (session != null) {
            if (async) {
                taskExecutorService.submit(() -> session.stopRecording());
            } else {
                session.stopRecording();
            }
            activeRecordings.remove(taskId);
        }
    }

    public void destroy() {
        activeRecordings.clear();
        taskExecutorService.shutdown();
    }
}
