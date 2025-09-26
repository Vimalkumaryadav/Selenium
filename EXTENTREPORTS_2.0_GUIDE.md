# ExtentReports 2.0 Configuration Guide

## Overview

This project has been configured with ExtentReports 2.0 (version 2.41.2), a powerful HTML reporting library for test automation frameworks. ExtentReports 2.0 provides beautiful, interactive HTML reports with screenshots, logs, and detailed test analytics.

## Configuration Details

### Maven Dependencies
```xml
<dependency>
    <groupId>com.relevantcodes</groupId>
    <artifactId>extentreports</artifactId>
    <version>2.41.2</version>
</dependency>
```

### Key Components

1. **ExtentReportsManager.java** - Central manager for ExtentReports operations
2. **ExtentReportsListener.java** - Dedicated TestNG listener for ExtentReports
3. **TestListener.java** - Enhanced existing listener with ExtentReports integration
4. **extent-config.xml** - ExtentReports configuration file

## Usage

### Automatic Reporting (Recommended)

The framework automatically generates ExtentReports for all tests through the `TestListener`. No additional code needed in test methods.

**Configuration:**
```properties
# In application.properties
reports.extent.enabled=true
reports.screenshots.enabled=true
reports.screenshots.on.failure=true
reports.screenshots.on.pass=false
```

### Manual Logging (Optional)

For custom logging within test methods:

```java
import com.automation.framework.reporting.ExtentReportsManager;
import com.relevantcodes.extentreports.LogStatus;

@Test
public void myTest() {
    // Custom step logging
    ExtentReportsManager.logStep(LogStatus.INFO, "Starting test step 1");
    
    // Your test logic here
    
    ExtentReportsManager.logStep(LogStatus.PASS, "Step 1 completed successfully");
    
    // Screenshot with custom message
    ExtentReportsManager.logStepWithScreenshot(LogStatus.INFO, "Page loaded", screenshotPath);
    
    // Add categories and author
    ExtentReportsManager.assignCategory("Smoke");
    ExtentReportsManager.assignAuthor("Test Engineer");
}
```

## Running Tests with ExtentReports

### Option 1: Using Maven Command
```bash
mvn clean test -Dreports.extent.enabled=true
```

### Option 2: Using Batch Script
```bash
run-tests-with-extent-reports.bat
```

### Option 3: IDE Execution
Set VM options in your IDE:
```
-Dreports.extent.enabled=true
-Dreports.screenshots.enabled=true
-Dreports.screenshots.on.failure=true
```

## Report Features

### Generated Reports Include:
- ✅ Test execution summary and statistics
- ✅ Pass/Fail/Skip status with timestamps
- ✅ Test execution time and performance metrics
- ✅ Screenshot capture on failures (and optionally on pass)
- ✅ Stack traces for failed tests
- ✅ Test categorization and author information
- ✅ System information (browser, OS, Java version, etc.)
- ✅ Custom CSS styling with dark theme
- ✅ Interactive dashboard with charts

### Report Location
```
test-output/extent-reports/ExtentReport_YYYY-MM-DD_HH-mm-ss.html
```

## ExtentReports 2.0 API Reference

### LogStatus Options
- `LogStatus.PASS` - Green status for passed steps
- `LogStatus.FAIL` - Red status for failed steps  
- `LogStatus.SKIP` - Orange status for skipped steps
- `LogStatus.INFO` - Blue status for informational steps
- `LogStatus.WARNING` - Yellow status for warnings

### ExtentReportsManager Methods
```java
// Test management
ExtentReportsManager.startTest(testName, description)
ExtentReportsManager.endTest()
ExtentReportsManager.getTest()

// Logging
ExtentReportsManager.logStep(LogStatus status, String message)
ExtentReportsManager.logStepWithScreenshot(LogStatus status, String message, String screenshotPath)

// Metadata
ExtentReportsManager.assignCategory(String category)
ExtentReportsManager.assignAuthor(String author)

// Report management
ExtentReportsManager.flush()
ExtentReportsManager.getReportPath()
```

## Configuration Files

### extent-config.xml
Located at: `src/test/resources/extent-config.xml`

Key configurations:
```xml
<theme>dark</theme>
<documentTitle>Selenium TestNG Automation Report</documentTitle>
<reportName>SauceDemo Test Execution Report</reportName>
<dateFormat>dd/MM/yyyy</dateFormat>
<timeFormat>HH:mm:ss</timeFormat>
```

### application.properties
ExtentReports settings:
```properties
reports.extent.enabled=true
reports.screenshots.enabled=true
reports.screenshots.on.failure=true
reports.screenshots.on.pass=false
```

## Integration with CI/CD

### GitHub Actions Integration
The framework includes GitHub Actions workflow support:
```yaml
# .github/workflows/extent-reports.yml
- name: Run Tests with ExtentReports
  run: mvn clean test -Dreports.extent.enabled=true
  
- name: Upload ExtentReports
  uses: actions/upload-artifact@v3
  with:
    name: extent-reports
    path: test-output/extent-reports/
```

## Troubleshooting

### Common Issues

1. **Reports not generated**
   - Ensure `reports.extent.enabled=true` in properties
   - Check TestListener is configured in TestNG XML files
   - Verify test-output directory permissions

2. **Screenshots not appearing**
   - Check screenshot capture settings in properties
   - Verify WebDriver is properly initialized
   - Ensure screenshot directory exists and is writable

3. **Report styling issues**
   - Verify extent-config.xml is in classpath
   - Check CSS syntax in configuration file
   - Ensure report files are not corrupted

### Debug Mode
Enable debug logging:
```properties
log.level=DEBUG
```

## Best Practices

1. **Use descriptive test names and descriptions**
   ```java
   @Test(description = "Verify user can login with valid credentials")
   ```

2. **Add meaningful categories**
   ```java
   ExtentReportsManager.assignCategory("Login");
   ExtentReportsManager.assignCategory("Smoke");
   ```

3. **Use appropriate log levels**
   - INFO for general steps
   - PASS for successful validations
   - FAIL for failures (handled automatically)
   - WARNING for non-critical issues

4. **Capture screenshots strategically**
   - Always on failures (automatic)
   - On key validation points (manual)
   - Avoid excessive screenshot capture

## Example Test

Refer to the main test files (e.g., `saucedemo.java`) for complete usage examples.

## References

- [ExtentReports 2.0 Documentation](https://www.extentreports.com/docs/versions/2/java/)
- [TestNG Integration Guide](https://testng.org/doc/documentation-main.html)
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)

---

**Note:** This configuration uses ExtentReports 2.0 (classic version) as specifically requested. For newer features, consider upgrading to ExtentReports 5.x in the future.