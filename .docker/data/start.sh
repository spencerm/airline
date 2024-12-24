#!/bin/sh
echo "===== START OF BACKEND ====="
cd /home/airline/airline/airline-data
sbt "runMain com.patson.MainSimulation"