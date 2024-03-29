package wade.selenium;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class Listeners implements ITestListener {

    Logger l = LogManager.getLogger(Listeners.class.getName());
    WebDriver driver;

    @Override
    public void onTestStart(ITestResult result) {
        l.info("Starting test: " + result.getName() + " from " + result.getTestClass().getRealClass().getSimpleName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        l.info("Test " + result.getName() + " successfully executed in " + (result.getEndMillis() - result.getStartMillis()) + " milliseconds");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        l.fatal("Test " + result.getName() + " failed: " + result.getThrowable());
        try {
            driver = (WebDriver) result.getTestClass().getRealClass().getDeclaredField("driver").get(result.getInstance());
        } catch (IllegalAccessException | NoSuchFieldException e) {
            l.fatal(e.getMessage());
        }
        l.trace("Creating a screenshot of the error page");
        File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(src, new File(System.getProperty("user.dir") + "\\src\\Logs\\Screenshots\\" +
                    result.getName() + " failure - [" + Calendar.getInstance().getTime().toString().replace(":", "-") + "]" + ".png"));
        } catch (IOException e) {
            l.fatal(e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        l.info("Skipping " + result.getName());

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {

    }

    @Override
    public void onTestFailedWithTimeout(ITestResult result) {

    }

    @Override
    public void onStart(ITestContext context) {

    }

    @Override
    public void onFinish(ITestContext context) {
    }
}
