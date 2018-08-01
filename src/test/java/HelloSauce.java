import org.junit.Test;

import org.openqa.selenium.By;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.Assert.assertTrue;

public class HelloSauce
{
	// get Sauce Labs username and access key from environment variables
	final String SAUCE_USERNAME = System.getenv("SAUCE_USERNAME");
	final String SAUCE_ACCESS_KEY = System.getenv("SAUCE_ACCESS_KEY");

	@Test
	public void openSauceLabsHomePage() throws MalformedURLException
	{
		// get the Sauce Labs Selenium Server URL
		URL sauceURL = new URL("https://SAUCE_USERNAME:SAUCE_ACCESS_KEY@ondemand.saucelabs.com/wd/hub"
				.replace("SAUCE_USERNAME", SAUCE_USERNAME)
				.replace("SAUCE_ACCESS_KEY", SAUCE_ACCESS_KEY));

		// set the Desired Capabilities
		DesiredCapabilities capabilities = new DesiredCapabilities()
		{{
				setCapability("browserName", "Chrome");
				setCapability("version", "latest");
				setCapability("platform", "Windows 10");
				setCapability("name", "Hello Sauce");

				// BUILD_TAG environment variable is set by Jenkins
				setCapability("build", System.getenv("BUILD_TAG"));
		}};

		// create a remote WebDriver session by passing the URL and capabilities
		RemoteWebDriver driver = new RemoteWebDriver(sauceURL, capabilities);

		// execute test steps
		driver.get("https://saucelabs.com");
		String title = driver.getTitle();
		System.out.println(title);

		// use an explicit wait
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until((ExpectedConditions.textToBePresentInElementLocated(By.tagName("h1"), "CONTINUOUS TESTING")));

		// check if test passed and report result to Sauce Labs
		try
		{
			assertTrue("title".contains("Sauce Labs"));
			driver.executeScript("sauce:job-result=passed");
		}
		catch (AssertionError e)
		{
			driver.executeScript("sauce:job-result=failed");
		}

		// Used by Jenkins plugin to track tests
		System.out.println("SauceOnDemandSessionID=" + driver.getSessionId().toString() + " jobname=Hello Sauce");

		// close browser and end session
		driver.quit();
	}
}
