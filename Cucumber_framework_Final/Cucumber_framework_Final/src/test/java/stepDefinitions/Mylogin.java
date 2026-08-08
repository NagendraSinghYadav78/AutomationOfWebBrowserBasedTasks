package stepDefinitions;



import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.After;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;

import com.mailosaur.MailosaurClient;
import com.mailosaur.MailosaurException;
import com.mailosaur.models.Message;
import com.mailosaur.models.MessageSearchParams;
import com.mailosaur.models.SearchCriteria;
import java.util.*;



public class Mylogin {

	// FIXED (Aug 2026 revision): The previous version hardcoded a live-looking
	// Mailosaur API key directly in this source file, which was then committed
	// to a public GitHub repository. That key should be treated as compromised:
	// rotate/revoke it in the Mailosaur dashboard and never commit a real key
	// again. It is now read from an environment variable instead.
	//
	// Set it before running: export MAILOSAUR_API_KEY=your_key_here
	String apiKey = System.getenv("MAILOSAUR_API_KEY");
	String serverId = "46m71knb";
	String serverDomain = "46m71knb.mailosaur.net";
	String from = "noreply@ncb.flipkart.com";
	
	
//public String getRandomEmail() {
//	return "user" +System.currentTimeMillis() + "@" + serverDomain;
//}
	
	
	public WebDriver driver;
	
	
	
	
	
	@Given("User is on the homepage of Flipkart")
	
	public void user_is_on_the_homepage_of_flipkart() {

		if (apiKey == null || apiKey.isEmpty()) {
			throw new IllegalStateException(
				"MAILOSAUR_API_KEY environment variable is not set. This test requires a valid, "
				+ "non-committed Mailosaur API key -- see the note at the top of this class.");
		}

		WebDriverManager.chromedriver().setup();
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--headless=new");
		options.addArguments("--window-size=1920,1080");
		options.addArguments("--disable-notifications");
		driver = new ChromeDriver(options);
		
	    // NOTE: unlike GreenKartStepDefinition/AddtoCart, this scenario tests a
	    // real login/OTP flow that has no equivalent on the GreenKart demo site,
	    // so it still targets live https://www.flipkart.com/. Because this hits
	    // a real production authentication system, do NOT include it in tight
	    // repeated (e.g. 30x) timing loops -- run it sparingly and treat it as a
	    // functional check, not a timing benchmark, unless you have explicit
	    // authorization to load-test Flipkart's login flow.
	    driver.get("https://www.flipkart.com/");
	    //driver.manage().window().maximize();
	    driver.findElement(By.linkText("Login")).click();
	}
	
	
	
	@When("user enters valid email {string} in Enter Email \\/ mobile number field")
	public void user_enters_valid_email_in_enter_email_mobile_number_field(String string) {

		//WebElement value = driver.findElement(By.cssSelector("#container > div > div.VCR99n > div > div.Sm1-5F.col.col-3-5 > div > form > div.I-qZ4M.vLRlQb > input"));
		driver.findElement(By.cssSelector(".c3Bd2c.yXUQVt")).sendKeys(string);
		
		//value.sendKeys(string);
		
		
	}
	@When("user clicks on request OTP button")
	public void user_clicks_on_request_otp_button() throws IOException, MailosaurException, InterruptedException {

		WebElement RequestOTPButton = driver.findElement(By.cssSelector(".dSM5Ub.Kv3ekh.KcXDCU"));
		Thread.sleep(2000);
		RequestOTPButton.click();
		

		
		
	   try {
	     MailosaurClient mailosaur = new MailosaurClient(apiKey);
	    
	   
	    MessageSearchParams params = new MessageSearchParams();
	    params.withServer(serverId);
	   
	    SearchCriteria criteria = new SearchCriteria();
	    criteria.withSentTo("power-moment@" + serverDomain);
	   
	   Message message = mailosaur.messages().get(params, criteria);
		   assertNotNull(message);
		   
		   String subject = message.subject();
		   System.out.println(subject);
		   //System.out.println(message.text().body()); // "Your access code is 243546."

		   Pattern pattern = Pattern.compile("Flipkart Account - .*([0-9]{6}).*");
		   Matcher matcher = pattern.matcher(subject);
		   matcher.find();
		   Thread.sleep(2000);

		   String otp = matcher.group(1);
		   System.out.println(otp); 
		   Thread.sleep(2000);
		   
		   
		// Locate all input fields for the OTP

		   
		   //WebElement inputField = driver.findElement(By.xpath("//div[@class='XDRRi5']//input[@type='text']"));
		   //inputField.sendKeys(otp);
		   
		   
		   
		   
		   WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		   try {
			   // FIXED (Aug 2026 revision): the xpath string below was missing its
			   // closing "]" bracket, which would throw InvalidSelectorException at
			   // runtime rather than the intended wait/timeout behavior.
			   wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//span[contains(text(), 'power-moment@46m71knb.mailosaur.net')]")));
			   System.out.println("Otp page loaded");
			   
		   }catch (Exception e) {
			   System.out.println("Otp page didnt load in time" + e.getMessage());
		   }
		   
		   
	        for (int i = 0; i < otp.length(); i++) {
	        	String xpath ="//input[@data-sharkid='__" + (i + 1) + "']";
	            // Find the input field using XPath
	        	WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
	        	
	
	        	inputField.sendKeys(String.valueOf(otp.charAt(i)));

	            // Enter the corresponding digit of the OTP

	           
	            Thread.sleep(200);
	           
	       }


	        System.out.println("OTP entered successfully");
            
            WebElement verifyButton=wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@class='dSM5Ub VGJZ_8 KtnAdx']")));
            verifyButton.click();
            System.out.println("Verify button clicked");

		   
		   
		   //driver.findElement(By.linkText("Verify"));
		  
		   }
	   
	   catch (MailosaurException | IOException e) {
           e.printStackTrace();
       }
	}
		
	@Then("user should be redircted to login page")
	public void user_should_be_redircted_to_login_page() {
	    // Write code here that turns the phrase above into concrete actions
	    System.out.println("Unable to redicrect to login page as the OTP was not filled in ");
	}
	@Then("account name should be displayed")
	public void account_name_should_be_displayed() {
	    // Write code here that turns the phrase above into concrete actions
		System.out.println("User is not logged in");
		
	}
	
	@After
	public void teardown()
	{
		if (driver !=null)
		{
			driver.quit();
		}
	}
	
	
	
}
