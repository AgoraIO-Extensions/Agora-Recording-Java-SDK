# Agora Recording Java SDK

## 目录

1. [开发环境要求](#开发环境要求)
   - [硬件环境](#硬件环境)
   - [网络要求](#网络要求)
   - [带宽需求](#带宽需求)
   - [软件环境](#软件环境)
2. [SDK下载](#SDK下载)
3. [快速开始](#快速开始)
   - [开通服务](#开通服务)
   - [集成录制SDK](#集成录制SDK)
   - [使用命令行录制](#使用命令行录制)
     - [前提条件](#前提条件)
     - [集成 SDK](#集成-sdk)
     - [编译](#编译)
     - [设置录制选项](#设置录制选项)
     - [开始录制](#开始录制)
     - [结束录制](#结束录制)
   - [调用 API 录制](#调用-api-录制)
     - [前提条件](#前提条件-1)
     - [调用 API 实现录制](#调用-api-实现录制)
       - [初始化服务](#初始化服务)
       - [加入频道](#加入频道)
       - [开始录制](#开始录制-1)
       - [结束录制](#结束录制)
4. [API 参考](#api-参考)
    - [AgoraService 类](#agoraservice-类)
    - [AgoraServiceConfiguration 类](#agoraserviceconfiguration-类)
    - [AgoraMediaComponentFactory 类](#agoramediacomponentfactory-类)
    - [AgoraMediaRtcRecorder 类](#agoramediartcrecorder-类)
    - [IAgoraMediaRtcRecorderEventHandler 类](#iagoramediartcrecordereventhandler-类)
    - [MediaRecorderConfiguration 类](#mediarecorderconfiguration-类)
    - [AgoraParameter 类](#agoraparameter-类)
5. [更新日志](#更新日志)
6. [其他参考](#其他参考)

## 开发环境要求

### 硬件环境

- **操作系统**：Ubuntu 14.04+ 或 CentOS 6.5+（推荐 7.0）
- **CPU 架构**：x86-64, arm64

### 网络要求

- **公网 IP**
- **域名访问**：允许访问 `.agora.io` 和 `.agoralab.co`

### 带宽需求

根据需要同时录制的频道数量和频道内情况确定所需带宽。以下数据可供参考：

- 录制一个分辨率为 640 × 480 的画面需要的带宽约为 500 Kbps
- 录制一个有两个人的频道则需 1 Mbps
- 同时录制 100 个这样的频道，需要带宽为 100 Mbps

### 软件环境

- **构建工具**：Apache Maven 或其他构建工具
- **JDK**：JDK 8+

## SDK下载

联系获取最新 SDK

## 快速开始

### 开通服务

参考 [官网开通服务](https://doc.shengwang.cn/doc/recording/java/get-started/enable-service)

### 集成录制SDK

下载的 SDK 为一个单独的 JAR 文件，需要手动解压出相应的 `so` 文件：

```sh
jar xvf agora-recording-sdk.jar
```

解压后的目录结构如下：

```
io          # Java 的 class 类所在，无需关注
META-INF    # JAR 文件和应用程序相关的元数据，无需关注
native      # 对应平台的 so 库文件，需要配置到运行的环境中
```

### 使用命令行录制

#### 前提条件

开始前请确保你已经完成录制 SDK 的环境准备和集成工作。

注：当录制 SDK 加入频道时，相当于一个哑客户端加入频道，因此需要跟声网 RTC SDK 加入相同的频道，并使用相同的 App ID 和频道场景。

#### 集成 SDK

1. 在 `Examples` 目录下创建 `libs` 文件夹（如果没有的话）。
2. 重命名下载的 JAR 为 `agora-recording-sdk.jar`，放入 `libs` 目录。
3. 将 JAR 解压出来的 `native` 文件放入 `libs` 目录。

确保目录结构如下：

```
libs/
├── agora-recording-sdk.jar
└── native/
```

#### 编译

进入 `Examples` 文件夹下，执行编译脚本：

```sh
cd Examples
./build.sh
```

#### 设置录制选项

参考 `Examples/config` 文件夹下的不同参数，注意参数为 JSON 格式，所有修改务必保证 JSON 格式正确。

各个参数含义参考 `Examples/config/recorder_json.example`：

以下是根据 JSON 文件详细介绍每个参数的含义：

| 参数名               | 类型            | 说明                                                                                     |
|--------------------|---------------|----------------------------------------------------------------------------------------|
| appId              | String        | 项目的 App ID，需要和 RTC SDK 中的 App ID 一致。                                           |
| token              | String        | 频道的 Token，如果频道设置了安全模式，需要传入 Token。                                         |
| channelName        | String        | 频道名称，需要和 RTC SDK 中的频道名称一致。                                                 |
| useStringUid       | Boolean       | 是否使用字符串类型的用户 ID。                                                              |
| useCloudProxy      | Boolean       | 是否使用云代理服务。                                                                       |
| userId             | String        | 用户 ID。                                                                                 |
| subAllAudio        | Boolean       | 是否订阅所有音频。如果为 false，需要在 subAudioUserList 中填入订阅的用户 ID。                        |
| subAudioUserList   | String []     | 订阅音频的用户 ID 列表，仅在 subAllAudio 为 false 时生效。                                      |
| subAllVideo        | Boolean       | 是否订阅所有视频。如果为 false，需要在 subVideoUserList 中填入订阅的用户 ID。                        |
| subVideoUserList   | String []     | 订阅视频的用户 ID 列表，仅在 subAllVideo 为 false 时生效。                                      |
| subStreamType      | String        | 订阅的流类型，支持 `high`（大流）和 `low`（小流）。                                             |
| isMix              | Boolean       | 是否合流录制。                                                                             |
| layoutMode         | String        | 合流录制布局模式，支持 `default`（默认布局），`bestfit`（自适应布局），`vertical`（垂直布局）。             |
| maxResolutionUid   | String        | 在 vertical 布局中，设定显示最大分辨率的用户 ID。                                              |
| recorderStreamType | String        | 录制类型，支持 `audio_only`（只录音频），`video_only`（只录视频），`both`（音视频都录）。              |
| recorderPath       | String        | 录制文件路径。合流录制时为录制的文件名;单流录制时为录制的目录，以每一个用户 ID 为名的 mp4 文件。           |
| audio              | Object        | 音频设置。                                                                                 |
| audio.sampleRate   | Integer       | 音频采样率。                                                                               |
| audio.numOfChannels| Integer       | 音频通道数量。                                                                             |
| video              | Object        | 视频设置。                                                                                 |
| video.width        | Integer       | 视频宽度。                                                                                 |
| video.height       | Integer       | 视频高度。                                                                                 |
| video.fps          | Integer       | 视频帧率。                                                                                 |
| waterMark          | Object[]      | 水印设置。                                                                                 |
| waterMark[].type   | String        | 水印类型，支持 `litera`（字幕水印），`time`（时间戳水印），`picture`（图片水印）。                      |
| waterMark[].litera | String        | 字幕内容，仅在 type 为 `litera` 时生效。                                                      |
| waterMark[].fontFilePath | String | 字体文件路径。                                                                              |
| waterMark[].fontSize | Integer     | 字体大小。                                                                                 |
| waterMark[].x      | Integer       | 水印的 X 坐标。                                                                             |
| waterMark[].y      | Integer       | 水印的 Y 坐标。                                                                             |
| waterMark[].width  | Integer       | 水印的宽度。                                                                                |
| waterMark[].height | Integer       | 水印的高度。                                                                                |
| waterMark[].zorder | Integer       | 水印的层级。                                                                                |
| waterMark[].imgUrl | String        | 图片水印的 URL，仅在 type 为 `picture` 时生效。                                               |
| encryption         | Object        | 媒体流加密设置。                                                                            |
| encryption.mode    | String        | 加密类型，支持 `AES_128_XTS`，`AES_128_ECB`，`AES_256_XTS`，`SM4_128_ECB`，`AES_128_GCM`，`AES_256_GCM`，`AES_128_GCM2`，`AES_256_GCM2`。 |
| encryption.key     | String        | 加密密钥。                                                                                 |
| encryption.salt    | String        | 加密盐值。                                                                                 |
| rotation           | Object[]      | 画面旋转设置。                                                                              |
| rotation[].uid     | String        | 需要旋转画面的用户 ID。                                                                       |
| rotation[].degree  | Integer       | 旋转的角度，支持 0，90，180，270。                                                           |

注：

- **执行录制前务必填写 JSON 中的 appId 和 token 参数。**
- **appId 和 channelName 的设置必须与声网 RTC SDK 中设置的一致。**
- **单流模式下，填入的 recorderPath 文件夹名，目前需要手动在 Examples 文件夹下创建，例如，"recorderPath": "recorder_result/"，需要确保 Examples/recorder_result/ 目录存在。**

#### 开始录制

进入示例目录并手动创建单流配置的文件夹：

```sh
cd Examples
mkdir recorder_result
```

根据测试场景，运行测试脚本：

```sh
./script/TestCaseName.sh
```

注：

- **预制的执行脚本只是几种简单场景，实际可以根据具体情况随便修改其中一个脚本对应的json config文件即可。**

#### 结束录制

终端控制台输入 `1` 即可实现结束录制。

#### 录制文件路径

单流在 `Examples` 目录下指定文件夹下生成单流录制的 mp4 文件，mp4 文件名是 UID 开头的。
合流在 `Examples` 目录下生成合流的录制 mp4 文件，文件名是 JSON 配置的。

### 调用 API 录制

#### 前提条件

开始前请确保你已经完成录制 SDK 的环境准备和集成工作，包括配置 jar 和对应平台的 so。

#### 调用 API 实现录制

##### 初始化服务

```java
AgoraServiceConfiguration config = new AgoraServiceConfiguration();
config.setEnableAudioDevice(false);
config.setEnableAudioProcessor(true);
config.setEnableVideo(true);
config.setAppId("APPID");
config.setUseStringUid(false);
agoraService.initialize(config);
```

注：

- **appId：项目的 App ID，需要和你传入 RTC SDK 中的 App ID 一致。**

##### 加入频道

```java
AgoraMediaComponentFactory factory = agoraService.createAgoraMediaComponentFactory();

AgoraMediaRtcRecorder agoraMediaRtcRecorder = factory.createMediaRtcRecorder();
agoraMediaRtcRecorder.initialize(agoraService, false);
AgoraMediaRtcRecorderEventHandler handler = new AgoraMediaRtcRecorderEventHandler();
agoraMediaRtcRecorder.registerRecorderEventHandler(handler);

agoraMediaRtcRecorder.joinChannel("token", "channelName", "0");
```

注：

- **channelName：和 RTC SDK 加入的频道名必须一致。**

##### 开始录制

```java
agoraMediaRtcRecorder.subscribeAllAudio();
VideoSubscriptionOptions options = new VideoSubscriptionOptions();
options.setEncodedFrameOnly(false);
options.setType(VideoStreamType.VIDEO_STREAM_HIGH);
agoraMediaRtcRecorder.subscribeAllVideo(options);

// set watermark
WatermarkConfig[] watermarks = new WatermarkConfig[1];
agoraMediaRtcRecorder.enableAndUpdateVideoWatermarks(watermarks);
```

- **设置水印位置不能超过视频的宽高。**

1. 合流录制：

```java
// set recorder config
MediaRecorderConfiguration mediaRecorderConfiguration = new MediaRecorderConfiguration();
agoraMediaRtcRecorder.setRecorderConfig(mediaRecorderConfiguration);

agoraMediaRtcRecorder.startRecording();
```

2. 单流录制

监听音视频回调，调用单流录制接口。

```java
    public static class AgoraMediaRtcRecorderEventHandler implements IAgoraMediaRtcRecorderEventHandler {
        @Override
        public void onFirstRemoteAudioDecoded(String channelId, String userId, int elapsed) {
            new Thread() {
                @Override
                public void run() {
                    MediaRecorderConfiguration mediaRecorderConfiguration = new MediaRecorderConfiguration();
                    agoraMediaRtcRecorder.setRecorderConfigByUid(mediaRecorderConfiguration, userId);

                    agoraMediaRtcRecorder.startSingleRecordingByUid(userId);
                }
            }.start();
        }

        @Override
        public void onFirstRemoteVideoDecoded(String channelId, String userId, int width, int height, int elapsed) {
            new Thread() {
                @Override
                public void run() {
                    if (isMix) {
                        VideoMixingLayout layout = new VideoMixingLayout();
                        agoraMediaRtcRecorder.setVideoMixingLayout(layout);
                    } else {
                        MediaRecorderConfiguration mediaRecorderConfiguration = new MediaRecorderConfiguration();
                        agoraMediaRtcRecorder.setRecorderConfigByUid(mediaRecorderConfiguration, userId);

                        agoraMediaRtcRecorder.startSingleRecordingByUid(userId);
                    }
                }
            }.start();
        }
    }
```

##### 结束录制

```java
agoraMediaRtcRecorder.unsubscribeAllAudio();
agoraMediaRtcRecorder.unsubscribeAllVideo();

if (isMix) {
    agoraMediaRtcRecorder.stopRecording();
} else {
    agoraMediaRtcRecorder.stopSingleRecordingByUid("userId");
}

agoraMediaRtcRecorder.unregisterRecorderEventHandle(handler);

agoraMediaRtcRecorder.leaveChannel();
agoraMediaRtcRecorder.release();

agoraService.release();
```

##### 获取录制文件路径

单流在 `Examples` 目录下指定文件夹下生成单流录制的 mp4 文件，mp4 文件名是 UID 开头的。
合流在 `Examples` 目录下生成合流的录制 mp4 文件，文件名是 MediaRecorderConfiguration 对象配置的。

## API 参考

### `AgoraService` 类

#### 简介

`AgoraService` 类提供了初始化和管理 Agora 服务的核心功能，是使用 Agora 录制功能的主要入口。

#### 方法

##### `AgoraService()`

构造一个 `AgoraService` 实例并初始化本地组件。一次只能初始化一个 `AgoraService` 实例。

##### `long getNativeHandle()`

获取与此 `AgoraService` 实例关联的本地句柄。

**返回值**：

- 返回用于本地方法调用的本地句柄值。

##### `int release()`

释放 `AgoraService` 对象及其关联的资源。调用此方法后，实例将失效。

**返回值**：

- `0`: 成功
- `< 0`: 失败

##### `int initialize(AgoraServiceConfiguration config)`

使用指定的配置初始化 `AgoraService` 对象。

**参数**：

- `config`：包含初始化参数的配置对象。

**返回值**：

- `0`: 成功
- `< 0`: 失败

##### `AgoraMediaComponentFactory createAgoraMediaComponentFactory()`

创建并返回一个 `AgoraMediaComponentFactory` 对象，用于创建媒体组件。

**返回值**：

- 返回一个 `AgoraMediaComponentFactory` 实例。

##### `AgoraParameter getAgoraParameter()`

创建并返回一个 `AgoraParameter` 对象，用于参数管理。

**返回值**：

- 成功时返回一个 `AgoraParameter` 实例。
- 失败时返回 `null`。

##### `static String getSdkVersion()`

获取 SDK 版本。

**返回值**：

- 返回 SDK 版本。

##### `int setLogFile(String filePath, int fileSize)`

设置 SDK 日志文件的路径和大小。

**参数**：

- `filePath`：日志文件的路径。确保日志文件的目录存在且可写。
- `fileSize`：SDK 日志文件的大小（字节），即每个日志文件的大小。

**返回值**：

- `0`: 成功
- `< 0`: 失败

##### `int setLogLevel(Constants.LogLevel level)`

设置 SDK 日志文件的等级。

**参数**：

- `level`：日志文件的等级。

**返回值**：

- `0`: 成功
- `< 0`: 失败

### `AgoraServiceConfiguration` 类

#### 简介

`AgoraServiceConfiguration` 类用于配置和初始化 Agora 服务实例。此类包含所有初始化和配置 Agora 服务实例所需的设置。

#### 属性

- **enableAudioProcessor**

  是否启用音频处理模块。
  - `true`：启用音频处理模块（默认）。
  - `false`：禁用音频处理模块。如果禁用音频处理模块，则无法创建音频轨道。

- **enableAudioDevice**

  是否启用音频设备模块。音频设备模块用于管理音频设备，例如录音和播放音频。
  - `true`：启用音频设备模块（默认）。音频录制和播放可用。
  - `false`：禁用音频设备模块。音频录制和播放不可用。
  
  注意：如果将 `enableAudioDevice` 设置为 `false`，并且将 `enableAudioProcessor` 设置为 `true`，则无法使用音频设备，但可以推送 PCM 音频数据。

- **enableVideo**

  是否启用视频。
  - `true`：启用视频。
  - `false`：禁用视频（默认）。

- **context**

  用户上下文对象。对于 Android，它是活动的上下文。

- **appId**

  项目的 App ID。

- **areaCode**

  支持的区域代码，默认值为 `AREA_CODE_GLOB`。

- **channelProfile**

  频道配置文件。默认频道配置文件为 `CHANNEL_PROFILE_LIVE_BROADCASTING`。

- **license**

  用于连接频道时的验证许可证。根据许可证收费。

- **audioScenario**

  音频场景。默认值为 `AUDIO_SCENARIO_DEFAULT`。

- **logConfig**

  用户自定义日志路径、日志大小和日志级别的配置。

- **useStringUid**

  是否启用字符串用户 ID。

- **useExternalEglContext**

  是否在当前线程中使用 EGL 上下文作为 SDK 的根 EGL 上下文，该上下文由所有与 EGL 相关的模块共享，例如摄像头捕获和视频渲染。
  注意：此属性仅适用于 Android。

- **domainLimit**

  是否启用域限制。
  - `true`：仅连接到已通过 DNS 解析的服务器。
  - `false`：连接到没有限制的服务器（默认）。

### `AgoraMediaComponentFactory` 类

#### 简介

`AgoraMediaComponentFactory` 类用于创建 Agora 媒体组件的工厂类。此类提供创建媒体录制组件实例的功能。

#### 方法

- **AgoraMediaComponentFactory(long handle)**

  构造一个 `AgoraMediaComponentFactory` 实例。
  - 参数:
    - `handle`: 工厂实例的本地句柄。

- **AgoraMediaRtcRecorder createMediaRtcRecorder()**

  创建一个新的 `AgoraMediaRtcRecorder` 实例。
  - 返回值:
    - 一个新的 `AgoraMediaRtcRecorder` 实例。
  - 异常:
    - `RuntimeException`: 如果本地录制器创建失败。

- **int release()**

  释放与工厂关联的本地资源。
  - 返回值:
    - 释放操作的结果。
      - `0`: 成功。
      - `< 0`: 失败。

### `AgoraMediaRtcRecorder` 类

#### 简介

`AgoraMediaRtcRecorder` 类提供了录制 Agora RTC 媒体流的功能。此类允许录制来自 Agora RTC 频道的音频和视频流，并提供流混合、加密和选择性订阅的选项。

#### 方法

- **AgoraMediaRtcRecorder(long handle)**

  构造一个 `AgoraMediaRtcRecorder` 实例。
  - 参数:
    - `handle`: 录制器实例的本地句柄。

- **int initialize(AgoraService service, boolean enableMix)**

  使用指定的服务和混合设置初始化录制器。
  - 参数:
    - `service`: 必须在调用此方法之前初始化的 Agora 服务实例。
    - `enableMix`: 是否启用流混合。
  - 返回值:
    - `0`: 初始化成功。
    - 负值: 初始化失败。

- **int joinChannel(String token, String channelName, String userId)**

  加入一个 Agora RTC 频道。
  - 参数:
    - `token`: 用于身份验证的令牌。
    - `channelName`: 要加入的频道名称。名称不能超过 64 字节，可以包含小写字母、大写字母、数字、空格和特殊字符。
    - `userId`: 本地用户的用户 ID。如果为 `null`，系统会自动分配一个。
  - 返回值:
    - `0`: 方法调用成功。
    - 负值: 方法调用失败。

- **int leaveChannel()**

  离开当前频道。
  - 返回值:
    - `0`: 方法调用成功。
    - 负值: 方法调用失败。

- **int enableEncryption(boolean enabled, EncryptionConfig config)**

  启用或禁用内置加密。
  - 参数:
    - `enabled`: 是否启用内置加密。
    - `config`: 加密配置参数。
  - 返回值:
    - `0`: 方法调用成功。
    - 负值: 方法调用失败。

  注意：如果启用加密，RTMP 流功能将被禁用。

- **int subscribeAllAudio()**

  订阅频道中所有远程用户的音频流。
  - 返回值:
    - `0`: 方法调用成功。
    - 负值: 方法调用失败。

- **int subscribeAllVideo(VideoSubscriptionOptions options)**

  订阅频道中所有远程用户的视频流。
  - 参数:
    - `options`: 视频订阅选项，包括流类型和其他参数。
  - 返回值:
    - `0`: 方法调用成功。
    - 负值: 方法调用失败。

- **int unsubscribeAllAudio()**

  停止订阅频道中所有远程用户的音频流。
  - 返回值:
    - `0`: 方法调用成功。
    - 负值: 方法调用失败。

- **int unsubscribeAllVideo()**

  停止订阅频道中所有远程用户的视频流。
  - 返回值:
    - `0`: 方法调用成功。
    - 负值: 方法调用失败。

- **int subscribeAudio(String userId)**

  订阅指定远程用户的音频流。
  - 参数:
    - `userId`: 要订阅其音频的远程用户的 ID。
  - 返回值:
    - `0`: 方法调用成功。
    - `-2`: userId 无效。
    - 其他负值: 方法调用失败。

- **int unsubscribeAudio(String userId)**

  停止订阅指定远程用户的音频流。
  - 参数:
    - `userId`: 要停止订阅其音频的远程用户的 ID。
  - 返回值:
    - `0`: 方法调用成功。
    - `-2`: userId 无效。
    - 其他负值: 方法调用失败。

- **int subscribeVideo(String userId, VideoSubscriptionOptions options)**

  订阅指定远程用户的视频流。
  - 参数:
    - `userId`: 要订阅其视频的远程用户的 ID。
    - `options`: 视频订阅选项，包括流类型和其他参数。
  - 返回值:
    - `0`: 方法调用成功。
    - `-2`: userId 无效。
    - 其他负值: 方法调用失败。

- **int unsubscribeVideo(String userId)**

  停止订阅指定远程用户的视频流。
  - 参数:
    - `userId`: 要停止订阅其视频的远程用户的 ID。
  - 返回值:
    - `0`: 方法调用成功。
    - `-2`: userId 无效。
    - 其他负值: 方法调用失败。

- **int setVideoMixingLayout(VideoMixingLayout layout)**

  设置混合视频流的布局。
  - 参数:
    - `layout`: 混合视频流的布局配置。
  - 返回值:
    - `0`: 方法调用成功。
    - 负值: 方法调用失败。

- **int setRecorderConfig(MediaRecorderConfiguration config)**

  配置录制器设置。此方法必须在开始录制之前调用。
  - 参数:
    - `config`: 录制器配置参数。
  - 返回值:
    - `0`: 方法调用成功。
    - 负值: 方法调用失败。

- **int setRecorderConfigByUid(MediaRecorderConfiguration config, String userId)**

  配置指定用户的录制器设置。
  - 参数:
    - `config`: 录制器配置参数。
    - `userId`: 要配置其录制设置的用户 ID。
  - 返回值:
    - `0`: 方法调用成功。
    - 负值: 方法调用失败。

- **int startRecording()**

  开始录制过程。请确保在调用此方法之前使用 `setRecorderConfig` 配置录制器。
  - 返回值:
    - `0`: 方法调用成功。
    - 负值: 方法调用失败。

- **int stopRecording()**

  停止录制过程。此方法停止所有正在进行的录制并保存录制的文件。
  - 返回值:
    - `0`: 方法调用成功。
    - 负值: 方法调用失败。

- **int startSingleRecordingByUid(String userId)**

  开始指定用户的录制过程。
  - 参数:
    - `userId`: 要开始录制的用户 ID。
  - 返回值:
    - `0`: 方法调用成功。
    - 负值: 方法调用失败。

- **int stopSingleRecordingByUid(String userId)**

  停止指定用户的录制过程。
  - 参数:
    - `userId`: 要停止录制的用户 ID。
  - 返回值:
    - `0`: 方法调用成功。
    - 负值: 方法调用失败。

- **int registerRecorderEventHandler(IAgoraMediaRtcRecorderEventHandler handler)**

  注册录制事件的事件处理程序。处理程序接收各种录制事件的回调，例如状态变化、错误和录制进度更新。
  - 参数:
    - `handler`: 实现 `IAgoraMediaRtcRecorderEventHandler` 接口的事件处理程序。
  - 返回值:
    - `0`: 注册成功。
    - 负值: 注册失败。

- **int unregisterRecorderEventHandle(IAgoraMediaRtcRecorderEventHandler handle)**

  注销先前注册的事件处理程序。
  - 参数:
    - `handle`: 要注销的事件处理程序。
  - 返回值:
    - `0`: 注销成功。
    - 负值: 注销失败。

- **int enableAndUpdateVideoWatermarks(WatermarkConfig[] watermarkConfigs)**

  为流添加水印。
  - 参数:
    - `watermarkConfigs`: 水印配置。
  - 返回值:
    - `0`: 方法调用成功。
    - 负值: 方法调用失败。

- **int disableVideoWatermarks()**

  禁用流的水印。
  - 返回值:
    - `0`: 方法调用成功。
    - 负值: 方法调用失败。

- **int enableAndUpdateVideoWatermarksByUid(WatermarkConfig[] watermarkConfigs, String userId)**

  为指定用户的流添加水印。
  - 参数:
    - `watermarkConfigs`: 水印配置。
    - `userId`: 要添加水印的用户 ID。
  - 返回值:
    - `0`: 方法调用成功。
    - 负值: 方法调用失败。

- **int disableVideoWatermarksByUid(String userId)**

  禁用指定用户流的水印。
  - 参数:
    - `userId`: 要禁用水印的用户 ID。
  - 返回值:
    - `0`: 方法调用成功。
    - 负值: 方法调用失败。

- **int release()**

  释放与录制器关联的本地资源。
  - 返回值:
    - 释放操作的结果。
      - `0`: 成功。
      - `< 0`: 失败。

### `IAgoraMediaRtcRecorderEventHandler` 类

#### 简介

`IAgoraMediaRtcRecorderEventHandler` 接口定义了 SDK 与 Agora 频道之间连接状态变化时的回调方法，以及其他与录制相关的事件回调。

#### 方法

- `onConnected(String channelId, String userId)`

  当 SDK 与 Agora 频道的连接状态变为 `CONNECTION_STATE_CONNECTED(3)` 时触发。

  - 参数：
    - `channelId`：频道 ID。
    - `userId`：用户 ID。

- `onDisconnected(String channelId, String userId, Constants.ConnectionChangedReasonType reason)`

  当 SDK 与 Agora 频道的连接状态变为 `CONNECTION_STATE_DISCONNECTED(1)` 时触发。

  - 参数：
    - `channelId`：频道 ID。
    - `userId`：用户 ID。
    - `reason`：连接状态变化的原因。参见 `Constants.ConnectionChangedReasonType`。

- `onReconnected(String channelId, String userId, Constants.ConnectionChangedReasonType reason)`

  当 SDK 与 Agora 频道的连接状态再次变为 `CONNECTION_STATE_CONNECTED(3)` 时触发。

  - 参数：
    - `channelId`：频道 ID。
    - `userId`：用户 ID。
    - `reason`：连接状态变化的原因。参见 `Constants.ConnectionChangedReasonType`。

- `onConnectionLost(String channelId, String userId)`

  当 SDK 与 Agora 频道失去连接时触发。

  - 参数：
    - `channelId`：频道 ID。
    - `userId`：用户 ID。

- `onUserJoined(String channelId, String userId)`

  当远端用户加入频道时触发。

  - 参数：
    - `channelId`：频道 ID。
    - `userId`：用户 ID。

- `onUserLeft(String channelId, String userId, Constants.UserOfflineReasonType reason)`

  当远端用户离开频道时触发。

  - 参数：
    - `channelId`：频道 ID。
    - `userId`：用户 ID。
    - `reason`：远端用户离开频道的原因。参见 `Constants.UserOfflineReasonType`。

- `onFirstRemoteVideoDecoded(String channelId, String userId, int width, int height, int elapsed)`

  当 SDK 解码出第一帧远端视频时触发。

  - 参数：
    - `channelId`：频道 ID。
    - `userId`：远端用户 ID。
    - `width`：视频流的宽度（像素）。
    - `height`：视频流的高度（像素）。
    - `elapsed`：从用户连接到 Agora 频道到解码出第一帧视频的时间（毫秒）。

- `onFirstRemoteAudioDecoded(String channelId, String userId, int elapsed)`

  当 SDK 解码出第一帧远端音频时触发。

  - 参数：
    - `channelId`：频道 ID。
    - `userId`：远端用户 ID。
    - `elapsed`：从用户连接到 Agora 频道到解码出第一帧音频的时间（毫秒）。

- `onAudioVolumeIndication(String channelId, String userId, int speakerNumber, int totalVolume)`

  报告正在说话的用户、说话者的音量以及本地用户是否在说话。

  - 参数：
    - `channelId`：频道 ID。
    - `userId`：远端用户 ID。
    - `speakerNumber`：说话者的总数。
    - `totalVolume`：音频混音后的总音量，范围为 0（最低音量）到 255（最高音量）。

- `onActiveSpeaker(String channelId, String userId)`

  当检测到活跃说话者时触发。

  - 参数：
    - `channelId`：频道 ID。
    - `userId`：活跃说话者的用户 ID。`userId` 为 `0` 表示本地用户。

- `onUserVideoStateChanged(String channelId, String userId, Constants.RemoteVideoState state, Constants.RemoteVideoStateReason reason, int elapsed)`

  当远端用户的视频状态变化时触发。

  - 参数：
    - `channelId`：频道 ID。
    - `userId`：远端用户 ID。
    - `state`：当前视频状态。参见 `Constants.RemoteVideoState`。
    - `reason`：状态变化的原因。参见 `Constants.RemoteVideoStateReason`。
    - `elapsed`：从用户连接到 Agora 频道到状态变化的时间（毫秒）。

- `onUserAudioStateChanged(String channelId, String userId, Constants.RemoteAudioState state, Constants.RemoteAudioStateReason reason, int elapsed)`

  当远端用户的音频状态变化时触发。

  - 参数：
    - `channelId`：频道 ID。
    - `userId`：远端用户 ID。
    - `state`：当前音频状态。参见 `Constants.RemoteAudioState`。
    - `reason`：状态变化的原因。参见 `Constants.RemoteAudioStateReason`。
    - `elapsed`：从用户连接到 Agora 频道到状态变化的时间（毫秒）。

- `onRemoteVideoStats(String channelId, String userId, RemoteVideoStatistics stats)`

  报告远端视频的统计信息。

  - 参数：
    - `channelId`：频道 ID。
    - `userId`：远端用户 ID。
    - `stats`：当前视频的统计信息。

- `onRemoteAudioStats(String channelId, String userId, RemoteAudioStatistics stats)`

  报告远端音频的统计信息。

  - 参数：
    - `channelId`：频道 ID。
    - `userId`：远端用户 ID。
    - `stats`：当前音频的统计信息。

- `onRecorderStateChanged(String channelId, String userId, Constants.RecorderState state, Constants.RecorderReasonCode reason, String fileName)`

  当录制状态变化时触发。

  - 参数：
    - `channelId`：频道名称。
    - `userId`：用户 ID。
    - `state`：当前录制状态。参见 `Constants.RecorderState`。
    - `reason`：状态变化的原因。参见 `Constants.RecorderReasonCode`。
    - `fileName`：录制的文件名。

- `onRecorderInfoUpdated(String channelId, String userId, RecorderInfo info)`

  当录制信息更新时触发。

  - 参数：
    - `channelId`：频道名称。
    - `userId`：用户 ID。
    - `info`：录制文件的信息。参见 `RecorderInfo`。

### `MediaRecorderConfiguration` 类

#### 简介

`MediaRecorderConfiguration` 类用于配置录制文件的相关参数。

#### 属性

- `storagePath`

  录制文件的绝对路径（包括文件名扩展名）。例如：
  - Windows: `C:\Users\<user_name>\AppData\Local\Agora\<process_name>\example.mp4`
  - iOS: `/App Sandbox/Library/Caches/example.mp4`
  - macOS: `/Library/Logs/example.mp4`
  - Android: `/storage/emulated/0/Android/data/<package name>/files/example.mp4`
  - Linux: `result/example.mp4`

  **注意**：确保指定的路径存在且可写。

- `containerFormat`

  录制文件的格式。参见 `Constants.MediaRecorderContainerFormat`。

- `streamType`

  录制内容类型。参见 `Constants.MediaRecorderStreamType`。

- `maxDurationMs`

  最大录制时长，单位为毫秒。默认值为 120000 毫秒。

- `recorderInfoUpdateInterval`

  更新录制信息的时间间隔，单位为毫秒。取值范围为 [1000, 10000]。根据设置的值，SDK 会触发 `IMediaRecorderObserver#onRecorderInfoUpdated` 回调来报告更新的录制信息。

- `width`

  录制视频的宽度，单位为像素。默认值为 1280。

- `height`

  录制视频的高度，单位为像素。默认值为 720。

- `fps`

  录制视频的帧率，单位为帧每秒。默认值为 30。

- `sampleRate`

  录制音频的采样率，单位为 Hz。默认值为 48000。

- `channelNum`

  录制音频的声道数。默认值为 1（单声道）。

- `videoSourceType`

  外部视频源的类型。参见 `Constants.VideoSourceType`。

### `AgoraParameter` 类

#### 简介

`AgoraParameter` 类提供了获取和设置 Agora SDK 配置参数的功能，支持多种数据类型，包括布尔值、整数、无符号整数、浮点数、字符串、对象和数组。

#### 方法

- `void release()`

  释放与此参数实例关联的所有资源。调用此方法后，实例将变为无效。

- `int setBool(String key, boolean value)`

  设置布尔类型的参数值。

  - 参数：
    - `key`：参数键。
    - `value`：要设置的布尔值。
  - 返回值：成功时返回 0，失败时返回负值。

- `int setInt(String key, int value)`

  设置整数类型的参数值。

  - 参数：
    - `key`：参数键。
    - `value`：要设置的整数值。
  - 返回值：成功时返回 0，失败时返回负值。

- `int setUInt(String key, int value)`

  设置无符号整数类型的参数值。

  - 参数：
    - `key`：参数键。
    - `value`：要设置的无符号整数值。
  - 返回值：成功时返回 0，失败时返回负值。

- `int setNumber(String key, double value)`

  设置浮点数类型的参数值。

  - 参数：
    - `key`：参数键。
    - `value`：要设置的浮点数值。
  - 返回值：成功时返回 0，失败时返回负值。

- `int setString(String key, String value)`

  设置字符串类型的参数值。

  - 参数：
    - `key`：参数键。
    - `value`：要设置的字符串值。
  - 返回值：成功时返回 0，失败时返回负值。

- `int setObject(String key, String value)`

  设置 JSON 格式的对象类型参数值。

  - 参数：
    - `key`：参数键。
    - `value`：表示对象的 JSON 字符串。
  - 返回值：成功时返回 0，失败时返回负值。

- `int setArray(String key, String value)`

  设置 JSON 格式的数组类型参数值。

  - 参数：
    - `key`：参数键。
    - `value`：表示数组的 JSON 字符串。
  - 返回值：成功时返回 0，失败时返回负值。

- `boolean getBool(String key)`

  获取布尔类型的参数值。

  - 参数：
    - `key`：参数键。
  - 返回值：与键关联的布尔值。
  - 异常：如果无法获取参数，将抛出 `IllegalStateException`。

- `int getInt(String key)`

  获取整数类型的参数值。

  - 参数：
    - `key`：参数键。
  - 返回值：与键关联的整数值。
  - 异常：如果无法获取参数，将抛出 `IllegalStateException`。

- `int getUInt(String key)`

  获取无符号整数类型的参数值。

  - 参数：
    - `key`：参数键。
  - 返回值：与键关联的无符号整数值。
  - 异常：如果无法获取参数，将抛出 `IllegalStateException`。

- `double getNumber(String key)`

  获取浮点数类型的参数值。

  - 参数：
    - `key`：参数键。
  - 返回值：与键关联的浮点数值。
  - 异常：如果无法获取参数，将抛出 `IllegalStateException`。

- `String getString(String key)`

  获取字符串类型的参数值。

  - 参数：
    - `key`：参数键。
  - 返回值：与键关联的字符串值，未找到时返回 null。

- `String getObject(String key)`

  获取 JSON 格式的对象类型参数值。

  - 参数：
    - `key`：参数键。
  - 返回值：表示对象的 JSON 字符串，未找到时返回 null。

- `String getArray(String key, String args)`

  获取 JSON 格式的数组类型参数值。

  - 参数：
    - `key`：参数键。
    - `args`：数组检索的附加参数。
  - 返回值：表示数组的 JSON 字符串，未找到时返回 null。

- `int setParameters(String parameters)`

  使用 JSON 字符串一次性设置多个参数。

  - 参数：
    - `parameters`：包含多个参数键值对的 JSON 字符串。
  - 返回值：成功时返回 0，失败时返回负值。

- `String convertPath(String filePath)`

  将文件路径转换为特定平台的格式。

  - 参数：
    - `filePath`：要转换的原始文件路径。
  - 返回值：转换后的特定平台文件路径，转换失败时返回 null。

## 更新日志

## 其他参考

详细参考官网（<https://doc.shengwang.cn/doc/recording/java/landing-page>）
