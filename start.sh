#!/bin/sh

./backend/gradlew -p ./backend/ bootRun &
composer server