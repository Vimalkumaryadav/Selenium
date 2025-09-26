# Selenium TestNG Automation Framework

A robust Selenium TestNG automation framework designed for web testing across Chrome, Firefox, and Edge browsers with comprehensive test management features and CI/CD integration.

## Fea      - name: Run Tests with ExtentReports
        run: mvn clean test -Dbrowser=${{ matrix.browser }} -Dbrowser.headless=true -Dreports.extent.enabled=true
      
      - name: Upload ExtentReports
        uses: actions/upload-artifact@v3
        with:
          name: extent-reports-${{ matrix.browser }}
          path: test-output/extent-reports/**Cross-browser Testing**: Support for Chrome, Firefox, and Edge browsers
- **Page Object Model**: Clean and maintainable test architecture
- **Parallel Execution**: Run tests in parallel for faster execution
- **Comprehensive Reporting**: ExtentReports 2.0 with beautiful HTML reports and screenshots
- **Advanced Logging**: Log4j2 logging system with configurable levels
- **Test Data Management**: JSON-based test data with utility classes
- **Screenshot Capture**: Automatic screenshot capture on test failures
- **CI/CD Ready**: GitHub Actions integration for continuous testing

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- Chrome, Firefox, or Edge browser
- Git for version control

## Project Structure

```
selenium-testng-framework/
├── src/
│   ├── main/java/com/automation/framework/
│   │   ├── base/           # Base test classes
│   │   ├── config/         # Configuration management
│   │   ├── driver/         # WebDriver management
│   │   ├── listeners/      # TestNG listeners
│   │   ├── pages/          # Page Object classes
│   │   └── utils/          # Utility classes
│   ├── main/resources/
│   │   ├── config/         # Configuration files
│   │   └── log4j2.xml      # Logging configuration
│   └── test/
│       ├── java/com/automation/tests/  # Test classes
│       └── resources/
│           ├── testdata/               # Test data files
│           └── testng-suites/         # TestNG suite files
├── pom.xml                 # Maven configuration
└── README.md              # This file
```

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd selenium-testng-framework
```

### 2. Install Dependencies

```bash
mvn clean compile
```

### 3. Run Tests

#### Run All Tests
```bash
mvn clean test
```

#### Run Smoke Tests
```bash
mvn clean test -Psmoke
```

#### Run Regression Tests
```bash
mvn clean test -Pregression
```

#### Run with Specific Browser
```bash
mvn clean test -Dbrowser=firefox
mvn clean test -Dbrowser=edge
```

#### Run in Headless Mode
```bash
mvn clean test -Dbrowser.headless=true
```

#### Run Tests in Parallel
```bash
mvn clean test -Pparallel
```

## Configuration

### Browser Configuration
Modify `src/main/resources/config/application.properties`:

```properties
# Browser settings
browser=chrome
browser.headless=false
browser.maximize=true

# Timeouts
test.timeout.implicit=10
test.timeout.explicit=30
test.timeout.page.load=60

# Environment
base.url=https://www.google.com
environment=dev
```

### TestNG Suite Configuration
Suite files are located in `src/test/resources/testng-suites/`:
- `testng.xml` - All tests
- `smoke-tests.xml` - Smoke tests only
- `regression-tests.xml` - Regression tests

## Test Data Management

Test data is stored in JSON format in `src/test/resources/testdata/`:

```json
{
  "searchQueries": {
    "valid": [
      {
        "query": "selenium webdriver",
        "expectedResultsContain": "selenium",
        "shouldPass": true,
        "description": "Basic Selenium WebDriver search"
      }
    ]
  }
}
```

## Reporting

### ExtentReports 2.0
```bash
# Run tests with ExtentReports enabled
mvn clean test -Dreports.extent.enabled=true

# Or use the convenient batch script
run-tests-with-extent-reports.bat
```

**Report Features:**
- Beautiful HTML reports with dark theme
- Automatic screenshot capture on failures
- Test execution metrics and trends
- Interactive dashboard with charts
- System information and environment details

**Report Location:** `test-output/extent-reports/ExtentReport_[timestamp].html`

For detailed usage guide, see [EXTENTREPORTS_2.0_GUIDE.md](EXTENTREPORTS_2.0_GUIDE.md)

### 🎯 Accessing Reports in GitHub Actions

When tests run in GitHub Actions CI/CD pipeline, reports are automatically generated and made available in multiple ways:

#### 📁 GitHub Actions Artifacts (Primary Method)
1. Go to **Actions** tab in your repository
2. Click on the latest workflow run
3. Scroll to **Artifacts** section
4. Download:
   - `extent-reports-chrome-[run_number]` - Main HTML reports with screenshots
   - `surefire-reports-[run_number]` - Maven test reports

#### 🌐 GitHub Pages (Optional - for web access)
Enable GitHub Pages in repository settings for direct web access:
- Reports URL: `https://[username].github.io/[repository]/`
- Automatically updated after each main branch run

📋 **For complete guide:** See [TEST_REPORTS_ACCESS_GUIDE.md](TEST_REPORTS_ACCESS_GUIDE.md)

### Screenshots
Screenshots are automatically captured on test failures and stored in `test-output/screenshots/`.

## Writing Tests

### 1. Create Page Object

```java
public class NewPage extends BasePage {
    @FindBy(id = "element-id")
    private WebElement element;
    
    @Override
    public boolean isPageLoaded() {
        return isDisplayed(By.id("element-id"));
    }
    
    @Step("Perform action on element")
    public void performAction() {
        click(element);
    }
}
```

### 2. Create Test Class

```java
@Epic("Feature Tests")
public class NewFeatureTests extends BaseTest {
    
    @Test
    @Story("Test Scenario")
    @Description("Test description")
    public void testNewFeature() {
        logStep("Starting test");
        
        NewPage page = new NewPage();
        page.navigateTo(config.getBaseUrl());
        page.performAction();
        
        Assert.assertTrue(page.isPageLoaded(), "Page should be loaded");
    }
}
```

## CI/CD Integration

### GitHub Actions Example

```yaml
name: Selenium Tests
on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    strategy:
      matrix:
        browser: [chrome, firefox]
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Run Tests
        run: mvn clean test -Dbrowser=${{ matrix.browser }} -Dbrowser.headless=true
      
      - name: Generate Allure Report
        run: mvn allure:report
      
      - name: Upload Test Results
        uses: actions/upload-artifact@v3
        with:
          name: test-results-${{ matrix.browser }}
          path: target/allure-results/
```

## Best Practices

1. **Page Object Model**: Keep page elements and actions separate from test logic
2. **Wait Strategies**: Use explicit waits instead of Thread.sleep()
3. **Test Independence**: Each test should be independent and able to run in isolation
4. **Data Management**: Use external data files for test data
5. **Logging**: Add meaningful log messages for debugging
6. **Assertions**: Use descriptive assertion messages
7. **Test Organization**: Group related tests and use appropriate annotations

## Troubleshooting

### Common Issues

1. **WebDriver Issues**: Ensure WebDriverManager is properly configured
2. **Element Not Found**: Verify locators and wait conditions
3. **Timeout Issues**: Adjust timeout values in configuration
4. **Browser Compatibility**: Check browser versions and driver compatibility

### Debug Mode
```bash
mvn clean test -Dlog.level=DEBUG
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Ensure all tests pass
6. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.