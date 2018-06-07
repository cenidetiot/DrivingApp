#! /bin/bash

clear

app="mx.edu.cenidet.app"
activity="mx.edu.cenidet.app/mx.edu.cenidet.app.activities.LoginActivity"
apk=""

if [ "$0" == "debug" ]; then
    echo "Compiling App"
    gradle assembleDebug
    apk="app/build/outputs/apk/debug/app-debug.apk"
fi

if [ "$0" == "release" ]; then
    echo "Compiling App"
    gradle assembleRelease 
    apk="app/build/outputs/apk/release/app-release.apk"
fi

if [ "$0" == "i" ] || ["$1" == "i"] || ["$2" == "i"]; then
    echo "Stoping App"
    adb shell am force-stop "$app"
    echo "Reinstaling App"
    adb install -r  "$apk"
fi

if [ "$0" == "r"] || ["$1" == "r"] || ["$2" == "r" ]; then
    echo "Running app"
    adb shell am start "$activity"
fi
