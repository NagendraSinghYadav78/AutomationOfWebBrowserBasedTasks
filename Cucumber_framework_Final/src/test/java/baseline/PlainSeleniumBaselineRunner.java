package baseline;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * ADDED (Aug 2026 revision) -- Baseline B: plain Selenium WebDriver + TestNG,
 * with NO Cucumber/Gherkin/BDD layer and no SparkReport reporting mechanism.
 * This exercises the exact same UI actions, on the exact same application
 * (GreenKart) and search term ("Cucumber"), as the proposed Cucumber-BDD
 * framework's two scenarios (GreenKartStepDefinition / AddtoCart), so the
 * timing data collected here is directly comparable to
 * test-output/timing-log.csv under the same repeated-measures protocol
 * (Section 4.4 / Section 9 of the paper).
 *
 * Scenario names are suffixed "(Plain Selenium+TestNG baseline)" so they
 * appear as separate groups when analyze_timings.py processes the shared
 * timing-log.csv -- no changes to that script are required.
 *
 * Class name ends in "Runner" to match the existing Surefire include pattern
 * (**{@literal /}*Runner.java) in pom.xml.
 */
public class PlainSeleniumBaselineRunner {

    private WebDriver driver;
    private long startNanos;
    private static final String CSV_PATH = "test-output/timing-log.csv";
    private static final String BASE_URL = "https://rahulshettyacademy.com/seleniumPractise/#/";
    private static final String SEARCH_TERM = "Cucumber";

    @BeforeMethod
    public void setUp() {
        startNanos = System.nanoTime();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
    }

    @Test
    public void productSearchBaseline() {
        driver.get(BASE_URL);
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfElementLocated(By.className("search-keyword")));

        WebElement searchBox = driver.findElement(By.className("search-keyword"));
        searchBox.sendKeys(SEARCH_TERM);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(driver1 -> {
            WebElement el = driver1.findElement(By.cssSelector("div.product h4.product-name"));
            return el.getText().toLowerCase().contains(SEARCH_TERM.toLowerCase()) ? el : null;
        });

        WebElement firstResultName = driver.findElement(By.cssSelector("div.product h4.product-name"));
        String extractedProductName = firstResultName.getText();
        boolean matches = extractedProductName.toLowerCase().contains(SEARCH_TERM.toLowerCase());
        org.testng.Assert.assertTrue(matches,
                "Expected product name to contain \"" + SEARCH_TERM + "\", got: " + extractedProductName);
    }

    @Test
    public void addToCartBaseline() {
        driver.get(BASE_URL);
        new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.presenceOfElementLocated(By.className("search-keyword")));

        WebElement searchBox = driver.findElement(By.className("search-keyword"));
        searchBox.sendKeys(SEARCH_TERM);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(driver1 -> {
            WebElement el = driver1.findElement(By.cssSelector("div.product h4.product-name"));
            return el.getText().toLowerCase().contains(SEARCH_TERM.toLowerCase()) ? el : null;
        });

        List<WebElement> addToCartButtons = driver.findElements(By.xpath("//button[text()='ADD TO CART']"));
        if (!addToCartButtons.isEmpty()) {
            addToCartButtons.get(0).click();
        }

        WebElement cartIcon = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("a.cart-icon")));
        cartIcon.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='PROCEED TO CHECKOUT']")));
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        long endNanos = System.nanoTime();
        double durationMs = (endNanos - startNanos) / 1_000_000.0;
        String status = result.isSuccess() ? "PASS" : "FAIL";

        String scenarioLabel;
        if (result.getMethod().getMethodName().equals("productSearchBaseline")) {
            scenarioLabel = "Product search (Plain Selenium+TestNG baseline)";
        } else {
            scenarioLabel = "Add to cart (Plain Selenium+TestNG baseline)";
        }

        writeRow(scenarioLabel, status, durationMs);

        if (driver != null) {
            driver.quit();
        }
    }

    private synchronized void writeRow(String scenarioName, String status, double durationMs) {
        try {
            Files.createDirectories(Paths.get("test-output"));
            boolean isNew = !Files.exists(Paths.get(CSV_PATH));
            try (FileWriter fw = new FileWriter(CSV_PATH, true)) {
                if (isNew) {
                    fw.write("timestamp,scenario,status,duration_ms\n");
                }
                fw.write(Instant.now().toString() + "," + scenarioName + "," + status + "," + durationMs + "\n");
            }
        } catch (IOException e) {
            System.err.println("[PlainSeleniumBaselineRunner] Failed to write timing log: " + e.getMessage());
        }
    }
}
