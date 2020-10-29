package PageObjects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class HomePage {

    WebDriver driver;
    private List<WebElement> allLinks;
    Logger l;
    private final String URL = "https://www.takealot.com/";


    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.l = LogManager.getLogger(HomePage.class.getName());
        loadLinks();
    }

    private void loadLinks() {
        l.trace("Loading all links on page");
        allLinks = driver.findElements(By.tagName("a"));
    }

    /*
    Implemented as a more robust way to make sure as many links a spossible are loaded on the landing page before testing,
    as there is considerable lag between when driver considers the page fully loaded and when all possible links are loaded
    The more elegant solution would usually be the below, but it was not performing satisfactorily in this case:
        new WebDriverWait(firefoxDriver, pageLoadTimeout).until(
            webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
     */
    private void waitForAllLinksToLoad() throws InterruptedException {
        l.info("Waiting for all the links on the page to load");
        while(true){
            int baseSize = allLinks.size();
            Thread.sleep(1000);
            loadLinks();
            if(allLinks.size() == baseSize){
                l.trace("No new links loaded after 1 second. Returning current links");
                break;
            }
        }
    }

    public List<WebElement> getAllLinks() throws InterruptedException {
//        waitForAllLinksToLoad();
        l.info("Returning a list of " + allLinks.size() + " links");
        return allLinks;
    }

    public String getURL() {
        return URL;
    }
}
