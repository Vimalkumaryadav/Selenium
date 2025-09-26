@echo off
echo ====================================
echo   Selenium TestNG Automation Tests
echo ====================================
echo.

:menu
echo Select test execution option:
echo 1. Run All Tests
echo 2. Run Smoke Tests
echo 3. Run Regression Tests
echo 4. Run Tests in Chrome
echo 5. Run Tests in Firefox
echo 6. Run Tests in Edge
echo 7. Run Tests in Headless Mode
echo 8. Run Tests in Parallel
echo 9. Generate Allure Report
echo 10. Setup Drivers Offline
echo 0. Exit
echo.

set /p choice="Enter your choice (0-10): "

if "%choice%"=="1" (
    echo Running all tests...
    set PATH=%PATH%;%CD%\temp-maven\apache-maven-3.9.5\bin
    mvn clean test
    goto end
)
if "%choice%"=="2" (
    echo Running smoke tests...
    set PATH=%PATH%;%CD%\temp-maven\apache-maven-3.9.5\bin
    mvn clean test -Psmoke
    goto end
)
if "%choice%"=="3" (
    echo Running regression tests...
    set PATH=%PATH%;%CD%\temp-maven\apache-maven-3.9.5\bin
    mvn clean test -Pregression
    goto end
)
if "%choice%"=="4" (
    echo Running tests in Chrome...
    set PATH=%PATH%;%CD%\temp-maven\apache-maven-3.9.5\bin
    mvn clean test -Dbrowser=chrome
    goto end
)
if "%choice%"=="5" (
    echo Running tests in Firefox...
    set PATH=%PATH%;%CD%\temp-maven\apache-maven-3.9.5\bin
    mvn clean test -Dbrowser=firefox
    goto end
)
if "%choice%"=="6" (
    echo Running tests in Edge...
    set PATH=%PATH%;%CD%\temp-maven\apache-maven-3.9.5\bin
    mvn clean test -Dbrowser=edge
    goto end
)
if "%choice%"=="7" (
    echo Running tests in headless mode...
    set PATH=%PATH%;%CD%\temp-maven\apache-maven-3.9.5\bin
    mvn clean test -Dbrowser.headless=true
    goto end
)
if "%choice%"=="8" (
    echo Running tests in parallel...
    set PATH=%PATH%;%CD%\temp-maven\apache-maven-3.9.5\bin
    mvn clean test -Pparallel
    goto end
)
if "%choice%"=="9" (
    echo Generating and serving Allure report...
    call generate-allure-report-fixed.bat
    goto end
)
if "%choice%"=="10" (
    echo Setting up drivers for offline use...
    set PATH=%PATH%;%CD%\temp-maven\apache-maven-3.9.5\bin
    mvn compile exec:java -Dexec.mainClass="com.automation.framework.utils.DriverSetupUtils" -Dexec.args="setup"
    goto end
)
if "%choice%"=="0" (
    echo Exiting...
    goto exit
)

echo Invalid choice. Please try again.
goto menu

:end
echo.
echo Test execution completed!
echo Check the reports in target/ directory
pause
goto menu

:exit