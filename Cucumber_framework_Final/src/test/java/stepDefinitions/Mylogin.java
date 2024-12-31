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
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import com.mailosaur.MailosaurClient;
import com.mailosaur.MailosaurException;
import com.mailosaur.models.Message;
import com.mailosaur.models.MessageSearchParams;
import com.mailosaur.models.SearchCriteria;
import java.util.*;



public class Mylogin {
	
	
	String apiKey = "BvcPY3K0oEcBrCkMiYfQzydzolNhUn3W";
	String serverId = "46m71knb";
	String serverDomain = "46m71knb.mailosaur.net";
	String from = "noreply@ncb.flipkart.com";
	
	
//public String getRandomEmail() {
//	return "user" +System.currentTimeMillis() + "@" + serverDomain;
//}
	
	
	public WebDriver driver;
	
	
	
	
	
	@Given("User is on the homepage of Flipkart")
	
	public void user_is_on_the_homepage_of_flipkart() {
		driver = new ChromeDriver();
	    driver.get("https://www.flipkart.com/");
	    driver.manage().window().maximize();
	    driver.findElement(By.linkText("Login")).click();
	}
	
	
	
	@When("user enters valid email {string} in Enter Email \\/ mobile number field")
	public void user_enters_valid_email_in_enter_email_mobile_number_field(String string) {

		WebElement value = driver.findElement(By.cssSelector("#container > div > div.VCR99n > div > div.Sm1-5F.col.col-3-5 > div > form > div.I-qZ4M.vLRlQb > input"));
		value.sendKeys(string);
		
		
	}
	@When("user clicks on request OTP button")
	public void user_clicks_on_request_otp_button() throws IOException, MailosaurException, InterruptedException {

		WebElement RequestOTPButton = driver.findElement(By.cssSelector("#container > div > div.VCR99n > div > div.Sm1-5F.col.col-3-5 > div > form > div.LSOAQH > button"));
		Thread.sleep(2000);
		RequestOTPButton.click();
		

		
		
	   try {
	     MailosaurClient mailosaur = new MailosaurClient(apiKey);
	    
	   
	    MessageSearchParams params = new MessageSearchParams();
	    params.withServer(serverId);
	   
	    SearchCriteria criteria = new SearchCriteria();
	    criteria.withSentTo("stand-morning@" + serverDomain);
	   
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
		   
		   
		   
		   
		   WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
	        for (int i = 0; i < otp.length(); i++) {

	            // Find the input field using XPath
	        	WebElement inputField = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@class='XDRRi5']//input[@type='text'][" + (i + 1) + "]")));
	        	inputField.click();
	        	inputField.sendKeys(String.valueOf(otp.charAt(i)));

	            // Enter the corresponding digit of the OTP

	            inputField.sendKeys(String.valueOf(otp.charAt(i)));
	            Thread.sleep(200);

	       }


		   
		   
		   
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
		driver.quit();
	}
	
	
}
