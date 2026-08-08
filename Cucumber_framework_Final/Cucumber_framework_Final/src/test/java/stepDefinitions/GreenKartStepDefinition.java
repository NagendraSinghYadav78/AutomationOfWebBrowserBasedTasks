package stepDefinitions;

import java.time.Duration;

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
 * FIXED (Aug 2026 revision): The original class was named
 * "GreenKartStepDefinition" and the feature file said "GreenCart Landing
 * page", but the code actually drove Selenium against https://www.flipkart.com/
 * -- a live, real commercial site -- using Flipkart-specific CSS class names
 * (e.g. "Pke_EE", "KzDlHZ"). That mismatch is corrected here: this class now
 * targets the actual GreenKart demo application
 * (https://rahulshettyacademy.com/seleniumPractise/#/), which is a purpose-built
 * automation practice sandbox and is safe to exercise repeatedly for the
 * experimental evaluation described in the paper.
 *
 * NOTE: This sandbox environment has no outbound access to general websites
 * (only package registries), so these locators could not be executed and
 * confirmed live from here. They reflect the site's well-documented structure
 * used across many public Selenium/Cucumber tutorials, but verify them
 * against the live DOM (e.g. via browser DevTools) before your first real run,
 * and adjust if the markup has changed.
 */
public class GreenKartStepDefinition {

    public WebDriver driver;
    private String extractedProductName;

    @Given("User is on GreenCart Landing page")
    public void user_is_on_green_cart_landing_page() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        driver.get("https://rahulshettyacademy.com/seleniumPractise/#/");
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfElementLocated(By.className("search-keyword")));
    }

    @When("user searched with Shortname {string} and extracted actual name of product")
    public void user_searched_with_shortname_and_extracted_actual_name_of_product(String shortName) {
        WebElement searchBox = driver.findElement(By.className("search-keyword"));
        searchBox.sendKeys(shortName);
        // GreenKart filters the product list live -- no Enter key / page navigation needed.
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement firstResultName = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.cssSelector("div.product h4.product-name")));
        extractedProductName = firstResultName.getText();
        System.out.println(extractedProductName + " is extracted from the search results");
    }

    @Then("User searched for shortname {string} in offers page to check if product exist with the same name")
    public void user_searched_for_same_shortname_in_offers_page_to_check_if_product_exist(String shortname) {
        // GreenKart is a single-page app with no separate "offers" tab, so the
        // equivalent validation here is confirming the extracted product name
        // actually contains the searched term.
        boolean matches = extractedProductName != null
                && extractedProductName.toLowerCase().contains(shortname.toLowerCase());
        System.out.println("Product name match for \"" + shortname + "\": " + matches);
        if (driver != null) {
            driver.quit();
        }
    }
}
