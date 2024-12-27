package io.agora.recording.test;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import io.agora.recording.AgoraMediaRtcRecorder;
import io.agora.recording.MixerLayoutConfig;
import io.agora.recording.UserMixerLayout;
import io.agora.recording.VideoMixingLayout;
import io.agora.recording.test.utils.SampleLogger;

public class VideoLayoutManager {
    private AgoraMediaRtcRecorder recorder;
    private RecorderConfig recorderConfig;
    private List<String> userIds;

    public VideoLayoutManager(AgoraMediaRtcRecorder recorder, RecorderConfig recorderConfig) {
        this.recorder = recorder;
        this.recorderConfig = recorderConfig;
        this.userIds = new CopyOnWriteArrayList<>();
    }

    public void release() {
        userIds.clear();
    }

    public synchronized void addUser(String userId) {
        SampleLogger.info("VideoLayoutManager addUser userId: " + userId);
        if (!userIds.contains(userId)) {
            userIds.add(userId);
        }
        updateVideoMixLayout();
    }

    public synchronized void removeUser(String userId) {
        SampleLogger.info("removeUser userId: " + userId);
        if (userIds.contains(userId)) {
            userIds.remove(userId);
        }
        updateVideoMixLayout();
    }

    private void updateVideoMixLayout() {
        if (userIds.size() > 0 && userIds.size() <= 17) {
            VideoMixingLayout layout = new VideoMixingLayout();
            layout.setCanvasWidth(recorderConfig.getVideo().getWidth());
            layout.setCanvasHeight(recorderConfig.getVideo().getHeight());
            layout.setCanvasFps(recorderConfig.getVideo().getFps());

            UserMixerLayout[] useLayout = new UserMixerLayout[userIds.size()];
            for (int i = 0; i < useLayout.length; i++) {
                useLayout[i] = new UserMixerLayout();
            }

            switch (recorderConfig.getLayoutMode()) {
                case ExampleConstants.BESTFIT_LAYOUT:
                    adjustBestFitVideoLayout(useLayout);
                    break;
                case ExampleConstants.DEFAULT_LAYOUT:
                    adjustDefaultVideoLayout(useLayout);
                    break;
                case ExampleConstants.VERTICAL_LAYOUT:
                    adjustVerticalLayout(useLayout, recorderConfig.getMaxResolutionUid());
                    break;
            }
            for (int i = 0; i < userIds.size(); i++) {
                String userId = useLayout[i].getUserId();
                for (RecorderConfig.Rotation rotation : recorderConfig.getRotation()) {
                    if (rotation.getUid().equals(userId)) {
                        useLayout[i].getConfig().setRotation(rotation.getDegree());
                    }
                }
            }

            layout.setUserLayoutConfigs(useLayout);
            int ret = recorder.setVideoMixingLayout(layout);
            SampleLogger.info("updateVideoMixLayout layout:" + layout + " ret: " + ret);
        }
    }

    private void adjustBestFitVideoLayout(UserMixerLayout[] regionList) {
        if (regionList.length == 0) {
            return;
        }

        if (userIds.size() == 1) {
            adjustBestFitLayouSquare(regionList, 1);
        } else if (userIds.size() == 2) {
            adjustBestFitLayout2(regionList);
        } else if (userIds.size() <= 4) {
            adjustBestFitLayouSquare(regionList, 2);
        } else if (userIds.size() <= 9) {
            adjustBestFitLayouSquare(regionList, 3);
        } else if (userIds.size() <= 16) {
            adjustBestFitLayouSquare(regionList, 4);
        } else if (userIds.size() == 17) {
            adjustBestFitLayout17(regionList);
        } else {
            SampleLogger.error("adjustBestFitVideoLayout is more than 17 users");
        }
    }

    private void adjustBestFitLayouSquare(UserMixerLayout[] regionList, int square) {
        if (regionList.length == 0) {
            return;
        }

        float viewWidth = 1.0f / square;
        float viewHEdge = 1.0f / square;
        int i = 0;

        for (String userId : userIds) {
            float xIndex = i % square;
            float yIndex = i / square;
            regionList[i].setUserId(userId);
            MixerLayoutConfig config = new MixerLayoutConfig();
            config.setX((int) (viewWidth * xIndex * recorderConfig.getVideo().getWidth()));
            config.setY((int) (viewHEdge * yIndex * recorderConfig.getVideo().getHeight()));
            config.setWidth((int) (viewWidth * recorderConfig.getVideo().getWidth()));
            config.setHeight((int) (viewHEdge * recorderConfig.getVideo().getHeight()));
            regionList[i].setConfig(config);

            i++;
        }
    }

    private void adjustBestFitLayout2(UserMixerLayout[] regionList) {
        int i = 0;

        for (String userId : userIds) {
            regionList[i].setUserId(userId);
            MixerLayoutConfig config = new MixerLayoutConfig();
            config.setX((int) (((i % 2 == 0) ? 0 : 0.5f) * recorderConfig.getVideo().getWidth()));
            config.setY(0);
            config.setWidth((int) (0.5f * recorderConfig.getVideo().getWidth()));
            config.setHeight(recorderConfig.getVideo().getHeight());
            regionList[i].setConfig(config);
            i++;
        }
    }

    private void adjustBestFitLayout17(UserMixerLayout[] regionList) {
        int n = 5;
        float viewWidth = 1.0f / n;
        float viewHEdge = 1.0f / n;

        int i = 0;
        for (String userId : userIds) {
            float xIndex = i % (n - 1);
            float yIndex = i / (n - 1);
            regionList[i].setUserId(userId);
            MixerLayoutConfig config = new MixerLayoutConfig();
            config.setWidth((int) (viewWidth * recorderConfig.getVideo().getWidth()));
            config.setHeight((int) (viewHEdge * recorderConfig.getVideo().getHeight()));

            if (i == 16) {
                config.setX((int) ((1 - viewWidth) * 0.5f * recorderConfig.getVideo().getWidth()));

            } else {
                config.setX((int) ((0.5f * viewWidth + viewWidth * xIndex) * recorderConfig.getVideo().getWidth()));
            }
            config.setY((int) ((1.0f / n) * yIndex * recorderConfig.getVideo().getHeight()));
            regionList[i].setConfig(config);
            i++;
        }
    }

    private void adjustDefaultVideoLayout(UserMixerLayout[] regionList) {
        if (regionList.length == 0) {
            return;
        }
        regionList[0].setUserId(userIds.get(0));
        MixerLayoutConfig config = new MixerLayoutConfig();
        config.setX(0);
        config.setY(0);
        config.setWidth(recorderConfig.getVideo().getWidth());
        config.setHeight(recorderConfig.getVideo().getHeight());
        config.setAlpha(1.0f);
        regionList[0].setConfig(config);

        float canvasWidth = recorderConfig.getVideo().getWidth();
        float canvasHeight = recorderConfig.getVideo().getHeight();

        float viewWidth = 0.235f;
        float viewHEdge = 0.012f;
        float viewHeight = viewWidth * (canvasWidth / canvasHeight);
        float viewVEdge = viewHEdge * (canvasWidth / canvasHeight);

        for (int i = 1; i < userIds.size(); i++) {
            regionList[i].setUserId(userIds.get(i));
            float xIndex = (i - 1) % 4;
            float yIndex = (i - 1) / 4;
            MixerLayoutConfig layoutConfig = new MixerLayoutConfig();
            layoutConfig.setX((int) ((xIndex * (viewWidth + viewHEdge) + viewHEdge) * canvasWidth));
            layoutConfig.setY((int) ((1 - (yIndex + 1) * (viewHeight + viewVEdge)) * canvasHeight));
            layoutConfig.setWidth((int) (viewWidth * canvasWidth));
            layoutConfig.setHeight((int) (viewHeight * canvasHeight));
            layoutConfig.setAlpha(i + 1);
            regionList[i].setConfig(layoutConfig);
        }
    }

    private void adjustVerticalLayout(UserMixerLayout[] regionList, String maxResolutionUid) {
        if (userIds.size() <= 5) {
            adjustVideo5Layout(regionList, maxResolutionUid);
        } else if (userIds.size() <= 7) {
            adjustVideo7Layout(regionList, maxResolutionUid);
        } else if (userIds.size() <= 9) {
            adjustVideo9Layout(regionList, maxResolutionUid);
        } else {
            adjustVideo17Layout(regionList, maxResolutionUid);
        }
    }

    private void setMaxResolutionUid(int number, String maxResolutionUid, UserMixerLayout[] regionList,
            double weightRatio) {
        regionList[number].setUserId(maxResolutionUid);
        MixerLayoutConfig layoutConfig = new MixerLayoutConfig();
        layoutConfig.setX(0);
        layoutConfig.setY(0);
        layoutConfig.setWidth((int) (weightRatio * recorderConfig.getVideo().getWidth()));
        layoutConfig.setHeight(recorderConfig.getVideo().getHeight());
        layoutConfig.setAlpha(1.0f);
        regionList[number].setConfig(layoutConfig);
    }

    private void adjustVideo5Layout(UserMixerLayout[] regionList, String maxResolutionUid) {
        boolean flag = false;
        int number = 0;
        int i = 0;

        for (String userId : userIds) {
            if (maxResolutionUid.equals(userId)) {
                flag = true;
                setMaxResolutionUid(number, maxResolutionUid, regionList, 0.8);
                number++;
                continue;
            }
            regionList[number].setUserId(userId);
            float yIndex = flag ? (number - 1) % 4 : number % 4;
            MixerLayoutConfig layoutConfig = new MixerLayoutConfig();
            layoutConfig.setX((int) (0.8f * recorderConfig.getVideo().getWidth()));
            layoutConfig.setY((int) (0.25f * yIndex * recorderConfig.getVideo().getHeight()));
            layoutConfig.setWidth((int) (0.2f * recorderConfig.getVideo().getWidth()));
            layoutConfig.setHeight((int) (0.25f * recorderConfig.getVideo().getHeight()));
            layoutConfig.setAlpha(1.0f);
            regionList[number].setConfig(layoutConfig);
            number++;
            i++;
            if (i == 4 && !flag) {
                adjustVideo7Layout(regionList, maxResolutionUid);
            }
        }
    }

    private void adjustVideo7Layout(UserMixerLayout[] regionList, String maxResolutionUid) {
        boolean flag = false;
        int number = 0;
        int i = 0;

        for (String userId : userIds) {
            if (maxResolutionUid.equals(userId)) {
                flag = true;
                setMaxResolutionUid(number, maxResolutionUid, regionList, 6.f / 7);
                number++;
                continue;
            }
            regionList[number].setUserId(userId);
            float yIndex = flag ? (number - 1) % 6 : number % 6;
            MixerLayoutConfig layoutConfig = new MixerLayoutConfig();
            layoutConfig.setX((int) (6.f / 7 * recorderConfig.getVideo().getWidth()));
            layoutConfig.setY((int) (1.f / 6 * yIndex * recorderConfig.getVideo().getHeight()));
            layoutConfig.setWidth((int) (1.f / 7 * recorderConfig.getVideo().getWidth()));
            layoutConfig.setHeight((int) (1.f / 6 * recorderConfig.getVideo().getHeight()));
            layoutConfig.setAlpha(1.0f);
            regionList[number].setConfig(layoutConfig);
            number++;
            i++;
            if (i == 6 && !flag) {
                adjustVideo9Layout(regionList, maxResolutionUid);
            }
        }
    }

    private void adjustVideo9Layout(UserMixerLayout[] regionList, String maxResolutionUid) {
        boolean flag = false;
        int number = 0;
        int i = 0;

        for (String userId : userIds) {
            if (maxResolutionUid.equals(userId)) {
                flag = true;
                setMaxResolutionUid(number, maxResolutionUid, regionList, 9.f / 5);
                number++;
                continue;
            }

            regionList[number].setUserId(userId);
            float yIndex = flag ? (number - 1) % 8 : number % 8;
            MixerLayoutConfig layoutConfig = new MixerLayoutConfig();
            layoutConfig.setX((int) (8.f / 9 * recorderConfig.getVideo().getWidth()));
            layoutConfig.setY((int) (1.f / 8 * yIndex * recorderConfig.getVideo().getHeight()));
            layoutConfig.setWidth((int) (1.f / 9 * recorderConfig.getVideo().getWidth()));
            layoutConfig.setHeight((int) (1.f / 8 * recorderConfig.getVideo().getHeight()));
            layoutConfig.setAlpha(1.0f);
            regionList[number].setConfig(layoutConfig);
            number++;
            i++;
            if (i == 8 && !flag) {
                adjustVideo17Layout(regionList, maxResolutionUid);
            }
        }
    }

    private void adjustVideo17Layout(UserMixerLayout[] regionList, String maxResolutionUid) {
        boolean flag = false;
        int number = 0;
        int i = 0;

        for (String userId : userIds) {
            if (maxResolutionUid.equals(userId)) {
                flag = true;
                setMaxResolutionUid(number, maxResolutionUid, regionList, 0.8);
                number++;
                continue;
            }
            if (!flag && i == 16) {
                break;
            }
            regionList[number].setUserId(userId);
            float yIndex = flag ? (number - 1) % 8 : number % 8;
            MixerLayoutConfig layoutConfig = new MixerLayoutConfig();
            layoutConfig.setX((int) (((flag && i > 8) || (!flag && i >= 8) ? (9.f / 10) : (8.f / 10))
                    * recorderConfig.getVideo().getWidth()));
            layoutConfig.setY((int) ((1.f / 8 * yIndex) * recorderConfig.getVideo().getHeight()));
            layoutConfig.setWidth((int) (1.f / 10 * recorderConfig.getVideo().getWidth()));
            layoutConfig.setHeight((int) (1.f / 8 * recorderConfig.getVideo().getHeight()));
            layoutConfig.setAlpha(1.0f);
            regionList[number].setConfig(layoutConfig);

            number++;
            i++;
        }
    }

}
