@echo off
cmd /c mvn package -Dmaven.test.skip=true
pause
