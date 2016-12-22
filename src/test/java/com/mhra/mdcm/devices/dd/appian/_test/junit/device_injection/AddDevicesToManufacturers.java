package com.mhra.mdcm.devices.dd.appian._test.junit.device_injection;

import com.mhra.mdcm.devices.dd.appian._test.junit.common.Common;
import com.mhra.mdcm.devices.dd.appian.domains.junit.User;
import com.mhra.mdcm.devices.dd.appian.domains.newaccounts.DeviceData;
import com.mhra.mdcm.devices.dd.appian.pageobjects.LoginPage;
import com.mhra.mdcm.devices.dd.appian.pageobjects.MainNavigationBar;
import com.mhra.mdcm.devices.dd.appian.pageobjects.external.sections.AddDevices;
import com.mhra.mdcm.devices.dd.appian.pageobjects.external.sections.ManufacturerDetails;
import com.mhra.mdcm.devices.dd.appian.pageobjects.external.sections.ManufacturerList;
import com.mhra.mdcm.devices.dd.appian.utils.datadriven.ExcelDataSheet;
import com.mhra.mdcm.devices.dd.appian.utils.datadriven.JUnitUtils;
import com.mhra.mdcm.devices.dd.appian.utils.driver.BrowserConfig;
import com.mhra.mdcm.devices.dd.appian.utils.selenium.others.FileUtils;
import com.mhra.mdcm.devices.dd.appian.utils.selenium.others.TestHarnessUtils;
import org.hamcrest.Matchers;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by TPD_Auto on 01/11/2016.
 */
@RunWith(Parameterized.class)
public class AddDevicesToManufacturers extends Common {


    public static List<DeviceData> listOfDeviceData = new ArrayList<>();
    public static WebDriver driver;
    public static String baseUrl;
    private String username;
    private String password;

    private ManufacturerList manufacturerList;
    private ManufacturerDetails manufacturerDetails;
    private AddDevices addDevices;
    private MainNavigationBar mainNavigationBar;

    @Parameterized.Parameters(name = "{0}")
    public static Collection<User> spreadsheetData() throws IOException {
        ExcelDataSheet excelUtils = new ExcelDataSheet();//
        List<User> listOfUsers = excelUtils.getListOfUsers("configs/data/excel/users.xlsx", "DeviceSetupLogins");
        listOfUsers = excelUtils.filterUsersBy(listOfUsers, "manufacturer");
        log.info("Manufacturer Users : " + listOfUsers);
        return listOfUsers;
    }

    public AddDevicesToManufacturers(User user) {
        this.username = user.getUserName();
        this.password = user.getPassword();
    }

    @BeforeClass
    public static void setUpDriver() {
        if (driver == null) {
            listOfDeviceData = excelUtils.getListOfDeviceData("configs/data/excel/DevicesData.xlsx", "TestDataWellFormed_Simple");
            driver = new BrowserConfig().getDriver();
            baseUrl = FileUtils.getTestUrl();
            log.warn("\n\nRUNNING MANUFACTURER SMOKE TESTS");
        }
    }

    @AfterClass
    public static void clearBrowsers() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Before
    public void setupTest() {
        //driver.manage().deleteAllCookies();
    }


    @Test
    public void setUpInitialDeviceDataForManufacturer() {

        //Login to app and add devices to the manufacturer
        LoginPage loginPage = new LoginPage(driver);
        loginPage = loginPage.loadPage(baseUrl);
        mainNavigationBar = loginPage.loginAs(username, password, false);
        externalHomePage = mainNavigationBar.clickHome();

        //Click on a random manufacturer
        manufacturerList = externalHomePage.gotoListOfManufacturerPage();
        String name = manufacturerList.getARandomManufacturerName();
        String registered = manufacturerList.getRegistrationStatus(name);
        log.info("Manufacturer selected : " + name + ", is " + registered);
        manufacturerDetails = manufacturerList.viewAManufacturer(name);

        //Add devices
        if(registered!=null && registered.toLowerCase().equals("registered"))
            addDevices = manufacturerDetails.clickAddDeviceBtn();

        //Assumes we are in add device page
        DeviceData dd = listOfDeviceData.get(0);
        addDevices = addDevices.addFollowingDevice(dd);

        //Verify option to add another device is there
        boolean isVisible = addDevices.isOptionToAddAnotherDeviceVisible();
        Assert.assertThat("Expected to see option to : Add another device" , isVisible, Matchers.is(true));

        //Confirm
        addDevices = addDevices.proceedToPayment();
        addDevices = addDevices.submitRegistration();
        externalHomePage = addDevices.finish();
    }

    @Test
    public void asAUserIShouldSeeErrorMessagesIfCredentialsAreIncorrect() {

        LoginPage loginPage = new LoginPage(driver);
        loginPage = loginPage.loadPage(baseUrl);
        password = "IsIncorrectPassword";
        loginPage.loginAs(username, password, false);

        String expectedErrorMsg = "The username/password entered is invalid";
        loginPage = new LoginPage(driver);
        boolean isCorrect = loginPage.isErrorMessageCorrect(expectedErrorMsg);
        Assert.assertThat("Error message should contain : " + expectedErrorMsg, isCorrect, Matchers.is(true));
    }


    @Test
    public void checkCorrectLinksAreDisplayedForManufacturer() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage = loginPage.loadPage(baseUrl);
        MainNavigationBar mainNavigationBar = loginPage.loginAs(username, password, false);

        externalHomePage = mainNavigationBar.clickHome();
        String delimitedLinks = "Start now";
        boolean areLinksVisible = externalHomePage.isStartNowLinkDisplayed();
        Assert.assertThat("Expected to see the following links : " + delimitedLinks, areLinksVisible, Matchers.is(true));
    }


    @Test
    public void asAUserIShouldBeAbleToLoginAndLogout() {

        LoginPage loginPage = new LoginPage(driver);
        loginPage = loginPage.loadPage(baseUrl);
        MainNavigationBar mainNavigationBar = loginPage.loginAs(username, password, false);
        String expectedHeading = JUnitUtils.getExpectedHeading(username);

        boolean isCorrectPage = mainNavigationBar.isCorrectPage(expectedHeading);
        Assert.assertThat("Expected page : " + expectedHeading, isCorrectPage, Matchers.is(true));

        //Logout and verify its in logout page
        loginPage = JUnitUtils.logoutIfLoggedIn(username, loginPage);

        boolean isLoginPage = loginPage.isInLoginPage();
        Assert.assertThat("Expected tobe in login page", isLoginPage, Matchers.is(true));
    }

    @Override
    public String toString() {
        return "SmokeTestsManufacturers";
    }
}