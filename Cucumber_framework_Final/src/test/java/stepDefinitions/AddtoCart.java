package stepDefinitions;

import java.util.Iterator;
import java.util.Set;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class AddtoCart 
{
	public WebDriver driver;
	
	@Given("User is on Landing page of the application")
	public void user_is_on_Landing_page_of_the_application() {
		driver = new ChromeDriver();
	    driver.get("https://www.flipkart.com/"); 
	    driver.manage().window().maximize();
	   
	}
	
	@When("user searched with product name {string}")
	public void user_searched_with_product_name(String ProductName) {
		WebElement value = driver.findElement(By.className("Pke_EE"));
		value.sendKeys(ProductName);
		value.sendKeys(Keys.ENTER);
		
	}
	
	@Then("User adds the product to cart")
	public void user_adds_the_product_to_cart() throws Exception {
		driver.findElement(By.className("KzDlHZ")).click();
			//window handle
			
		
		
		Set<String> s1= driver.getWindowHandles();
			Iterator<String> i1 = s1.iterator();
			String parentWindow = i1.next();
			String childWindow = i1.next();
			driver.switchTo().window(childWindow);

			driver.findElement(By.className("AFOXgu")).sendKeys("334001");
			driver.findElement(By.className("i40dM4")).click();
			Thread.sleep(2000);
			driver.findElement(By.className("yeLeBC")).click();
			Thread.sleep(2000);
			driver.navigate().refresh();
			//driver.findElement(By.className("_1TWLMK icF5zO")).click();
			Thread.sleep(2000);
			
			
			
		
			driver.findElement(By.cssSelector("#container > div > div._39kFie.N3De93.JxFEK3._48O0EI > div.DOjaWF.YJG4Cf > div.DOjaWF.gdgoEp.col-5-12.MfqIAz > div:nth-child(2) > div > ul > li:nth-child(1) > button")).click();;
			
			Thread.sleep(2000);
			driver.navigate().refresh();
			
			
	
			
		driver.quit();
	}

}
