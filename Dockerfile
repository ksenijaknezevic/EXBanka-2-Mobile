# ============================================================
# EXBanka Mobile — Android Build Container
#
# PURPOSE: Build the Android APK inside Docker for CI/CD.
#          This container produces an .apk artifact.
#
# IMPORTANT: Android apps run on an Android emulator or physical
#            Android device, NOT inside this container.
#            Docker is used only for the BUILD step (compile + assemble APK).
#
# Usage:
#   # Build the image
#   docker build -t exbanka-mobile-build .
#
#   # Run and extract the APK
#   docker run --rm -v $(pwd)/output:/output exbanka-mobile-build
#   # APK will be at: ./output/app-debug.apk
# ============================================================

FROM eclipse-temurin:17-jdk-jammy AS base

# ── Install required tools ─────────────────────────────────
RUN apt-get update && apt-get install -y \
    wget \
    unzip \
    curl \
    && rm -rf /var/lib/apt/lists/*

# ── Install Gradle ─────────────────────────────────────────
ENV GRADLE_VERSION=8.6
ENV GRADLE_HOME=/opt/gradle/gradle-${GRADLE_VERSION}
ENV PATH=${PATH}:${GRADLE_HOME}/bin

RUN wget -q "https://services.gradle.org/distributions/gradle-${GRADLE_VERSION}-bin.zip" \
      -O /tmp/gradle.zip \
    && unzip -q /tmp/gradle.zip -d /opt/gradle \
    && rm /tmp/gradle.zip

# ── Install Android SDK command-line tools ─────────────────
ENV ANDROID_HOME=/opt/android-sdk
ENV PATH=${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools:${ANDROID_HOME}/build-tools/34.0.0

RUN mkdir -p ${ANDROID_HOME}/cmdline-tools \
    && wget -q "https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip" \
         -O /tmp/cmdline-tools.zip \
    && unzip -q /tmp/cmdline-tools.zip -d ${ANDROID_HOME}/cmdline-tools \
    && mv ${ANDROID_HOME}/cmdline-tools/cmdline-tools ${ANDROID_HOME}/cmdline-tools/latest \
    && rm /tmp/cmdline-tools.zip

# Accept licenses and install required SDK packages
RUN yes | sdkmanager --licenses > /dev/null 2>&1; \
    sdkmanager \
      "platforms;android-34" \
      "build-tools;34.0.0" \
      "platform-tools"

# ── Copy project and build ─────────────────────────────────
WORKDIR /app
COPY . .

# Warm up Gradle dependency cache
RUN gradle dependencies --no-daemon --configuration releaseRuntimeClasspath || true

# Build debug APK
RUN gradle assembleDebug --no-daemon --stacktrace

# ── Copy APK to /output for extraction ─────────────────────
RUN mkdir -p /output \
    && cp app/build/outputs/apk/debug/app-debug.apk /output/exbanka-verification-debug.apk \
    && echo "✓ APK built: /output/exbanka-verification-debug.apk"

CMD ["sh", "-c", "cp /output/exbanka-verification-debug.apk /host-output/ 2>/dev/null || echo 'Mount /host-output to extract APK'"]
