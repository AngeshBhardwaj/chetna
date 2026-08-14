# Headless Android 15 emulator for automated verification only (adb
# screencap, connectedAndroidTest) — no display/VNC layer. A human wanting
# to see the app live runs their own separate emulator (e.g. Android Studio)
# and installs the APK there directly; see design.md's testing-workflow note.
FROM saschpe/android-emulator:35-jdk23.0.2_7

# The base image's default nonroot user (uid 1001) can't chmod /dev/kvm or
# write to a fresh AVD volume mount. Running as root for the whole container
# (build and runtime) avoids privilege-drop complexity for what's purely a
# local, throwaway dev tool with no production exposure.
USER root

# The base image ships the bare SDK with no system images at all (verified
# directly). This is static SDK content, not per-machine AVD state, so it's
# baked in here rather than downloaded on first container start.
RUN yes | /opt/android-sdk-linux/cmdline-tools/latest/bin/sdkmanager --licenses >/dev/null \
    && /opt/android-sdk-linux/cmdline-tools/latest/bin/sdkmanager \
        "system-images;android-35;google_apis;x86_64"

COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh

ENTRYPOINT ["/entrypoint.sh"]
