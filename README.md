# Agora Recording Java SDK

[中文](README.zh.md) | English

## Table of Contents

1. [Development Environment Requirements](#development-environment-requirements)
   - [Hardware Environment](#hardware-environment)
   - [Network Requirements](#network-requirements)
   - [Bandwidth Requirements](#bandwidth-requirements)
   - [Software Environment](#software-environment)
2. [SDK Download](#sdk-download)
3. [Quick Start](#quick-start)
   - [Enable Service](#enable-service)
   - [Integrate Recording SDK](#integrate-recording-sdk)
   - [Recording via Command Line](#recording-via-command-line)
     - [Prerequisites](#prerequisites)
     - [Integrate SDK](#integrate-sdk)
     - [Compile](#compile)
     - [Set Recording Options](#set-recording-options)
     - [Start Recording](#start-recording)
     - [Stop Recording](#stop-recording)
   - [Recording via API](#recording-via-api)
     - [Prerequisites](#prerequisites-1)
     - [Implement Recording via API](#implement-recording-via-api)
       - [Initialize Service](#initialize-service)
       - [Join Channel](#join-channel)
       - [Start Recording](#start-recording-1)
       - [Stop Recording](#stop-recording)
4. [API Reference](#api-reference)
   - [AgoraService Class](#agoraservice-class)
   - [AgoraServiceConfiguration Class](#agoraserviceconfiguration-class)
   - [AgoraMediaComponentFactory Class](#agoramediacomponentfactory-class)
   - [AgoraMediaRtcRecorder Class](#agoramediartcrecorder-class)
   - [IAgoraMediaRtcRecorderEventHandler Class](#iagoramediartcrecordereventhandler-class)
   - [MediaRecorderConfiguration Class](#mediarecorderconfiguration-class)
   - [AgoraParameter Class](#agoraparameter-class)
5. [Changelog](#changelog)
6. [Other References](#other-references)

## Development Environment Requirements

### Hardware Environment

- **Operating System**: Ubuntu 14.04+ or CentOS 6.5+ (recommended 7.0)
- **CPU Architecture**: x86-64

### Network Requirements

- **Public IP**
- **Domain Access**: Allow access to `.agora.io` and `.agoralab.co`

### Bandwidth Requirements

The required bandwidth depends on the number of channels being recorded simultaneously and the situation within the channel. The following data can be used as a reference:

- Recording a 640 × 480 resolution video requires approximately 500 Kbps of bandwidth.
- Recording a channel with two people requires 1 Mbps.
- Simultaneously recording 100 such channels requires 100 Mbps of bandwidth.

### Software Environment

- **Build Tools**: Apache Maven or other build tools
- **JDK**: JDK 8+

## SDK Download

### Maven Download

#### x86_64 Platform

```xml
<dependency>
    <groupId>io.agora.rtc</groupId>
    <artifactId>linux-recording-java-sdk</artifactId>
    <version>4.4.150</version>
</dependency>
```

[linux-recording-java-sdk-4.4.150](https://repo1.maven.org/maven2/io/agora/rtc/linux-recording-java-sdk/4.4.150/linux-recording-java-sdk-4.4.150.jar)

#### arm64 Platform

```xml
<dependency>
    <groupId>io.agora.rtc</groupId>
    <artifactId>linux-recording-java-sdk</artifactId>
    <version>4.4.150-aarch64</version>
</dependency>
```

[linux-recording-java-sdk-4.4.150-aarch64](https://repo1.maven.org/maven2/io/agora/rtc/linux-recording-java-sdk/4.4.150-aarch64/linux-recording-java-sdk-4.4.150-aarch64.jar)

### CDN Download

#### x86_64 Platform

[Agora-Linux-Recording-Java-SDK-v4.4.150-x86_64-534965-4423b3dcaf-20250121_110348](https://download.agora.io/sdk/release/Agora-Linux-Recording-Java-SDK-v4.4.150-x86_64-534965-4423b3dcaf-20250121_110348.jar)

#### arm64 Platform

[Agora-Linux-Recording-Java-SDK-v4.4.150-aarch64-565361-c502888569-20250213_112934](https://download.agora.io/sdk/release/Agora-Linux-Recording-Java-SDK-v4.4.150-aarch64-565361-c502888569-20250213_112934.jar)

## Quick Start

### Enable Service

Refer to [Enable Service on Official Website](https://doc.shengwang.cn/doc/recording/java/get-started/enable-service)

### Integrate Recording SDK

The downloaded SDK is a standalone JAR file, which needs to be manually extracted to obtain the `so` files:

```sh
jar xvf agora-recording-sdk.jar
```

The extracted directory structure is as follows:

```
io          # Java class files, no need to pay attention
META-INF    # Metadata related to JAR files and applications, no need to pay attention
native      # Corresponding platform's so library files, need to configure into the running environment
```

### Recording via Command Line

#### Prerequisites

Before starting, ensure that you have completed the environment preparation and integration of the recording SDK.

Note: When the recording SDK joins a channel, it is equivalent to a mute client joining the channel, so it needs to join the same channel as the Agora RTC SDK and use the same App ID and channel scenario.

#### Integrate SDK

1. Create a `libs` folder in the `Examples` directory (if not already present).
2. Rename the downloaded JAR to `agora-recording-sdk.jar` and place it in the `libs` directory.
3. Place the `native` files extracted from the JAR into the `libs` directory.

Ensure the directory structure is as follows:

```
libs/
├── agora-recording-sdk.jar
└── native/
```

#### Compile

Navigate to the `Examples` folder and run the build script:

```sh
cd Examples
./build.sh
```

#### Set Recording Options

Refer to the different parameters in the `Examples/config` folder. Note that the parameters are in JSON format, so ensure that any modifications are valid JSON.

Refer to `Examples/config/recorder_json.example` for the meaning of each parameter:

The following is a detailed explanation of each parameter based on the JSON file:

| Parameter Name           | Type      | Description                                                                                                                                                         |
| ------------------------ | --------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| appId                    | String    | The App ID of the project, which needs to be consistent with the App ID in the RTC SDK.                                                                             |
| token                    | String    | The token of the channel. If the channel is set to secure mode, a token is required.                                                                                |
| channelName              | String    | The channel name, which needs to be consistent with the channel name in the RTC SDK.                                                                                |
| useStringUid             | Boolean   | Whether to use string type user ID.                                                                                                                                 |
| useCloudProxy            | Boolean   | Whether to use cloud proxy service.                                                                                                                                 |
| userId                   | String    | User ID.                                                                                                                                                            |
| subAllAudio              | Boolean   | Whether to subscribe to all audio. If false, fill in the user IDs to subscribe to in subAudioUserList.                                                              |
| subAudioUserList         | String [] | List of user IDs to subscribe to audio, only effective when subAllAudio is false.                                                                                   |
| subAllVideo              | Boolean   | Whether to subscribe to all video. If false, fill in the user IDs to subscribe to in subVideoUserList.                                                              |
| subVideoUserList         | String [] | List of user IDs to subscribe to video, only effective when subAllVideo is false.                                                                                   |
| subStreamType            | String    | Stream type to subscribe to, supports `high` (high stream) and `low` (low stream).                                                                                  |
| isMix                    | Boolean   | Whether to record in mixed mode.                                                                                                                                    |
| layoutMode               | String    | Layout mode for mixed recording, supports `default` (default layout), `bestfit` (adaptive layout), `vertical` (vertical layout).                                    |
| maxResolutionUid         | String    | In vertical layout, set the user ID to display the maximum resolution.                                                                                              |
| recorderStreamType       | String    | Recording type, supports `audio_only` (audio only), `video_only` (video only), `both` (both audio and video).                                                       |
| recorderPath             | String    | Recording file path. For mixed recording, it is the recording file name; for single stream recording, it is the directory, with mp4 files named after each user ID. |
| maxDuration              | Integer   | Recording duration, in seconds.                                                                                                                                     |
| recoverFile              | Boolean   | Whether to write h264 and aac files simultaneously during recording, allowing recovery of mp4 files after program crash.                                            |
| audio                    | Object    | Audio settings.                                                                                                                                                     |
| audio.sampleRate         | Integer   | Audio sample rate.                                                                                                                                                  |
| audio.numOfChannels      | Integer   | Number of audio channels.                                                                                                                                           |
| video                    | Object    | Video settings.                                                                                                                                                     |
| video.width              | Integer   | Video width.                                                                                                                                                        |
| video.height             | Integer   | Video height.                                                                                                                                                       |
| video.fps                | Integer   | Video frame rate.                                                                                                                                                   |
| waterMark                | Object[]  | Watermark settings.                                                                                                                                                 |
| waterMark[].type         | String    | Watermark type, supports `litera` (subtitle watermark), `time` (timestamp watermark), `picture` (image watermark).                                                  |
| waterMark[].litera       | String    | Subtitle content, only effective when type is `litera`.                                                                                                             |
| waterMark[].fontFilePath | String    | Font file path.                                                                                                                                                     |
| waterMark[].fontSize     | Integer   | Font size.                                                                                                                                                          |
| waterMark[].x            | Integer   | X coordinate of the watermark.                                                                                                                                      |
| waterMark[].y            | Integer   | Y coordinate of the watermark.                                                                                                                                      |
| waterMark[].width        | Integer   | Width of the watermark.                                                                                                                                             |
| waterMark[].height       | Integer   | Height of the watermark.                                                                                                                                            |
| waterMark[].zorder       | Integer   | Layer order of the watermark.                                                                                                                                       |
| waterMark[].imgUrl       | String    | URL of the image watermark, only effective when type is `picture`.                                                                                                  |
| encryption               | Object    | Media stream encryption settings.                                                                                                                                   |
| encryption.mode          | String    | Encryption type, supports `AES_128_XTS`, `AES_128_ECB`, `AES_256_XTS`, `SM4_128_ECB`, `AES_128_GCM`, `AES_256_GCM`, `AES_128_GCM2`, `AES_256_GCM2`.                 |
| encryption.key           | String    | Encryption key.                                                                                                                                                     |
| encryption.salt          | String    | Encryption salt, a 32-character string, e.g., "ABC123".                                                                                                             |
| rotation                 | Object[]  | Video rotation settings.                                                                                                                                            |
| rotation[].uid           | String    | User ID of the video to be rotated.                                                                                                                                 |
| rotation[].degree        | Integer   | Rotation angle, supports 0, 90, 180, 270.                                                                                                                           |

Note:

- **Before executing the recording, be sure to fill in the appId and token parameters in the JSON.**
- **The appId and channelName settings must be consistent with those in the Agora RTC SDK.**
- **In single stream mode, the recorderPath folder name needs to be manually created in the Examples folder, for example, "recorderPath": "recorder_result/", ensure that the Examples/recorder_result/ directory exists.**

#### Start Recording

Navigate to the example directory and manually create the folder for single stream configuration:

```sh
cd Examples
mkdir recorder_result
```

Run the test script according to the test scenario:

```sh
./script/TestCaseName.sh
```

Note:

- **The pre-configured scripts are just simple scenarios. You can modify any one of the script's corresponding json config files according to the specific situation.**

#### Stop Recording

Enter `1` in the terminal console to stop recording.

#### Recording File Path

For single stream, mp4 files are generated in the specified folder under the `Examples` directory, with the file name starting with the UID.
For mixed stream, the mp4 file is generated in the `Examples` directory with the file name specified in the JSON configuration.

### Recording via API

#### Prerequisites

Before starting, ensure that you have completed the environment preparation and integration of the recording SDK, including configuring the jar and corresponding platform's so files.

#### Implement Recording via API

##### Initialize Service

```java
AgoraServiceConfiguration config = new AgoraServiceConfiguration();
config.setEnableAudioDevice(false);
config.setEnableAudioProcessor(true);
config.setEnableVideo(true);
config.setAppId("APPID");
config.setUseStringUid(false);
agoraService.initialize(config);
```

Note:

- **appId: The App ID of the project, which needs to be consistent with the App ID in the RTC SDK.**

##### Join Channel

```java
AgoraMediaComponentFactory factory = agoraService.createAgoraMediaComponentFactory();

AgoraMediaRtcRecorder agoraMediaRtcRecorder = factory.createMediaRtcRecorder();
agoraMediaRtcRecorder.initialize(agoraService, false);
AgoraMediaRtcRecorderEventHandler handler = new AgoraMediaRtcRecorderEventHandler();
agoraMediaRtcRecorder.registerRecorderEventHandler(handler);

agoraMediaRtcRecorder.joinChannel("token", "channelName", "0");
```

Note:

- **channelName: Must be consistent with the channel name joined by the RTC SDK.**

##### Start Recording

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

- **The watermark position cannot exceed the width and height of the video.**

1. Mixed Recording:

```java
// set recorder config
MediaRecorderConfiguration mediaRecorderConfiguration = new MediaRecorderConfiguration();
agoraMediaRtcRecorder.setRecorderConfig(mediaRecorderConfiguration);

agoraMediaRtcRecorder.startRecording();
```

2. Single Stream Recording

Listen for audio and video callbacks, and call the single stream recording interface.

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

##### Stop Recording

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

##### Get Recording File Path

For single stream, mp4 files are generated in the specified folder under the `Examples` directory, with the file name starting with the UID.
For mixed stream, the mp4 file is generated in the `Examples` directory with the file name specified in the MediaRecorderConfiguration object.

## API Reference

### `AgoraService` Class

#### Overview

The `AgoraService` class provides core functionalities for initializing and managing Agora services. It is the primary entry point for using Agora's recording features.

#### Methods

##### `AgoraService()`

Constructs an `AgoraService` instance and initializes local components. Only one `AgoraService` instance can be initialized at a time.

##### `long getNativeHandle()`

Retrieves the native handle associated with this `AgoraService` instance.

**Returns**:

- The native handle value used for local method calls.

##### `int release()`

Releases the `AgoraService` object and its associated resources. After calling this method, the instance becomes invalid.

**Returns**:

- `0`: Success
- `< 0`: Failure

##### `int initialize(AgoraServiceConfiguration config)`

Initializes the `AgoraService` object with the specified configuration.

**Parameters**:

- `config`: The configuration object containing initialization parameters.

**Returns**:

- `0`: Success
- `< 0`: Failure

##### `AgoraMediaComponentFactory createAgoraMediaComponentFactory()`

Creates and returns an `AgoraMediaComponentFactory` object for creating media components.

**Returns**:

- An `AgoraMediaComponentFactory` instance.

##### `AgoraParameter getAgoraParameter()`

Creates and returns an `AgoraParameter` object for parameter management.

**Returns**:

- On success, returns an `AgoraParameter` instance.
- On failure, returns `null`.

##### `static String getSdkVersion()`

Retrieves the SDK version.

**Returns**:

- The SDK version.

##### `int setLogFile(String filePath, int fileSize)`

Sets the path and size of the SDK log file.

**Parameters**:

- `filePath`: The path to the log file. Ensure the directory exists and is writable.
- `fileSize`: The size of the SDK log file (in bytes), i.e., the size of each log file.

**Returns**:

- `0`: Success
- `< 0`: Failure

##### `int setLogLevel(Constants.LogLevel level)`

Sets the log level for the SDK log file.

**Parameters**:

- `level`: The log level.

**Returns**:

- `0`: Success
- `< 0`: Failure

### `AgoraServiceConfiguration` Class

#### Overview

The `AgoraServiceConfiguration` class is used to configure and initialize an Agora service instance. This class contains all the settings needed to initialize and configure the Agora service instance.

#### Properties

- **enableAudioProcessor**

  Indicates whether to enable the audio processing module.

  - `true`: Enables the audio processing module (default).
  - `false`: Disables the audio processing module. If the audio processing module is disabled, audio tracks cannot be created.

- **enableAudioDevice**

  Indicates whether to enable the audio device module. The audio device module manages audio devices such as recording and playback.

  - `true`: Enables the audio device module (default). Audio recording and playback are available.
  - `false`: Disables the audio device module. Audio recording and playback are unavailable.

  Note: If `enableAudioDevice` is set to `false` and `enableAudioProcessor` is set to `true`, audio devices cannot be used, but PCM audio data can be pushed.

- **enableVideo**

  Indicates whether to enable video.

  - `true`: Enables video.
  - `false`: Disables video (default).

- **context**

  The user context object. For Android, it is the context of the activity.

- **appId**

  The App ID of the project.

- **areaCode**

  The supported area code. The default value is `AREA_CODE_GLOB`.

- **channelProfile**

  The channel profile. The default channel profile is `CHANNEL_PROFILE_LIVE_BROADCASTING`.

- **license**

  The authentication license used when connecting to a channel. Charges are based on the license.

- **audioScenario**

  The audio scenario. The default value is `AUDIO_SCENARIO_DEFAULT`.

- **logConfig**

  Configuration for custom log path, log size, and log level.

- **useStringUid**

  Indicates whether to enable string user IDs.

- **useExternalEglContext**

  Indicates whether to use the EGL context in the current thread as the root EGL context for the SDK. This context is shared by all EGL-related modules, such as camera capture and video rendering.
  Note: This property is only applicable to Android.

- **domainLimit**

  Indicates whether to enable domain limitation.

  - `true`: Only connects to servers resolved via DNS.
  - `false`: Connects to servers without limitations (default).

### `AgoraMediaComponentFactory` Class

#### Overview

The `AgoraMediaComponentFactory` class is a factory class for creating Agora media components. It provides functionalities to create instances of media recording components.

#### Methods

- **AgoraMediaComponentFactory(long handle)**

  Constructs an `AgoraMediaComponentFactory` instance.

  - Parameters:
    - `handle`: The native handle for the factory instance.

- **AgoraMediaRtcRecorder createMediaRtcRecorder()**

  Creates a new `AgoraMediaRtcRecorder` instance.

  - Returns:
    - A new `AgoraMediaRtcRecorder` instance.
  - Exceptions:
    - `RuntimeException`: If the local recorder creation fails.

- **int release()**

  Releases the local resources associated with the factory.

  - Returns:
    - The result of the release operation.
      - `0`: Success.
      - `< 0`: Failure.

### `AgoraMediaRtcRecorder` Class

#### Overview

The `AgoraMediaRtcRecorder` class provides functionalities for recording Agora RTC media streams. This class allows recording audio and video streams from an Agora RTC channel, with options for stream mixing, encryption, and selective subscription.

#### Methods

- **AgoraMediaRtcRecorder(long handle)**

  Constructs an `AgoraMediaRtcRecorder` instance.

  - Parameters:
    - `handle`: The native handle for the recorder instance.

- **int initialize(AgoraService service, boolean enableMix)**

  Initializes the recorder with the specified service and mixing settings.

  - Parameters:
    - `service`: The Agora service instance that must be initialized before calling this method.
    - `enableMix`: Whether to enable stream mixing.
  - Returns:
    - `0`: Initialization successful.
    - Negative value: Initialization failed.

- **int joinChannel(String token, String channelName, String userId)**

  Joins an Agora RTC channel.

  - Parameters:
    - `token`: The token for authentication.
    - `channelName`: The name of the channel to join. The name cannot exceed 64 bytes and can include lowercase letters, uppercase letters, numbers, spaces, and special characters.
    - `userId`: The user ID of the local user. If `null`, the system will automatically assign one.
  - Returns:
    - `0`: Method call successful.
    - Negative value: Method call failed.

- **int leaveChannel()**

  Leaves the current channel.

  - Returns:
    - `0`: Method call successful.
    - Negative value: Method call failed.

- **int enableEncryption(boolean enabled, EncryptionConfig config)**

  Enables or disables built-in encryption.

  - Parameters:
    - `enabled`: Whether to enable built-in encryption.
    - `config`: The encryption configuration parameters.
  - Returns:
    - `0`: Method call successful.
    - Negative value: Method call failed.

  Note: If encryption is enabled, the RTMP stream feature will be disabled.

- **int subscribeAllAudio()**

  Subscribes to the audio streams of all remote users in the channel.

  - Returns:
    - `0`: Method call successful.
    - Negative value: Method call failed.

- **int subscribeAllVideo(VideoSubscriptionOptions options)**

  Subscribes to the video streams of all remote users in the channel.

  - Parameters:
    - `options`: Video subscription options, including stream type and other parameters.
  - Returns:
    - `0`: Method call successful.
    - Negative value: Method call failed.

- **int unsubscribeAllAudio()**

  Stops subscribing to the audio streams of all remote users in the channel.

  - Returns:
    - `0`: Method call successful.
    - Negative value: Method call failed.

- **int unsubscribeAllVideo()**

  Stops subscribing to the video streams of all remote users in the channel.

  - Returns:
    - `0`: Method call successful.
    - Negative value: Method call failed.

- **int subscribeAudio(String userId)**

  Subscribes to the audio stream of a specified remote user.

  - Parameters:
    - `userId`: The ID of the remote user whose audio is to be subscribed to.
  - Returns:
    - `0`: Method call successful.
    - `-2`: Invalid userId.
    - Other negative values: Method call failed.

- **int unsubscribeAudio(String userId)**

  Stops subscribing to the audio stream of a specified remote user.

  - Parameters:
    - `userId`: The ID of the remote user whose audio subscription is to be stopped.
  - Returns:
    - `0`: Method call successful.
    - `-2`: Invalid userId.
    - Other negative values: Method call failed.

- **int subscribeVideo(String userId, VideoSubscriptionOptions options)**

  Subscribes to the video stream of a specified remote user.

  - Parameters:
    - `userId`: The ID of the remote user whose video is to be subscribed to.
    - `options`: Video subscription options, including stream type and other parameters.
  - Returns:
    - `0`: Method call successful.
    - `-2`: Invalid userId.
    - Other negative values: Method call failed.

- **int unsubscribeVideo(String userId)**

  Stops subscribing to the video stream of a specified remote user.

  - Parameters:
    - `userId`: The ID of the remote user whose video subscription is to be stopped.
  - Returns:
    - `0`: Method call successful.
    - `-2`: Invalid userId.
    - Other negative values: Method call failed.

- **int setVideoMixingLayout(VideoMixingLayout layout)**

  Sets the layout for mixed video streams.

  - Parameters:
    - `layout`: The layout configuration for mixed video streams.
  - Returns:
    - `0`: Method call successful.
    - Negative value: Method call failed.

- **int setRecorderConfig(MediaRecorderConfiguration config)**

  Configures the recorder settings. This method must be called before starting the recording.

  - Parameters:
    - `config`: The recorder configuration parameters.
  - Returns:
    - `0`: Method call successful.
    - Negative value: Method call failed.

- **int setRecorderConfigByUid(MediaRecorderConfiguration config, String userId)**

  Configures the recorder settings for a specified user.

  - Parameters:
    - `config`: The recorder configuration parameters.
    - `userId`: The user ID of the user whose recorder settings are to be configured.
  - Returns:
    - `0`: Method call successful.
    - Negative value: Method call failed.

- **int startRecording()**

  Starts the recording process. Ensure to configure the recorder using `setRecorderConfig` before calling this method.

  - Returns:
    - `0`: Method call successful.
    - Negative value: Method call failed.

- **int stopRecording()**

  Stops the recording process. This method stops all ongoing recordings and saves the recorded files.

  - Returns:
    - `0`: Method call successful.
    - Negative value: Method call failed.

- **int startSingleRecordingByUid(String userId)**

  Starts the recording process for a specified user.

  - Parameters:
    - `userId`: The user ID of the user whose recording is to be started.
  - Returns:
    - `0`: Method call successful.
    - Negative value: Method call failed.

- **int stopSingleRecordingByUid(String userId)**

  Stops the recording process for a specified user.

  - Parameters:
    - `userId`: The user ID of the user whose recording is to be stopped.
  - Returns:
    - `0`: Method call successful.
    - Negative value: Method call failed.

- **int registerRecorderEventHandler(IAgoraMediaRtcRecorderEventHandler handler)**

  Registers an event handler for recording events. The handler receives callbacks for various recording events, such as state changes, errors, and recording progress updates.

  - Parameters:
    - `handler`: The event handler implementing the `IAgoraMediaRtcRecorderEventHandler` interface.
  - Returns:
    - `0`: Registration successful.
    - Negative value: Registration failed.

- **int unregisterRecorderEventHandle(IAgoraMediaRtcRecorderEventHandler handler)**

  Unregisters a previously registered event handler.

  - Parameters:
    - `handle`: The event handler to be unregistered.
  - Returns:
    - `0`: Unregistration successful.
    - Negative value: Unregistration failed.

- **int enableAndUpdateVideoWatermarks(WatermarkConfig[] watermarkConfigs)**

  Adds watermarks to the stream.

  - Parameters:
    - `watermarkConfigs`: The watermark configurations.
  - Returns:
    - `0`: Method call successful.
    - Negative value: Method call failed.

- **int disableVideoWatermarks()**

  Disables watermarks on the stream.

  - Returns:
    - `0`: Method call successful.
    - Negative value: Method call failed.

- **int enableAndUpdateVideoWatermarksByUid(WatermarkConfig[] watermarkConfigs, String userId)**

  Adds watermarks to the stream of a specified user.

  - Parameters:
    - `watermarkConfigs`: The watermark configurations.
    - `userId`: The user ID of the user whose stream watermarks are to be added.
  - Returns:
    - `0`: Method call successful.
    - Negative value: Method call failed.

- **int disableVideoWatermarksByUid(String userId)**

  Disables watermarks on the stream of a specified user.

  - Parameters:
    - `userId`: The user ID of the user whose stream watermarks are to be disabled.
  - Returns:
    - `0`: Method call successful.
    - Negative value: Method call failed.

- **int release()**

  Releases the local resources associated with the recorder.

  - Returns:
    - The result of the release operation.
      - `0`: Success.
      - `< 0`: Failure.

### `IAgoraMediaRtcRecorderEventHandler` Interface

#### Overview

The `IAgoraMediaRtcRecorderEventHandler` interface defines callback methods for changes in connection state between the SDK and the Agora channel, as well as other recording-related events.

#### Methods

- **onConnected(String channelId, String userId)**

  Triggered when the connection state between the SDK and the Agora channel becomes `CONNECTION_STATE_CONNECTED(3)`.

  - Parameters:
    - `channelId`: The channel ID.
    - `userId`: The user ID.

- **onDisconnected(String channelId, String userId, Constants.ConnectionChangedReasonType reason)**

  Triggered when the connection state between the SDK and the Agora channel becomes `CONNECTION_STATE_DISCONNECTED(1)`.

  - Parameters:
    - `channelId`: The channel ID.
    - `userId`: The user ID.
    - `reason`: The reason for the connection state change. See `Constants.ConnectionChangedReasonType`.

- **onReconnected(String channelId, String userId, Constants.ConnectionChangedReasonType reason)**

  Triggered when the connection state between the SDK and the Agora channel becomes `CONNECTION_STATE_CONNECTED(3)` again.

  - Parameters:
    - `channelId`: The channel ID.
    - `userId`: The user ID.
    - `reason`: The reason for the connection state change. See `Constants.ConnectionChangedReasonType`.

- **onConnectionLost(String channelId, String userId)**

  Triggered when the SDK loses connection with the Agora channel.

  - Parameters:
    - `channelId`: The channel ID.
    - `userId`: The user ID.

- **onUserJoined(String channelId, String userId)**

  Triggered when a remote user joins the channel.

  - Parameters:
    - `channelId`: The channel ID.
    - `userId`: The user ID.

- **onUserLeft(String channelId, String userId, Constants.UserOfflineReasonType reason)**

  Triggered when a remote user leaves the channel.

  - Parameters:
    - `channelId`: The channel ID.
    - `userId`: The user ID.
    - `reason`: The reason the remote user left the channel. See `Constants.UserOfflineReasonType`.

- **onFirstRemoteVideoDecoded(String channelId, String userId, int width, int height, int elapsed)**

  Triggered when the SDK decodes the first frame of remote video.

  - Parameters:
    - `channelId`: The channel ID.
    - `userId`: The remote user ID.
    - `width`: The width of the video stream (pixels).
    - `height`: The height of the video stream (pixels).
    - `elapsed`: The time elapsed (in milliseconds) from the user joining the Agora channel to the first video frame being decoded.

- **onFirstRemoteAudioDecoded(String channelId, String userId, int elapsed)**

  Triggered when the SDK decodes the first frame of remote audio.

  - Parameters:
    - `channelId`: The channel ID.
    - `userId`: The remote user ID.
    - `elapsed`: The time elapsed (in milliseconds) from the user joining the Agora channel to the first audio frame being decoded.

- **onAudioVolumeIndication(String channelId, String userId, int speakerNumber, int totalVolume)**

  Reports the users who are speaking, the volume of the speaker, and whether the local user is speaking.

  - Parameters:
    - `channelId`: The channel ID.
    - `userId`: The remote user ID.
    - `speakerNumber`: The total number of speakers.
    - `totalVolume`: The total volume after audio mixing, ranging from 0 (lowest volume) to 255 (highest volume).

- **onActiveSpeaker(String channelId, String userId)**

  Triggered when an active speaker is detected.

  - Parameters:
    - `channelId`: The channel ID.
    - `userId`: The user ID of the active speaker. A `userId` of `0` indicates the local user.

- **onUserVideoStateChanged(String channelId, String userId, Constants.RemoteVideoState state, Constants.RemoteVideoStateReason reason, int elapsed)**

  Triggered when the video state of a remote user changes.

  - Parameters:
    - `channelId`: The channel ID.
    - `userId`: The remote user ID.
    - `state`: The current video state. See `Constants.RemoteVideoState`.
    - `reason`: The reason for the state change. See `Constants.RemoteVideoStateReason`.
    - `elapsed`: The time elapsed (in milliseconds) from the user joining the Agora channel to the state change.

- **onUserAudioStateChanged(String channelId, String userId, Constants.RemoteAudioState state, Constants.RemoteAudioStateReason reason, int elapsed)**

  Triggered when the audio state of a remote user changes.

  - Parameters:
    - `channelId`: The channel ID.
    - `userId`: The remote user ID.
    - `state`: The current audio state. See `Constants.RemoteAudioState`.
    - `reason`: The reason for the state change. See `Constants.RemoteAudioStateReason`.
    - `elapsed`: The time elapsed (in milliseconds) from the user joining the Agora channel to the state change.

- **onRemoteVideoStats(String channelId, String userId, RemoteVideoStatistics stats)**

  Reports the statistics of the remote video.

  - Parameters:
    - `channelId`: The channel ID.
    - `userId`: The remote user ID.
    - `stats`: The statistics of the current video.

- **onRemoteAudioStats(String channelId, String userId, RemoteAudioStatistics stats)**

  Reports the statistics of the remote audio.

  - Parameters:
    - `channelId`: The channel ID.
    - `userId`: The remote user ID.
    - `stats`: The statistics of the current audio.

- **onRecorderStateChanged(String channelId, String userId, Constants.RecorderState state, Constants.RecorderReasonCode reason, String fileName)**

  Triggered when the recording state changes.

  - Parameters:
    - `channelId`: The channel name.
    - `userId`: The user ID.
    - `state`: The current recording state. See `Constants.RecorderState`.
    - `reason`: The reason for the state change. See `Constants.RecorderReasonCode`.
    - `fileName`: The name of the recorded file.

- **onRecorderInfoUpdated(String channelId, String userId, RecorderInfo info)**

  Triggered when the recording information is updated.

  - Parameters:
    - `channelId`: The channel name.
    - `userId`: The user ID.
    - `info`: The information of the recorded file. See `RecorderInfo`.

### `MediaRecorderConfiguration` Class

#### Overview

The `MediaRecorderConfiguration` class is used to configure parameters related to the recording file.

#### Properties

- **`storagePath`**

  The absolute path of the recording file (including the file name and extension). For example:

  - Windows: `C:\Users\<user_name>\AppData\Local\Agora\<process_name>\example.mp4`
  - iOS: `/App Sandbox/Library/Caches/example.mp4`
  - macOS: `/Library/Logs/example.mp4`
  - Android: `/storage/emulated/0/Android/data/<package name>/files/example.mp4`
  - Linux: `result/example.mp4`

  **Note**: Ensure that the specified path exists and is writable.

- **`containerFormat`**

  The format of the recording file. Refer to `Constants.MediaRecorderContainerFormat`.

- **`streamType`**

  The type of content to be recorded. Refer to `Constants.MediaRecorderStreamType`.

- **`maxDurationMs`**

  The maximum recording duration in milliseconds. The default value is 120,000 milliseconds (2 minutes).

- **`recorderInfoUpdateInterval`**

  The interval for updating recording information, in milliseconds. The value range is [1000, 10000]. Based on the set value, the SDK triggers the `IMediaRecorderObserver#onRecorderInfoUpdated` callback to report updated recording information.

- **`width`**

  The width of the recording video in pixels. The default value is 1280.

- **`height`**

  The height of the recording video in pixels. The default value is 720.

- **`fps`**

  The frame rate of the recording video, in frames per second. The default value is 30.

- **`sampleRate`**

  The sample rate of the recording audio, in Hz. The default value is 48,000 Hz.

- **`channelNum`**

  The number of audio channels for recording. The default value is 1 (mono).

- **`videoSourceType`**

  The type of external video source. Refer to `Constants.VideoSourceType`.

### `AgoraParameter` Class

#### Overview

The `AgoraParameter` class provides functionalities to get and set Agora SDK configuration parameters, supporting various data types including boolean, integer, unsigned integer, floating-point, string, object, and array.

#### Methods

- **`void release()`**

  Releases all resources associated with this parameter instance. After calling this method, the instance becomes invalid.

- **`int setBool(String key, boolean value)`**

  Sets a boolean parameter value.

  - Parameters:
    - `key`: The parameter key.
    - `value`: The boolean value to set.
  - Returns: `0` on success, negative value on failure.

- **`int setInt(String key, int value)`**

  Sets an integer parameter value.

  - Parameters:
    - `key`: The parameter key.
    - `value`: The integer value to set.
  - Returns: `0` on success, negative value on failure.

- **`int setUInt(String key, int value)`**

  Sets an unsigned integer parameter value.

  - Parameters:
    - `key`: The parameter key.
    - `value`: The unsigned integer value to set.
  - Returns: `0` on success, negative value on failure.

- **`int setNumber(String key, double value)`**

  Sets a floating-point parameter value.

  - Parameters:
    - `key`: The parameter key.
    - `value`: The floating-point value to set.
  - Returns: `0` on success, negative value on failure.

- **`int setString(String key, String value)`**

  Sets a string parameter value.

  - Parameters:
    - `key`: The parameter key.
    - `value`: The string value to set.
  - Returns: `0` on success, negative value on failure.

- **`int setObject(String key, String value)`**

  Sets an object parameter value in JSON format.

  - Parameters:
    - `key`: The parameter key.
    - `value`: The JSON string representing the object.
  - Returns: `0` on success, negative value on failure.

- **`int setArray(String key, String value)`**

  Sets an array parameter value in JSON format.

  - Parameters:
    - `key`: The parameter key.
    - `value`: The JSON string representing the array.
  - Returns: `0` on success, negative value on failure.

- **`boolean getBool(String key)`**

  Gets a boolean parameter value.

  - Parameters:
    - `key`: The parameter key.
  - Returns: The boolean value associated with the key.
  - Exceptions: Throws `IllegalStateException` if the parameter cannot be retrieved.

- **`int getInt(String key)`**

  Gets an integer parameter value.

  - Parameters:
    - `key`: The parameter key.
  - Returns: The integer value associated with the key.
  - Exceptions: Throws `IllegalStateException` if the parameter cannot be retrieved.

- **`int getUInt(String key)`**

  Gets an unsigned integer parameter value.

  - Parameters:
    - `key`: The parameter key.
  - Returns: The unsigned integer value associated with the key.
  - Exceptions: Throws `IllegalStateException` if the parameter cannot be retrieved.

- **`double getNumber(String key)`**

  Gets a floating-point parameter value.

  - Parameters:
    - `key`: The parameter key.
  - Returns: The floating-point value associated with the key.
  - Exceptions: Throws `IllegalStateException` if the parameter cannot be retrieved.

- **`String getString(String key)`**

  Gets a string parameter value.

  - Parameters:
    - `key`: The parameter key.
  - Returns: The string value associated with the key, or `null` if not found.

- **`String getObject(String key)`**

  Gets an object parameter value in JSON format.

  - Parameters:
    - `key`: The parameter key.
  - Returns: The JSON string representing the object, or `null` if not found.

- **`String getArray(String key, String args)`**

  Gets an array parameter value in JSON format.

  - Parameters:
    - `key`: The parameter key.
    - `args`: Additional parameters for array retrieval.
  - Returns: The JSON string representing the array, or `null` if not found.

- **`int setParameters(String parameters)`**

  Sets multiple parameters at once using a JSON string.

  - Parameters:
    - `parameters`: A JSON string containing multiple parameter key-value pairs.
  - Returns: `0` on success, negative value on failure.

- **`String convertPath(String filePath)`**

  Converts a file path to a platform-specific format.

  - Parameters:
    - `filePath`: The original file path to convert.
  - Returns: The platform-specific file path, or `null` if the conversion fails.

## Changelog

### v4.4.150-aarch64（2025-02-24）

- Released version 4.4.150-aarch64, including basic features and performance optimizations.

### v4.4.150（2025-01-21）

- Released version 4.4.150, including basic features and performance optimizations.

## Additional References

For detailed references, please visit the official website: [Agora Documentation](https://doc.shengwang.cn/doc/recording/java/landing-page)
