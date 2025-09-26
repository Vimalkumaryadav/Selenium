@echo off
echo ==========================================
echo   Selenium Driver Setup Utility
echo ==========================================
echo.

echo This script will attempt to download browser drivers for offline use.
echo Drivers will be saved to the 'drivers' directory.
echo.

set PATH=%PATH%;%CD%\temp-maven\apache-maven-3.9.5\bin

echo Creating drivers directory...
if not exist "drivers" mkdir drivers
if not exist "drivers\.cache" mkdir "drivers\.cache"

echo.
echo Attempting to download drivers...
echo This may take a few minutes depending on your internet connection.
echo.

echo Compiling utilities...
mvn compile -q

echo.
echo Running driver setup...
mvn exec:java -Dexec.mainClass="com.automation.framework.utils.DriverSetupUtils" -Dexec.args="setup" -q

echo.
echo ==========================================
echo   Driver Setup Completed
echo ==========================================
echo.
echo If downloads failed due to network issues:
echo 1. Check your internet connection
echo 2. Configure proxy settings in application.properties if needed
echo 3. Manually download drivers and place in 'drivers' directory:
echo    - ChromeDriver: https://chromedriver.chromium.org/
echo    - EdgeDriver: https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/
echo    - FirefoxDriver: https://github.com/mozilla/geckodriver/releases
echo.
echo Driver files should be named:
echo - chromedriver.exe (for Chrome)
echo - msedgedriver.exe (for Edge)  
echo - geckodriver.exe (for Firefox)
echo.

pause