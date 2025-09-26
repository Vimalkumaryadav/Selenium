# Alternative Azure Pipeline Configurations

## Configuration 1: Free Tier (Current - Single Job)
```yaml
# For organizations without purchased parallelism
pool:
  vmImage: 'ubuntu-latest'
  demands: []

jobs:
- job: Build
  timeoutInMinutes: 60
  strategy:
    maxParallel: 1
```

## Configuration 2: Paid Tier (Multiple Parallel Jobs)
```yaml
# For organizations with purchased parallelism
pool:
  vmImage: 'ubuntu-latest'
  demands: []

jobs:
- job: Build
  timeoutInMinutes: 60
  strategy:
    maxParallel: 4  # Adjust based on purchased parallelism
```

## Configuration 3: Matrix Strategy (Test Suite Parallelization)
```yaml
# Run different test suites in parallel
jobs:
- job: TestExecution
  timeoutInMinutes: 60
  strategy:
    maxParallel: 3
    matrix:
      SmokeTests:
        testSuite: 'smoke-tests.xml'
        displayName: 'Smoke Tests'
      RegressionTests:
        testSuite: 'regression-tests.xml'
        displayName: 'Regression Tests'
      FullTests:
        testSuite: 'testng.xml'
        displayName: 'Full Test Suite'
  
  steps:
  - task: Maven@3
    displayName: 'Run $(displayName)'
    inputs:
      mavenPomFile: 'pom.xml'
      goals: 'test'
      options: '-Dsurefire.suiteXmlFiles=src/test/resources/testng-suites/$(testSuite)'
```

## Configuration 4: Self-Hosted Agents
```yaml
# For immediate execution using self-hosted agents
pool:
  name: 'Default'  # Your self-hosted agent pool
  demands:
  - agent.name -equals YourAgentName  # Optional: specific agent

jobs:
- job: Build
  timeoutInMinutes: 120  # Can be longer for self-hosted
  strategy:
    maxParallel: 2  # Based on your self-hosted capacity
```

## Configuration 5: Multi-Stage Pipeline
```yaml
# Sequential stages for better organization
stages:
- stage: Build
  displayName: 'Build Stage'
  jobs:
  - job: Compile
    pool:
      vmImage: 'ubuntu-latest'
    steps:
    - task: Maven@3
      inputs:
        goals: 'clean compile package'
        options: '-DskipTests=true'

- stage: Test
  displayName: 'Test Stage'
  dependsOn: Build
  condition: succeeded()
  jobs:
  - job: UnitTests
    pool:
      vmImage: 'ubuntu-latest'
    steps:
    - task: Maven@3
      inputs:
        goals: 'test'
        options: '-Dtest=*Test'
  
  - job: IntegrationTests
    pool:
      vmImage: 'ubuntu-latest'
    dependsOn: UnitTests
    steps:
    - task: Maven@3
      inputs:
        goals: 'test'
        options: '-Dtest=*IT'
```

## Resource Requirements by Configuration

| Configuration | Parallelism Needed | Estimated Duration | Cost Impact |
|---------------|-------------------|-------------------|-------------|
| Single Job    | 1                 | 15-20 minutes     | Free        |
| Parallel Jobs | 2-4               | 8-12 minutes      | Paid        |
| Matrix Tests  | 3                 | 10-15 minutes     | Paid        |
| Self-Hosted   | Variable          | Variable          | Infrastructure |
| Multi-Stage   | 1 per stage       | 20-25 minutes     | Free/Paid   |

## How to Switch Configurations

1. **Backup current azure-pipelines.yml**
2. **Replace content** with desired configuration
3. **Commit and push** changes
4. **Monitor pipeline execution**
5. **Adjust as needed** based on performance

## Performance Optimization Tips

1. **Use caching** for Maven dependencies
2. **Parallel test execution** within Maven (surefire plugin)
3. **Test categorization** (smoke, regression, integration)
4. **Artifact publishing** only when needed
5. **Conditional steps** based on branch or changes