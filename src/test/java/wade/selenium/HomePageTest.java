package wade.selenium;

import PageObjects.HomePage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class HomePageTest extends BaseTest {

    private WebDriver driver;
    private Logger l;

    @BeforeTest
    @Parameters("browser")
    public void intializeTest(String browser) throws IOException {
        l = LogManager.getFormatterLogger(HomePageTest.class.getName());
        l.trace("Initializing driver");
        driver = initializeDriver(browser);
        WebDriverWait w = new WebDriverWait(driver, 10);
    }

    @Test
    public void CheckAllLinks() throws InterruptedException {
        SoftAssert a = new SoftAssert();
        String url = "https://www.takealot.com/";
        l.trace("Opening webpage " + url);
        driver.get(url);
        l.trace("Retrieving HomePage object");
        HomePage homePage = new HomePage(driver);
        l.trace("Retrieving all page links");
        List<WebElement> allLinks = homePage.getAllLinks();
        int index = -1;
        for(WebElement link : allLinks){
            index++;
            l.trace("Testing link " + (link.getText().equals("") ? link.getAttribute("href") : link.getText()));
            try{
                HttpURLConnection conn = (HttpURLConnection) new URL(link.getAttribute("href")).openConnection();
                conn.setConnectTimeout(5000);
                conn.connect();
                int responseCode = conn.getResponseCode();
                if(responseCode >= 400){
                    l.error("Link with text " + link.getText() + " at index " + index + " is broken, response code = " + responseCode);
                }
                a.assertTrue(responseCode < 400);

            } catch (Exception e) {
                l.error("Link with text " + link.getText() + " at index " + index + "threw an exception: " + e.getMessage());
                a.assertTrue(false);
            }
        }
        a.assertAll();
    }

    @AfterTest
    public void teardown(){
        l.trace("Quitting driver");
        driver.quit();
    }

}
