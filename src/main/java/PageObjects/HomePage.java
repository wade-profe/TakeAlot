package PageObjects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class HomePage {

    WebDriver driver;
    private List<WebElement> allLinks;
    Logger l;
    @FindBy(name = "search")
    WebElement search;

    public HomePage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
        this.l = LogManager.getLogger(HomePage.class.getName());
        l.trace("Creating home page object");
    }

    private void loadLinks() {
        l.trace("Gathering all links currently on the page into a list");
        allLinks = driver.findElements(By.tagName("a"));
    }

    /*
    Implemented as a more robust way to make sure as many links as possible are loaded on the landing page before testing,
    as there is considerable lag between when driver considers the page fully loaded and when all possible links are loaded
    The more elegant solution would usually be the below, but it was not performing satisfactorily in this case:
        new WebDriverWait(firefoxDriver, pageLoadTimeout).until(
            webDriver -> ((JavascriptExecutor) webDriver).executeScript("return document.readyState").equals("complete"));
     */
    private void waitForAllLinksToLoad() throws InterruptedException {
        l.info("Waiting for all the links on the page to load");
        while(true){
            loadLinks();
            int baseSize = allLinks.size();
            Thread.sleep(1000);
            loadLinks();
            if(allLinks.size() == baseSize){
                l.trace("No new links loaded after 1 second");
                break;
            }
        }
    }

    public List<WebElement> getAllLinks() throws InterruptedException {
        waitForAllLinksToLoad();
        l.info("Acquired a list of " + allLinks.size() + " links on the current page");
        return allLinks;
    }

    public SearchResultPage performSearch(String searchQuery) {
        l.trace("Performing a search for " + searchQuery);
        search.click();
        search.sendKeys(searchQuery);
        search.sendKeys(Keys.ENTER);
        return new SearchResultPage(this.driver, searchQuery);
    }
}
