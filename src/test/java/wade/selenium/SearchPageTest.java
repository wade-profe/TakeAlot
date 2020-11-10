package wade.selenium;

import PageObjects.HomePage;
import PageObjects.SearchResultPage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.io.IOException;

public class SearchPageTest extends BaseTest {

    public WebDriver driver;
    WebDriverWait w;
    private Logger l;

    @BeforeTest
    @Parameters("browser")
    public void intializeTest(String browser) throws IOException, InterruptedException {
        l = LogManager.getLogger(SearchPageTest.class.getName());
        l.info("Initializing driver");
        driver = initializeDriver(browser);
        w = new WebDriverWait(driver, 10);
    }

    @Test(enabled = false, dataProvider = "searchTerms")
    public void checkSearch(String searchTerm) throws InterruptedException {
        HomePage homePage = new HomePage(driver);
        SearchResultPage searchResultPage = homePage.performSearch(searchTerm);
        Assert.assertTrue(searchResultPage.checkResultDescription());
        searchResultPage.takeScreenshot();
        if (searchResultPage.moreResultsAvailable()) {
            int initialResults = searchResultPage.getResultNumber();
            searchResultPage.loadMore();
            Assert.assertTrue(searchResultPage.getResultNumber() > initialResults);
        }
    }

    @Test(enabled = true, dataProvider = "searchFilters") // Only runs once after last parameter
    public void testFiltering(String filter) {
        HomePage homePage = new HomePage(driver);
        SearchResultPage searchResultPage = homePage.performSearch("brandy");
        Assert.assertTrue(searchResultPage.checkResultDescription());
        Select filterMenu = new Select(searchResultPage.getFilter());
        filterMenu.selectByVisibleText(filter);
        w.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div[class*='listing-container-module_fade-cards']")));
        switch (filter) {
            case "Relevance":
                Assert.assertEquals(filterMenu.getFirstSelectedOption().getText(), "Relevance");
                break;
            case "Price: High to Low":
                searchResultPage.getAllPrices().forEach(System.out::println);
                break;
            default:
                System.out.println("Blah");
        }

    }

    @AfterMethod
    public void returnToLandingPage() {
        returnToLandingPage(driver);
    }

    @AfterTest
    public void teardown() {
        l.trace("Quitting driver");
        driver.quit();
    }

    @DataProvider
    public Object[] searchTerms() {
        Object[] searchTerms = new Object[5];
        searchTerms[0] = "samsung";
        searchTerms[1] = "brandy";
        searchTerms[2] = "nappies";
        searchTerms[3] = "kindle";
        searchTerms[4] = "books";
        return searchTerms;
    }

    @DataProvider
    public Object[] searchFilters() {
        Object[] searchTerms = new Object[5];
        searchTerms[0] = "Relevance";
        searchTerms[1] = "Price: High to Low";
        searchTerms[2] = "Price: Low to High";
        searchTerms[3] = "Top Rated";
        searchTerms[4] = "Newest Arrivals";
        return searchTerms;
    }

}
