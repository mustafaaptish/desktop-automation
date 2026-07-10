package insight.runners

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

// Re-runs only the scenarios recorded in target/failedrerun.txt by the previous Runner execution.
// Select it with -Drunner=FailedRunner.
@RunWith(Cucumber::class)
@CucumberOptions(
    features = ["@target/failedrerun.txt"],
    glue = ["insight.steps", "insight.hooks"],
    plugin = ["pretty",
        "json:target/cucumber-reports/cucumber-report.json",
        "html:target/cucumber-reports/cucumber-reports.html",
        "rerun:target/failedrerun.txt"]
)
class FailedRunner
