# Agora Recording Java SDK

[中文](./README.zh.md) | English

## Table of Contents

- [Agora Recording Java SDK](#agora-recording-java-sdk)
  - [Table of Contents](#table-of-contents)
  - [Introduction](#introduction)
  - [Development Environment Requirements](#development-environment-requirements)
    - [Hardware Environment](#hardware-environment)
    - [Network Requirements](#network-requirements)
    - [Bandwidth Requirements](#bandwidth-requirements)
    - [Software Environment](#software-environment)
  - [SDK Download](#sdk-download)
    - [Maven Download](#maven-download)
      - [x86\_64 Platform](#x86_64-platform)
      - [arm64 Platform](#arm64-platform)
    - [CDN Download](#cdn-download)
      - [x86\_64 Platform](#x86_64-platform-1)
      - [arm64 Platform](#arm64-platform-1)
  - [Integrating the SDK](#integrating-the-sdk)
    - [1. Maven Integration](#1-maven-integration)
      - [1.1 Add Maven Dependency](#11-add-maven-dependency)
      - [1.2 Integrate .so Library Files](#12-integrate-so-library-files)
    - [2. Local SDK Integration](#2-local-sdk-integration)
      - [2.1 SDK Package Structure](#21-sdk-package-structure)
      - [2.2 Integrate JAR File](#22-integrate-jar-file)
          - [Local Maven Repository Method](#local-maven-repository-method)
          - [Direct Reference Method](#direct-reference-method)
      - [2.3 Integrate .so Library Files](#23-integrate-so-library-files)
    - [3. Loading Native Libraries (.so files)](#3-loading-native-libraries-so-files)
      - [3.1 Extracting .so Files](#31-extracting-so-files)
      - [3.2 Configuring Load Paths](#32-configuring-load-paths)
  - [Quick Start](#quick-start)
    - [Enable Service](#enable-service)
    - [Run the Maven Project](#run-the-maven-project)
      - [1. Configure Keys](#1-configure-keys)
      - [2. Configure JAR and .so Libraries](#2-configure-jar-and-so-libraries)
      - [3. Build the Project](#3-build-the-project)
      - [4. Run the Example Service](#4-run-the-example-service)
      - [5. RESTful API Recording Control](#5-restful-api-recording-control)
      - [6. Troubleshooting](#6-troubleshooting)
    - [Recording via Command Line (Examples-Mvn)](#recording-via-command-line-examples-mvn)
      - [Prerequisites](#prerequisites)
      - [Run Command](#run-command)
      - [Config Files and Meanings (located in `Examples-Mvn/src/main/resources/`)](#config-files-and-meanings-located-in-examples-mvnsrcmainresources)
      - [recorder\_json.example Parameter Reference](#recorder_jsonexample-parameter-reference)
    - [Recording via API](#recording-via-api)
      - [Prerequisites](#prerequisites-1)
      - [Implement Recording via API](#implement-recording-via-api)
        - [Initialize Service](#initialize-service)
        - [Join Channel](#join-channel)
        - [Configure and Start Recording](#configure-and-start-recording)
        - [Recording Event Handling](#recording-event-handling)
        - [Stop Recording](#stop-recording)
        - [Getting Recorded Files](#getting-recorded-files)
      - [Snapshot Feature (API Example)](#snapshot-feature-api-example)
  - [API Reference](#api-reference)
  - [Changelog](#changelog)
    - [v4.4.151 / v4.4.151-aarch64 (2025-09-04)](#v44151--v44151-aarch64-2025-09-04)
      - [API Changes](#api-changes)
    - [v4.4.150.5 (2025-06-30)](#v441505-2025-06-30)
      - [API Changes](#api-changes-1)
      - [Improvements \& Bug Fixes](#improvements--bug-fixes)
    - [v4.4.150.4 (2025-06-11)](#v441504-2025-06-11)
      - [API Changes](#api-changes-2)
      - [Improvements \& Bug Fixes](#improvements--bug-fixes-1)
    - [v4.4.150.3 (2025-05-20)](#v441503-2025-05-20)
      - [API Changes](#api-changes-3)
    - [v4.4.150.2 (2025-05-09)](#v441502-2025-05-09)
      - [API Changes](#api-changes-4)
      - [Improvements \& Optimizations](#improvements--optimizations)
    - [v4.4.150.1 (2025-03-28)](#v441501-2025-03-28)
      - [API Changes](#api-changes-5)
      - [Improvements \& Optimizations](#improvements--optimizations-1)
    - [v4.4.150-aarch64 (2025-02-24)](#v44150-aarch64-2025-02-24)
      - [API Changes](#api-changes-6)
      - [Improvements \& Optimizations](#improvements--optimizations-2)
    - [v4.4.150 (2025-01-21)](#v44150-2025-01-21)
      - [API Changes](#api-changes-7)
      - [Improvements \& Optimizations](#improvements--optimizations-3)
  - [Other References](#other-references)

## Introduction

The Agora Recording Java SDK (v4.4.151) provides powerful real-time audio and video recording capabilities that can be seamlessly integrated into Java applications on Linux servers. With this SDK, your server can join an Agora channel as a dummy client to pull, subscribe to, and record audio and video streams within the channel in real-time. The recorded files can be used for content archiving, moderation, analysis, or other business-related advanced features.

## Development Environment Requirements

### Hardware Environment

- **Operating System**: Ubuntu 14.04+ or CentOS 6.5+ (7.0 recommended)
- **CPU Architecture**: x86-64, arm64

### Network Requirements

- **Public IP Address**
- **Domain Access**: Allow access to `.agora.io` and `.agoralab.co`

### Bandwidth Requirements

The required bandwidth depends on the number of channels to be recorded simultaneously and the conditions within those channels. The following data serves as a reference:

- Recording a 640 × 480 resolution stream requires approximately 500 Kbps.
- Recording a channel with two participants requires approximately 1 Mbps.
- Recording 100 such channels simultaneously requires approximately 100 Mbps.

### Software Environment

- **Build Tool**: Apache Maven or other build tools
- **JDK**: JDK 8+

## SDK Download

### Maven Download

#### x86_64 Platform

```xml
<dependency>
    <groupId>io.agora.rtc</groupId>
    <artifactId>linux-recording-java-sdk</artifactId>
    <version>4.4.151</version>
</dependency>
```

#### arm64 Platform

```xml
<dependency>
    <groupId>io.agora.rtc</groupId>
    <artifactId>linux-recording-java-sdk</artifactId>
    <version>4.4.151-aarch64</version>
</dependency>
```

### CDN Download

#### x86_64 Platform

[Agora-Linux-Recording-Java-SDK-v4.4.151-x86_64-869516-6f3284e71a-20250904_152151](https://download.agora.io/sdk/release/Agora-Linux-Recording-Java-SDK-v4.4.151-x86_64-869516-6f3284e71a-20250904_152151.zip)

#### arm64 Platform

[Agora-Linux-Recording-Java-SDK-v4.4.151-aarch64-869533-8256baf788-20250904_155630](https://download.agora.io/sdk/release/Agora-Linux-Recording-Java-SDK-v4.4.151-aarch64-869533-8256baf788-20250904_155630.zip)

## Integrating the SDK

There are two ways to integrate the SDK: via Maven integration or local SDK integration.

### 1. Maven Integration

Maven integration is the simplest way, automatically managing Java dependencies.

#### 1.1 Add Maven Dependency

Add the following dependency to your project's `pom.xml` file:

```xml
<!-- x86_64 Platform -->
<dependency>
    <groupId>io.agora.rtc</groupId>
    <artifactId>linux-recording-java-sdk</artifactId>
    <version>4.4.151</version>
</dependency>

<!-- arm64 Platform -->
<dependency>
    <groupId>io.agora.rtc</groupId>
    <artifactId>linux-recording-java-sdk</artifactId>
    <version>4.4.151-aarch64</version>
</dependency>
```

#### 1.2 Integrate .so Library Files

The Maven dependency includes the required JAR file, but you still need to manually handle the `.so` library files to run the application. Please refer to the **Loading Native Libraries (.so files)** section below.

### 2. Local SDK Integration

The local SDK is a complete package containing all necessary files, suitable for scenarios requiring more flexible control.

#### 2.1 SDK Package Structure

The SDK package (zip format) downloaded from the official website contains the following:

- **doc/** - JavaDoc documentation, detailed API descriptions
- **examples/** - Example code and projects
- **sdk/** - Core SDK files
  - `agora-recording-sdk.jar` - Java library
  - `agora-recording-sdk-javadoc.jar` - JavaDoc documentation

#### 2.2 Integrate JAR File

You can integrate the JAR file in two ways:

###### Local Maven Repository Method

Method 1: Install only the SDK JAR

```sh
mvn install:install-file \
  -Dfile=sdk/agora-recording-sdk.jar \
  -DgroupId=io.agora.rtc \
  -DartifactId=linux-recording-java-sdk \
  -Dversion=4.4.151 \
  -Dpackaging=jar \
  -DgeneratePom=true
```

Method 2: Install both SDK JAR and JavaDoc JAR

```sh
mvn install:install-file \
  -Dfile=sdk/agora-recording-sdk.jar \
  -DgroupId=io.agora.rtc \
  -DartifactId=linux-recording-java-sdk \
  -Dversion=4.4.151 \
  -Dpackaging=jar \
  -DgeneratePom=true \
  -Djavadoc=sdk/agora-recording-sdk-javadoc.jar
```

After installation, add the dependency in `pom.xml`:

```xml
<dependency>
    <groupId>io.agora.rtc</groupId>
    <artifactId>linux-recording-java-sdk</artifactId>
    <version>4.4.151</version>
</dependency>
```

###### Direct Reference Method

1. Copy the JAR files to your project's `libs` directory:

   ```sh
   mkdir -p libs
   cp sdk/agora-recording-sdk.jar libs/
   cp sdk/agora-recording-sdk-javadoc.jar libs/  # Optional, for IDE support
   ```

2. Add the classpath reference in your Java project:

   ```sh
   # Use the SDK JAR
   java -cp .:libs/agora-recording-sdk.jar YourMainClass

   # Configure JavaDoc in your IDE (common IDEs like IntelliJ IDEA or Eclipse support direct association of JavaDoc JARs)
   ```

#### 2.3 Integrate .so Library Files

The downloaded SDK package already includes the `.so` files. You need to ensure that the Java program can find these files at runtime. Please refer to the **Loading Native Libraries (.so files)** section below.

### 3. Loading Native Libraries (.so files)

The Agora Linux Recording Java SDK depends on underlying C++ native libraries (`.so` files). Whether integrating via Maven or locally, you need to ensure the Java Virtual Machine (JVM) can find and load these libraries at runtime.

#### 3.1 Extracting .so Files

The `.so` files are contained within the `agora-recording-sdk.jar` or `linux-recording-java-sdk-x.x.x.x.jar` file. You need to extract them first:

1. Create a directory in your project or deployment location to store the library files, for example, `libs`:

   ```sh
   mkdir -p libs
   cd libs
   ```

2. Use the `jar` command to extract the contents from the SDK's JAR file (assuming the JAR file is in the `libs` directory or Maven cache):

   ```sh
   # If using local integration, the JAR file is usually in the libs directory
   jar xvf agora-recording-sdk.jar

   # If using Maven integration, the JAR file is in the Maven cache, e.g.:
   # jar xvf ~/.m2/repository/io/agora/rtc/linux-recording-java-sdk/4.4.151/linux-recording-java-sdk-4.4.151.jar
   ```

3. After extraction, a `native/linux/x86_64` subdirectory (or `aarch64` for ARM) will be generated in the `libs` directory, containing the required `.so` files:

   ```
   libs/
   ├── agora-recording-sdk.jar (or empty, if only used for extraction)
   ├── io/          # Java class files location, no need to worry about
   ├── META-INF/    # JAR file and application-related metadata, no need to worry about
   └── native/      # Native library files for the corresponding platform
       └── linux/
           └── x86_64/   # x86_64 platform .so libraries
               ├── libagora_rtc_sdk.so
               ├── libagora-fdkaac.so
               ├── libaosl.so
               └── librecording.so
           └── aarch64/  # arm64 platform .so libraries (if available)
               ├── libagora_rtc_sdk.so
               ├── libagora-fdkaac.so
               ├── libaosl.so
               └── librecording.so
   ```

#### 3.2 Configuring Load Paths

There are two main methods to let the JVM find the `.so` files:

**Method 1: Setting the `LD_LIBRARY_PATH` Environment Variable (Recommended)**

This is the most reliable way, especially when there are dependencies between `.so` files.

```sh
# Determine the directory containing your .so files, assuming ./libs/native/linux/x86_64
LIB_DIR=$(pwd)/libs/native/linux/x86_64

# Set the LD_LIBRARY_PATH environment variable, adding the library directory to the front of the existing path
export LD_LIBRARY_PATH=$LIB_DIR:$LD_LIBRARY_PATH

# Run your Java application
java -jar YourApp.jar
# Or using classpath
# java -cp "YourClasspath" YourMainClass
```

**Method 2: Using the JVM Parameter `-Djava.library.path`**

This method directly tells the JVM where to look for library files.

```sh
# Determine the directory containing your .so files, assuming ./libs/native/linux/x86_64
LIB_DIR=$(pwd)/libs/native/linux/x86_64

# Run the Java application, specifying the library path via the -D parameter
java -Djava.library.path=$LIB_DIR -jar YourApp.jar
# Or using classpath
# java -Djava.library.path=$LIB_DIR -cp "YourClasspath" YourMainClass
```

> **Note**:
>
> - Method 1 (`LD_LIBRARY_PATH`) is recommended because it handles dependencies between libraries better. If you only use `-Djava.library.path`, loading might fail sometimes because a library cannot find other libraries it depends on.
> - Ensure `$LIB_DIR` points to the **exact directory** containing files like `libagora_rtc_sdk.so`.
> - You can place the command to set the environment variable in a startup script so it's configured automatically each time the application runs.

Refer to the following script example, which combines both methods and sets the classpath:

```sh
#!/bin/bash
# Get the absolute path of the directory where the script is located
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# Determine the .so library file path (assuming in libs/native/linux/x86_64 relative to the script)
# Adjust x86_64 to your target architecture (e.g., aarch64) if needed
LIB_PATH="$SCRIPT_DIR/libs/native/linux/x86_64"
# SDK JAR path (assuming in libs relative to the script)
SDK_JAR="$SCRIPT_DIR/libs/agora-recording-sdk.jar"
# Your application's main class
MAIN_CLASS="YourMainClass"
# Your application's other dependency classpath (if any)
APP_CP="YourOtherClasspath"

# Check if the library directory exists
if [ ! -d "$LIB_PATH" ]; then
  echo "Error: Library directory not found: $LIB_PATH" >&2
  exit 1
fi

# Set the library path environment variable
export LD_LIBRARY_PATH=$LIB_PATH:$LD_LIBRARY_PATH

# Combine the classpath
CLASSPATH=".:$SDK_JAR:$APP_CP" # '.' represents the current directory

# Execute the Java program
# Use both LD_LIBRARY_PATH and -Djava.library.path for compatibility
java -Djava.library.path=$LIB_PATH -cp "$CLASSPATH" $MAIN_CLASS
```

## Quick Start

### Enable Service

Refer to [Enable Service on the official website](https://docs.agora.io/en/recording/java/get-started/enable-service)

### Run the Maven Project

The SDK provides a Spring Boot-based Maven example project for quick verification and secondary development. Steps to run `Examples-Mvn`:

#### 1. Configure Keys

Create a `.keys` file under `Examples-Mvn` (replace with your values):

```
APP_ID=YourAppId
TOKEN=YourToken
```

#### 2. Configure JAR and .so Libraries

- Configure JAR (choose one of the following):
  - Use online Maven version: edit `Examples-Mvn/pom.xml` and choose version by platform (x86_64 uses `4.4.151`, arm64 uses `4.4.151-aarch64`):
    ```xml
    <dependency>
        <groupId>io.agora.rtc</groupId>
        <artifactId>linux-recording-java-sdk</artifactId>
        <version>4.4.151</version>
    </dependency>
    <!-- For arm64 platform, replace with 4.4.151-aarch64 -->
    ```
  - Use local version: in the `Examples-Mvn` directory, run the script to install the local JAR into your local Maven repository (the script reads `libs/agora-recording-sdk.jar` and its javadoc):
    ```sh
    cd Examples-Mvn
    ./build_install_local_maven.sh
    ```

- Prepare .so libraries: ensure `libs/native/linux/x86_64/` (or `aarch64/`) contains the required `.so` files (e.g. `libagora_rtc_sdk.so`, `librecording.so`, etc.).

#### 3. Build the Project

In `Examples-Mvn`:

```sh
./build.sh
```

After building, `target/agora-example.jar` will be generated.

#### 4. Run the Example Service

In `Examples-Mvn`:

```sh
./build.sh start
```

- This starts the Spring Boot service on port 18080.
- To change the port, modify `-Dserver.port`.

#### 5. RESTful API Recording Control

- Start recording:

  ```
  http://<server_ip>:18080/api/recording/start?configFileName=mix_stream_recorder_audio_video_water_marks.json
  ```

- Stop recording:

  ```
  http://<server_ip>:18080/api/recording/stop?taskId=<task_id>
  ```

> Place recording config files in `Examples-Mvn/src/main/resources/`.

#### 6. Troubleshooting

- If the service fails to start, check `.so` paths, `.keys` content, and port usage.
- If there is no recording output, ensure there are active users in the channel and that AppId/Token/channel name are correct.

### Recording via Command Line (Examples-Mvn)

#### Prerequisites

Ensure SDK integration, `.keys` configuration, and `.so` libraries are prepared (see “Run the Maven Project”).

#### Run Command

In `Examples-Mvn`:

```sh
./build.sh cli <configFileName>
# Example:
./build.sh cli capture_type_encoded_frame_mix_stream.json
```

After startup, enter `1` in the terminal to stop and exit.

#### Config Files and Meanings (located in `Examples-Mvn/src/main/resources/`)

- Mixed-stream Recording:
  - `mix_stream_recorder_audio_video.json`: mixed recording (audio + video)
  - `mix_stream_recorder_audio.json`: mixed, audio only
  - `mix_stream_recorder_video.json`: mixed, video only
  - `mix_stream_recorder_audio_video_encryption.json`: mixed, with encryption
  - `mix_stream_recorder_audio_video_string_uid.json`: mixed, with string UID
  - `mix_stream_recorder_audio_video_water_marks.json`: mixed, with watermarks
  - `mix_stream_recorder_audio_video_water_marks_bg.json`: mixed, watermarks + background
  - `mix_stream_recorder_audio_video_water_marks_recover.json`: mixed, write h264/aac to allow MP4 recovery

- Single-stream Recording:
  - `single_stream_recorder_audio_video.json`: single, audio + video
  - `single_stream_recorder_audio.json`: single, audio only
  - `single_stream_recorder_video.json`: single, video only
  - `single_stream_recorder_audio_video_water_marks.json`: single, with watermarks

- Snapshot Feature Examples:
  - `capture_type_encoded_frame_mix_stream.json`: mixed recording + encoded frame snapshots (H.264, etc.), outputs MP4 and snapshot data; `videoFrameCaptureType=ENCODED`, `isMix=true`.
  - `capture_type_yuv_frame_single_stream.json`: single recording + YUV frame snapshots, outputs single MP4 and YUV snapshots; `videoFrameCaptureType=YUV`, `isMix=false`.
  - `capture_type_encoded_frame.json`: encoded frame snapshots only (no recording), mixed; `videoFrameCaptureType=ENCODED`, `enableRecording=false`.
  - `capture_type_yuv_frame.json`: YUV frame snapshots only (no recording), mixed; `videoFrameCaptureType=YUV`, `enableRecording=false`.
  - `capture_type_jpg_frame.json`: JPG frame snapshots (memory callback and save), mixed; `videoFrameCaptureType=JPG_FRAME`, `enableRecording=false`.
  - `capture_type_jpg_file.json`: JPG files written directly by SDK, mixed; `videoFrameCaptureType=JPG_FILE`, `enableRecording=false`.

> Tip: `recorderPath` is the MP4 output path (file for mixed, for single it is the per-uid file prefix). `capturePath` is the snapshot output prefix or directory. See `Constants.VideoFrameCaptureType` for enum values.

#### recorder_json.example Parameter Reference

| Parameter                    | Type     | Description                                                                                                                                                     |
| ---------------------------- | -------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| appId                        | String   | Project App ID (can also be specified in `.keys`, which has priority).                                                                                          |
| token                        | String   | Channel Token; can be empty if token authentication is disabled (can also be in `.keys`, which has priority).                                                   |
| channelName                  | String   | Channel name, must match the client.                                                                                                                            |
| useStringUid                 | Boolean  | Whether to use string UID. `false` means numeric UID.                                                                                                           |
| useCloudProxy                | Boolean  | Whether to enable cloud proxy.                                                                                                                                  |
| userId                       | String   | Recorder user ID; when set to "0" it will be assigned automatically.                                                                                            |
| subAllAudio                  | Boolean  | Subscribe to all audio; when `false`, use `subAudioUserList`.                                                                                                   |
| subAudioUserList             | String[] | List of user IDs to subscribe audio from (effective when `subAllAudio=false`).                                                                                  |
| subAllVideo                  | Boolean  | Subscribe to all video; when `false`, use `subVideoUserList`.                                                                                                   |
| subVideoUserList             | String[] | List of user IDs to subscribe video from (effective when `subAllVideo=false`).                                                                                  |
| subStreamType                | String   | Stream type: `high` (high stream) or `low` (low stream).                                                                                                        |
| enableRecording              | Boolean  | Whether to record MP4. When `false`, you can snapshot only.                                                                                                     |
| enableCapture                | Boolean  | Whether to enable snapshot capability.                                                                                                                          |
| videoFrameCaptureType        | Integer  | Snapshot type: 0=ENCODED, 1=YUV, 2=JPG_FRAME (memory callback & save), 3=JPG_FILE (SDK writes JPG). Maps to `Constants.VideoFrameType`/`VideoFrameCaptureType`. |
| isMix                        | Boolean  | Whether to use mixed recording; `false` means single-stream.                                                                                                    |
| backgroundColor              | Long     | Mixed background color (0xRRGGBB as long). Effective when `isMix=true`.                                                                                         |
| backgroundImage              | String   | Mixed background image (PNG/JPG). Takes precedence over `backgroundColor` when both are set.                                                                    |
| layoutMode                   | String   | Mixed layout: `default`, `bestfit`, `vertical`.                                                                                                                 |
| maxResolutionUid             | String   | UID displayed at maximum resolution in `vertical` layout.                                                                                                       |
| recorderStreamType           | String   | Recording type: `audio_only`, `video_only`, `both`.                                                                                                             |
| recorderPath                 | String   | Output path: for mixed it's a file path; for single it's a directory (each UID generates a separate MP4). Ensure parent directories exist.                      |
| capturePath                  | String   | Snapshot output prefix or directory: for JPG_FILE it's a directory; for others it's a file prefix.                                                              |
| maxDuration                  | Integer  | Recording duration (seconds). Stops automatically when reached.                                                                                                 |
| recoverFile                  | Boolean  | Whether to write h264/aac simultaneously to allow MP4 recovery after crashes (recording-related).                                                               |
| audio.sampleRate             | Integer  | Audio sample rate (Hz).                                                                                                                                         |
| audio.numOfChannels          | Integer  | Number of audio channels.                                                                                                                                       |
| video.width                  | Integer  | Video width (pixels).                                                                                                                                           |
| video.height                 | Integer  | Video height (pixels).                                                                                                                                          |
| video.fps                    | Integer  | Video frame rate (fps).                                                                                                                                         |
| waterMark[].type             | String   | Watermark type: `litera` (text), `time` (timestamp), `picture` (image).                                                                                         |
| waterMark[].litera           | String   | Text watermark content (when type=litera).                                                                                                                      |
| waterMark[].fontFilePath     | String   | Font file path (for litera/time).                                                                                                                               |
| waterMark[].fontSize         | Integer  | Font size.                                                                                                                                                      |
| waterMark[].x/y/width/height | Integer  | Watermark rectangle position and size.                                                                                                                          |
| waterMark[].zorder           | Integer  | Watermark layer order.                                                                                                                                          |
| waterMark[].imgUrl           | String   | Image watermark path (when type=picture).                                                                                                                       |
| encryption.mode              | String   | Encryption type: `AES_128_XTS`, `AES_128_ECB`, `AES_256_XTS`, `SM4_128_ECB`, `AES_128_GCM`, `AES_256_GCM`, `AES_128_GCM2`, `AES_256_GCM2`.                      |
| encryption.key               | String   | Encryption key.                                                                                                                                                 |
| encryption.salt              | String   | Encryption salt (32 characters; required by some modes).                                                                                                        |
| rotation[].uid               | String   | UID whose video requires rotation.                                                                                                                              |
| rotation[].degree            | Integer  | Rotation angle: 0, 90, 180, 270.                                                                                                                                |

### Recording via API

#### Prerequisites

Before starting, ensure SDK environment and integration are complete, including the JAR and `.so` libraries.

#### Implement Recording via API

The following example comes from the actual `Examples-Mvn` project and shows how to use the Recording SDK through API calls.

##### Initialize Service

```java
// Create AgoraService instance
AgoraService agoraService = new AgoraService();

// Configure local access point (must be set before initialize)
LocalAccessPointConfiguration localAccessPointConfig = new LocalAccessPointConfiguration();
localAccessPointConfig.setMode(Constants.LocalProxyMode.LocalOnly);
localAccessPointConfig.setIpList(new String[] { "10.xx.xx.xx" });
localAccessPointConfig.setIpListSize(1);
localAccessPointConfig.setVerifyDomainName("ap.xxx.agora.local");
int setGlobalLocalAccessPointRet = agoraService.setGlobalLocalAccessPoint(localAccessPointConfig);

// Create and configure service
AgoraServiceConfiguration config = new AgoraServiceConfiguration();
config.setEnableAudioDevice(false);
config.setEnableAudioProcessor(true);
config.setEnableVideo(true);
config.setAppId("YOUR_APPID");
config.setUseStringUid(false);
agoraService.initialize(config);

// Optional: set cloud proxy
AgoraParameter parameter = agoraService.getAgoraParameter();
if (parameter != null) {
    parameter.setBool("rtc.enable_proxy", true);
}
```

##### Join Channel

```java
// Create and initialize recorder
AgoraMediaRtcRecorder agoraMediaRtcRecorder = agoraService.createMediaRtcRecorder();
// The second parameter indicates whether to enable mixed recording: true=mixed, false=single
agoraMediaRtcRecorder.initialize(agoraService, false);

// Create and register event handler
IAgoraMediaRtcRecorderEventHandler handler = new AgoraMediaRtcRecorderEventHandler();
agoraMediaRtcRecorder.registerRecorderEventHandler(handler);

// Join channel
agoraMediaRtcRecorder.joinChannel(
    "YOUR_TOKEN",     // Channel token; can be null if not enforced
    "YOUR_CHANNEL",   // Channel name
    "0"               // User ID; "0" lets the system assign one
);
```

##### Configure and Start Recording

```java
// Subscribe audio
if (subscribeAllAudio) {
    agoraMediaRtcRecorder.subscribeAllAudio();
} else {
    agoraMediaRtcRecorder.subscribeAudio("USER_ID");
}

// Subscribe video
VideoSubscriptionOptions options = new VideoSubscriptionOptions();
options.setEncodedFrameOnly(false);
options.setType(VideoStreamType.VIDEO_STREAM_HIGH);
if (subscribeAllVideo) {
    agoraMediaRtcRecorder.subscribeAllVideo(options);
} else {
    agoraMediaRtcRecorder.subscribeVideo("USER_ID", options);
}

// Mixed layout (only when mixed is enabled)
if (enableMix) {
    VideoMixingLayout layout = new VideoMixingLayout();
    // configure ...
    agoraMediaRtcRecorder.setVideoMixingLayout(layout);
}

// Recorder configuration
MediaRecorderConfiguration mediaRecorderConfiguration = new MediaRecorderConfiguration();
mediaRecorderConfiguration.setWidth(640);
mediaRecorderConfiguration.setHeight(480);
mediaRecorderConfiguration.setFps(15);
mediaRecorderConfiguration.setMaxDurationMs(60 * 60 * 1000);
mediaRecorderConfiguration.setStoragePath("/path/to/save/recording.mp4");

// Mixed
agoraMediaRtcRecorder.setRecorderConfig(mediaRecorderConfiguration);

// Or single (uncomment if needed)
// agoraMediaRtcRecorder.setRecorderConfigByUid(mediaRecorderConfiguration, "USER_ID");

// Watermark (optional)
WatermarkConfig[] watermarks = new WatermarkConfig[1];
watermarks[0] = new WatermarkConfig();
// configure ...
agoraMediaRtcRecorder.enableAndUpdateVideoWatermarks(watermarks);

// Encryption (optional)
if (enableEncryption) {
    EncryptionConfig encryptionConfig = new EncryptionConfig();
    encryptionConfig.setEncryptionMode(EncryptionMode.AES_128_GCM);
    encryptionConfig.setEncryptionKey("YOUR_ENCRYPTION_KEY");
    agoraMediaRtcRecorder.enableEncryption(true, encryptionConfig);
}

// Start
if (enableMix) {
    agoraMediaRtcRecorder.startRecording();
} else {
    agoraMediaRtcRecorder.startSingleRecordingByUid("USER_ID");
}
```

##### Recording Event Handling

```java
public static class AgoraMediaRtcRecorderEventHandler implements IAgoraMediaRtcRecorderEventHandler {
    @Override
    public void onFirstRemoteAudioDecoded(String channelId, String userId, int elapsed) {
        // Start single audio recording here when first remote audio is decoded
        new Thread() {
            @Override
            public void run() {
                MediaRecorderConfiguration mediaRecorderConfiguration = new MediaRecorderConfiguration();
                // configure ...
                agoraMediaRtcRecorder.setRecorderConfigByUid(mediaRecorderConfiguration, userId);
                agoraMediaRtcRecorder.startSingleRecordingByUid(userId);
            }
        }.start();
    }

    @Override
    public void onFirstRemoteVideoDecoded(String channelId, String userId, int width, int height, int elapsed) {
        // Update mixed layout or start single video recording
        new Thread() {
            @Override
            public void run() {
                if (enableMix) {
                    VideoMixingLayout layout = new VideoMixingLayout();
                    // configure ...
                    agoraMediaRtcRecorder.setVideoMixingLayout(layout);
                } else {
                    MediaRecorderConfiguration mediaRecorderConfiguration = new MediaRecorderConfiguration();
                    // configure ...
                    agoraMediaRtcRecorder.setRecorderConfigByUid(mediaRecorderConfiguration, userId);
                    agoraMediaRtcRecorder.startSingleRecordingByUid(userId);
                }
            }
        }.start();
    }

    @Override
    public void onRecorderStateChanged(String channelId, String userId, Constants.RecorderState state,
            Constants.RecorderReasonCode reason, String fileName) {
        // Observe state changes
    }

    // Other events ...
}
```

##### Stop Recording

```java
// Unsubscribe
agoraMediaRtcRecorder.unsubscribeAllAudio();
agoraMediaRtcRecorder.unsubscribeAllVideo();

// Stop
if (enableMix) {
    agoraMediaRtcRecorder.stopRecording();
} else {
    agoraMediaRtcRecorder.stopSingleRecordingByUid("USER_ID");
}

// Unregister handler
agoraMediaRtcRecorder.unregisterRecorderEventHandler(handler);

// Leave and release
agoraMediaRtcRecorder.leaveChannel();
agoraMediaRtcRecorder.release();

// Release service
agoraService.release();
```

##### Getting Recorded Files

Recorded files are saved depending on recording type:

- Single-stream: MP4 files in the directory under `Examples-Mvn`, e.g. `recorder_result/single/recorder_audio_video_uid_123456_timestamp.mp4`.

- Mixed-stream: a single MP4 in the path specified by `MediaRecorderConfiguration#storagePath`, e.g. `recorder_result/mix/mix_audio_video_water_marks_timestamp.mp4`.

It is recommended to set a unique file path per session (channel name, timestamp, etc.) to avoid overwriting.

More options are available in the `MediaRecorderConfiguration` API.

#### Snapshot Feature (API Example)

```java
// 1) Implement snapshot observer
public static class MySnapshotObserver implements io.agora.recording.IRecorderVideoFrameObserver {
    @Override
    public void onYuvFrameCaptured(String channelId, String userId, io.agora.recording.VideoFrame frame) {
        System.out.println("YUV frame: " + frame.getWidth() + "x" + frame.getHeight() + ", uid=" + userId);
        // TODO: handle YUV data
    }

    @Override
    public void onEncodedFrameReceived(String channelId, String userId, byte[] imageBuffer,
                                       io.agora.recording.EncodedVideoFrameInfo info) {
        System.out.println("Encoded frame: type=" + info.getFrameType() + ", codec=" + info.getCodecType()
                + ", uid=" + userId + ", size=" + (imageBuffer != null ? imageBuffer.length : 0));
        // TODO: persist .h264 / .jpg buffer, or post-process
    }

    @Override
    public void onJPGFileSaved(String channelId, String userId, String filename) {
        System.out.println("JPG saved: " + filename + ", uid=" + userId);
    }
}

// 2) Build capture config and enable
io.agora.recording.RecorderVideoFrameCaptureConfig capCfg = new io.agora.recording.RecorderVideoFrameCaptureConfig();
capCfg.setObserver(new MySnapshotObserver());
capCfg.setVideoFrameType(io.agora.recording.Constants.VideoFrameType.VIDEO_FRAME_TYPE_ENCODED); // ENCODED/YUV/JPG/JPG_FILE
capCfg.setJpgFileStorePath("/path/to/snapshots/"); // JPG_FILE mode only
capCfg.setJpgCaptureIntervalInSec(5); // interval (sec) for JPG/JPG_FILE modes

int ret = agoraMediaRtcRecorder.enableRecorderVideoFrameCapture(true, capCfg);
if (ret != 0) {
    System.err.println("enableRecorderVideoFrameCapture failed: " + ret);
}

// 3) Disable when needed
// agoraMediaRtcRecorder.enableRecorderVideoFrameCapture(false, capCfg);
```

> Note: For encoded frame snapshots (ENCODED), use the initialize overload with `recordEncodedOnly=true`, otherwise you may not achieve pure bitstream writing. Watermarks are not supported in this mode.

```java
// Initialization example: third parameter is recordEncodedOnly
boolean enableMix = false;
agoraMediaRtcRecorder.initialize(agoraService, enableMix, /* recordEncodedOnly = */ true);
// Watermarks are not supported with recordEncodedOnly=true
```

## API Reference

See [API-reference.md](API-reference.md) for detailed SDK APIs.

## Changelog

### v4.4.151 / v4.4.151-aarch64 (2025-09-04)

#### API Changes

- Removed: `AgoraMediaComponentFactory` class. Create recorder via `AgoraService#createMediaRtcRecorder()`.
- Added: `AgoraMediaRtcRecorder#enableRecorderVideoFrameCapture(boolean, RecorderVideoFrameCaptureConfig)` to support ENCODED/YUV/JPG/JPG_FILE snapshots.
- Added: `AgoraMediaRtcRecorder#initialize(AgoraService, boolean, boolean recordEncodedOnly)`; with `recordEncodedOnly=true`, video is not decoded and H.264 bitstream is written directly (no watermarks, no YUV/JPG callbacks).
- Added: `IRecorderVideoFrameObserver` callbacks: `onYuvFrameCaptured`, `onEncodedFrameReceived`, `onJPGFileSaved`.
- Added: `RecorderVideoFrameCaptureConfig` fields `videoFrameType`, `jpgFileStorePath`, `jpgCaptureIntervalInSec`, `observer`.
- Added: `Constants` enums `VideoCodecType`, `VideoFrameType`, `VideoOrientation`.

### v4.4.150.5 (2025-06-30)

#### API Changes

- **Changed**: `unregisterRecorderEventHandle` method in `AgoraMediaRtcRecorder` class renamed to `unregisterRecorderEventHandler` to unify method naming conventions

#### Improvements & Bug Fixes

- **Fixed**: Fixed the thread safety issue of the `setVideoMixingLayout` method in the `AgoraMediaRtcRecorder` class
- **Changed**: The maximum size of a single log file is changed to 1GB

### v4.4.150.4 (2025-06-11)

#### API Changes

- **Added**: Added `renewToken` method to `AgoraMediaRtcRecorder` class to support dynamic channel token renewal, preventing recording interruption due to token expiration
- **Changed**: Renamed `Constants.WaterMaskFitMode` to `Constants.WatermarkFitMode` to fix spelling error and maintain naming consistency

#### Improvements & Bug Fixes

- **Fixed**: Fixed `imagePath` property setting issue in `MixerLayoutConfig` class to ensure proper background image path configuration

### v4.4.150.3 (2025-05-20)

#### API Changes

- **Added**: `onError`、`onTokenPrivilegeWillExpire`、`onTokenPrivilegeDidExpire` callback methods in `IAgoraMediaRtcRecorderEventHandler` for error notifications and token expiration notifications.

### v4.4.150.2 (2025-05-09)

#### API Changes

- **Added**: Added the `setGlobalLocalAccessPoint` function to the `AgoraService` class for configuring the global local access point.

#### Improvements & Optimizations

- **Fixed**: Fixed the callback handling issue when packaging with SpringBot.

### v4.4.150.1 (2025-03-28)

#### API Changes

- **Added**: `onEncryptionError` callback in `IAgoraMediaRtcRecorderEventHandler` for encryption error notifications.
- **Added**: `setAudioVolumeIndicationParameters` method in `AgoraMediaRtcRecorder` to configure the interval for remote user volume callbacks.
- **Refactored**: Optimized the parameter structure of the `onAudioVolumeIndication` callback in `IAgoraMediaRtcRecorderEventHandler`.

#### Improvements & Optimizations

- **Enhanced**: Improved `VideoMixingLayout`, fixed `backgroundColor` property.
- **New Feature**: Added `backgroundImage` property to `VideoMixingLayout` to support setting background images.

### v4.4.150-aarch64 (2025-02-24)

#### API Changes

- **Compatibility**: API compatible with v4.4.150.

#### Improvements & Optimizations

- **Platform**: Initial support for ARM64 architecture.

### v4.4.150 (2025-01-21)

#### API Changes

- **Initial Release**: Basic API structure published.

#### Improvements & Optimizations

- **Performance**: Basic functionality and performance optimizations.

## Other References

Refer to the official documentation website for details: (e.g., <https://docs.agora.io/en/recording/java/landing-page>) (Update link as necessary)
