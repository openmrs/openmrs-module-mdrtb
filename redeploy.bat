@echo off

set "CATALINA_HOME=C:\apache-tomcat-9"
set "PATH=%CATALINA_HOME%\bin;%PATH%"

call "%CATALINA_HOME%\bin\shutdown.bat"
timeout /t 5 /nobreak > nul

REM Build Module
call mvn clean install -DskipTests

if errorlevel 1 (
    echo Build failed.
    pause
    exit /b 1
)

REM Clear OpenMRS Cache
rmdir /s /q "C:\Users\owais\AppData\Roaming\OpenMRS\.openmrs-lib-cache" 2>nul

REM Remove Existing OMOD
del /q "C:\Users\owais\AppData\Roaming\OpenMRS\modules\mdrtb*.omod"

REM Copy New OMOD
for %%f in (omod\target\*.omod) do (
    copy /Y "%%f" "C:\Users\owais\AppData\Roaming\OpenMRS\modules\"
)

REM Start Tomcat
call "%CATALINA_HOME%\bin\startup.bat"

echo Waiting for OpenMRS startup...
timeout /t 20 /nobreak > nul

REM Launch Browser
start "" http://localhost:8080/openmrs