package wade.selenium;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.asserts.SoftAssert;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class BaseTest {

    Logger l = LogManager.getLogger(BaseTest.class.getName());
    static Properties properties;
    Actions actions;
    WebDriverWait w;
    SoftAssert a;

    static {
        properties = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(System.getProperty("user.dir") + "\\global.properties");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            properties.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WebDriver initializeDriver(String browser) throws InterruptedException {
        a = new SoftAssert();
        WebDriver driver;
        l.info("Initializing driver for " + browser);
        if (browser.toLowerCase().contains("chrome")) {
            System.setProperty("webdriver.chrome.driver", properties.getProperty("chromedriver_path"));
            ChromeOptions options = new ChromeOptions();
            if (browser.toLowerCase().contains("headless")) {
                l.debug("Setting Chromedriver to headless mode");
                options.addArguments("--headless");
            }
            driver = new ChromeDriver(options);
        } else if (browser.toLowerCase().contains("firefox")) {
            System.setProperty("webdriver.gecko.driver", properties.getProperty("geckodriver_path"));
            FirefoxOptions options = new FirefoxOptions();
            if (browser.toLowerCase().contains("headless")) {
                l.debug("Setting Geckodriver to headless mode");
                options.setHeadless(true);
            }
            driver = new FirefoxDriver(options);
        } else {
            l.fatal(browser + " not recognized");
            driver = null;
        }

        if (driver != null) {
            l.trace("Maximizing browser window");
            driver.manage().window().maximize();
            int implicitWaitTime = 5;
            l.trace("Setting implicit wait to " + implicitWaitTime);
            driver.manage().timeouts().implicitlyWait(implicitWaitTime, TimeUnit.SECONDS);
            l.trace("Opening the landing page");
            driver.get(properties.getProperty("homePageUrl"));
            w = new WebDriverWait(driver, 5);
            try {
                w.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("img[alt*='Promotional popup']")));
                actions = new Actions(driver);
                actions.sendKeys(Keys.ESCAPE).build().perform();
            } catch (Exception e) {

            }
        }
        return driver;
    }

    public void returnToLandingPage(WebDriver driver) {
        l.debug("Returning to the landing page");
        driver.get(properties.getProperty("homePageUrl"));
    }
}
