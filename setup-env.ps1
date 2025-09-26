# Maven Environment Setup for Selenium Framework
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "   Setting up Maven Environment" -ForegroundColor Cyan  
Write-Host "========================================" -ForegroundColor Cyan

# Set Maven Home to the local installation
$MAVEN_HOME = Join-Path $PSScriptRoot "temp-maven\apache-maven-3.9.5"
$env:MAVEN_HOME = $MAVEN_HOME
$env:PATH = "$MAVEN_HOME\bin;$env:PATH"

Write-Host "Maven Home: $MAVEN_HOME" -ForegroundColor Green
Write-Host ""

# Verify Maven installation
Write-Host "Checking Maven version..." -ForegroundColor Yellow
try {
    mvn --version
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "   Environment Setup Complete!" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
    Write-Host ""
    Write-Host "You can now use Maven commands:" -ForegroundColor White
    Write-Host "  mvn clean compile" -ForegroundColor Cyan
    Write-Host "  mvn clean test" -ForegroundColor Cyan
    Write-Host "  mvn allure:report" -ForegroundColor Cyan
    Write-Host "  mvn allure:serve" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Note: This setup is temporary for this session." -ForegroundColor Yellow
    Write-Host "For permanent setup, add Maven to your system PATH." -ForegroundColor Yellow
}
catch {
    Write-Host "Error: Failed to setup Maven environment" -ForegroundColor Red
    Write-Host "Please check if temp-maven/apache-maven-3.9.5 exists" -ForegroundColor Red
}