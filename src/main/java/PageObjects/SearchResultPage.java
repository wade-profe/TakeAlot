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
import java.util.List;

public class SearchResultPage {

    private WebDriver driver;
    private String searchQuery;
    private Logger l;
    private WebDriverWait w;

    @FindBy(how = How.CSS, using = ".search-count.toolbar-module_search-count_P0ViI.search-count-module_search-count_1oyVQ")
    WebElement resultDescription;
    @FindBy(how = How.CSS, using = ".button.ghost.search-listings-module_load-more_OwyvW")
    WebElement loadMore;
    @FindBy(how = How.ID_OR_NAME, using = "searchDrop")
    WebElement filter;
    private By loadMoreLoadingIcon = By.cssSelector(".button.ghost.search-listings-module_load-more_OwyvW.loading");
    private By resultBy = By.cssSelector("div[class*='cell small'] div[class*='search-product']");
    private By priceBy = By.cssSelector("div[class*='cell small'] div[class*='search-product']  div[class*='card-section'] div[class*='price-wrapper'] ul li:nth-child(1) span");

    public SearchResultPage(WebDriver driver, String searchQuery) {
        this.driver = driver;
        this.searchQuery = searchQuery;
        PageFactory.initElements(driver, this);
        this.l = LogManager.getLogger(SearchResultPage.class.getName());
        w = new WebDriverWait(driver, 10);
    }

    public boolean checkResultDescription() {
        return resultDescription.getText().contains(this.searchQuery);
    }

    public void takeScreenshot() {
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(src, new File(System.getProperty("user.dir") + "\\src\\Logs\\" +
                    "Search result for search term '" + searchQuery + "'.png"));
        } catch (IOException e) {
            l.fatal(e.getMessage());
        }
    }

    public int getResultNumber() {
        return driver.findElements(resultBy).size();
    }

    public void loadMore() {
        loadMore.click();
        w.until(ExpectedConditions.invisibilityOfElementLocated(loadMoreLoadingIcon));
    }

    public boolean moreResultsAvailable() {
        try {
            return loadMore.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    public WebElement getFilter() {
        return filter;
    }

    public List<Integer> getAllPrices() {
        loadAllResults();
        List<WebElement> priceElements = driver.findElements(priceBy);
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

    public void loadAllResults() {
        while (moreResultsAvailable()) {
            loadMore();
        }
    }
}
