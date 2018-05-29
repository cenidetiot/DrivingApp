#! /bin/bash

clear

echo "Stoping App"

adb shell am force-stop mx.edu.cenidet.drivingapp

echo "Compiling App"

gradle assembleDebug

echo "Reinstaling App"

adb install -r  app/build/outputs/apk/debug/app-debug.apk 
