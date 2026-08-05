@echo off
cd /d "%~dp0"
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.8-hotspot"
set "PATH=C:\Program Files\Eclipse Adoptium\jdk-17.0.20.8-hotspot\bin;%PATH%"
"C:\Program Files\Apache\Maven\apache-maven-3.9.16\bin\mvn.cmd" -q clean test
echo EXIT=%ERRORLEVEL%
