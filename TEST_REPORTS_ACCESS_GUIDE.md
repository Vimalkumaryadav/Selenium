# 📊 Test Reports Access Guide

## Where to Find Your Test Reports

### 1. 🎯 GitHub Actions Artifacts (Always Available)

**This is the primary way to access your test reports:**

1. **Navigate to your repository on GitHub**
2. **Click the "Actions" tab**
3. **Find and click on your latest workflow run**
4. **Scroll down to the "Artifacts" section**
5. **Download the following artifacts:**
   - `extent-reports-chrome-[run_number]` - 📊 **Main ExtentReports** (HTML reports with screenshots)
   - `surefire-reports-[run_number]` - 📋 **Maven Surefire Reports** (XML/TXT format)
   - `failure-screenshots-[run_number]` - 📸 **Screenshots** (only if tests failed)
   - `test-logs-chrome-[run_number]` - 📝 **Logs** (only if tests failed)

6. **Extract the downloaded ZIP files**
7. **Open the HTML files in your web browser**

### 2. 🌐 GitHub Pages (Optional - Web Access)

**For easy web-based report access (requires setup):**

#### Enable GitHub Pages:
1. Go to your **Repository Settings**
2. Scroll to the **"Pages"** section
3. Set **Source** to **"GitHub Actions"**
4. Save the settings

#### After enabling Pages:
- Reports will be automatically deployed to: `https://[your-username].github.io/[repository-name]/`
- New reports are published after each successful run on the `main` branch
- You can share the URL with team members for easy access

### 3. 📁 Local Development

**When running tests locally:**
- Reports are generated in: `test-output/extent-reports/`
- Open the HTML files directly from your file system

## 📋 Report Types Explained

### ExtentReports (Primary)
- **Location:** `test-output/extent-reports/ExtentReport_[timestamp].html`
- **Features:**
  - 🎨 Rich HTML interface
  - 📊 Test execution dashboard
  - 📸 Automatic screenshots
  - 📈 Charts and graphs
  - 🏷️ Test categorization
  - 📝 Detailed step-by-step logs

### Surefire Reports (Maven)
- **Location:** `target/surefire-reports/`
- **Files:**
  - `TEST-TestSuite.xml` - XML format test results
  - `TestSuite.txt` - Plain text summary
- **Features:**
  - 📊 Test statistics
  - ❌ Failure details
  - ⏱️ Execution times

## 🔍 What to Look For in Reports

### ✅ Successful Tests
- Green status indicators
- ✓ Pass marks
- Screenshots of successful steps

### ❌ Failed Tests
- Red status indicators
- ✗ Failure marks
- Error messages and stack traces
- Screenshots at failure points
- Detailed step logs

### 📊 Summary Dashboard
- Total tests executed
- Pass/Fail/Skip counts
- Execution time
- Browser information
- Environment details

## 🚀 Quick Access URLs

After your workflow runs, you can quickly access:

1. **Latest Workflow Run:** `https://github.com/[owner]/[repo]/actions`
2. **GitHub Pages (if enabled):** `https://[owner].github.io/[repo]/`

## 🛠️ Troubleshooting

### Can't Find Artifacts?
- Artifacts are only available for **30 days**
- Check if the workflow completed successfully
- Look for the workflow run in the Actions tab

### Reports Not Generated?
- Check if tests actually ran
- Look at the workflow logs for errors
- Verify the `test-output/extent-reports/` directory exists

### GitHub Pages Not Working?
- Ensure Pages is enabled in repository settings
- Check that the workflow ran on the `main` branch
- Wait a few minutes for deployment to complete

## 📧 Team Sharing

To share reports with your team:

1. **For Artifacts:** Share the GitHub Actions run URL
2. **For Pages:** Share the GitHub Pages URL (if enabled)
3. **For Local Reports:** Use tools like GitHub Releases or cloud storage

---

**💡 Pro Tip:** Enable GitHub Pages for the best experience - your team can always access the latest reports via a simple web URL!