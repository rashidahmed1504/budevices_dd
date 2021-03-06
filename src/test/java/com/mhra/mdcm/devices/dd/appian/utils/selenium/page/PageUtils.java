package com.mhra.mdcm.devices.dd.appian.utils.selenium.page;

import com.mhra.mdcm.devices.dd.appian.pageobjects._Page;
import com.mhra.mdcm.devices.dd.appian.utils.jenkins.ProxyAuthenticationSikuli;
import com.mhra.mdcm.devices.dd.appian.utils.jenkins.ProxyAuthenticationSikuliFirefox;
import com.mhra.mdcm.devices.dd.appian.utils.selenium.others.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.io.File;
import java.util.List;

/**
 * @author TPD_Auto
 */
public class PageUtils {

    public static void doubleClick(WebDriver driver, WebElement element) {
        Actions ac = new Actions(driver);
        ac.moveToElement(element).doubleClick(element).build().perform();
    }

    public static void singleClick(WebDriver driver, WebElement element) {
        Actions ac = new Actions(driver);
        ac.moveToElement(element).click(element).build().perform();
    }

    public static void clickIfVisible(WebDriver driver, WebElement element) {
        try {
            //IE sometimes doesn't click the element
            element.sendKeys(Keys.SPACE);
        } catch (Exception e) {
            try {
                if (element.isDisplayed() && !element.isSelected()) {
                    Actions ac = new Actions(driver);
                    ac.moveToElement(element).click(element).sendKeys(Keys.SPACE).build().perform();
                }
            } catch (Exception e2) {
            }
        }
    }


    public static String getText(WebElement element) {
        element.click();
        String existingName = element.getText();
        if (existingName.equals(""))
            existingName = element.getAttribute("value");
        return existingName;
    }

    public static void acceptAlert(WebDriver driver, String accept) {
        try {
            WaitUtils.waitForAlert(driver, 5, false);
            if (accept.equals("accept")) {
                driver.switchTo().alert().accept();
            } else {
                driver.switchTo().alert().dismiss();
            }
        } catch (Exception e) {
        }
    }

    public static void acceptAlert(WebDriver driver, boolean accept) {
        try {
            WaitUtils.waitForAlert(driver, 5, false);
            if (accept) {
                driver.switchTo().alert().accept();
            } else {
                driver.switchTo().alert().dismiss();
            }
        } catch (Exception e) {
        }
    }


    public static void acceptAlert(WebDriver driver, boolean accept, int timeToWait) {
        try {
            WaitUtils.waitForAlert(driver, timeToWait, false);

            if (accept) {
                driver.switchTo().alert().accept();
            } else {
                driver.switchTo().alert().dismiss();
            }
        } catch (Exception e) {
        }
    }


    /**
     * Native wait is required here, because the file dialog is a native element
     * @param element
     * @param fileName
     * @param timeWaitForItToBeClickable
     * @param timeWaitForDocumentUploadToFinish
     */
    public static void uploadDocument(WebElement element, String fileName, int timeWaitForItToBeClickable, int timeWaitForDocumentUploadToFinish) {
        String fullPath = FileUtils.getFileFullPath("tmp" + File.separator + "data" + File.separator + "reps", fileName);
        WaitUtils.nativeWaitDontUseMeOverSeleniumWaits(timeWaitForItToBeClickable);
        element.sendKeys(fullPath);
        //We will have to wait for uploading to finish
        WaitUtils.nativeWaitDontUseMeOverSeleniumWaits(timeWaitForDocumentUploadToFinish);
    }

    public static void clearAndTypeText(WebElement element, String text, boolean clearField) {
        if (clearField) {
            element.clear();
        }
        element.sendKeys(text);
    }


    public static void selectFromAutoSuggestedListItems(WebDriver driver, String elementPath, String countryName, boolean throwException) throws Exception {
        boolean completed = true;
        int count = 0;
        do {
            try {

                count++;    //It will go forever without this
                WebElement country = driver.findElements(By.cssSelector(elementPath)).get(0);
                country.sendKeys(countryName);
                WaitUtils.nativeWaitDontUseMeOverSeleniumWaits(1);
                //new WebDriverWait(driver, 3).until(ExpectedConditions.elementToBeClickable(By.cssSelector("li[role='option']")));
                WaitUtils.waitForElementToBeClickable(driver,By.cssSelector("li[role='option']") , _Page.TIMEOUT_5_SECOND, false);

                //Get list of options displayed
                WaitUtils.isPageLoadingComplete(driver, 1);
                List<WebElement> countryOptions = driver.findElements(By.cssSelector("li[role='option']"));
                WebElement item = countryOptions.get(0);
                String text = item.getText();
                //System.out.println("country : " + text);

                if(text!=null && !text.contains("Searching")) {
                    PageUtils.singleClick(driver, item);
                    completed = true;
                }
            } catch (Exception e) {
                completed = false;
            }
        } while (!completed && count < 3);

        if (!completed && throwException) {
            throw new Exception("Country name not selected");
        }
    }


    public static void selectFromAutoSuggestedListItemsManufacturers(WebDriver driver, String elementPath, String countryName, boolean throwException) throws Exception {
        boolean completed = true;
        int count = 0;
        do {
            try {

                count++;    //It will go forever without this
                WebElement country = driver.findElements(By.cssSelector(elementPath)).get(0);
                country.sendKeys(countryName);
                WaitUtils.nativeWaitDontUseMeOverSeleniumWaits(1);
                //new WebDriverWait(driver, 3).until(ExpectedConditions.elementToBeClickable(By.cssSelector("li[role='option']")));
                WaitUtils.waitForElementToBeClickable(driver,By.cssSelector("li[role='option']") , _Page.TIMEOUT_5_SECOND, false);

                //Get list of options displayed
                WaitUtils.nativeWaitDontUseMeOverSeleniumWaits(1);
                List<WebElement> countryOptions = driver.findElements(By.cssSelector("li[role='option']"));
                WebElement item = countryOptions.get(0);
                String text = item.getText();
                //System.out.println("country : " + text);

                if(text!=null && !text.contains("Searching")) {
                    PageUtils.singleClick(driver, item);
                    completed = true;
                }
            } catch (Exception e) {
                completed = false;
            }
        } while (!completed && count < 3);

        if (!completed && throwException) {
            throw new Exception("Country name not selected");
        }
    }

    public static void performBasicAuthentication(WebDriver driver, String baseUrl) {
        String browser = System.getProperty("current.browser");
        String iSremote = System.getProperty("is.remote");
        if(iSremote != null && browser!=null && browser.toLowerCase().equals("gc") && iSremote.equals("true")) {
            //Only required if behind a proxy : works for Chrome
            driver.get(baseUrl);
            try {
                new ProxyAuthenticationSikuli(driver, baseUrl).login();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if(iSremote != null && browser!=null && browser.toLowerCase().equals("ff")) {
            driver.get(baseUrl);
            try {
                new ProxyAuthenticationSikuliFirefox(driver, baseUrl).login();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            driver.manage().window().maximize();
        }
    }


    public static void selectFromDropDown(WebDriver driver, WebElement element, String text, boolean throwException){
        boolean completed = true;
        int count = 0;
        do {
            try {
                count++;    //It will go forever without this
                PageUtils.singleClick(driver, element);
                WaitUtils.isPageLoadingComplete(driver, _Page.TIMEOUT_PAGE_LOAD);
                WaitUtils.waitForElementToBeClickable(driver, By.xpath(".//div[contains(text(), '"+ text + "')]"), _Page.TIMEOUT_3_SECOND);
                WebElement titleToSelect = driver.findElement(By.xpath(".//div[contains(text(), '"+ text + "')]"));
                PageUtils.singleClick(driver, titleToSelect);
                completed = true;
            } catch (Exception e) {
                completed = false;
            }
        } while (!completed && count < 3);

    }

    public static boolean clickOneOfTheFollowing(WebDriver driver, WebElement btn, WebElement btn2, int timeout) {
        boolean clicked = clickElement(driver, btn, timeout, true);
        if(!clicked){
            clicked = clickElement(driver, btn2,timeout,true);
        }
        return clicked;
    }
    private static boolean clickElement(WebDriver driver, WebElement btn, int timeout, boolean singleClick) {
        boolean clicked = true;
        try {
            WaitUtils.waitForElementToBeClickable(driver, btn, timeout);
            if(singleClick) {
                singleClick(driver, btn);
            }else{
                doubleClick(driver, btn);
            }
        }catch (Exception e){
            clicked = false;
        }
        return clicked;
    }

    public static boolean isElementClickable(WebDriver driver, WebElement element, int timeoutSecond) {
        boolean clickable = true;
        try {
            WaitUtils.waitForElementToBeClickable(driver, element, timeoutSecond);
        } catch (Exception e) {
            //Its not clickable
            clickable = false;
        }
        return clickable;
    }
}
