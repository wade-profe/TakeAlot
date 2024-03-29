package wade.selenium;

import PageObjects.HomePage;
import PageObjects.SearchResultPage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.util.ArrayList;
import java.util.List;

public class SearchPageTest extends BaseTest {

    public WebDriver driver;
    WebDriverWait w;
    private Logger l;

    @BeforeTest
    @Parameters("browser")
    public void intializeTest(String browser) throws InterruptedException {
        l = LogManager.getLogger(SearchPageTest.class.getName());
        driver = initializeDriver(browser);
        l.debug("Creating an explicit wait object");
        w = new WebDriverWait(driver, 10);
    }

    @Test(dataProvider = "searchTerms")
    public void checkSearch(String searchTerm) {
        HomePage homePage = new HomePage(driver);
        SearchResultPage searchResultPage = homePage.performSearch(searchTerm);
        Assert.assertTrue(searchResultPage.checkResultDescription());
        searchResultPage.takeScreenshot("Search result page for " + searchTerm);
        l.info("Checking the load more functionality");
        if (searchResultPage.moreResultsAvailable()) {
            int initialResults = searchResultPage.getResultNumber();
            searchResultPage.loadMore();
            Assert.assertTrue(searchResultPage.getResultNumber() > initialResults);
        } else {
            l.debug("No load more option available for this search result");
        }
    }

    @Test(dataProvider = "searchTerms")
    public void testSorting(String searchTerm) throws InterruptedException {
        l.trace("Running test with searchTerm: " + searchTerm);
        HomePage homePage = new HomePage(driver);
        SearchResultPage searchResultPage = homePage.performSearch(searchTerm);
        Assert.assertTrue(searchResultPage.checkResultDescription(), "Result description does not match search term");
        Select filterMenu = new Select(searchResultPage.getFilter());
        List<WebElement> filterOptions = filterMenu.getOptions();
        l.trace("Extracting sort menu options as text");
        List<String> filterOptionsText = new ArrayList<>(filterOptions.size());
        filterOptions.forEach((we) -> {
            filterOptionsText.add(we.getText());
        });
        List<WebElement> results;
        int referencePrice;
        double referenceRating;
        int errors = 0;
        for (String option : filterOptionsText) {
            l.info("Sorting by " + option);
            filterMenu.selectByVisibleText(option);
            l.trace("Waiting for page to load");
            w.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector("div[class*='listing-container-module_fade-cards']")));
            switch (option) {
                case "Price: High to Low":
                    results = searchResultPage.loadAllResults();
                    referencePrice = Integer.MAX_VALUE;
                    for (WebElement e : results) {
                        Integer currentPrice = searchResultPage.getResultPrice(e);
                        if (!(currentPrice <= referencePrice)) {
                            searchResultPage.scrollToElement(e);
                            l.fatal("Error with sort - " + option + " - the current price is " + currentPrice +
                                    " but the previous price was " + referencePrice);
                            searchResultPage.takeScreenshot(option.replace(":", "-") + " sorting error");
                            errors++;
                        }
                        referencePrice = currentPrice;
                    }
                    break;
                case "Price: Low to High":
                    results = searchResultPage.loadAllResults();
                    referencePrice = Integer.MIN_VALUE;
                    for (WebElement e : results) {
                        Integer currentPrice = searchResultPage.getResultPrice(e);
                        if (!(currentPrice >= referencePrice)) {
                            searchResultPage.scrollToElement(e);
                            l.fatal("Error with sort - " + option + " - the current price is " + currentPrice +
                                    " but the previous price was " + referencePrice);
                            searchResultPage.takeScreenshot(option.replace(":", "-") + " sorting error");
                            errors++;
                        }
                        referencePrice = currentPrice;
                    }
                    break;
                case "Top Rated":
                    results = searchResultPage.loadAllResults();
                    referenceRating = 5.0;
                    for (WebElement e : results) {
                        double currentRating = searchResultPage.getResultRating(e);
                        if (!(currentRating <= referenceRating)) {
                            searchResultPage.scrollToElement(e);
                            l.fatal("Error with sort - " + option + " - the current rating is " + currentRating +
                                    " but the previous rating was " + referenceRating);
                            searchResultPage.takeScreenshot(option + " sorting error");
                            errors++;
                        }
                        referenceRating = currentRating;
                    }
                    break;
                default:
                    if (!(filterMenu.getFirstSelectedOption().getText().equals(option))) {
                        l.fatal("The sort option" + option + " did not select properly");
                        errors++;
                    }
                    break;
            }
        }
        Assert.assertEquals(errors, 0, "There were " + errors + " errors with sorting. See logs for details.");
    }

    @AfterMethod
    public void returnToLandingPage() {
        returnToLandingPage(driver);
    }

    @AfterTest
    public void teardown() {
        l.info("Quitting driver");
        driver.quit();
    }

    @DataProvider
    public Object[] searchTerms() {
        Object[] searchTerms = new Object[4];
        searchTerms[0] = "senheiser";
        searchTerms[1] = "brandy";
        searchTerms[2] = "nappies";
        searchTerms[3] = "kindle";
        return searchTerms;
    }

}
