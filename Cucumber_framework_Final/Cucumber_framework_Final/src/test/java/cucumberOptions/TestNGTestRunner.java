package cucumberOptions;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

//glue refers to the path of step definition files
@CucumberOptions(features="src/test/java/features",glue="stepDefinitions"
,monochrome=true , plugin = {"com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"})

public class TestNGTestRunner extends AbstractTestNGCucumberTests{

		
	
}
