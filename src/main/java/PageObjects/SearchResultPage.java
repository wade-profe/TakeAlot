package PageObjects;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class SearchResultPage {

    private final By loadMoreLoadingIcon = By.cssSelector(".button.ghost.search-listings-module_load-more_OwyvW.loading");
    private final By resultBy = By.cssSelector("div[class*='cell small'] div[class*='search-product']");
    private final By priceBy = By.cssSelector("div[class*='cell small'] div[class*='search-product']  div[class*='card-section'] div[class*='price-wrapper'] ul li:nth-child(1) span");
    private final By ratingBy = By.cssSelector("div[class*='cell small'] div[class*='search-product'] div[class*='product-card'] div[class*='card-section'] div[class*='rating']");
    @FindBy(how = How.CSS, using = ".search-count.toolbar-module_search-count_P0ViI.search-count-module_search-count_1oyVQ")
    WebElement resultDescription;
    @FindBy(how = How.CSS, using = ".button.ghost.search-listings-module_load-more_OwyvW")
    WebElement loadMore;
    @FindBy(how = How.ID_OR_NAME, using = "searchDrop")
    WebElement filter;
    private WebDriver driver;
    private String searchQuery;
    private Logger l;
    private WebDriverWait w;

    // TODO Investigate whether headless mode works
    //
    //TODO Add firefox test suite
    //
    //TODO ExtentReport

    public SearchResultPage(WebDriver driver, String searchQuery) {
        this.driver = driver;
        this.searchQuery = searchQuery;
        PageFactory.initElements(driver, this);
        this.l = LogManager.getLogger(SearchResultPage.class.getName());
        w = new WebDriverWait(driver, 10);
        l.trace("Creating search page object");
    }

    public boolean checkResultDescription() {
        l.trace("Checking if the search result description is the same as the search term used");
        return resultDescription.getText().contains(this.searchQuery);
    }

    public void takeScreenshot(String description) {
        l.trace("Taking a screenshot of the current page");
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(src, new File(System.getProperty("user.dir") + "\\src\\Logs\\Screenshots\\" +
                    description + " - " + Calendar.getInstance().getTime().toString().replace(":", "-") + ".png"));
        } catch (IOException e) {
            l.fatal(e.getMessage());
        }
    }

    public int getResultNumber() {
        l.trace("Returning the number of results for the search");
        return driver.findElements(resultBy).size();
    }

    public void loadMore() {
        l.trace("Clicking the load more button");
        loadMore.click();
        w.until(ExpectedConditions.invisibilityOfElementLocated(loadMoreLoadingIcon));
    }

    public boolean moreResultsAvailable() {
        l.trace("Checking if the search results contain more items to be loaded");
        try {
            boolean result = loadMore.isDisplayed();
            l.trace("More results are available");
            return result;
        } catch (Exception e) {
            l.trace("No more results are available");
            return false;
        }
    }

    public WebElement getFilter() {
        l.trace("Returning the filter menu object");
        return filter;
    }

    public List<Integer> getAllPrices() {
        loadAllResults();
        l.trace("Retrieving all the prices of the search results");
        List<WebElement> priceElements = driver.findElements(priceBy);
        priceElements = priceElements.stream().filter(we -> !(we.getText().toLowerCase().contains("from"))).collect(Collectors.toList());
        List<String[]> priceStrings = new ArrayList<>(priceElements.size());
        priceElements.forEach((e) -> {
            priceStrings.add(e.getText().split("R"));
        });
        List<Integer> prices = new ArrayList<>(priceElements.size());
        priceStrings.forEach((s) -> {
            prices.add(Integer.parseInt(s[1].replace(",", "").trim()));
        });
        return prices;
    }

    public List<Double> getAllRatings() {
        loadAllResults();
        l.trace("Retrieving all the ratings of the search results");
        List<WebElement> ratingElements = driver.findElements(ratingBy);
        List<String[]> ratingStrings = new ArrayList<>(ratingElements.size());
        ratingElements.forEach((we) -> {
            ratingStrings.add(we.getText().split(" "));
        });
        List<Double> ratings = new ArrayList<>(ratingStrings.size());
        ratingStrings.forEach((s) -> {
            ratings.add(Double.parseDouble(s[0].trim()));
        });
        return ratings;
    }

    public void loadAllResults() {
        l.trace("Loading all the results on the page");
        while (moreResultsAvailable()) {
            loadMore();
        }
    }
}
