# Agora Recording Java SDK

中文 | [English](./README.md)

## 目录

- [Agora Recording Java SDK](#agora-recording-java-sdk)
  - [目录](#目录)
  - [简介](#简介)
  - [开发环境要求](#开发环境要求)
    - [硬件环境](#硬件环境)
    - [网络要求](#网络要求)
    - [带宽需求](#带宽需求)
    - [软件环境](#软件环境)
  - [SDK 下载](#sdk-下载)
    - [Maven 下载](#maven-下载)
      - [x86\_64 平台](#x86_64-平台)
      - [arm64 平台](#arm64-平台)
    - [CDN 下载](#cdn-下载)
      - [x86\_64 平台](#x86_64-平台-1)
      - [arm64 平台](#arm64-平台-1)
  - [集成 SDK](#集成-sdk)
    - [1. Maven 集成](#1-maven-集成)
      - [1.1 添加 Maven 依赖](#11-添加-maven-依赖)
      - [1.2 集成 so 库文件](#12-集成-so-库文件)
    - [2. 本地 SDK 集成](#2-本地-sdk-集成)
      - [2.1 SDK 包结构](#21-sdk-包结构)
      - [2.2 集成 JAR 文件](#22-集成-jar-文件)
          - [本地 Maven 仓库方法](#本地-maven-仓库方法)
          - [直接引用方法](#直接引用方法)
      - [2.3 集成 so 库文件](#23-集成-so-库文件)
    - [加载原生库 (.so 文件)](#加载原生库-so-文件)
      - [3.1 提取 so 库文件](#31-提取-so-库文件)
      - [3.2 配置加载路径](#32-配置加载路径)
  - [快速开始](#快速开始)
    - [开通服务](#开通服务)
    - [跑通 Maven 工程](#跑通-maven-工程)
      - [1. 配置密钥](#1-配置密钥)
      - [2. 配置 JAR 和 so 库](#2-配置-jar-和-so-库)
      - [3. 编译打包](#3-编译打包)
      - [4. 运行示例服务](#4-运行示例服务)
      - [5. RESTful API 录制控制](#5-restful-api-录制控制)
      - [6. recordEncodedOnly 和 subscribeEncodedFrameOnly](#6-recordencodedonly-和-subscribeencodedframeonly)
        - [参数含义说明](#参数含义说明)
        - [四种组合模式对照表](#四种组合模式对照表)
        - [代码用法示例](#代码用法示例)
        - [使用场景建议](#使用场景建议)
      - [7. 常见问题](#7-常见问题)
    - [使用命令行录制（Examples-Mvn）](#使用命令行录制examples-mvn)
      - [前提条件](#前提条件)
      - [运行命令](#运行命令)
      - [配置文件与含义（放置于 `Examples-Mvn/src/main/resources/`）](#配置文件与含义放置于-examples-mvnsrcmainresources)
      - [recorder\_json.example 参数说明](#recorder_jsonexample-参数说明)
    - [调用 API 录制](#调用-api-录制)
      - [前提条件](#前提条件-1)
      - [调用 API 实现录制](#调用-api-实现录制)
        - [初始化服务](#初始化服务)
        - [加入频道](#加入频道)
        - [配置和开始录制](#配置和开始录制)
        - [录制事件处理](#录制事件处理)
        - [结束录制](#结束录制)
        - [获取录制文件](#获取录制文件)
      - [截图功能（API 示例）](#截图功能api-示例)
  - [API 参考](#api-参考)
  - [更新日志](#更新日志)
    - [v4.4.151 / v4.4.151-aarch64(2025-09-04)](#v44151--v44151-aarch642025-09-04)
      - [API 变更](#api-变更)
    - [v4.4.150.5（2025-06-30）](#v4415052025-06-30)
      - [API 变更](#api-变更-1)
      - [改进与优化](#改进与优化)
    - [v4.4.150.4（2025-06-11）](#v4415042025-06-11)
      - [API 变更](#api-变更-2)
      - [改进与优化](#改进与优化-1)
    - [v4.4.150.3（2025-05-20）](#v4415032025-05-20)
      - [API 变更](#api-变更-3)
    - [v4.4.150.2（2025-05-09）](#v4415022025-05-09)
      - [API 变更](#api-变更-4)
      - [改进与优化](#改进与优化-2)
    - [v4.4.150.1（2025-03-28）](#v4415012025-03-28)
      - [API 变更](#api-变更-5)
      - [改进与优化](#改进与优化-3)
    - [v4.4.150-aarch64（2025-02-24）](#v44150-aarch642025-02-24)
      - [API 变更](#api-变更-6)
      - [改进与优化](#改进与优化-4)
    - [v4.4.150（2025-01-21）](#v441502025-01-21)
      - [API 变更](#api-变更-7)
      - [改进与优化](#改进与优化-5)
  - [其他参考](#其他参考)

## 简介

Agora Recording Java SDK (v4.4.151) 为您提供了强大的实时音视频录制能力，可无缝集成到 Linux 服务器端的 Java 应用程序中。借助此 SDK，您的服务器可以作为一个哑客户端加入 Agora 频道，实时拉取、订阅和录制频道内的音视频流。录制文件可用于内容存档、审核、分析或其他业务相关的高级功能。

## 开发环境要求

### 硬件环境

- **操作系统**：Ubuntu 14.04+ 或 CentOS 6.5+（推荐 7.0）
- **CPU 架构**：x86-64，arm64

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

## SDK 下载

### Maven 下载

#### x86_64 平台

```xml
<dependency>
    <groupId>io.agora.rtc</groupId>
    <artifactId>linux-recording-java-sdk</artifactId>
    <version>4.4.151</version>
</dependency>
```

#### arm64 平台

```xml
<dependency>
    <groupId>io.agora.rtc</groupId>
    <artifactId>linux-recording-java-sdk</artifactId>
    <version>4.4.151-aarch64</version>
</dependency>
```

### CDN 下载

#### x86_64 平台

[Agora-Linux-Recording-Java-SDK-v4.4.151-x86_64-869516-6f3284e71a-20250904_152151](https://download.agora.io/sdk/release/Agora-Linux-Recording-Java-SDK-v4.4.151-x86_64-869516-6f3284e71a-20250904_152151.zip)

#### arm64 平台

[Agora-Linux-Recording-Java-SDK-v4.4.151-aarch64-869533-8256baf788-20250904_155630](https://download.agora.io/sdk/release/Agora-Linux-Recording-Java-SDK-v4.4.151-aarch64-869533-8256baf788-20250904_155630.zip)

## 集成 SDK

SDK 集成有两种方式：通过 Maven 集成和本地 SDK 集成。

### 1. Maven 集成

Maven 集成是最简单的方式，可以自动管理 Java 依赖关系。

#### 1.1 添加 Maven 依赖

在项目的 `pom.xml` 文件中添加以下依赖：

```xml
<!-- x86_64 平台 -->
<dependency>
    <groupId>io.agora.rtc</groupId>
    <artifactId>linux-recording-java-sdk</artifactId>
    <version>4.4.151</version>
</dependency>

<!-- arm64 平台 -->
<dependency>
    <groupId>io.agora.rtc</groupId>
    <artifactId>linux-recording-java-sdk</artifactId>
    <version>4.4.151-aarch64</version>
</dependency>
```

#### 1.2 集成 so 库文件

Maven 依赖包含了所需的 JAR 文件，但仍需手动处理 `.so` 库文件才能运行。请参考下面的 **加载原生库 (.so 文件)** 部分。

### 2. 本地 SDK 集成

本地 SDK 是一个包含所有必要文件的完整包，适合需要更灵活控制的场景。

#### 2.1 SDK 包结构

从官网下载的 SDK 包（zip 格式）包含以下内容：

- **doc/** - JavaDoc 文档，详细的 API 说明
- **examples/** - 示例代码和项目
- **sdk/** - 核心 SDK 文件
  - `agora-recording-sdk.jar` - Java 类库
  - `agora-recording-sdk-javadoc.jar` - JavaDoc 文档

#### 2.2 集成 JAR 文件

你可以通过两种方式集成 JAR 文件：

###### 本地 Maven 仓库方法

方法一：只安装 SDK JAR

```sh
mvn install:install-file \
  -Dfile=sdk/agora-recording-sdk.jar \
  -DgroupId=io.agora.rtc \
  -DartifactId=linux-recording-java-sdk \
  -Dversion=4.4.151 \
  -Dpackaging=jar \
  -DgeneratePom=true
```

方法二：同时安装 SDK JAR 和 JavaDoc JAR

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

安装后，在 `pom.xml` 中添加依赖：

```xml
<dependency>
    <groupId>io.agora.rtc</groupId>
    <artifactId>linux-recording-java-sdk</artifactId>
    <version>4.4.151</version>
</dependency>
```

###### 直接引用方法

1. 将 JAR 文件复制到项目的 `libs` 目录：

   ```sh
   mkdir -p libs
   cp sdk/agora-recording-sdk.jar libs/
   cp sdk/agora-recording-sdk-javadoc.jar libs/  # 可选，用于 IDE 支持
   ```

2. 在 Java 项目中添加 classpath 引用：

   ```sh
   # 使用 SDK JAR
   java -cp .:libs/agora-recording-sdk.jar 你的主类

   # 在 IDE 中配置 JavaDoc（常见的 IDE 如 IntelliJ IDEA 或 Eclipse 支持直接关联 JavaDoc JAR）
   ```

#### 2.3 集成 so 库文件

下载的 SDK 包中已经包含了 `.so` 文件。你需要确保 Java 程序运行时能够找到这些文件。请参考下面的 **加载原生库 (.so 文件)** 部分。

### 加载原生库 (.so 文件)

Agora Linux Recording Java SDK 依赖于底层的 C++ 原生库（`.so` 文件）。无论是通过 Maven 集成还是本地集成，都需要确保 Java 虚拟机 (JVM) 在运行时能够找到并加载这些库。

#### 3.1 提取 so 库文件

`.so` 文件包含在 `agora-recording-sdk.jar` 或 `linux-recording-java-sdk-x.x.x.x.jar` 文件内部。你需要先将它们提取出来：

1. 在你的项目或部署目录下创建一个用于存放库文件的目录，例如 `libs`：

   ```sh
   mkdir -p libs
   cd libs
   ```

2. 使用 `jar` 命令从 SDK 的 JAR 文件中提取内容（假设 JAR 文件位于 `libs` 目录下或 Maven 缓存中）：

   ```sh
   # 如果使用本地集成方式，JAR 文件通常在 libs 目录下
   jar xvf agora-recording-sdk.jar

   # 如果使用 Maven 集成方式，JAR 文件在 Maven 缓存中，例如：
   # jar xvf ~/.m2/repository/io/agora/rtc/linux-recording-java-sdk/4.4.151/linux-recording-java-sdk-4.4.151.jar
   ```

3. 提取后，`libs` 目录下会生成 `native/linux/x86_64` 子目录，其中包含所需的 `.so` 文件：

   ```
   libs/
   ├── agora-recording-sdk.jar (或者空的，如果仅用于提取)
   ├── io/          # Java 的 class 类所在，无需关注
   ├── META-INF/    # JAR 文件和应用程序相关的元数据，无需关注
   └── native/      # 对应平台的 so 库文件
       └── linux/
           └── x86_64/   # x86_64 平台 so 库
               ├── libagora_rtc_sdk.so
               ├── libagora-fdkaac.so
               ├── libaosl.so
               └── librecording.so
           └── aarch64/   # arm64 平台 so 库 (如果存在)
               ├── libagora_rtc_sdk.so
               ├── libagora-fdkaac.so
               ├── libaosl.so
               └── librecording.so
   ```

#### 3.2 配置加载路径

有两种主要方法让 JVM 找到 `.so` 文件：

**方法一：通过设置环境变量 `LD_LIBRARY_PATH` (推荐)**

这是最可靠的方式，特别是在 `.so` 文件之间存在依赖关系时。

```sh
# 确定你的 .so 文件所在的目录，假设在 ./libs/native/linux/x86_64
LIB_DIR=$(pwd)/libs/native/linux/x86_64

# 设置 LD_LIBRARY_PATH 环境变量，将库目录添加到现有路径的前面
export LD_LIBRARY_PATH=$LIB_DIR:$LD_LIBRARY_PATH

# 运行你的 Java 应用
java -jar 你的应用.jar
# 或者使用 classpath
# java -cp "你的classpath" 你的主类
```

**方法二：通过 JVM 参数 `-Djava.library.path`**

这种方法直接告诉 JVM 在哪里查找库文件。

```sh
# 确定你的 .so 文件所在的目录，假设在 ./libs/native/linux/x86_64
LIB_DIR=$(pwd)/libs/native/linux/x86_64

# 运行 Java 应用，并通过 -D 参数指定库路径
java -Djava.library.path=$LIB_DIR -jar 你的应用.jar
# 或者使用 classpath
# java -Djava.library.path=$LIB_DIR -cp "你的classpath" 你的主类
```

> **注意**：
>
> - 推荐使用方法一 (`LD_LIBRARY_PATH`)，因为它能更好地处理库之间的依赖。如果仅使用 `-Djava.library.path`，有时可能因为库找不到其依赖的其他库而加载失败。
> - 确保 `$LIB_DIR` 指向包含 `libagora_rtc_sdk.so` 等文件的 **确切目录**。
> - 你可以将设置环境变量的命令放入启动脚本中，以便每次运行应用时自动配置。

参考以下脚本示例，它结合了两种方法，并设置了 classpath：

```sh
#!/bin/bash
# 获取当前脚本所在目录的绝对路径
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# 确定 so 库文件路径 (假设在脚本目录下的 libs/native/linux/x86_64)
LIB_PATH="$SCRIPT_DIR/libs/native/linux/x86_64"
# SDK JAR 路径 (假设在脚本目录下的 libs)
SDK_JAR="$SCRIPT_DIR/libs/agora-recording-sdk.jar"
# 你的应用主类
MAIN_CLASS="你的主类"
# 你的应用的其他依赖 classpath (如果有)
APP_CP="你的其他classpath"

# 设置库路径环境变量
export LD_LIBRARY_PATH=$LIB_PATH:$LD_LIBRARY_PATH

# 组合 classpath
CLASSPATH=".:$SDK_JAR:$APP_CP" # '.' 表示当前目录

# 执行 Java 程序
# 同时使用 LD_LIBRARY_PATH 和 -Djava.library.path 以确保兼容性
java -Djava.library.path=$LIB_PATH -cp "$CLASSPATH" $MAIN_CLASS
```

## 快速开始

### 开通服务

参考 [官网开通服务](https://doc.shengwang.cn/doc/recording/java/get-started/enable-service)

### 跑通 Maven 工程

本 SDK 提供了基于 Spring Boot 的 Maven 示例工程，方便你快速验证和二次开发。以下为跑通 `Examples-Mvn` 工程的基本流程：

#### 1. 配置密钥

在 `Examples-Mvn` 目录下创建 `.keys` 文件，内容如下（请替换为你的实际信息）：

```
APP_ID=你的AppId
TOKEN=你的Token
```

#### 2. 配置 JAR 和 so 库

- 配置 JAR（两种方式二选一）：
  - 使用线上 Maven 版本：编辑 `Examples-Mvn/pom.xml`，按平台选择版本（x86_64 使用 `4.4.151`，arm64 使用 `4.4.151-aarch64`）：
    ```xml
    <dependency>
        <groupId>io.agora.rtc</groupId>
        <artifactId>linux-recording-java-sdk</artifactId>
        <version>4.4.151</version>
    </dependency>
    <!-- arm64 平台请将版本替换为 4.4.151-aarch64 -->
    ```
  - 使用本地版本：在 `Examples-Mvn` 目录执行脚本将本地 JAR 安装到本地 Maven 仓库（脚本读取 `libs/agora-recording-sdk.jar` 及其 javadoc）：
    ```sh
    cd Examples-Mvn
    ./build_install_local_maven.sh
    ```

- 准备 so 库：确保 `libs/native/linux/x86_64/`（或 `aarch64/`）目录下包含必要的 so 文件（如 `libagora_rtc_sdk.so`、`librecording.so` 等）。

#### 3. 编译打包

进入 `Examples-Mvn` 目录，执行：

```sh
./build.sh
```

编译成功后，会在 `target/` 目录下生成 `agora-example.jar`。

#### 4. 运行示例服务

在 `Examples-Mvn` 目录下执行：

```sh
./build.sh start
```

- 该命令会启动 Spring Boot 服务，监听 18080 端口。
- 如需更换端口，可修改 `-Dserver.port` 参数。

#### 5. RESTful API 录制控制

- 启动录制：

  ```
  http://<服务器IP>:18080/api/recording/start?configFileName=mix_stream_recorder_audio_video_water_marks.json
  ```

- 停止录制：

  ```
  http://<服务器IP>:18080/api/recording/stop?taskId=<任务ID>
  ```

> 录制配置文件需放在 `Examples-Mvn/src/main/resources/` 目录下。

#### 6. recordEncodedOnly 和 subscribeEncodedFrameOnly

这两个参数分别控制录制文件的写入方式和订阅视频流的处理方式：

- **recordEncodedOnly**：控制录制文件是否直接将编码数据写入 MP4 文件
- **subscribeEncodedFrameOnly**：控制订阅的视频流是否进行解码处理

##### 参数含义说明

| 参数                      | 值    | 含义                | 说明                                                                      |
| ------------------------- | ----- | ------------------- | ------------------------------------------------------------------------- |
| recordEncodedOnly         | true  | 编码数据直接写入MP4 | 将 H.264/H.265 编码数据直接写入 MP4 文件，不解码，性能高但无法添加水印    |
| recordEncodedOnly         | false | 解码后重新编码写入  | 先解码再重新编码写入 MP4，支持水印叠加，但消耗更多 CPU 资源               |
| subscribeEncodedFrameOnly | true  | 订阅流不解码        | 订阅时不对视频流进行解码，直接获取编码数据，适用于编码帧截图              |
| subscribeEncodedFrameOnly | false | 订阅流解码          | 订阅时对视频流进行解码，可获取 YUV 原始数据，适用于需要处理原始视频的场景 |

##### 四种组合模式对照表

| recordEncodedOnly | subscribeEncodedFrameOnly | 模式说明     | 特点                              | 适用场景                                          |
| ----------------- | ------------------------- | ------------ | --------------------------------- | ------------------------------------------------- |
| false             | false                     | **标准模式** | 订阅流解码 + 解码后重新编码录制   | 需要水印、视频处理、YUV截图的标准录制场景         |
| false             | true                      | **混合模式** | 订阅流不解码 + 解码后重新编码录制 | 需要水印功能，同时进行编码帧截图的场景            |
| true              | false                     | **性能模式** | 订阅流解码 + 编码数据直接写入     | 高性能录制，需要YUV处理但不需要水印               |
| true              | true                      | **极速模式** | 订阅流不解码 + 编码数据直接写入   | 最高性能录制，仅需编码帧截图，不支持水印和YUV处理 |

##### 代码用法示例

**1. 设置 recordEncodedOnly（初始化录制器时）**

```java
// 创建录制器
AgoraMediaRtcRecorder agoraMediaRtcRecorder = agoraService.createMediaRtcRecorder();

// 方式一：使用默认值（recordEncodedOnly = false）
boolean enableMix = false; // 是否合流
agoraMediaRtcRecorder.initialize(agoraService, enableMix);

// 方式二：明确设置 recordEncodedOnly
boolean enableMix = false; // 是否合流
boolean recordEncodedOnly = true; // 仅录制编码帧，提高性能
agoraMediaRtcRecorder.initialize(agoraService, enableMix, recordEncodedOnly);
```

**2. 设置 subscribeEncodedFrameOnly（订阅视频时）**

```java
// 创建视频订阅选项
VideoSubscriptionOptions options = new VideoSubscriptionOptions();

// 设置是否仅订阅编码帧
boolean subscribeEncodedFrameOnly = true; // 仅订阅编码帧，用于编码帧截图
options.setEncodedFrameOnly(subscribeEncodedFrameOnly);
options.setType(VideoStreamType.VIDEO_STREAM_HIGH);

// 订阅视频
if (需要订阅所有视频) {
    agoraMediaRtcRecorder.subscribeAllVideo(options);
} else {
    agoraMediaRtcRecorder.subscribeVideo("用户ID", options);
}
```

**3. 配置文件中的设置**

在 JSON 配置文件中设置这两个参数：

```json
{
    "recordEncodedOnly": true,              // 仅录制编码帧
    "subscribeEncodedFrameOnly": true,      // 仅订阅编码帧
    "videoFrameCaptureType": 0,             // 0=ENCODED（编码帧截图）
    "enableRecording": true,
    "enableCapture": true
}
```

##### 使用场景建议

- **标准录制场景**：`recordEncodedOnly=false` + `subscribeEncodedFrameOnly=false`
  - 支持水印、视频处理、YUV截图等完整功能
  - CPU消耗较高，适合功能完整性要求高的场景

- **编码帧截图 + 水印录制**：`recordEncodedOnly=false` + `subscribeEncodedFrameOnly=true`
  - 既能添加水印录制，又能进行编码帧截图
  - 平衡性能和功能需求

- **高性能录制**：`recordEncodedOnly=true` + `subscribeEncodedFrameOnly=false`
  - 录制性能高，支持YUV处理，但不支持水印
  - 适合需要YUV数据处理但对录制性能要求高的场景

- **极速录制**：`recordEncodedOnly=true` + `subscribeEncodedFrameOnly=true`
  - 最高性能，最低CPU消耗
  - 仅支持编码帧截图，不支持水印和YUV处理
  - 适合大规模并发录制场景


#### 7. 常见问题

- 若服务无法启动，请检查 so 文件路径、.keys 文件内容及端口占用。
- 录制无输出时，请检查频道内有无活跃用户、AppId/Token/频道名是否正确。

### 使用命令行录制（Examples-Mvn）

#### 前提条件

开始前请确保你已经完成 SDK 集成、`.keys` 配置与 `.so` 库准备（见“跑通 Maven 工程”）。

#### 运行命令

在 `Examples-Mvn` 目录下执行：

```sh
./build.sh cli <configFileName>
# 示例：
./build.sh cli capture_type_encoded_frame_mix_stream.json
```

程序启动后，在终端输入 `1` 并回车可停止并退出。

#### 配置文件与含义（放置于 `Examples-Mvn/src/main/resources/`）

- 合流录制：
  - `mix_stream_recorder_audio_video.json`：合流录制音视频。
  - `mix_stream_recorder_audio.json`：合流仅录音频。
  - `mix_stream_recorder_video.json`：合流仅录视频。
  - `mix_stream_recorder_audio_video_encryption.json`：合流录制，启用加密。
  - `mix_stream_recorder_audio_video_string_uid.json`：合流录制，字符串 UID。
  - `mix_stream_recorder_audio_video_water_marks.json`：合流录制，添加水印。
  - `mix_stream_recorder_audio_video_water_marks_bg.json`：合流录制，水印 + 背景。
  - `mix_stream_recorder_audio_video_water_marks_recover.json`：合流录制，生成 h264/aac 便于断电恢复 MP4。

- 单流录制：
  - `single_stream_recorder_audio_video.json`：单流录制音视频。
  - `single_stream_recorder_audio.json`：单流仅录音频。
  - `single_stream_recorder_video.json`：单流仅录视频。
  - `single_stream_recorder_audio_video_water_marks.json`：单流录制，添加水印。

 - 截图功能示例：
  - `capture_type_encoded_frame_mix_stream.json`：合流录制 + 编码帧截图（H.264 等），输出 MP4 与截图数据；`videoFrameCaptureType=ENCODED`，`isMix=true`。
  - `capture_type_yuv_frame_single_stream.json`：单流录制 + YUV 帧截图，输出单流 MP4 与 YUV 截图；`videoFrameCaptureType=YUV`，`isMix=false`。
  - `capture_type_encoded_frame.json`：仅编码帧截图（不录制），合流场景；`videoFrameCaptureType=ENCODED`，`enableRecording=false`。
  - `capture_type_yuv_frame.json`：仅 YUV 帧截图（不录制），合流场景；`videoFrameCaptureType=YUV`，`enableRecording=false`。
  - `capture_type_jpg_frame.json`：仅 JPG 帧截图（内存回调并保存），合流场景；`videoFrameCaptureType=JPG_FRAME`，`enableRecording=false`。
  - `capture_type_jpg_file.json`：JPG 文件直存（SDK 直接输出 JPG 文件），合流场景；`videoFrameCaptureType=JPG_FILE`，`enableRecording=false`。

> 提示：`recorderPath` 为输出 MP4 路径（合流为文件，单流为单个uid的文件开头），`capturePath` 为截图输出前缀或目录。`videoFrameCaptureType` 枚举参见 `Constants.VideoFrameCaptureType`。

#### recorder_json.example 参数说明

| 参数                           | 类型     | 说明                                                                                                                                                   |
| ------------------------------ | -------- | ------------------------------------------------------------------------------------------------------------------------------------------------------ |
| appId                          | String   | 项目 App ID（也可在 `.keys` 中配置优先覆盖）。                                                                                                         |
| token                          | String   | 频道 Token；若未开启 Token 校验可留空（可在 `.keys` 中配置优先覆盖）。                                                                                 |
| channelName                    | String   | 频道名，需与客户端一致。                                                                                                                               |
| useStringUid                   | Boolean  | 是否使用字符串 UID。false 表示数字 UID。                                                                                                               |
| useCloudProxy                  | Boolean  | 是否启用云代理。                                                                                                                                       |
| userId                         | String   | 录制端用户 ID；为 "0" 时由系统自动分配。                                                                                                               |
| subAllAudio                    | Boolean  | 是否订阅所有音频；为 false 时使用 `subAudioUserList` 指定。                                                                                            |
| subAudioUserList               | String[] | 需订阅音频的用户 ID 列表（`subAllAudio=false` 生效）。                                                                                                 |
| subAllVideo                    | Boolean  | 是否订阅所有视频；为 false 时使用 `subVideoUserList` 指定。                                                                                            |
| subVideoUserList               | String[] | 需订阅视频的用户 ID 列表（`subAllVideo=false` 生效）。                                                                                                 |
| subStreamType                  | String   | 订阅流类型：`high`（大流）、`low`（小流）。                                                                                                            |
| enableRecording                | Boolean  | 是否进行 MP4 录制。false 时可仅进行截图。                                                                                                              |
| enableCapture                  | Boolean  | 是否启用截图能力。                                                                                                                                     |
| videoFrameCaptureType          | Integer  | 截图类型：0=ENCODED（编码帧），1=YUV，2=JPG_FRAME（内存回调保存），3=JPG_FILE（SDK直存JPG）。对应 `Constants.VideoFrameType`/`VideoFrameCaptureType`。 |
| jpgCaptureIntervalInSec        | Integer  | JPG 截图间隔时间（秒，默认：5）。仅当 `videoFrameCaptureType=3`（JPG_FILE）时生效。                                                                    |
| isMix                          | Boolean  | 是否合流录制；false 为单流录制。                                                                                                                       |
| recordEncodedOnly              | Boolean  | 是否仅录制编码帧。为 true 时，录制时直接将 H.264/H.265 码流写入 MP4 文件，不进行解码（默认：false）。                                                  |
| subscribeEncodedFrameOnly      | Boolean  | 是否仅订阅编码帧。为 true 时，订阅时仅订阅编码帧，不进行解码（默认：false）。                                                                          |
| backgroundColor                | Long     | 合流背景色（0xRRGGBB，需转 long）。`isMix=true` 时可用。                                                                                               |
| backgroundImage                | String   | 合流背景图（PNG/JPG）。与 `backgroundColor` 同时设置时，背景图优先生效。                                                                               |
| layoutMode                     | String   | 合流布局：`default`、`bestfit`、`vertical`。                                                                                                           |
| maxResolutionUid               | String   | `vertical` 布局下显示最大分辨率的 UID。                                                                                                                |
| recorderStreamType             | String   | 录制类型：`audio_only`、`video_only`、`both`。                                                                                                         |
| recorderPath                   | String   | 输出路径：合流为文件路径；单流为目录（每 UID 生成独立 MP4）。需确保父目录存在。                                                                        |
| capturePath                    | String   | 截图输出前缀或目录：JPG_FILE 为目录；其他类型为文件前缀。                                                                                              |
| maxDuration                    | Integer  | 录制时长（秒）。到时自动停止。                                                                                                                         |
| recoverFile                    | Boolean  | 是否同时写 h264/aac 以便异常后恢复 MP4（仅录制相关）。                                                                                                 |
| audio.sampleRate               | Integer  | 音频采样率（Hz）。                                                                                                                                     |
| audio.numOfChannels            | Integer  | 音频通道数。                                                                                                                                           |
| video.width                    | Integer  | 视频宽度（像素）。                                                                                                                                     |
| video.height                   | Integer  | 视频高度（像素）。                                                                                                                                     |
| video.fps                      | Integer  | 视频帧率（fps）。                                                                                                                                      |
| waterMark[].type               | String   | 水印类型：`litera`（字幕）、`time`（时间戳）、`picture`（图片）。                                                                                      |
| waterMark[].litera             | String   | 字幕水印内容（type=litera）。                                                                                                                          |
| waterMark[].fontFilePath       | String   | 字体文件路径（litera/time）。                                                                                                                          |
| waterMark[].fontSize           | Integer  | 字体大小。                                                                                                                                             |
| waterMark[].x/y/width/height   | Integer  | 水印矩形区域位置与尺寸。                                                                                                                               |
| waterMark[].zorder             | Integer  | 水印图层顺序。                                                                                                                                         |
| waterMark[].imgUrl             | String   | 图片水印路径（type=picture）。                                                                                                                         |
| encryption.mode                | String   | 加密类型：`AES_128_XTS`、`AES_128_ECB`、`AES_256_XTS`、`SM4_128_ECB`、`AES_128_GCM`、`AES_256_GCM`、`AES_128_GCM2`、`AES_256_GCM2`。                   |
| encryption.key                 | String   | 加密密钥。                                                                                                                                             |
| encryption.salt                | String   | 加密盐，32 字符（部分模式需要/可选）。                                                                                                                 |
| rotation[].uid                 | String   | 需旋转视频的 UID。                                                                                                                                     |
| rotation[].degree              | Integer  | 旋转角度：0、90、180、270。                                                                                                                            |
| stressTest.enable              | Boolean  | 是否启用压力测试模式（默认：false）。                                                                                                                  |
| stressTest.enableSingleChannel | Boolean  | 压力测试中是否所有线程使用单一频道（默认：false）。为 false 时，每个线程使用独立频道。                                                                 |
| stressTest.threadNum           | Integer  | 压力测试并发线程数（默认：1）。                                                                                                                        |
| stressTest.testTime            | Integer  | 压力测试总时长，单位秒（默认：10）。                                                                                                                   |
| stressTest.oneTestTime         | Integer  | 单次录制会话时长，单位秒（默认：3）。                                                                                                                  |
| stressTest.sleepTime           | Integer  | 录制会话间隔时间，单位秒（默认：1）。                                                                                                                  |

### 调用 API 录制

#### 前提条件

开始前请确保你已经完成录制 SDK 的环境准备和集成工作，包括配置 jar 和对应平台的 so。

#### 调用 API 实现录制

以下示例代码基于 `Examples-Mvn` 目录中的实际示例项目，展示了如何使用录制 SDK API 进行录制。

##### 初始化服务

```java
// 创建 AgoraService 实例
AgoraService agoraService = new AgoraService();

// 配置本地代理，配置必须放在 initialize 之前
LocalAccessPointConfiguration localAccessPointConfig = new LocalAccessPointConfiguration();
localAccessPointConfig.setMode(Constants.LocalProxyMode.LocalOnly);
localAccessPointConfig.setIpList(new String[] { "10.xx.xx.xx" });
localAccessPointConfig.setIpListSize(1);
localAccessPointConfig.setVerifyDomainName("ap.xxx.agora.local");
int setGlobalLocalAccessPointRet = agoraService.setGlobalLocalAccessPoint(localAccessPointConfig);

// 创建并配置服务配置对象
AgoraServiceConfiguration config = new AgoraServiceConfiguration();
config.setEnableAudioDevice(false);    // 是否启用音频设备（通常设为 false）
config.setEnableAudioProcessor(true);  // 启用音频处理
config.setEnableVideo(true);           // 启用视频功能
config.setAppId("您的APPID");           // 设置您的 App ID
config.setUseStringUid(false);         // 是否使用字符串 UID
agoraService.initialize(config);       // 初始化服务

// 可选：设置云代理
AgoraParameter parameter = agoraService.getAgoraParameter();
if (parameter != null) {
    parameter.setBool("rtc.enable_proxy", true);
}
```

##### 加入频道

```java
// 创建并初始化录制器
AgoraMediaRtcRecorder agoraMediaRtcRecorder = agoraService.createMediaRtcRecorder();
// 第二个参数表示是否启用混流录制：true=混流，false=单流
agoraMediaRtcRecorder.initialize(agoraService, false);

// 创建并注册事件处理器
IAgoraMediaRtcRecorderEventHandler handler = new AgoraMediaRtcRecorderEventHandler();
agoraMediaRtcRecorder.registerRecorderEventHandler(handler);

// 加入频道
agoraMediaRtcRecorder.joinChannel(
    "您的Token",        // 频道 Token，如不启用 Token 验证可为 null
    "您的频道名",       // 频道名称
    "0"                // 用户 ID，如果设置为 0 将由系统自动分配
);
```

##### 配置和开始录制

```java
// 订阅音频流
if (需要订阅所有音频) {
    agoraMediaRtcRecorder.subscribeAllAudio();
} else {
    // 仅订阅特定用户的音频
    agoraMediaRtcRecorder.subscribeAudio("用户ID");
}

// 订阅视频流
VideoSubscriptionOptions options = new VideoSubscriptionOptions();
options.setEncodedFrameOnly(false);
options.setType(VideoStreamType.VIDEO_STREAM_HIGH); // 可选：VIDEO_STREAM_LOW
if (需要订阅所有视频) {
    agoraMediaRtcRecorder.subscribeAllVideo(options);
} else {
    // 仅订阅特定用户的视频
    agoraMediaRtcRecorder.subscribeVideo("用户ID", options);
}

// 配置混流布局（仅在混流模式下需要）
if (启用混流) {
    VideoMixingLayout layout = new VideoMixingLayout();
    // 配置布局参数...
    agoraMediaRtcRecorder.setVideoMixingLayout(layout);
}

// 配置录制参数
MediaRecorderConfiguration mediaRecorderConfiguration = new MediaRecorderConfiguration();
mediaRecorderConfiguration.setWidth(640);       // 设置录制视频宽度
mediaRecorderConfiguration.setHeight(480);      // 设置录制视频高度
mediaRecorderConfiguration.setFps(15);          // 设置录制帧率
mediaRecorderConfiguration.setMaxDurationMs(60 * 60 * 1000); // 最大录制时长，单位毫秒
mediaRecorderConfiguration.setStoragePath("/path/to/save/recording.mp4"); // 录制文件保存路径

// 合流录制配置
agoraMediaRtcRecorder.setRecorderConfig(mediaRecorderConfiguration);

// 或者单流录制配置
//agoraMediaRtcRecorder.setRecorderConfigByUid(mediaRecorderConfiguration, "用户ID");

// 添加水印（可选）
WatermarkConfig[] watermarks = new WatermarkConfig[1];
watermarks[0] = new WatermarkConfig();
// 配置水印参数...
agoraMediaRtcRecorder.enableAndUpdateVideoWatermarks(watermarks);

// 启用加密（可选）
if (需要加密) {
    EncryptionConfig encryptionConfig = new EncryptionConfig();
    encryptionConfig.setEncryptionMode(EncryptionMode.AES_128_GCM); // 设置加密模式
    encryptionConfig.setEncryptionKey("加密密钥");
    agoraMediaRtcRecorder.enableEncryption(true, encryptionConfig);
}

// 开始录制
if (启用混流) {
    agoraMediaRtcRecorder.startRecording();
} else {
    // 单流录制
    agoraMediaRtcRecorder.startSingleRecordingByUid("用户ID");
}
```

##### 录制事件处理

```java
public static class AgoraMediaRtcRecorderEventHandler implements IAgoraMediaRtcRecorderEventHandler {
    @Override
    public void onFirstRemoteAudioDecoded(String channelId, String userId, int elapsed) {
        // 首次检测到远程音频解码时触发，可在此开始单流音频录制
        new Thread() {
            @Override
            public void run() {
                MediaRecorderConfiguration mediaRecorderConfiguration = new MediaRecorderConfiguration();
                // 配置录制参数...
                agoraMediaRtcRecorder.setRecorderConfigByUid(mediaRecorderConfiguration, userId);
                agoraMediaRtcRecorder.startSingleRecordingByUid(userId);
            }
        }.start();
    }

    @Override
    public void onFirstRemoteVideoDecoded(String channelId, String userId, int width, int height, int elapsed) {
        // 首次检测到远程视频解码时触发，可在此更新混流布局或开始单流视频录制
        new Thread() {
            @Override
            public void run() {
                if (启用混流) {
                    VideoMixingLayout layout = new VideoMixingLayout();
                    // 配置混流布局...
                    agoraMediaRtcRecorder.setVideoMixingLayout(layout);
                } else {
                    MediaRecorderConfiguration mediaRecorderConfiguration = new MediaRecorderConfiguration();
                    // 配置录制参数...
                    agoraMediaRtcRecorder.setRecorderConfigByUid(mediaRecorderConfiguration, userId);
                    agoraMediaRtcRecorder.startSingleRecordingByUid(userId);
                }
            }
        }.start();
    }

    @Override
    public void onRecorderStateChanged(String channelId, String userId, Constants.RecorderState state,
            Constants.RecorderReasonCode reason, String fileName) {
        // 录制状态变化回调，可据此了解录制进程
    }

    // 其他事件处理方法...
}
```

##### 结束录制

```java
// 取消订阅流
agoraMediaRtcRecorder.unsubscribeAllAudio();
agoraMediaRtcRecorder.unsubscribeAllVideo();

// 停止录制
if (启用混流) {
    agoraMediaRtcRecorder.stopRecording();
} else {
    // 停止单流录制
    agoraMediaRtcRecorder.stopSingleRecordingByUid("用户ID");
}

// 注销事件处理器
agoraMediaRtcRecorder.unregisterRecorderEventHandler(handler);

// 离开频道并释放资源
agoraMediaRtcRecorder.leaveChannel();
agoraMediaRtcRecorder.release();

// 释放服务
agoraService.release();
```

##### 获取录制文件

录制文件将根据录制类型保存在不同位置：

- **单流录制**：在 `Examples-Mvn` 目录下指定文件夹下生成单流录制的 mp4 文件，如 `recorder_result/single/recorder_audio_video_uid_123456_timestamp.mp4`。

- **合流录制**：在 `Examples-Mvn` 目录下生成合流的录制 mp4 文件，文件名是通过 `MediaRecorderConfiguration` 对象的 `storagePath` 参数配置的，如 `recorder_result/mix/mix_audio_video_water_marks_timestamp.mp4`。

在实际应用中，建议为每次录制设置唯一的文件路径，可以使用频道名、时间戳等作为文件名的一部分，以避免文件覆盖。

更多录制选项和高级功能，请参考 `MediaRecorderConfiguration` 类的 API 文档。

#### 截图功能（API 示例）

```java
// 1) 实现截图回调观察者（任意需要的方法即可）
public static class MySnapshotObserver implements io.agora.recording.IRecorderVideoFrameObserver {
    @Override
    public void onYuvFrameCaptured(String channelId, String userId, io.agora.recording.VideoFrame frame) {
        System.out.println("YUV frame: " + frame.getWidth() + "x" + frame.getHeight() + ", uid=" + userId);
        // TODO: 处理 YUV 数据，如写入文件/转码等
    }

    @Override
    public void onEncodedFrameReceived(String channelId, String userId, byte[] imageBuffer,
                                       io.agora.recording.EncodedVideoFrameInfo info) {
        System.out.println("Encoded frame: type=" + info.getFrameType() + ", codec=" + info.getCodecType()
                + ", uid=" + userId + ", size=" + (imageBuffer != null ? imageBuffer.length : 0));
        // TODO: 落盘 .h264 / .jpg buffer，或送入后处理
    }

    @Override
    public void onJPGFileSaved(String channelId, String userId, String filename) {
        System.out.println("JPG saved: " + filename + ", uid=" + userId);
    }
}

// 2) 组装截图配置并启用
io.agora.recording.RecorderVideoFrameCaptureConfig capCfg = new io.agora.recording.RecorderVideoFrameCaptureConfig();
capCfg.setObserver(new MySnapshotObserver());
capCfg.setVideoFrameType(io.agora.recording.Constants.VideoFrameType.VIDEO_FRAME_TYPE_ENCODED); // ENCODED/YUV/JPG/JPG_FILE
capCfg.setJpgFileStorePath("/path/to/snapshots/"); // 仅 JPG_FILE 模式生效（保存目录）
capCfg.setJpgCaptureIntervalInSec(5); // JPG/JPG_FILE 模式的抓拍间隔（秒）

// 启用截图功能；若只截图不录制，可先 setRecorderConfig 决定录制输出或关闭 enableRecording
int ret = agoraMediaRtcRecorder.enableRecorderVideoFrameCapture(true, capCfg);
if (ret != 0) {
    System.err.println("enableRecorderVideoFrameCapture failed: " + ret);
}

// 3) 根据需要关闭
// agoraMediaRtcRecorder.enableRecorderVideoFrameCapture(false, capCfg);
```

> 注意：若进行"编码帧截图"（ENCODED），需要进行以下设置：
> 1. 在视频订阅选项中设置 `setEncodedFrameOnly(true)`，这是截屏编码帧的必要条件

```java
// 设置视频订阅选项以支持编码帧截图
VideoSubscriptionOptions options = new VideoSubscriptionOptions();
boolean encodedFrameOnly = true; // 设置为 true 才能截屏编码帧
options.setEncodedFrameOnly(encodedFrameOnly);
options.setType(Utils.convertToVideoStreamType(subStreamType));
```


## API 参考

有关 SDK API 的详细说明，请参考 [API-reference.zh.md](API-reference.zh.md) 文档，每个类和方法都提供了详细的参数说明、返回值解释。

## 更新日志

### v4.4.151 / v4.4.151-aarch64(2025-09-04)

#### API 变更

- **移除**：移除`AgoraMediaComponentFactory` 类，录制器创建通过 `AgoraService#createMediaRtcRecorder()` 方法创建。
- **新增**：`AgoraMediaRtcRecorder` 类新增 `enableRecorderVideoFrameCapture(boolean, RecorderVideoFrameCaptureConfig)` 方法，支持 ENCODED/YUV/JPG/JPG_FILE 帧捕获。
- **新增**：`AgoraMediaRtcRecorder` 类重载 `initialize(AgoraService, boolean, boolean recordEncodedOnly)` 方法；当 `recordEncodedOnly=true` 时，不解码直写 H.264 / H.265 码流到MP4文件。
- **新增**：`IRecorderVideoFrameObserver` 回调：`onYuvFrameCaptured`、`onEncodedFrameReceived`、`onJPGFileSaved`。
- **新增**：`RecorderVideoFrameCaptureConfig` 增加 `videoFrameType`、`jpgFileStorePath`、`jpgCaptureIntervalInSec`、`observer` 配置项。
- **新增**：`Constants` 增加 `VideoCodecType`、`VideoFrameType`、`VideoOrientation` 枚举。

### v4.4.150.5（2025-06-30）

#### API 变更

- **修改**：`AgoraMediaRtcRecorder` 类中 `unregisterRecorderEventHandle` 方法名修正为 `unregisterRecorderEventHandler`，统一方法命名规范

#### 改进与优化

- **修复**：修复设置画面布局接口的线程安全问题，提升多线程环境下的稳定性
- **优化**：调整单个日志文件最大大小限制至 1GB，改善日志管理和磁盘空间利用

### v4.4.150.4（2025-06-11）

#### API 变更

- **新增**：`AgoraMediaRtcRecorder` 类新增 `renewToken` 方法，支持动态更新频道 Token，避免 Token 过期导致录制中断
- **修改**：将 `Constants.WaterMaskFitMode` 重命名为 `Constants.WatermarkFitMode`，修正拼写错误并保持命名一致性

#### 改进与优化

- **修复**：修复 `MixerLayoutConfig` 类中 `imagePath` 属性设置问题，确保背景图片路径正确配置

### v4.4.150.3（2025-05-20）

#### API 变更

- **新增**：`IAgoraMediaRtcRecorderEventHandler` 新增 `onError`、`onTokenPrivilegeWillExpire`、`onTokenPrivilegeDidExpire` 回调方法，支持错误上报及 Token 即将过期/已过期通知。

### v4.4.150.2（2025-05-09）

#### API 变更

- **新增**：`AgoraService` 类中添加 `setGlobalLocalAccessPoint` 函数，用于配置全局本地接入点。

#### 改进与优化

- **修复**：修复 SpringBot 打包回调处理问题

### v4.4.150.1（2025-03-28）

#### API 变更

- **新增**：`IAgoraMediaRtcRecorderEventHandler` 类中添加 `onEncryptionError` 回调函数，支持加密错误通知
- **新增**：`AgoraMediaRtcRecorder` 类中添加 `setAudioVolumeIndicationParameters` 方法，用于配置远端用户音量回调间隔
- **重构**：优化 `IAgoraMediaRtcRecorderEventHandler` 类中 `onAudioVolumeIndication` 回调函数的参数结构

#### 改进与优化

- **增强**：改进 `VideoMixingLayout` 类，修复 `backgroundColor` 属性
- **新功能**：`VideoMixingLayout` 类新增 `backgroundImage` 属性，支持设置背景图片

### v4.4.150-aarch64（2025-02-24）

#### API 变更

- **兼容**：与 v4.4.150 版本保持 API 兼容

#### 改进与优化

- **平台**：首次支持 ARM64 架构

### v4.4.150（2025-01-21）

#### API 变更

- **初始版本**：发布基础 API 结构

#### 改进与优化

- **性能**：基础功能和性能优化

## 其他参考

详细参考官网（<https://doc.shengwang.cn/doc/recording/java/landing-page>）
