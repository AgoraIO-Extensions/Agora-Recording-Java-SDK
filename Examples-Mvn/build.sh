#!/bin/bash

# Conditional build script
# Usage:
#   ./build.sh            - Only build the project
#   ./build.sh start      - Build and start the application
#   ./build.sh cli <configFileName>  - Build and run CLI mode

set -e

# Initialize action
action="compile"
cli_config=""
cli_extra_args=()

# Parse command line arguments
while [[ $# -gt 0 ]]; do
    case "$1" in
    start)
        action="start"
        shift
        ;;
    cli)
        action="cli"
        shift
        if [[ -z "$1" ]]; then
            echo "Usage: $0 cli <configFileName>"
            exit 1
        fi
        cli_config="$1"
        shift
        if [[ $# -gt 0 ]]; then
            echo "Unknown extra arguments for cli: $*"
            echo "Usage: $0 cli <configFileName>"
            exit 1
        fi
        ;;
    *)
        echo "Unknown option: $1"
        echo "Usage: $0 [start] | $0 cli <configFileName>"
        exit 1
        ;;
    esac
done

echo ""
echo "=== Start building ==="
echo "Building project..."
mvn clean package

# Check build result
if [ $? -eq 0 ]; then
    echo ""
    echo "🎉 Build succeeded!"
    echo ""

    # Detect architecture-specific native library paths
    ARCH=$(uname -m)
    SYS_LIB=""
    APP_LIB=""
    case "$ARCH" in
        x86_64|amd64)
            SYS_LIB="/usr/lib/x86_64-linux-gnu"
            APP_LIB="libs/native/linux/x86_64"
            ;;
        aarch64|arm64)
            SYS_LIB="/usr/lib/aarch64-linux-gnu"
            APP_LIB="libs/native/linux/aarch64"
            ;;
        *)
            SYS_LIB="/usr/lib/x86_64-linux-gnu"
            APP_LIB="libs/native/linux/x86_64"
            echo "Unknown architecture: $ARCH, defaulting to x86_64 paths"
            ;;
    esac

    # Start application if action is "start"
    if [ "$action" = "start" ]; then
        echo "=== Clean port and start application ==="

        # Check if port 18080 is occupied using lsof
        echo "Checking port 18080..."
        if lsof -t -i:18080 2>/dev/null | read -r; then
            echo "Port 18080 is occupied, attempting to kill..."
            # Use xargs to handle multiple PIDs
            lsof -t -i:18080 2>/dev/null | xargs kill -9 2>/dev/null || {
                echo "Failed to kill process(es) with regular user, trying with sudo..."
                lsof -t -i:18080 2>/dev/null | xargs sudo kill -9 2>/dev/null || echo "Failed to kill process(es), continuing..."
            }
            sleep 2 # Give OS time to release the port
            echo "Port cleaned"
        else
            echo "Port 18080 is not occupied"
        fi

        # Set environment variable and start application
        echo "Starting application..."
        echo "Setting LD_LIBRARY_PATH..."
        export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:$SYS_LIB:$APP_LIB"
        echo "LD_LIBRARY_PATH = $LD_LIBRARY_PATH"

        echo "Starting agora-example.jar on port 18080..."
        java -Dserver.port=18080 -jar target/agora-example.jar
    elif [ "$action" = "cli" ]; then
        echo "=== Run CLI mode ==="
        echo "Setting LD_LIBRARY_PATH..."
        export LD_LIBRARY_PATH="$LD_LIBRARY_PATH:$SYS_LIB:$APP_LIB"
        echo "LD_LIBRARY_PATH = $LD_LIBRARY_PATH"
        echo "Running: java -Dloader.main=io.agora.example.recording.cli.CliLauncher -cp target/agora-example.jar org.springframework.boot.loader.PropertiesLauncher --configFileName=$cli_config"
        java -Dloader.main=io.agora.example.recording.cli.CliLauncher -cp target/agora-example.jar org.springframework.boot.loader.PropertiesLauncher --configFileName="$cli_config"
    else
        echo "Build finished! To start the application, use: ./build.sh start"
        echo "Run CLI: ./build.sh cli <configFileName>"
    fi

else
    echo ""
    echo "❌ Build failed!"
    exit 1
fi
