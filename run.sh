#! /bin/bash

clear

echo "Stoping App"

adb shell am force-stop mx.edu.cenidet.app

echo "Compiling App"

gradle assembleDebug

echo "Reinstaling App"

adb install -r  app/build/outputs/apk/debug/app-debug.apk 

echo "Running app"

adb shell am start mx.edu.cenidet.app/mx.edu.cenidet.app.activities.LoginActivity
