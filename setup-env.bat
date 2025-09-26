@echo off
echo ========================================
echo   Setting up Maven Environment
echo ========================================

REM Set Maven Home to the local installation
set MAVEN_HOME=%~dp0temp-maven\apache-maven-3.9.5
set PATH=%MAVEN_HOME%\bin;%PATH%

echo Maven Home: %MAVEN_HOME%
echo.

REM Verify Maven installation
echo Checking Maven version...
mvn --version

echo.
echo ========================================
echo   Environment Setup Complete!
echo ========================================
echo.
echo You can now use Maven commands:
echo   mvn clean compile
echo   mvn clean test
echo   mvn allure:report
echo   mvn allure:serve
echo.
echo Note: This setup is temporary for this session.
echo For permanent setup, add Maven to your system PATH.
echo.