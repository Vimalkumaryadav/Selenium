@echo off
setlocal EnableDelayedExpansion

echo ========================================
echo   Test Execution with Auto-Reporting
echo ========================================

REM Setup Maven environment
set MAVEN_HOME=%~dp0temp-maven\apache-maven-3.9.5
set PATH=%MAVEN_HOME%\bin;%PATH%

:menu
echo.
echo Select Test Execution Option:
echo ==============================
echo 1.  Run All Tests + Generate Report
echo 2.  Run Smoke Tests + Generate Report  
echo 3.  Run Regression Tests + Generate Report
echo 4.  Run Tests in Chrome + Generate Report
echo 5.  Run Tests in Firefox + Generate Report
echo 6.  Run Tests in Edge + Generate Report
echo 7.  Run Tests in Headless Mode + Generate Report
echo 8.  Run Tests in Parallel + Generate Report
echo 9.  Run Custom Test + Generate Report
echo 10. Generate Report Only (from existing results)
echo 11. Serve Existing Report
echo 12. Cleanup Old Reports
echo 0.  Exit
echo.

set /p choice="Enter your choice (0-12): "

REM Set default reporting options
set AUTO_OPEN=true
set SERVE_AFTER=false
set CLEANUP_BEFORE=false

if "%choice%"=="1" (
    set TEST_COMMAND=mvn clean test
    set TEST_DESCRIPTION=All Tests
    goto run_tests
)
if "%choice%"=="2" (
    set TEST_COMMAND=mvn clean test -Psmoke
    set TEST_DESCRIPTION=Smoke Tests
    goto run_tests
)
if "%choice%"=="3" (
    set TEST_COMMAND=mvn clean test -Pregression
    set TEST_DESCRIPTION=Regression Tests
    goto run_tests
)
if "%choice%"=="4" (
    set TEST_COMMAND=mvn clean test -Dbrowser=chrome
    set TEST_DESCRIPTION=Chrome Tests
    goto run_tests
)
if "%choice%"=="5" (
    set TEST_COMMAND=mvn clean test -Dbrowser=firefox
    set TEST_DESCRIPTION=Firefox Tests
    goto run_tests
)
if "%choice%"=="6" (
    set TEST_COMMAND=mvn clean test -Dbrowser=edge
    set TEST_DESCRIPTION=Edge Tests
    goto run_tests
)
if "%choice%"=="7" (
    set TEST_COMMAND=mvn clean test -Dbrowser.headless=true
    set TEST_DESCRIPTION=Headless Tests
    goto run_tests
)
if "%choice%"=="8" (
    set TEST_COMMAND=mvn clean test -Pparallel
    set TEST_DESCRIPTION=Parallel Tests
    goto run_tests
)
if "%choice%"=="9" (
    echo.
    set /p test_name="Enter test class or method name: "
    if "!test_name!"=="" (
        echo Invalid test name. Returning to menu.
        goto menu
    )
    set TEST_COMMAND=mvn clean test -Dtest=!test_name!
    set TEST_DESCRIPTION=Custom Test: !test_name!
    goto run_tests
)
if "%choice%"=="10" (
    goto generate_only
)
if "%choice%"=="11" (
    goto serve_only
)
if "%choice%"=="12" (
    goto cleanup_only
)
if "%choice%"=="0" (
    goto end
)

echo Invalid choice. Please try again.
goto menu

:run_tests
echo.
echo ========================================
echo   Executing: %TEST_DESCRIPTION%
echo ========================================
echo.
echo Command: %TEST_COMMAND%
echo.

REM Optional cleanup before test
echo Do you want to clean up old reports before running tests?
set /p cleanup_choice="(y/N): "
if /i "%cleanup_choice%"=="y" (
    echo Cleaning up old reports...
    call :cleanup_reports
)

REM Execute tests
echo.
echo Starting test execution...
echo Timestamp: %date% %time%
echo.

%TEST_COMMAND%

set TEST_EXIT_CODE=%errorlevel%

echo.
echo ========================================
echo   Test Execution Summary
echo ========================================
echo.

if %TEST_EXIT_CODE% == 0 (
    echo ✓ Tests completed successfully!
    set TEST_STATUS=PASSED
) else (
    echo ✗ Some tests failed or encountered errors
    set TEST_STATUS=FAILED
)

echo Exit Code: %TEST_EXIT_CODE%
echo Status: %TEST_STATUS%
echo.

REM Always generate report regardless of test outcome
echo ========================================
echo   Generating Allure Report
echo ========================================
echo.

call :generate_report

REM Ask about serving the report
echo.
echo Do you want to serve the report in browser?
set /p serve_choice="(Y/n): "
if /i not "%serve_choice%"=="n" (
    call :serve_report
)

goto end

:generate_only
echo.
echo ========================================
echo   Generating Report from Existing Results
echo ========================================
echo.

call :generate_report
goto menu

:serve_only
echo.
echo ========================================
echo   Serving Existing Report
echo ========================================
echo.

call :serve_report
goto menu

:cleanup_only
echo.
echo ========================================
echo   Cleaning Up Old Reports
echo ========================================
echo.

call :cleanup_reports
goto menu

:generate_report
echo Generating comprehensive Allure report...

if exist "generate-allure-reports.bat" (
    call generate-allure-reports.bat
) else (
    echo Generating via Allure CLI...
    call npx allure generate target/allure-results --clean -o target/allure-report
    if %errorlevel% == 0 (
        echo ✓ Report generated successfully!
        echo Report location: target\allure-report\index.html
    ) else (
        echo ✗ Failed to generate report
    )
)
goto :eof

:serve_report
echo Starting Allure report server...

if exist "allure-auto-serve.bat" (
    call allure-auto-serve.bat
) else (
    echo Serving via Allure CLI...
    call npx allure open -p 0 target/allure-report/index.html
)
goto :eof

:cleanup_reports
echo Cleaning up reports older than 30 days...

REM Use PowerShell for advanced cleanup
powershell -ExecutionPolicy Bypass -Command "& { if (Test-Path '.\Allure-ReportManager.ps1') { .\Allure-ReportManager.ps1 cleanup } else { Write-Host 'PowerShell cleanup script not found' } }"

goto :eof

:end
echo.
echo ========================================
echo   Test Execution Session Complete
echo ========================================
echo.
echo Available report files:
if exist "target\allure-report\index.html" (
    echo ✓ HTML Report: target\allure-report\index.html
)
if exist "target\allure-results" (
    echo ✓ Raw Results: target\allure-results\
)
if exist "reports\archive" (
    echo ✓ Archived Reports: reports\archive\
)
echo.
echo Thank you for using the automated test execution system!
pause