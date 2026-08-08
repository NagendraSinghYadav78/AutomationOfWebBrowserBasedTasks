package stepDefinitions;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * FIXED (Aug 2026 revision): Previously targeted live https://www.flipkart.com/
 * with hardcoded Flipkart CSS classes and a hardcoded pincode. Retargeted to
 * the real GreenKart demo app for the same reasons as GreenKartStepDefinition.
 * Selectors are best-effort from the site's documented structure -- verify
 * against the live DOM before your first real run (see class-level note in
 * GreenKartStepDefinition.java for why this could not be confirmed live here).
 */
public class AddtoCart {

    public WebDriver driver;

    @Given("User is on Landing page of the application")
    public void user_is_on_Landing_page_of_the_application() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        driver.get("https://rahulshettyacademy.com/seleniumPractise/#/");
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfElementLocated(By.className("search-keyword")));
    }

    @When("user searched with product name {string}")
    public void user_searched_with_product_name(String productName) {
        WebElement searchBox = driver.findElement(By.className("search-keyword"));
        searchBox.sendKeys(productName);
        new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.product h4.product-name")));
    }

    @Then("User adds the product to cart")
    public void user_adds_the_product_to_cart() throws Exception {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<WebElement> addToCartButtons = driver.findElements(By.xpath("//button[text()='ADD TO CART']"));
        if (!addToCartButtons.isEmpty()) {
            addToCartButtons.get(0).click();
        }
        // Open the cart preview
        WebElement cartIcon = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.cart-icon")));
        cartIcon.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h4[text()='PROCEED TO CHECKOUT']")));

        driver.quit();
    }
}
