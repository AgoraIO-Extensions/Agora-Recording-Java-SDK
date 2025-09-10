package io.agora.example.recording.agora;

import io.agora.example.recording.model.RecordingUserInfo;
import io.agora.example.recording.utils.Utils;
import io.agora.recording.AgoraMediaRtcRecorder;
import io.agora.recording.AgoraService;
import io.agora.recording.Constants;
import io.agora.recording.Constants.WatermarkSourceType;
import io.agora.recording.EncodedVideoFrameInfo;
import io.agora.recording.EncryptionConfig;
import io.agora.recording.IAgoraMediaRtcRecorderEventHandler;
import io.agora.recording.IRecorderVideoFrameObserver;
import io.agora.recording.MediaRecorderConfiguration;
import io.agora.recording.RecorderInfo;
import io.agora.recording.RecorderVideoFrameCaptureConfig;
import io.agora.recording.RemoteAudioStatistics;
import io.agora.recording.RemoteVideoStatistics;
import io.agora.recording.SpeakVolumeInfo;
import io.agora.recording.VideoFrame;
import io.agora.recording.VideoSubscriptionOptions;
import io.agora.recording.WatermarkConfig;
import io.agora.recording.WatermarkLitera;
import io.agora.recording.WatermarkOptions;
import io.agora.recording.WatermarkTimestamp;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RecordingSession implements IAgoraMediaRtcRecorderEventHandler {
    private final String taskId;
    private final RecorderConfig recorderConfig;
    private final ThreadPoolExecutor taskExecutorService;
    private final ExecutorService singleExecutorService;

    private AgoraMediaRtcRecorder agoraMediaRtcRecorder;
    private AgoraService agoraService = null;
    private RecorderVideoFrameCaptureConfig recorderVideoFrameCaptureConfig = null;

    private final List<String> singleRecordingUserList = new CopyOnWriteArrayList<>();
    private VideoLayoutManager videoLayoutManager;
    private Constants.RecorderState recorderState = io.agora.recording.Constants.RecorderState.RECORDER_STATE_ERROR;
    private final AtomicBoolean startRecordingDone = new AtomicBoolean(false);
    private final List<RecordingUserInfo> waitForUpdateUiUserInfos = new CopyOnWriteArrayList<>();

    private String channelNameInternal;
    private String currentUserId;

    private String resultFilePath;

    public RecordingSession(
            String taskId, RecorderConfig recorderConfig, ThreadPoolExecutor taskExecutorService) {
        this.taskId = taskId;
        this.recorderConfig = recorderConfig;
        this.agoraService = AgoraServiceInitializer.getAgoraService();
        this.taskExecutorService = taskExecutorService;
        this.singleExecutorService = Executors.newSingleThreadExecutor();
        this.currentUserId = recorderConfig.getUserId();
    }

    public void joinChannel(String channelName) {
        log.info("[" + taskId + "]joinChannel channelName:" + channelName);
        if (null == agoraService || null == recorderConfig) {
            log.info("createRtcRecorder agoraService or factory  or eventHandler or "
                    + "recordingConfig is null");
            return;
        }

        if (null == agoraService || null == recorderConfig) {
            log.info("createRtcRecorder agoraService or factory  or eventHandler or "
                    + "recordingConfig is null");
            return;
        }

        agoraMediaRtcRecorder = agoraService.createMediaRtcRecorder();
        boolean recordEncodedOnly = false;
        if (recorderConfig.isEnableCapture() && (recorderConfig
                .getVideoFrameCaptureType() == Constants.VideoFrameCaptureType.VIDEO_FORMAT_ENCODED_FRAME_TYPE
                        .getValue())) {
            recordEncodedOnly = true;
        }
        log.info("[" + taskId + "]joinChannel recordEncodedOnly:" + recordEncodedOnly);
        agoraMediaRtcRecorder.initialize(agoraService, recorderConfig.isMix(), recordEncodedOnly);
        agoraMediaRtcRecorder.registerRecorderEventHandler(this);
        // agoraMediaRtcRecorder.setAudioVolumeIndicationParameters(500);

        this.videoLayoutManager = new VideoLayoutManager(agoraMediaRtcRecorder, recorderConfig);

        boolean enableEncryption = !io.agora.recording.utils.Utils
                .isNullOrEmpty(recorderConfig.getEncryption().getMode());

        if (enableEncryption) {
            EncryptionConfig encryptionConfig = new EncryptionConfig();
            encryptionConfig.setEncryptionMode(
                    Utils.convertToEncryptionMode(recorderConfig.getEncryption().getMode()));
            encryptionConfig.setEncryptionKey(recorderConfig.getEncryption().getKey());
            if (!io.agora.recording.utils.Utils.isNullOrEmpty(
                    recorderConfig.getEncryption().getSalt())) {
                encryptionConfig.setEncryptionKdfSalt(
                        io.agora.recording.utils.Utils.byteStringToByteArray(
                                recorderConfig.getEncryption().getSalt()));
            }
            log.info("[" + taskId
                    + "]joinChannel enableEncryption encryptionConfig:" + encryptionConfig);
            int ret = agoraMediaRtcRecorder.enableEncryption(true, encryptionConfig);
            log.info("[" + taskId + "]joinChannel enableEncryption ret:" + ret);
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
        if (recorderConfig.isSubAllVideo()) {
            agoraMediaRtcRecorder.subscribeAllVideo(options);
        } else {
            for (String userId : recorderConfig.getSubVideoUserList()) {
                agoraMediaRtcRecorder.subscribeVideo(userId, options);
            }
        }
        channelNameInternal = recorderConfig.getChannelName();
        if (!io.agora.recording.utils.Utils.isNullOrEmpty(channelName)) {
            channelNameInternal = channelName;
        }
        int ret = agoraMediaRtcRecorder.joinChannel(
                recorderConfig.getToken(), channelNameInternal, recorderConfig.getUserId());
        log.info("[" + taskId + "]joinChannel channelName:" + channelNameInternal
                + " userId:" + recorderConfig.getUserId() + " ret:" + ret);

        startRecordingDone.set(false);
    }

    private void setConfigBeforeStartRecording(String userId) {
        checkRecordingPath(recorderConfig.getRecorderPath());
        if (null != recorderConfig.getWaterMark() && recorderConfig.getWaterMark().size() > 0) {
            WatermarkConfig[] watermarks = new WatermarkConfig[recorderConfig.getWaterMark().size()];
            for (int i = 0; i < recorderConfig.getWaterMark().size(); i++) {
                watermarks[i] = new WatermarkConfig();
                watermarks[i].setIndex(i + 1);
                WatermarkSourceType watermarkSourceType = Utils.convertToWatermarkSourceType(
                        recorderConfig.getWaterMark().get(i).getType());
                watermarks[i].setType(watermarkSourceType);
                if (watermarkSourceType == WatermarkSourceType.LITERA) {
                    WatermarkLitera watermarkLitera = new WatermarkLitera();
                    watermarkLitera.setWmLitera(recorderConfig.getWaterMark().get(i).getLitera());
                    watermarkLitera.setFontFilePath(
                            recorderConfig.getWaterMark().get(i).getFontFilePath());
                    watermarkLitera.setFontSize(recorderConfig.getWaterMark().get(i).getFontSize());
                    watermarks[i].setLiteraSource(watermarkLitera);
                } else if (watermarkSourceType == WatermarkSourceType.TIMESTAMPS) {
                    WatermarkTimestamp watermarkTimestamp = new WatermarkTimestamp();
                    watermarkTimestamp.setFontFilePath(
                            recorderConfig.getWaterMark().get(i).getFontFilePath());
                    watermarkTimestamp.setFontSize(
                            recorderConfig.getWaterMark().get(i).getFontSize());
                    watermarks[i].setTimestampSource(watermarkTimestamp);
                } else if (watermarkSourceType == WatermarkSourceType.PICTURE) {
                    watermarks[i].setImageUrl(recorderConfig.getWaterMark().get(i).getImgUrl());
                }

                WatermarkOptions watermarkOptions = new WatermarkOptions();
                watermarkOptions.setMode(Constants.WatermarkFitMode.FIT_MODE_COVER_POSITION);
                watermarkOptions.setZOrder(recorderConfig.getWaterMark().get(i).getZorder());

                io.agora.recording.Rectangle positionInPortraitMode = new io.agora.recording.Rectangle();
                positionInPortraitMode.setX(recorderConfig.getWaterMark().get(i).getX());
                positionInPortraitMode.setY(recorderConfig.getWaterMark().get(i).getY());
                positionInPortraitMode.setWidth(recorderConfig.getWaterMark().get(i).getWidth());
                positionInPortraitMode.setHeight(recorderConfig.getWaterMark().get(i).getHeight());

                io.agora.recording.Rectangle positionInLandscapeMode = new io.agora.recording.Rectangle();
                positionInLandscapeMode.setX(recorderConfig.getWaterMark().get(i).getX());
                positionInLandscapeMode.setY(recorderConfig.getWaterMark().get(i).getY());
                positionInLandscapeMode.setWidth(recorderConfig.getWaterMark().get(i).getWidth());
                positionInLandscapeMode.setHeight(recorderConfig.getWaterMark().get(i).getHeight());

                watermarkOptions.setPositionInLandscapeMode(positionInLandscapeMode);
                watermarkOptions.setPositionInPortraitMode(positionInPortraitMode);
                watermarks[i].setOptions(watermarkOptions);
            }

            log.info("[" + taskId
                    + "] enableAndUpdateVideoWatermarks watermarks:" + Arrays.toString(watermarks));

            int ret = -1;

            if (io.agora.recording.utils.Utils.isNullOrEmpty(userId)) {
                ret = agoraMediaRtcRecorder.enableAndUpdateVideoWatermarks(watermarks);
            } else {
                ret = agoraMediaRtcRecorder.enableAndUpdateVideoWatermarksByUid(watermarks, userId);
            }
            log.info(
                    "[" + taskId + "] enableAndUpdateVideoWatermarks userId " + userId + " ret:" + ret);
        }
    }

    private void checkRecordingPath(String path) {
        if (io.agora.recording.utils.Utils.isNullOrEmpty(path)) {
            log.warn("[" + taskId + "] checkRecordingPath skipped: recorderPath is empty");
            return;
        }

        File target = new File(path);
        boolean treatAsFile = !path.toLowerCase().endsWith("/");

        File dirToEnsure = treatAsFile ? target.getParentFile() : target;

        if (dirToEnsure == null) {
            // No parent directory to ensure (e.g., relative file in CWD)
            log.info("[" + taskId + "] checkRecordingPath: no parent dir to create for path=" + path);
            return;
        }

        if (dirToEnsure.exists()) {
            if (!dirToEnsure.isDirectory()) {
                throw new IllegalStateException("Path exists but is not a directory: "
                        + dirToEnsure.getAbsolutePath());
            }
            return;
        }

        if (!dirToEnsure.mkdirs()) {
            throw new IllegalStateException("Failed to create directory: " + dirToEnsure.getAbsolutePath());
        }

        log.info("[" + taskId + "] checkRecordingPath: created directory " + dirToEnsure.getAbsolutePath());
    }

    public void startRecording(String userId, int width, int height) {
        if (null == agoraMediaRtcRecorder) {
            log.info("startRecording agoraMediaRtcRecorder is null");
            return;
        }
        log.info("[" + taskId + "]startRecording channelName:" + channelNameInternal
                + " userId:" + userId + " width:" + width + " height:" + height);
        if (singleRecordingUserList.contains(userId)) {
            log.info("[" + taskId + "]startRecording userId:" + userId + " is already recording");
            return;
        }

        setConfigBeforeStartRecording(userId);

        MediaRecorderConfiguration mediaRecorderConfiguration = new MediaRecorderConfiguration();
        mediaRecorderConfiguration.setWidth(
                width != 0 ? width : recorderConfig.getVideo().getWidth());
        mediaRecorderConfiguration.setHeight(
                height != 0 ? height : recorderConfig.getVideo().getHeight());
        mediaRecorderConfiguration.setFps(recorderConfig.getVideo().getFps());
        mediaRecorderConfiguration.setMaxDurationMs(recorderConfig.getMaxDuration() * 1000);
        if (io.agora.recording.utils.Utils.isNullOrEmpty(userId)) {
            resultFilePath = recorderConfig.getRecorderPath().substring(
                    0, recorderConfig.getRecorderPath().lastIndexOf(".mp4"))
                    + "_" + channelNameInternal + "_" + currentUserId + "_"
                    + io.agora.recording.utils.Utils.formatTimestamp(
                            System.currentTimeMillis(), "yyyyMMdd-HHmmssSSS")
                    + ".mp4";
        } else {
            resultFilePath = recorderConfig.getRecorderPath() + userId
                    + "_" + channelNameInternal + "_" + currentUserId + "_"
                    + io.agora.recording.utils.Utils.formatTimestamp(
                            System.currentTimeMillis(), "yyyyMMdd-HHmmssSSS")
                    + ".mp4";
        }
        mediaRecorderConfiguration.setStoragePath(resultFilePath);
        mediaRecorderConfiguration.setSampleRate(recorderConfig.getAudio().getSampleRate());
        mediaRecorderConfiguration.setChannelNum(recorderConfig.getAudio().getNumOfChannels());
        mediaRecorderConfiguration.setStreamType(
                Utils.convertToMediaRecorderStreamType(recorderConfig.getRecorderStreamType()));
        mediaRecorderConfiguration.setVideoSourceType(
                io.agora.recording.Constants.VideoSourceType.VIDEO_SOURCE_CAMERA_SECONDARY);

        log.info("[" + taskId
                + "]startRecording mediaRecorderConfiguration:" + mediaRecorderConfiguration);

        int ret = -1;
        if (io.agora.recording.utils.Utils.isNullOrEmpty(userId)) {
            ret = agoraMediaRtcRecorder.setRecorderConfig(mediaRecorderConfiguration);
            log.info("[" + taskId + "]startRecording setRecorderConfig ret:" + ret);
            ret = agoraMediaRtcRecorder.startRecording();
            log.info("[" + taskId + "]startRecording  ret:" + ret);
        } else {
            ret = agoraMediaRtcRecorder.setRecorderConfigByUid(mediaRecorderConfiguration, userId);
            log.info("[" + taskId + "]startRecording setRecorderConfigByUid ret:" + ret);
            ret = agoraMediaRtcRecorder.startSingleRecordingByUid(userId);
            singleRecordingUserList.add(userId);
            log.info("[" + taskId + "]startRecordingByUserId userId:" + userId + " ret:" + ret);
        }
        startRecordingDone.set(true);

        if (!waitForUpdateUiUserInfos.isEmpty()) {
            for (RecordingUserInfo waitUserId : waitForUpdateUiUserInfos) {
                taskExecutorService.submit(
                        () -> videoLayoutManager.addRecordingUserInfo(waitUserId.getUserId(),
                                waitUserId.getVideoWidth(), waitUserId.getVideoHeight()));
            }
            waitForUpdateUiUserInfos.clear();
        }
    }

    public void stopRecording() {
        log.info("[" + taskId + "]stopRecording");
        if (null == agoraMediaRtcRecorder) {
            log.info("stopRecording agoraMediaRtcRecorder is null");
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
            for (String userId : singleRecordingUserList) {
                agoraMediaRtcRecorder.unsubscribeVideo(userId);
            }
        }
        if (recorderConfig.isEnableRecording()) {
            if (recorderState == io.agora.recording.Constants.RecorderState.RECORDER_STATE_START) {
                if (recorderConfig.isMix()) {
                    agoraMediaRtcRecorder.stopRecording();
                } else {
                    for (String userId : singleRecordingUserList) {
                        stopRecordingByUserId(userId);
                    }
                }
            }
        }

        if (recorderConfig.isEnableCapture()) {
            startCapture(false);
        }

        int ret = agoraMediaRtcRecorder.leaveChannel();
        log.info("[" + taskId + "]stopRecording leaveChannel ret:" + ret);

        try {
            Thread.sleep(1 * 1000);
        } catch (InterruptedException e) {
            log.error("stopRecording Thread.sleep failed");
        }
        agoraMediaRtcRecorder.unregisterRecorderEventHandler(this);

        agoraMediaRtcRecorder.release();
        agoraMediaRtcRecorder = null;

        if (null != videoLayoutManager) {
            videoLayoutManager.release();
            videoLayoutManager = null;
        }

        startRecordingDone.set(false);
        waitForUpdateUiUserInfos.clear();
        singleExecutorService.shutdown();
    }

    public void stopRecordingByUserId(String userId) {
        log.info("[" + taskId + "]stopRecordingByUserId userId:" + userId);
        if (null == agoraMediaRtcRecorder) {
            log.info("stopRecordingByUserId agoraMediaRtcRecorder is null");
            return;
        }

        if (singleRecordingUserList.contains(userId)) {
            agoraMediaRtcRecorder.stopSingleRecordingByUid(userId);
            singleRecordingUserList.remove(userId);
        }
    }

    private void startCapture(boolean enable) {
        log.info("[" + taskId + "]startCapture enable:" + enable);
        if (null == agoraMediaRtcRecorder) {
            log.info("startCapture agoraMediaRtcRecorder is null");
            return;
        }

        if (enable) {
            checkRecordingPath(recorderConfig.getCapturePath());
            recorderVideoFrameCaptureConfig = new RecorderVideoFrameCaptureConfig();
            recorderVideoFrameCaptureConfig
                    .setVideoFrameType(
                            Constants.VideoFrameCaptureType.fromInt(recorderConfig.getVideoFrameCaptureType()));
            if (recorderConfig.getVideoFrameCaptureType() == Constants.VideoFrameCaptureType.VIDEO_FORMAT_JPG_FILE_TYPE
                    .getValue()) {
                String jpgCapturePath = recorderConfig.getCapturePath() + currentUserId + "/";
                checkRecordingPath(jpgCapturePath);
                recorderVideoFrameCaptureConfig
                        .setJpgFileStorePath(jpgCapturePath);
                recorderVideoFrameCaptureConfig.setJpgCaptureIntervalInSec(recorderConfig.getJpgCaptureIntervalInSec());
            }
            recorderVideoFrameCaptureConfig.setObserver(new IRecorderVideoFrameObserver() {
                @Override
                public void onYuvFrameCaptured(String channelId, String userId, VideoFrame frame) {
                    log.info("[" + taskId + "]onYuvFrameCaptured channelId:" + channelId + "userId:" + userId
                            + " frame:" + frame);
                    if (frame == null) {
                        log.info("[" + taskId + "]onYuvFrameCaptured frame is null");
                        return;
                    }
                    if (io.agora.recording.utils.Utils.isNullOrEmpty(recorderConfig.getCapturePath())) {
                        return;
                    }
                    // Calculate actual data size without padding
                    int width = frame.getWidth();
                    int height = frame.getHeight();
                    int yStride = frame.getyStride();
                    int uStride = frame.getuStride();
                    int vStride = frame.getvStride();

                    // YUV420P format: Y plane full size, U/V planes quarter size
                    int yDataSize = width * height;
                    int uvDataSize = (width / 2) * (height / 2);

                    // Check if stride and buffer are valid
                    if (yStride < width || uStride < (width / 2) || vStride < (width / 2)) {
                        log.warn("[" + taskId + "] Invalid stride: yStride=" + yStride +
                                ", uStride=" + uStride + ", vStride=" + vStride +
                                ", width=" + width + ", height=" + height);
                        return;
                    }

                    if (frame.getyBuffer().length < yStride * height ||
                            frame.getuBuffer().length < uStride * (height / 2) ||
                            frame.getvBuffer().length < vStride * (height / 2)) {
                        log.warn("[" + taskId + "] YUV buffer size insufficient for stride data");
                        return;
                    }

                    byte[] data = new byte[yDataSize + uvDataSize + uvDataSize];
                    int dataOffset = 0;

                    // Copy Y plane line by line to remove padding
                    byte[] yBuffer = frame.getyBuffer();
                    for (int row = 0; row < height; row++) {
                        System.arraycopy(yBuffer, row * yStride, data, dataOffset, width);
                        dataOffset += width;
                    }

                    // Copy U plane line by line to remove padding
                    byte[] uBuffer = frame.getuBuffer();
                    int uvWidth = width / 2;
                    int uvHeight = height / 2;
                    for (int row = 0; row < uvHeight; row++) {
                        System.arraycopy(uBuffer, row * uStride, data, dataOffset, uvWidth);
                        dataOffset += uvWidth;
                    }

                    // Copy V plane line by line to remove padding
                    byte[] vBuffer = frame.getvBuffer();
                    for (int row = 0; row < uvHeight; row++) {
                        System.arraycopy(vBuffer, row * vStride, data, dataOffset, uvWidth);
                        dataOffset += uvWidth;
                    }

                    // Standard YUV420P format (no padding), add original stride info for reference
                    String savePath = recorderConfig.getCapturePath() + channelNameInternal + "_"
                            + currentUserId + "_" + userId + "_w" + frame.getWidth() + "_h" + frame.getHeight()
                            + ".yuv";
                    singleExecutorService.submit(() -> Utils.saveDataToFile(savePath, data, true));
                }

                @Override
                public void onEncodedFrameReceived(String channelId, String userId, byte[] imageBuffer,
                        EncodedVideoFrameInfo info) {
                    log.info("[" + taskId + "]onEncodedFrameReceived channelId:" + channelId + " userId:" + userId
                            + " imageBuffer.length:" + imageBuffer.length + " info:" + info);
                    if (io.agora.recording.utils.Utils.isNullOrEmpty(recorderConfig.getCapturePath())) {
                        return;
                    }
                    if (info != null) {
                        if (info.getCodecType() == Constants.VideoCodecType.VIDEO_CODEC_H264.getValue()) {
                            String savePath = recorderConfig.getCapturePath() + channelNameInternal + "_"
                                    + currentUserId + "_" + userId + ".h264";
                            singleExecutorService.submit(() -> Utils.saveDataToFile(savePath, imageBuffer, true));
                        } else if (info.getCodecType() == Constants.VideoCodecType.VIDEO_CODEC_GENERIC_JPEG
                                .getValue()) {
                            String savePath = recorderConfig.getCapturePath() + channelNameInternal + "_"
                                    + currentUserId + "_" + userId + "_"

                                    + io.agora.recording.utils.Utils.formatTimestamp(
                                            System.currentTimeMillis(), "yyyyMMdd-HHmmssSSS")
                                    + ".jpg";
                            singleExecutorService.submit(() -> Utils.saveDataToFile(savePath, imageBuffer, true));
                        }
                    }
                }

                @Override
                public void onJPGFileSaved(String channelId, String userId, String filename) {
                    log.info("[" + taskId + "]onJPGFileSaved channelId:" + channelId + " userId:" + userId
                            + " filename:" + filename);
                }
            });
            log.info("[" + taskId + "]startCapture recorderVideoFrameCaptureConfig:" + recorderVideoFrameCaptureConfig);
            int ret = agoraMediaRtcRecorder.enableRecorderVideoFrameCapture(true, recorderVideoFrameCaptureConfig);
            log.info("[" + taskId + "]startCapture enableRecorderVideoFrameCapture ret:" + ret);
        } else {
            int ret = agoraMediaRtcRecorder.enableRecorderVideoFrameCapture(false, recorderVideoFrameCaptureConfig);
            log.info("[" + taskId + "]startCapture enableRecorderVideoFrameCapture ret:" + ret);
        }
    }

    public String getResultFilePath() {
        return resultFilePath;
    }

    @Override
    public void onConnected(String channelId, String userId) {
        log.info("[" + taskId + "]onConnected channelId:" + channelId + " userId:" + userId);
        this.currentUserId = userId;
        if (recorderConfig.isEnableRecording()) {
            taskExecutorService.submit(() -> {
                if (recorderConfig.isMix()) {
                    startRecording("", 0, 0);
                }
            });
        }
        if (recorderConfig.isEnableCapture()) {
            taskExecutorService.submit(() -> startCapture(true));
        }
    }

    @Override
    public void onDisconnected(
            String channelId, String userId, Constants.ConnectionChangedReasonType reason) {
        log.info("[" + taskId + "]onDisconnected channelId:" + channelId + " userId:" + userId
                + " reason:" + reason);
    }

    @Override
    public void onReconnected(
            String channelId, String userId, Constants.ConnectionChangedReasonType reason) {
        log.info("[" + taskId + "]onReconnected channelId:" + channelId + " userId:" + userId
                + " reason:" + reason);
    }

    @Override
    public void onConnectionLost(String channelId, String userId) {
        log.info("[" + taskId + "]onConnectionLost channelId:" + channelId + " userId:" + userId);
        taskExecutorService.submit(() -> stopRecording());
    }

    @Override
    public void onUserJoined(String channelId, String userId) {
        log.info("[" + taskId + "]onUserJoined channelId:" + channelId + " userId:" + userId);
        if (!recorderConfig.isSubAllVideo()
                && !recorderConfig.getSubVideoUserList().contains(userId)) {
            return;
        }
    }

    @Override
    public void onUserLeft(
            String channelId, String userId, Constants.UserOfflineReasonType reason) {
        log.info("[" + taskId + "]onUserLeft channelId:" + channelId + " userId:" + userId
                + " reason:" + reason);
        if (!recorderConfig.isMix()) {
            if (!singleRecordingUserList.isEmpty() && singleRecordingUserList.contains(userId)) {
                taskExecutorService.submit(() -> stopRecordingByUserId(userId));
            }
        } else {
            if (!recorderConfig.isSubAllVideo()
                    && !recorderConfig.getSubVideoUserList().contains(userId)) {
                return;
            }
            if (Utils.recorderIsVideo(
                    Utils.convertToMediaRecorderStreamType(recorderConfig.getRecorderStreamType()))
                    && null != videoLayoutManager) {
                taskExecutorService.submit(
                        () -> videoLayoutManager.removeRecordingUserInfo(userId));
            }
        }
    }

    @Override
    public void onFirstRemoteVideoDecoded(
            String channelId, String userId, int width, int height, int elapsed) {
        log.info("[" + taskId + "]onFirstRemoteVideoDecoded channelId:" + channelId + " userId:"
                + userId + " width:" + width + " height:" + height + " elapsed:" + elapsed);
        if (recorderConfig.isEnableRecording()) {
            if (!recorderConfig.isMix()
                    && Utils.recorderIsVideo(
                            Utils.convertToMediaRecorderStreamType(recorderConfig.getRecorderStreamType()))) {
                if (recorderConfig.isSubAllVideo()
                        || (!recorderConfig.isSubAllVideo()
                                && recorderConfig.getSubVideoUserList().contains(userId))) {
                    taskExecutorService.submit(() -> startRecording(userId, width, height));
                }
            }
            if (recorderConfig.isMix()) {
                if (Utils.recorderIsVideo(
                        Utils.convertToMediaRecorderStreamType(recorderConfig.getRecorderStreamType()))
                        && null != videoLayoutManager) {
                    if (startRecordingDone.get()) {
                        taskExecutorService.submit(
                                () -> videoLayoutManager.addRecordingUserInfo(userId, width, height));
                    } else {
                        RecordingUserInfo recordingUserInfo = new RecordingUserInfo();
                        recordingUserInfo.setUserId(userId);
                        recordingUserInfo.setVideoWidth(width);
                        recordingUserInfo.setVideoHeight(height);
                        waitForUpdateUiUserInfos.add(recordingUserInfo);
                    }
                }
            }
        }
    }

    @Override
    public void onFirstRemoteAudioDecoded(String channelId, String userId, int elapsed) {
        log.info("[" + taskId + "]onFirstRemoteAudioDecoded channelId:" + channelId
                + " userId:" + userId + " elapsed:" + elapsed);
        if (recorderConfig.isEnableRecording()) {
            if (!recorderConfig.isMix()
                    && Utils.convertToMediaRecorderStreamType(recorderConfig
                            .getRecorderStreamType()) == Constants.MediaRecorderStreamType.STREAM_TYPE_AUDIO) {
                if (recorderConfig.isSubAllAudio()
                        || (!recorderConfig.isSubAllAudio()
                                && recorderConfig.getSubAudioUserList().contains(userId))) {
                    taskExecutorService.submit(() -> startRecording(userId, 0, 0));
                }
            }
        }
    }

    @Override
    public void onAudioVolumeIndication(
            String channelId, SpeakVolumeInfo[] speakers, int speakerNumber) {
        log.info("[" + taskId + "]onAudioVolumeIndication channelId:" + channelId
                + " speakerNumber:" + speakerNumber);
        if (speakers != null && speakers.length > 0) {
            for (SpeakVolumeInfo speaker : speakers) {
                log.info("[" + taskId + "]onAudioVolumeIndication speaker:" + speaker.getUserId()
                        + " volume:" + speaker.getVolume());
            }
        }
    }

    @Override
    public void onActiveSpeaker(String channelId, String userId) {
        log.info("[" + taskId + "]onActiveSpeaker channelId:" + channelId + " userId:" + userId);
    }

    @Override
    public void onUserVideoStateChanged(String channelId, String userId,
            Constants.RemoteVideoState state, Constants.RemoteVideoStateReason reason, int elapsed) {
        log.info("[" + taskId + "]onUserVideoStateChanged channelId:" + channelId + " userId:"
                + userId + " state:" + state + " reason:" + reason + " elapsed:" + elapsed);
    }

    @Override
    public void onUserAudioStateChanged(String channelId, String userId,
            Constants.RemoteAudioState state, Constants.RemoteAudioStateReason reason, int elapsed) {
        log.info("[" + taskId + "]onUserAudioStateChanged channelId:" + channelId + " userId:"
                + userId + " state:" + state + " reason:" + reason + " elapsed:" + elapsed);
    }

    @Override
    public void onRemoteVideoStats(String channelId, String userId, RemoteVideoStatistics stats) {
        log.info(
                "onRemoteVideoStats channelId:" + channelId + " userId:" + userId + " stats:" + stats);
    }

    @Override
    public void onRemoteAudioStats(String channelId, String userId, RemoteAudioStatistics stats) {
        log.info(
                "onRemoteAudioStats channelId:" + channelId + " userId:" + userId + " stats:" + stats);
    }

    @Override
    public void onRecorderStateChanged(String channelId, String userId,
            Constants.RecorderState state, Constants.RecorderReasonCode reason, String fileName) {
        log.info("[" + taskId + "]onRecorderStateChanged channelId:" + channelId + " userId:"
                + userId + " state:" + state + " reason:" + reason + " fileName:" + fileName);
        recorderState = state;
        if (state == Constants.RecorderState.RECORDER_STATE_START) {
            if (!new File(fileName).exists()) {
                log.info("[" + taskId + "]onRecorderStateChanged fileName:" + fileName + " not exists");
                System.exit(1);
            }
            resultFilePath = fileName;
        }
    }

    @Override
    public void onRecorderInfoUpdated(String channelId, String userId, RecorderInfo info) {
        log.info("[" + taskId + "]onRecorderInfoUpdated channelId:" + channelId
                + " userId:" + userId + " info:" + info);
    }

    @Override
    public void onEncryptionError(String channelId, Constants.EncryptionErrorType errorType) {
        log.info(
                "[" + taskId + "]onEncryptionError channelId:" + channelId + " errorType:" + errorType);
    }

    @Override
    public void onError(String channelId, Constants.ErrorCodeType error, String message) {
        log.info("[" + taskId + "]onError channelId:" + channelId + " error:" + error
                + " message:" + message);
    }

    @Override
    public void onTokenPrivilegeWillExpire(String channelId, String token) {
        log.info("[" + taskId + "]onTokenPrivilegeWillExpire channelId:" + channelId
                + " token:" + token);
        taskExecutorService.submit(() -> {
            if (null != agoraMediaRtcRecorder) {
                // just for test, renew token when token privilege did expire
                agoraMediaRtcRecorder.renewToken(recorderConfig.getToken());
            }
        });
    }

    @Override
    public void onTokenPrivilegeDidExpire(String channelId) {
        log.info("[" + taskId + "]onTokenPrivilegeDidExpire channelId:" + channelId);
        taskExecutorService.submit(() -> {
            if (null != agoraMediaRtcRecorder) {
                // just for test, renew token when token privilege did expire
                agoraMediaRtcRecorder.renewToken(recorderConfig.getToken());
            }
        });
    }

    public void onVideoSizeChanged(String channelId, String userId, int width, int height, int rotation) {
        log.info("[" + taskId + "]onVideoSizeChanged channelId:" + channelId + " userId:" + userId
                + " width:" + width + " height:" + height + " rotation:" + rotation);
    }

}
