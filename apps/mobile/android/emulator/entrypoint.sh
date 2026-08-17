#!/usr/bin/env bash
# Boots a headless AVD for automated verification (adb screencap,
# connectedAndroidTest via `docker exec`) — no display/VNC layer.
set -euo pipefail

AVD_NAME="chetna_test"
SDK="/opt/android-sdk-linux"

# The base image's default nonroot user can't touch /dev/kvm; we run as root
# (see the Dockerfile) so this is a plain chmod.
if [ -e /dev/kvm ]; then
  chmod 666 /dev/kvm
fi

if [ ! -d "$HOME/.android/avd/${AVD_NAME}.avd" ]; then
  echo "No existing AVD found in the mounted volume — creating ${AVD_NAME} (first-boot-only cost)."
  echo "no" | "$SDK/cmdline-tools/latest/bin/avdmanager" create avd \
    --name "$AVD_NAME" \
    --package "system-images;android-35;google_apis;x86_64" \
    --device "pixel_9"
else
  echo "Reusing existing AVD ${AVD_NAME} from the mounted volume."
fi

# -read-only: a container restart leaves a stale AVD lock from the last
# instance (killed non-gracefully), which the emulator otherwise treats as a
# real concurrent-instance conflict and refuses to start.
exec "$SDK/emulator/emulator" -avd "$AVD_NAME" -no-window -read-only -no-snapshot -no-audio -gpu swiftshader_indirect
