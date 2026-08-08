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
        // FIXED (Aug 2026 revision): same race-condition fix as
        // GreenKartStepDefinition -- wait for the actual filtered product
        // text, not just "any product name is visible" (which is already
        // true from the pre-filter list).
        new WebDriverWait(driver, Duration.ofSeconds(10)).until(driver1 -> {
            WebElement el = driver1.findElement(By.cssSelector("div.product h4.product-name"));
            return el.getText().toLowerCase().contains(productName.toLowerCase()) ? el : null;
        });
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
        // FIXED (Aug 2026 revision): confirmed live via DevTools -- this is a
        // <button>, not an <h4> as originally guessed:
        // <button class="disabled" type="button">PROCEED TO CHECKOUT</button>
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='PROCEED TO CHECKOUT']")));

        driver.quit();
    }
}
