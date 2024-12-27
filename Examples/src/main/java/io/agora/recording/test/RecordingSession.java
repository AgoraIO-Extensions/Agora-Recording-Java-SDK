package io.agora.recording.test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadPoolExecutor;

import io.agora.recording.AgoraMediaComponentFactory;
import io.agora.recording.AgoraMediaRtcRecorder;
import io.agora.recording.AgoraService;
import io.agora.recording.Constants;
import io.agora.recording.Constants.WatermarkSourceType;
import io.agora.recording.EncryptionConfig;
import io.agora.recording.IAgoraMediaRtcRecorderEventHandler;
import io.agora.recording.MediaRecorderConfiguration;
import io.agora.recording.RecorderInfo;
import io.agora.recording.Rectangle;
import io.agora.recording.RemoteAudioStatistics;
import io.agora.recording.RemoteVideoStatistics;
import io.agora.recording.VideoSubscriptionOptions;
import io.agora.recording.WatermarkConfig;
import io.agora.recording.WatermarkLitera;
import io.agora.recording.WatermarkOptions;
import io.agora.recording.WatermarkTimestamp;
import io.agora.recording.test.utils.SampleLogger;
import io.agora.recording.test.utils.Utils;

public class RecordingSession implements IAgoraMediaRtcRecorderEventHandler {
    private final String taskId;
    private final RecorderConfig recorderConfig;
    private final ThreadPoolExecutor taskExecutorService;

    private AgoraMediaRtcRecorder agoraMediaRtcRecorder;
    private AgoraService agoraService = null;
    private AgoraMediaComponentFactory factory = null;
    private List<String> singleRecordingUserList = new CopyOnWriteArrayList<>();
    private VideoLayoutManager videoLayoutManager;

    public RecordingSession(String taskId, RecorderConfig recorderConfig, ThreadPoolExecutor taskExecutorService) {
        this.taskId = taskId;
        this.recorderConfig = recorderConfig;
        this.agoraService = AgoraServiceInitializer.getAgoraService();
        this.factory = AgoraServiceInitializer.getFactory();
        this.taskExecutorService = taskExecutorService;
    }

    public void joinChannel() {
        SampleLogger.info("[" + taskId + "]joinChannel");
        if (null == agoraService || null == factory || null == recorderConfig) {
            SampleLogger.info("createRtcRecorder agoraService or factory  or eventHandler or recordingConfig is null");
            return;
        }

        if (null == agoraService || null == factory || null == recorderConfig) {
            SampleLogger.info("createRtcRecorder agoraService or factory  or eventHandler or recordingConfig is null");
            return;
        }

        agoraMediaRtcRecorder = factory.createMediaRtcRecorder();
        agoraMediaRtcRecorder.initialize(agoraService, recorderConfig.isMix());
        agoraMediaRtcRecorder.registerRecorderEventHandler(this);

        this.videoLayoutManager = new VideoLayoutManager(agoraMediaRtcRecorder, recorderConfig);

        boolean enableEncryption = !io.agora.recording.utils.Utils
                .isNullOrEmpty(recorderConfig.getEncryption().getMode());

        if (enableEncryption) {
            EncryptionConfig encryptionConfig = new EncryptionConfig();
            encryptionConfig.setEncryptionMode(Utils.convertToEncryptionMode(recorderConfig.getEncryption().getMode()));
            encryptionConfig.setEncryptionKey(recorderConfig.getEncryption().getKey());
            if (!io.agora.recording.utils.Utils.isNullOrEmpty(recorderConfig.getEncryption().getSalt())) {
                encryptionConfig.setEncryptionKdfSalt(
                        recorderConfig.getEncryption().getSalt().getBytes(StandardCharsets.UTF_8));
            }
            SampleLogger.info("[" + taskId + "]joinChannel enableEncryption encryptionConfig:" + encryptionConfig);
            int ret = agoraMediaRtcRecorder.enableEncryption(true, encryptionConfig);
            SampleLogger.info("[" + taskId + "]joinChannel enableEncryption ret:" + ret);
        }

        agoraMediaRtcRecorder.joinChannel(recorderConfig.getToken(),
                recorderConfig.getChannelName(),
                recorderConfig.getUserId());
    }

    private void setConfigBeforeStartRecording(String userId) {
        if (null != recorderConfig.getWaterMark() && recorderConfig.getWaterMark().size() > 0) {
            WatermarkConfig[] watermarks = new WatermarkConfig[recorderConfig.getWaterMark().size()];
            for (int i = 0; i < recorderConfig.getWaterMark().size(); i++) {
                watermarks[i] = new WatermarkConfig();
                watermarks[i].setIndex(i + 1);
                WatermarkSourceType watermarkSourceType = Utils
                        .convertToWatermarkSourceType(recorderConfig.getWaterMark().get(i).getType());
                watermarks[i].setType(watermarkSourceType);
                if (watermarkSourceType == WatermarkSourceType.LITERA) {
                    WatermarkLitera watermarkLitera = new WatermarkLitera();
                    watermarkLitera.setWmLitera(recorderConfig.getWaterMark().get(i).getLitera());
                    watermarkLitera.setFontFilePath(recorderConfig.getWaterMark().get(i).getFontFilePath());
                    watermarkLitera.setFontSize(recorderConfig.getWaterMark().get(i).getFontSize());
                    watermarks[i].setLiteraSource(watermarkLitera);
                } else if (watermarkSourceType == WatermarkSourceType.TIMESTAMPS) {
                    WatermarkTimestamp watermarkTimestamp = new WatermarkTimestamp();
                    watermarkTimestamp.setFontFilePath(recorderConfig.getWaterMark().get(i).getFontFilePath());
                    watermarkTimestamp.setFontSize(recorderConfig.getWaterMark().get(i).getFontSize());
                    watermarks[i].setTimestampSource(watermarkTimestamp);
                } else if (watermarkSourceType == WatermarkSourceType.PICTURE) {
                    watermarks[i].setImageUrl(recorderConfig.getWaterMark().get(i).getImgUrl());
                }

                WatermarkOptions watermarkOptions = new WatermarkOptions();
                watermarkOptions.setMode(Constants.WaterMaskFitMode.FIT_MODE_COVER_POSITION);
                watermarkOptions.setZOrder(recorderConfig.getWaterMark().get(i).getZorder());

                Rectangle positionInPortraitMode = new Rectangle();
                positionInPortraitMode.setX(recorderConfig.getWaterMark().get(i).getX());
                positionInPortraitMode.setY(recorderConfig.getWaterMark().get(i).getY());
                positionInPortraitMode.setWidth(recorderConfig.getWaterMark().get(i).getWidth());
                positionInPortraitMode.setHeight(recorderConfig.getWaterMark().get(i).getHeight());

                Rectangle positionInLandscapeMode = new Rectangle();
                positionInLandscapeMode.setX(recorderConfig.getWaterMark().get(i).getX());
                positionInLandscapeMode.setY(recorderConfig.getWaterMark().get(i).getY());
                positionInLandscapeMode.setWidth(recorderConfig.getWaterMark().get(i).getWidth());
                positionInLandscapeMode.setHeight(recorderConfig.getWaterMark().get(i).getHeight());

                watermarkOptions.setPositionInLandscapeMode(positionInLandscapeMode);
                watermarkOptions.setPositionInPortraitMode(positionInPortraitMode);
                watermarks[i].setOptions(watermarkOptions);
            }

            SampleLogger
                    .info("[" + taskId + "] enableAndUpdateVideoWatermarks watermarks:" + Arrays.toString(watermarks));

            int ret = -1;

            if (io.agora.recording.utils.Utils.isNullOrEmpty(userId)) {
                ret = agoraMediaRtcRecorder.enableAndUpdateVideoWatermarks(watermarks);
            } else {
                ret = agoraMediaRtcRecorder.enableAndUpdateVideoWatermarksByUid(watermarks, userId);
            }
            SampleLogger.info("[" + taskId + "] enableAndUpdateVideoWatermarks ret:" + ret);
        }

    }

    public void startRecording() {
        if (null == agoraMediaRtcRecorder) {
            SampleLogger.info("startRecording agoraMediaRtcRecorder is null");
            return;
        }
        if (recorderConfig.isSubAllAudio()) {
            agoraMediaRtcRecorder.subscribeAllAudio();
        } else {
            for (String userId : recorderConfig.getSubAudioUserList()) {
                agoraMediaRtcRecorder.subscribeAudio(userId);
            }
        }
        VideoSubscriptionOptions options = new VideoSubscriptionOptions();
        options.setEncodedFrameOnly(false);
        options.setType(Utils.convertToVideoStreamType(recorderConfig.getSubStreamType()));
        SampleLogger.info("[" + taskId + "]startRecording VideoSubscriptionOptions:" + options);
        if (recorderConfig.isSubAllVideo()) {
            agoraMediaRtcRecorder.subscribeAllVideo(options);
        } else {
            for (String userId : recorderConfig.getSubVideoUserList()) {
                agoraMediaRtcRecorder.subscribeVideo(userId, options);
            }
        }

        if (recorderConfig.isMix()) {
            MediaRecorderConfiguration mediaRecorderConfiguration = new MediaRecorderConfiguration();
            mediaRecorderConfiguration.setWidth(recorderConfig.getVideo().getWidth());
            mediaRecorderConfiguration.setHeight(recorderConfig.getVideo().getHeight());
            mediaRecorderConfiguration.setFps(recorderConfig.getVideo().getFps());
            mediaRecorderConfiguration.setStoragePath(recorderConfig.getRecorderPath());
            mediaRecorderConfiguration.setSampleRate(recorderConfig.getAudio().getSampleRate());
            mediaRecorderConfiguration.setChannelNum(recorderConfig.getAudio().getNumOfChannels());
            mediaRecorderConfiguration
                    .setStreamType(Utils.convertToMediaRecorderStreamType(recorderConfig.getRecorderStreamType()));
            int ret = agoraMediaRtcRecorder.setRecorderConfig(mediaRecorderConfiguration);
            SampleLogger.info("[" + taskId + "]startRecording setRecorderConfig mediaRecorderConfiguration:"
                    + mediaRecorderConfiguration + " ret:" + ret);

            setConfigBeforeStartRecording("");

            agoraMediaRtcRecorder.startRecording();
        }
    }

    public void startRecordingByUserId(String userId, int width, int height) {
        SampleLogger.info("[" + taskId + "]startRecordingByUserId userId:" + userId + " width:" + width + " height:"
                + height);
        if (singleRecordingUserList.contains(userId)) {
            SampleLogger.info("[" + taskId + "]startRecordingByUserId userId:" + userId + " is already recording");
            return;
        }
        MediaRecorderConfiguration mediaRecorderConfiguration = new MediaRecorderConfiguration();
        mediaRecorderConfiguration.setWidth(width != 0 ? width : recorderConfig.getVideo().getWidth());
        mediaRecorderConfiguration.setHeight(height != 0 ? height : recorderConfig.getVideo().getHeight());
        mediaRecorderConfiguration.setFps(recorderConfig.getVideo().getFps());
        mediaRecorderConfiguration.setStoragePath(recorderConfig.getRecorderPath() + userId + ".mp4");
        mediaRecorderConfiguration.setSampleRate(recorderConfig.getAudio().getSampleRate());
        mediaRecorderConfiguration.setChannelNum(recorderConfig.getAudio().getNumOfChannels());
        mediaRecorderConfiguration
                .setStreamType(Utils.convertToMediaRecorderStreamType(recorderConfig.getRecorderStreamType()));
        int ret = agoraMediaRtcRecorder.setRecorderConfigByUid(mediaRecorderConfiguration, userId);
        SampleLogger.info("[" + taskId + "]startRecordingByUserId setRecorderConfigByUid mediaRecorderConfiguration:"
                + mediaRecorderConfiguration + " ret:" + ret);

        setConfigBeforeStartRecording(userId);

        ret = agoraMediaRtcRecorder.startSingleRecordingByUid(userId);
        SampleLogger.info("[" + taskId + "]startRecordingByUserId startSingleRecordingByUid ret:" + ret);
        singleRecordingUserList.add(userId);
    }

    public void stopRecording() {
        SampleLogger.info("[" + taskId + "]stopRecording");
        if (null == agoraMediaRtcRecorder) {
            SampleLogger.info("stopRecording agoraMediaRtcRecorder is null");
            return;
        }

        if (recorderConfig.isSubAllAudio()) {
            agoraMediaRtcRecorder.unsubscribeAllAudio();
        } else {
            for (String userId : recorderConfig.getSubAudioUserList()) {
                agoraMediaRtcRecorder.unsubscribeAudio(userId);
            }
        }
        if (recorderConfig.isSubAllVideo()) {
            agoraMediaRtcRecorder.unsubscribeAllVideo();
        } else {
            for (String userId : recorderConfig.getSubVideoUserList()) {
                agoraMediaRtcRecorder.unsubscribeVideo(userId);
            }
        }
        if (recorderConfig.isMix()) {
            agoraMediaRtcRecorder.stopRecording();
        } else {
            for (String userId : singleRecordingUserList) {
                agoraMediaRtcRecorder.stopSingleRecordingByUid(userId);
            }
        }

        int ret = agoraMediaRtcRecorder.leaveChannel();
        SampleLogger.info("[" + taskId + "]stopRecording leaveChannel ret:" + ret);

        try {
            Thread.sleep(1 * 1000);
        } catch (InterruptedException e) {
            SampleLogger.error("stopRecording Thread.sleep failed");
        }
        agoraMediaRtcRecorder.unregisterRecorderEventHandle(this);

        agoraMediaRtcRecorder.release();
        agoraMediaRtcRecorder = null;

        if (null != videoLayoutManager) {
            videoLayoutManager.release();
            videoLayoutManager = null;
        }
    }

    public void stopRecordingByUserId(String userId) {
        SampleLogger.info("[" + taskId + "]stopRecordingByUserId userId:" + userId);
        if (null == agoraMediaRtcRecorder) {
            SampleLogger.info("stopRecordingByUserId agoraMediaRtcRecorder is null");
            return;
        }

        if (singleRecordingUserList.contains(userId)) {
            agoraMediaRtcRecorder.stopSingleRecordingByUid(userId);
            singleRecordingUserList.remove(userId);
        }
    }

    @Override
    public void onConnected(String channelId, String userId) {
        SampleLogger.info("onConnected channelId:" + channelId + " userId:" + userId);
        taskExecutorService.submit(() -> startRecording());
    }

    @Override
    public void onDisconnected(String channelId, String userId, Constants.ConnectionChangedReasonType reason) {
        SampleLogger.info("onDisconnected channelId:" + channelId + " userId:" + userId + " reason:" + reason);
    }

    @Override
    public void onReconnected(String channelId, String userId, Constants.ConnectionChangedReasonType reason) {
        SampleLogger.info("onReconnected channelId:" + channelId + " userId:" + userId + " reason:" + reason);
    }

    @Override
    public void onConnectionLost(String channelId, String userId) {
        SampleLogger.info("onConnectionLost channelId:" + channelId + " userId:" + userId);
        taskExecutorService.submit(() -> stopRecording());
    }

    @Override
    public void onUserJoined(String channelId, String userId) {
        SampleLogger.info("onUserJoined channelId:" + channelId + " userId:" + userId);
        if (!recorderConfig.isSubAllVideo() && !recorderConfig.getSubVideoUserList().contains(userId)) {
            return;
        }

        if (recorderConfig.isMix()) {
            if (Utils.recorderIsVideo(Utils.convertToMediaRecorderStreamType(recorderConfig.getRecorderStreamType()))
                    && null != videoLayoutManager) {
                taskExecutorService.submit(() -> videoLayoutManager.addUser(userId));
            }
        }
    }

    @Override
    public void onUserLeft(String channelId, String userId, Constants.UserOfflineReasonType reason) {
        SampleLogger.info("onUserLeft channelId:" + channelId + " userId:" + userId + " reason:" + reason);
        if (!recorderConfig.isMix()) {
            if (!singleRecordingUserList.isEmpty() && singleRecordingUserList.contains(userId)) {
                taskExecutorService.submit(() -> stopRecordingByUserId(userId));
            }
        } else {
            if (!recorderConfig.isSubAllVideo() && !recorderConfig.getSubVideoUserList().contains(userId)) {
                return;
            }
            if (Utils.recorderIsVideo(Utils.convertToMediaRecorderStreamType(recorderConfig.getRecorderStreamType()))
                    && null != videoLayoutManager) {
                taskExecutorService.submit(() -> videoLayoutManager.removeUser(userId));
            }
        }
    }

    @Override
    public void onFirstRemoteVideoDecoded(String channelId, String userId, int width, int height,
            int elapsed) {
        SampleLogger.info("onFirstRemoteVideoDecoded channelId:" + channelId + " userId:" + userId + " width:" + width
                + " height:" + height + " elapsed:" + elapsed);
        if (!recorderConfig.isMix() && Utils
                .recorderIsVideo(Utils.convertToMediaRecorderStreamType(recorderConfig.getRecorderStreamType()))) {
            if (recorderConfig.isSubAllVideo()
                    || (!recorderConfig.isSubAllVideo() && recorderConfig.getSubVideoUserList().contains(userId))) {
                taskExecutorService.submit(() -> startRecordingByUserId(userId, width, height));
            }
        }
    }

    @Override
    public void onFirstRemoteAudioDecoded(String channelId, String userId, int elapsed) {
        SampleLogger.info("onFirstRemoteAudioDecoded channelId:" + channelId + " userId:" + userId + " elapsed:"
                + elapsed);
        if (!recorderConfig.isMix() && Utils
                .recorderIsAudio(Utils.convertToMediaRecorderStreamType(recorderConfig.getRecorderStreamType()))) {
            if (recorderConfig.isSubAllAudio()
                    || (!recorderConfig.isSubAllAudio() && recorderConfig.getSubAudioUserList().contains(userId))) {
                taskExecutorService.submit(() -> startRecordingByUserId(userId, 0, 0));
            }
        }
    }

    @Override
    public void onAudioVolumeIndication(String channelId, String userId, int speakerNumber, int totalVolume) {
        SampleLogger.info("onAudioVolumeIndication channelId:" + channelId + " userId:" + userId + " speakerNumber:"
                + speakerNumber + " totalVolume:" + totalVolume);
    }

    @Override
    public void onActiveSpeaker(String channelId, String userId) {
        SampleLogger.info("onActiveSpeaker channelId:" + channelId + " userId:" + userId);
    }

    @Override
    public void onUserVideoStateChanged(String channelId, String userId, Constants.RemoteVideoState state,
            Constants.RemoteVideoStateReason reason, int elapsed) {
        SampleLogger.info("onUserVideoStateChanged channelId:" + channelId + " userId:" + userId + " state:" + state
                + " reason:" + reason + " elapsed:" + elapsed);
    }

    @Override
    public void onUserAudioStateChanged(String channelId, String userId, Constants.RemoteAudioState state,
            Constants.RemoteAudioStateReason reason, int elapsed) {
        SampleLogger.info("onUserAudioStateChanged channelId:" + channelId + " userId:" + userId + " state:" + state
                + " reason:" + reason + " elapsed:" + elapsed);
    }

    @Override
    public void onRemoteVideoStats(String channelId, String userId, RemoteVideoStatistics stats) {
        // SampleLogger.info("onRemoteVideoStats channelId:" + channelId + " userId:" +
        // userId + " stats:" + stats);
    }

    @Override
    public void onRemoteAudioStats(String channelId, String userId, RemoteAudioStatistics stats) {
        // SampleLogger.info("onRemoteAudioStats channelId:" + channelId + " userId:" +
        // userId + " stats:" + stats);
    }

    @Override
    public void onRecorderStateChanged(String channelId, String userId, Constants.RecorderState state,
            Constants.RecorderReasonCode reason, String fileName) {
        SampleLogger.info("onRecorderStateChanged channelId:" + channelId + " userId:" + userId + " state:" + state
                + " reason:" + reason + " fileName:" + fileName);
    }

    @Override
    public void onRecorderInfoUpdated(String channelId, String userId, RecorderInfo info) {
        SampleLogger.info("onRecorderInfoUpdated channelId:" + channelId + " userId:" + userId + " info:" + info);
    }
}
