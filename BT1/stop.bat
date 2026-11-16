@echo off
REM Tat Tomcat 11
set "CATALINA_HOME=D:\apache-tomcat-11.0.27"
echo Dang tat Tomcat...
call "%CATALINA_HOME%\bin\shutdown.bat"
echo Da tat.
timeout /t 2 /nobreak >nul
