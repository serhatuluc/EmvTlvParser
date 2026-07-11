@echo off
set PATH=C:\Tools\apache-maven-3.9.16\bin;%PATH%
cd /d "%~dp0"
call mvn -q javafx:run
pause
