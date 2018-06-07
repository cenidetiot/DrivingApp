import os 
import sys

app="mx.edu.cenidet.app"
activity="mx.edu.cenidet.app/mx.edu.cenidet.app.activities.LoginActivity"
apk=""

os.system("clear")

# python run.py debug -i -r

if (sys.argv[1] == "debug") :
    os.system("gradle assembleDebug")
    apk="app/build/outputs/apk/debug/app-debug.apk"

if (sys.argv[1] == "release") :
    os.system("gradle assembleRelease")
    apk="app/build/outputs/apk/release/app-release.apk"

if  (sys.argv[1] == "-i"  or sys.argv[2] == "-i" or sys.argv[3] == "-i"):
    print ("Stoping App")
    os.system("adb shell am force-stop " + app)
    if (sys.argv[1] == "-i"):
        apk = sys.argv[2]
    print ("Reinstaling App")
    os.system("adb install -r " + apk)

if  (sys.argv[1] == "-r"  or sys.argv[2] == "-r" or sys.argv[3] == "-r"):
    print("Running App")
    os.system("adb shell am start " + activity)