# Setup Instructions

This document provides step-by-step instructions to set up the Selenium TestNG Automation Framework on Windows.

## Prerequisites Installation

### 1. Java 17 Installation
✅ **Already Installed** - Java 17.0.10 detected

### 2. Maven Installation

#### Option A: Download and Install Manually
1. Visit https://maven.apache.org/download.cgi
2. Download the Binary zip archive (apache-maven-3.9.x-bin.zip)
3. Extract to `C:\Program Files\Apache\maven`
4. Add Maven to PATH:
   - Open System Properties → Advanced → Environment Variables
   - Add new System Variable: `MAVEN_HOME` = `C:\Program Files\Apache\maven`
   - Edit PATH variable and add: `%MAVEN_HOME%\bin`
5. Verify installation: Open new Command Prompt and run `mvn -version`

#### Option B: Using Chocolatey (if installed)
```powershell
choco install maven
```

#### Option C: Using Scoop (if installed)
```powershell
scoop install maven
```

### 3. Git Installation (Optional but Recommended)
1. Download from https://git-scm.com/download/win
2. Install with default options
3. Verify: `git --version`

### 4. Browser Installation
Ensure you have at least one of the following browsers installed:
- Google Chrome (recommended)
- Mozilla Firefox
- Microsoft Edge

## Project Setup

### 1. Navigate to Project Directory
```bash
cd "C:\Users\pc\Desktop\Selenium Java"
```

### 2. Install Dependencies
```bash
mvn clean compile
```

### 3. Download WebDriver Dependencies
```bash
mvn dependency:resolve
```

### 4. Verify Setup
```bash
mvn clean test -Dtest=GoogleSearchTests#testGoogleSearchSmoke
```

## IDE Setup (Optional)

### IntelliJ IDEA
1. Open IntelliJ IDEA
2. File → Open → Select the project folder
3. IDEA will auto-detect the Maven project
4. Wait for dependency resolution
5. Mark `src/test/java` as Test Sources Root (if not auto-detected)

### Eclipse
1. Open Eclipse
2. File → Import → Existing Maven Projects
3. Browse to project folder and import
4. Right-click project → Maven → Reload Projects

### Visual Studio Code
1. Install Java Extension Pack
2. Open folder in VS Code
3. Extensions will auto-detect Maven project

## Quick Start Commands

### Run Tests
```bash
# All tests
mvn clean test

# Smoke tests only
mvn clean test -Psmoke

# Specific browser
mvn clean test -Dbrowser=chrome

# Headless mode
mvn clean test -Dbrowser.headless=true
```

### Generate Reports
```bash
# Allure report
mvn allure:report
mvn allure:serve

# View ExtentReports
# Open target/extent-reports/ExtentReport.html in browser
```

## Troubleshooting

### Common Issues

1. **Maven not recognized**
   - Ensure Maven is in PATH
   - Restart command prompt/IDE after installation

2. **Java version mismatch**
   - Ensure JAVA_HOME points to Java 17
   - Check with: `java -version` and `mvn -version`

3. **WebDriver issues**
   - Framework uses WebDriverManager for automatic driver management
   - Ensure internet connection for driver downloads

4. **Test failures**
   - Check browser version compatibility
   - Verify internet connection
   - Check if antivirus is blocking WebDriver

### Debug Commands
```bash
# Verbose Maven output
mvn clean test -X

# Debug logging
mvn clean test -Dlog.level=DEBUG

# Skip tests compilation check
mvn clean test -Dmaven.test.skip.exec=true
```

## Next Steps

1. Review the README.md for framework usage
2. Explore example tests in `src/test/java/com/automation/tests/`
3. Modify configuration in `src/main/resources/config/application.properties`
4. Add your own page objects and tests

## Support

If you encounter any issues:
1. Check the logs in `logs/` directory
2. Review Maven output for dependency issues
3. Ensure all prerequisites are properly installed
4. Check browser and WebDriver compatibility