#!/bin/sh
echo "===== INITIALIZING (if this fails, run again until it works) ====="
cd /home/airline/airline/airline-data
sbt publishLocal
for i in {1..5}; do sbt "runMain com.patson.init.MainInit" && break || sleep 5; done
echo "===== DONE ====="