package stepDefinitions;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
public class GreenKartStepDefinition {
public WebDriver driver;
	
		@Given("User is on GreenCart Landing page")
		public void user_is_on_green_cart_landing_page() {
			driver = new ChromeDriver();
		    driver.get("https://www.flipkart.com/");
		    driver.manage().window().maximize();
		    
		   
		   
		}
		
		
		@When("user searched with Shortname {string} and extracted actual name of product")
		public void user_searched_with_shortname_and_extracted_actual_name_of_product(String shortName) {
			WebElement value = driver.findElement(By.className("Pke_EE"));
			value.sendKeys(shortName);
			value.sendKeys(Keys.ENTER);
			String productName = driver.findElement(By.className("KzDlHZ")).getText();
		    System.out.println(productName +"is extracted from Home Page");
	
		}
		
		
		@Then("User searched for shortname {string} in offers page to check if product exist with the same name")
		public void user_searched_for_same_shortname_in_offers_page_to_check_if_product_exist(String shortname) {
			driver.findElement(By.className("KzDlHZ")).click();
				//window handle
				Set<String> s1= driver.getWindowHandles();
				Iterator<String> i1 = s1.iterator();
				String parentWindow = i1.next();
				String childWindow = i1.next();
				driver.switchTo().window(childWindow);
		
				//search for product
				WebElement value =driver.findElement(By.className("zDPmFV"));
				value.sendKeys(shortname);
				value.sendKeys(Keys.ENTER);
			driver.quit();
		}


	

}
