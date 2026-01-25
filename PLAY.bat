@echo off
chcp 65001 >nul
title GUESS THE ASCII - ULTIMATE
cls
echo Pokrecem igru...

java -Dfile.encoding=UTF-8 -jar guessing-game.jar

pause