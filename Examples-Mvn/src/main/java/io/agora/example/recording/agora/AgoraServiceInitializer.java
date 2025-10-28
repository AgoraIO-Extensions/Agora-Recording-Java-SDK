package io.agora.example.recording.agora;

import io.agora.recording.AgoraParameter;
import io.agora.recording.AgoraService;
import io.agora.recording.AgoraServiceConfiguration;
import io.agora.recording.LocalAccessPointConfiguration;
import io.agora.recording.LogConfig;
import java.io.File;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AgoraServiceInitializer {
    private static AgoraService agoraService;

    public static void initService(RecorderConfig recorderConfig) {
        if (recorderConfig == null) {
            log.info("recorderConfig is null");
            return;
        }

        if (agoraService == null) {
            log.info("AgoraService initService SDK Version: " + AgoraService.getSdkVersion());
            agoraService = new AgoraService();

            if (recorderConfig.getLocalAccessPoint() != null && recorderConfig.getLocalAccessPoint().getIpList() != null
                    && recorderConfig.getLocalAccessPoint().getIpList().size() > 0) {
                LocalAccessPointConfiguration localAccessPointConfig = new LocalAccessPointConfiguration();
                localAccessPointConfig.setMode(io.agora.recording.Constants.LocalProxyMode.LocalOnly);
                localAccessPointConfig
                        .setIpList(recorderConfig.getLocalAccessPoint().getIpList().toArray(new String[0]));
                localAccessPointConfig.setIpListSize(recorderConfig.getLocalAccessPoint().getIpList().size());
                localAccessPointConfig.setVerifyDomainName(recorderConfig.getLocalAccessPoint().getVerifyDomainName());
                int setGlobalLocalAccessPointRet = agoraService.setGlobalLocalAccessPoint(localAccessPointConfig);
                log.info("setGlobalLocalAccessPointRet: " + setGlobalLocalAccessPointRet);
            }

            AgoraServiceConfiguration config = new AgoraServiceConfiguration();
            config.setEnableAudioDevice(false);
            config.setEnableAudioProcessor(true);
            config.setEnableVideo(true);
            config.setAppId(recorderConfig.getAppId());
            config.setUseStringUid(recorderConfig.isUseStringUid());
            LogConfig logConfig = new LogConfig();
            logConfig.setFileSizeInKB(1024 * 20);
            logConfig.setFilePath("logs/agora_logs/agorasdk.log");
            config.setLogConfig(logConfig);
            int ret = agoraService.initialize(config);
            if (ret != 0) {
                log.info("Failed to initialize AgoraService, error: " + ret);
                return;
            }

            AgoraParameter parameter = agoraService.getAgoraParameter();
            if (recorderConfig.isUseCloudProxy()) {
                if (parameter != null) {
                    parameter.setBool("rtc.enable_proxy", true);
                    log.info("set the Cloud_Proxy Open!");
                }
            }
            if (recorderConfig.isRecoverFile()) {
                if (parameter != null) {
                    parameter.setBool("che.media_recorder_recover_files", true);
                }
            }

            if (recorderConfig.getLocalAccessPoint() != null && recorderConfig.getLocalAccessPoint().getIpList() != null
                    && recorderConfig.getLocalAccessPoint().getIpList().size() > 0) {
                if (parameter != null) {
                    parameter.setParameters("{\"rtc.enable_nasa2\":false}");
                }
            }
            initData(recorderConfig);
            log.info("AgoraService initialized");
        }
    }

    private static void initData(RecorderConfig recorderConfig) {
        if (recorderConfig == null) {
            log.info("recorderConfig is null");
            return;
        }

    }

    public static void destroy() {
        log.info("destroy");
        if (null == agoraService) {
            log.info("destroy agoraService is null");
            return;
        }
        agoraService.release();
        agoraService = null;
    }

    public static AgoraService getAgoraService() {
        return agoraService;
    }
}
