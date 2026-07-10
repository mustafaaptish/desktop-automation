package insight.runners

import io.cucumber.junit.Cucumber
import io.cucumber.junit.CucumberOptions
import org.junit.runner.RunWith

// Tags are overridden at runtime with -Dcucumber.filter.tags=<expression>
@RunWith(Cucumber::class)
@CucumberOptions(
    features = ["src/test/resources/features"],
    glue = ["insight.steps", "insight.hooks"],
    tags = "",
    plugin = ["pretty",
        "json:target/cucumber-reports/cucumber-report.json",
        "html:target/cucumber-reports/cucumber-reports.html",
        "rerun:target/failedrerun.txt"]
)
class Runner
