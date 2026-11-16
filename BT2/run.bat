@echo off
REM ============================================================
REM  BT2 - Build + deploy len Tomcat 11 + mo trinh duyet
REM  Chi can double-click file nay.
REM ============================================================
setlocal

set "CATALINA_HOME=D:\apache-tomcat-11.0.27"
set "APP_NAME=bt2"
set "PROJECT_DIR=%~dp0"
set "URL=http://localhost:8080/%APP_NAME%/admin/categories"

cd /d "%PROJECT_DIR%"

if not exist "%CATALINA_HOME%\bin\startup.bat" (
    echo [LOI] Khong tim thay Tomcat tai "%CATALINA_HOME%"
    echo       Hay sua bien CATALINA_HOME trong file run.bat
    pause
    exit /b 1
)

echo [1/3] Dang build project...
call mvn -B clean package
if errorlevel 1 (
    echo.
    echo [LOI] Build that bai. Xem log ben tren.
    pause
    exit /b 1
)

echo [2/3] Dang deploy %APP_NAME%.war len Tomcat...
if exist "%CATALINA_HOME%\webapps\%APP_NAME%" rmdir /S /Q "%CATALINA_HOME%\webapps\%APP_NAME%"
copy /Y "%PROJECT_DIR%target\%APP_NAME%.war" "%CATALINA_HOME%\webapps\" >nul
if errorlevel 1 (
    echo [LOI] Khong copy duoc file war. Tomcat co the dang khoa file.
    pause
    exit /b 1
)

echo [3/3] Kiem tra Tomcat...
netstat -ano | findstr /R /C:":8080 .*LISTENING" >nul
if errorlevel 1 (
    echo       Tomcat chua chay - dang khoi dong...
    call "%CATALINA_HOME%\bin\startup.bat"
    timeout /t 6 /nobreak >nul
) else (
    echo       Tomcat dang chay - se tu dong redeploy.
    timeout /t 6 /nobreak >nul
)

echo.
echo ============================================================
echo   XONG! Mo trinh duyet tai: %URL%
echo   Luu y: SQL Server phai dang chay va da chay database\init.sql
echo ============================================================
start "" "%URL%"
endlocal
