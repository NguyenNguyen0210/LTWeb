@echo off
REM ============================================================
REM  Tat Tomcat 11
REM ============================================================
set "CATALINA_HOME=D:\apache-tomcat-11.0.27"

if not exist "%CATALINA_HOME%\bin\shutdown.bat" (
    echo [LOI] Khong tim thay Tomcat tai "%CATALINA_HOME%"
    pause
    exit /b 1
)

call "%CATALINA_HOME%\bin\shutdown.bat"
echo.
echo Da gui lenh tat Tomcat.
pause
