# Azure DevOps Pipeline Parallelism Configuration Guide

## Current Pipeline Status
Your Azure Pipeline has been configured with appropriate parallelism settings for the free tier and future scaling.

## Parallelism Issue Resolution Steps

### 1. Request Free Parallelism Grant
If you're encountering the "No hosted parallelism has been purchased or granted" error:

1. **Visit the Microsoft Form**: https://aka.ms/azpipelines-parallelism-request
2. **Complete the free parallelism grant form** with the following information:
   - Your Azure DevOps organization name
   - Project details
   - Intended use case (Selenium test automation)
   - Business justification

3. **Wait for approval**: Typically takes 2-3 business days
4. **Check email**: Microsoft will send confirmation once approved

### 2. Pipeline Configuration Explained

#### Current Settings
```yaml
pool:
  vmImage: 'ubuntu-latest'
  demands: []

jobs:
- job: Build
  timeoutInMinutes: 60
  strategy:
    maxParallel: 1  # Conservative setting for free tier
```

#### Key Configuration Elements

**Pool Configuration:**
- `vmImage: 'ubuntu-latest'`: Uses Microsoft-hosted Ubuntu agents
- `demands: []`: No specific agent capabilities required

**Job Configuration:**
- `timeoutInMinutes: 60`: Prevents jobs from running indefinitely
- `maxParallel: 1`: Limits concurrent jobs to 1 (suitable for free tier)

**Performance Optimizations:**
- Maven dependency caching to reduce build time
- Separate compile and test phases
- Proper artifact publishing for test results

### 3. Scaling After Parallelism Grant Approval

Once your parallelism request is approved, you can increase concurrency:

#### Option 1: Increase Parallel Jobs
```yaml
strategy:
  maxParallel: 2  # Or higher based on granted parallelism
```

#### Option 2: Matrix Strategy for Multiple Test Suites
```yaml
strategy:
  maxParallel: 3
  matrix:
    SmokeTests:
      testSuite: 'smoke-tests.xml'
    RegressionTests:
      testSuite: 'regression-tests.xml'
    FullSuite:
      testSuite: 'testng.xml'
```

### 4. Alternative Solutions for Immediate Execution

#### Self-Hosted Agents
If you need immediate pipeline execution:

1. **Set up a self-hosted agent** on your local machine or cloud VM
2. **Update pool configuration**:
```yaml
pool:
  name: 'Default'  # Your self-hosted agent pool name
```

#### Sequential Pipeline Stages
Split the pipeline into stages that run sequentially:

```yaml
stages:
- stage: Build
  jobs:
  - job: Compile
    # Build steps here

- stage: Test
  dependsOn: Build
  jobs:
  - job: RunTests
    # Test steps here
```

### 5. Monitoring and Optimization

#### Pipeline Performance Metrics
- **Build Duration**: Monitor average build times
- **Success Rate**: Track test pass/fail rates
- **Agent Utilization**: Optimize parallelism based on usage

#### Cost Optimization
- Use caching for Maven dependencies
- Optimize test execution order
- Consider test categorization (smoke, regression, full)

### 6. Troubleshooting Common Issues

#### "No hosted parallelism" Error
- **Solution**: Complete the parallelism request form
- **Temporary workaround**: Use self-hosted agents

#### Pipeline Timeout
- **Current setting**: 60 minutes timeout
- **Adjustment**: Increase if needed for larger test suites

#### Test Failures
- **Configuration**: Tests won't fail the pipeline (`maven.test.failure.ignore=true`)
- **Reports**: Check Extent Reports for detailed test results

## Next Steps

1. **Submit parallelism request** if not already done
2. **Test current pipeline** with maxParallel: 1
3. **Monitor performance** and optimize as needed
4. **Scale up** after parallelism approval

## Support Resources

- [Azure Pipelines Documentation](https://docs.microsoft.com/en-us/azure/devops/pipelines/)
- [Maven Task Documentation](https://docs.microsoft.com/en-us/azure/devops/pipelines/tasks/build/maven)
- [Parallelism in Azure Pipelines](https://docs.microsoft.com/en-us/azure/devops/pipelines/licensing/concurrent-jobs)