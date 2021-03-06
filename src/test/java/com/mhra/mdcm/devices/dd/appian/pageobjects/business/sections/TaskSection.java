package com.mhra.mdcm.devices.dd.appian.pageobjects.business.sections;

import com.mhra.mdcm.devices.dd.appian.pageobjects._Page;
import com.mhra.mdcm.devices.dd.appian.pageobjects.business.TasksPage;
import com.mhra.mdcm.devices.dd.appian.utils.selenium.others.RandomDataUtils;
import com.mhra.mdcm.devices.dd.appian.utils.selenium.page.PageUtils;
import com.mhra.mdcm.devices.dd.appian.utils.selenium.page.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.List;

/**
 * Created by TPD_Auto
 */

public class TaskSection extends _Page {

    @FindBy(xpath = ".//h3")
    WebElement taskHeading;
    @FindBy(xpath = ".//a[contains(text(),'Organisation Details')]//following::p[1]")
    WebElement taskHeading2;

    //AWIP table headings
    @FindBy(xpath = ".//div[contains(text(),'Submitted')]")
    WebElement thSubmitted;
    @FindBy(xpath = ".//div[contains(text(),'Date')]")
    WebElement thDate;

    //Accept taskSection
    @FindBy(xpath = ".//button[contains(text(), 'Accept')]")
    WebElement accept;
    @FindBy(xpath = ".//button[.='Go Back']")
    WebElement goBack;

    //Approve reject taskSection
    @FindBy(xpath = ".//button[contains(text(), 'Approve')]")
    WebElement approveNewAccount;
    @FindBy(xpath = ".//button[.='Accept Registration']")
    WebElement approve;
    @FindBy(xpath = ".//button[.='Reject Registration']")
    WebElement reject;

    //Application WIP page
    @FindBy(xpath = ".//*[text()='Priority']/following::tr/td[1]")
    List<WebElement> listOfApplicationReferences;
    @FindBy(xpath = ".//*[contains(text(), 'Search by manufacturer')]/following::input[1]")
    WebElement tbxSearchByManufacturer;
    @FindBy(xpath = ".//button[text()='Search']")
    WebElement btnSearchForManufacuturer;
    @FindBy(xpath = ".//button[text()='Assign to myself']")
    WebElement btnAssignToMe;
    @FindBy(xpath = ".//button[text()='Yes']")
    WebElement btnConfirmYesAssignToMe;
    @FindBy(xpath = ".//button[text()='No']")
    WebElement btnConfirmNoAssignToMe;
    @FindBy(xpath = ".//button[contains(text(), 'Approve manufacturer')]")
    WebElement btnApproveManufacturer;
    @FindBy(xpath = ".//button[contains(text(), 'Approve all devices')]")
    WebElement btnApproveAllDevices;
    @FindBy(xpath = ".//button[contains(text(), 'Complete the application')]")
    WebElement btnCompleteTheApplication;
    @FindBy(xpath = ".//button[contains(text(), 'Approve account')]")
    WebElement btnApproveNewAccount;

    //BACS confirm payment
    @FindBy(xpath = ".//button[contains(text(), 'Confirm Payment')]")
    WebElement btnConfirmPayment;
    @FindBy(xpath = ".//*[contains(text(), 'Payment received')]/following::input[1]")
    WebElement tbxPaymentDate;
    @FindBy(xpath = ".//*[contains(text(), 'Payment received')]/following::input[2]")
    WebElement tbxPaymentHour;

    //Rejection reason
    @FindBy(xpath = ".//label[.='Other']")
    WebElement other;
    @FindBy(xpath = ".//textarea[1]")
    WebElement commentArea;
    @FindBy(xpath = ".//button[.='Submit']")
    WebElement submitBtn;
    @FindBy(xpath = ".//button[contains(text(), 'Save')]")
    WebElement btnSave;

    //Search and clear
    @FindBy(xpath = ".//button[text()='Clear']")
    WebElement btnClearSearchField;



    public TaskSection(WebDriver driver) {
        super(driver);
    }

    public TasksPage approveTaskNewAccount() {
        WaitUtils.waitForElementToBeClickable(driver, approveNewAccount, TIMEOUT_10_SECOND, false);
        PageUtils.singleClick(driver, approveNewAccount);
        return new TasksPage(driver);
    }


    public TaskSection sortBy(String sortBy, int numberOfTimesToClick) {
        //WaitUtils.isPageLoadingComplete(driver, TIMEOUT_PAGE_LOAD);
        WaitUtils.waitForElementToBeClickable(driver, thDate, TIMEOUT_10_SECOND, false);
        if (sortBy.equals("Submitted")) {
            for (int c = 0; c < numberOfTimesToClick; c++) {
                thSubmitted.click();
                WaitUtils.isPageLoadingComplete(driver, TIMEOUT_PAGE_LOAD);
                WaitUtils.nativeWaitDontUseMeOverSeleniumWaits(2);
            }
        }else if (sortBy.equals("Date")) {
            for (int c = 0; c < numberOfTimesToClick; c++) {
                thDate.click();
                WaitUtils.isPageLoadingComplete(driver, TIMEOUT_PAGE_LOAD);
                WaitUtils.nativeWaitDontUseMeOverSeleniumWaits(2);
            }
        }

        return new TaskSection(driver);
    }

    public TaskSection searchAWIPPageForAccount(String accountNameOrReference) {
        WaitUtils.waitForElementToBeClickable(driver, tbxSearchByManufacturer, TIMEOUT_10_SECOND);
        tbxSearchByManufacturer.sendKeys(accountNameOrReference);
        btnSearchForManufacuturer.click();
        listOfApplicationReferences.size();
        return new TaskSection(driver);
    }

    public boolean isSearchingCompleted() {
        WaitUtils.isPageLoadingComplete(driver, TIMEOUT_PAGE_LOAD);
        return PageUtils.isElementClickable(driver, btnClearSearchField, TIMEOUT_10_SECOND);
    }

    public TaskSection clickOnApplicationReferenceLink(String accountNameOrReference) {
        WaitUtils.waitForElementToBeClickable(driver, By.partialLinkText(accountNameOrReference), TIMEOUT_5_SECOND, false);
        WebElement taskLink = driver.findElement(By.partialLinkText(accountNameOrReference));
        taskLink.click();
        System.out.println("Reference found for : " + accountNameOrReference);
        return new TaskSection(driver);
    }


    public TaskSection assignTaskToMe() {
        WaitUtils.waitForElementToBeClickable(driver, btnAssignToMe, TIMEOUT_10_SECOND);
        btnAssignToMe.click();
        return new TaskSection(driver);
    }

    public TaskSection confirmAssignment(boolean clickYes) {
        if(PageUtils.isElementClickable(driver, btnConfirmYesAssignToMe, TIMEOUT_3_SECOND)) {
            if (clickYes) {
                btnConfirmYesAssignToMe.click();
            } else {
                btnConfirmNoAssignToMe.click();
            }
        }
        return new TaskSection(driver);
    }

    public TaskSection approveAWIPTaskNewAccount() {
        WaitUtils.waitForElementToBeClickable(driver, btnApproveNewAccount, TIMEOUT_3_SECOND);
        PageUtils.doubleClick(driver, btnApproveNewAccount);
        System.out.println("Task should be approved now");
        return new TaskSection(driver);
    }

    public TaskSection approveAWIPManufacturerTask() {
        WaitUtils.waitForElementToBeClickable(driver, btnApproveManufacturer, TIMEOUT_10_SECOND);
        PageUtils.doubleClick(driver, btnApproveManufacturer);
        System.out.println("Approved the manufacturer");
        return new TaskSection(driver);
    }

    public TaskSection approveAWIPAllDevices() {
        WaitUtils.waitForElementToBeClickable(driver, btnApproveAllDevices, TIMEOUT_10_SECOND);
        PageUtils.doubleClick(driver, btnApproveAllDevices);
        System.out.println("Approved all the devices");
        return new TaskSection(driver);
    }

    public TaskSection completeTheApplication() {
        WaitUtils.waitForElementToBeClickable(driver, btnCompleteTheApplication, TIMEOUT_10_SECOND);
        PageUtils.doubleClick(driver, btnCompleteTheApplication);
        System.out.println("Application completed");
        return new TaskSection(driver);
    }


    public TaskSection confirmPayment() {
        WaitUtils.waitForElementToBeClickable(driver, btnConfirmPayment, TIMEOUT_DEFAULT);
        PageUtils.doubleClick(driver, btnConfirmPayment);
        log.info("Confirm Payment");
        return new TaskSection(driver);
    }

    public TaskSection enterDateAndTimeOfPayment() {
        WaitUtils.waitForElementToBeClickable(driver, tbxPaymentDate, TIMEOUT_DEFAULT);
        tbxPaymentDate.sendKeys(RandomDataUtils.getDateInFutureDays(0), Keys.TAB);
        tbxPaymentHour.sendKeys("00:01", Keys.TAB);

        WaitUtils.waitForElementToBeClickable(driver, btnSave, TIMEOUT_15_SECOND);
        btnSave.click();

        return new TaskSection(driver);
    }
}
