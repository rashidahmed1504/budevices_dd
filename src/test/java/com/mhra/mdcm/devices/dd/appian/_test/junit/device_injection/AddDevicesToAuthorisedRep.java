package com.mhra.mdcm.devices.dd.appian._test.junit.device_injection;

import com.mhra.mdcm.devices.dd.appian._test.junit.common.Common;
import com.mhra.mdcm.devices.dd.appian.domains.junit.User;
import com.mhra.mdcm.devices.dd.appian.domains.newaccounts.DeviceData;
import com.mhra.mdcm.devices.dd.appian.pageobjects.LoginPage;
import com.mhra.mdcm.devices.dd.appian.pageobjects.MainNavigationBar;
import com.mhra.mdcm.devices.dd.appian.pageobjects.external.sections.AddDevices;
import com.mhra.mdcm.devices.dd.appian.utils.datadriven.ExcelDataSheet;
import com.mhra.mdcm.devices.dd.appian.utils.datadriven.JUnitUtils;
import com.mhra.mdcm.devices.dd.appian.utils.driver.BrowserConfig;
import com.mhra.mdcm.devices.dd.appian.utils.selenium.others.FileUtils;
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
public class AddDevicesToAuthorisedRep extends Common {

    public static List<DeviceData> listOfDeviceData = new ArrayList<>();
    public static WebDriver driver;
    public static String baseUrl;
    private String username;
    private String password;

    @Parameterized.Parameters(name = "{0}")
    public static Collection<User> spreadsheetData() throws IOException {
        ExcelDataSheet excelUtils = new ExcelDataSheet();//
        List<User> listOfUsers = excelUtils.getListOfUsers("configs/data/excel/users.xlsx", "DeviceSetupLogins");
        listOfUsers = excelUtils.filterUsersBy(listOfUsers, "authorised");
        log.info("AuthorisedRep Users : " + listOfUsers);
        return listOfUsers;
    }

    public AddDevicesToAuthorisedRep(User user) {
        this.username = user.getUserName();
        this.password = user.getPassword();
    }

    @BeforeClass
    public static void setUpDriver() {
        if (driver == null) {
            listOfDeviceData = excelUtils.getListOfDeviceData("configs/data/excel/DevicesData.xlsx", "TestDataWellFormed_Simple");
            driver = new BrowserConfig().getDriver();
            driver.manage().window().maximize();
            baseUrl = FileUtils.getTestUrl();
            log.warn("\n\nINSERT DEVICES AS AUTHORISEDREP USER");
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
    public void setUpInitialDeviceDataForAuthorisedRep() {

        //Login to app and add devices to the manufacturer
        LoginPage loginPage = new LoginPage(driver);
        loginPage = loginPage.loadPage(baseUrl);
        mainNavigationBar = loginPage.loginAsManufacturer(username, password);
        externalHomePage = mainNavigationBar.clickHome();

        //Click on a random manufacturer
        manufacturerList = externalHomePage.gotoListOfManufacturerPage();
        String name = manufacturerList.getARandomManufacturerName();
        String registered = manufacturerList.getRegistrationStatus(name);
        log.info("Manufacturer selected : " + name + ", is " + registered);
        manufacturerDetails = manufacturerList.viewAManufacturer(name);

        //Add devices: This needs to change to add all the devices
        if(registered!=null && registered.toLowerCase().equals("registered"))
            addDevices = manufacturerDetails.clickAddDeviceBtn();
        else
            addDevices = new AddDevices(driver);

        //Assumes we are in add device page
        //DeviceData dd = listOfDeviceData.get(0);
        //addDevices = addDevices.addFollowingDevice(dd);

        List<DeviceData> listOfDevicesWhichHadProblems = new ArrayList<>();

        int count = 0;
        int debugFromThisPosition = 0;
        //Lets try to add multiple devices, it will take a long time
        for(DeviceData dd: listOfDeviceData){

            //Only for DEBUGGING
            dd = listOfDeviceData.get(debugFromThisPosition);

            if(dd.validatedData.toLowerCase().equals("y")) {
                try {
                    //Only for DEBUGGING
                    System.out.println("\n----------------------------------------------------------");
                    System.out.println("Product number : " + (count+1) );
                    System.out.println("Line number : " + debugFromThisPosition );
                    System.out.println("Device Type : " + dd);
                    System.out.println("----------------------------------------------------------\n");

                    addDevices = addDevices.addFollowingDevice(dd);
                    boolean isVisible = addDevices.isOptionToAddAnotherDeviceVisible();
                    if (!isVisible) {
                        System.out.println("\nERROR ::::: Problem adding device TRY AGAIN");
                        //Try again :
                        addDevices = addDevices.addFollowingDevice(dd);
                        isVisible = addDevices.isOptionToAddAnotherDeviceVisible();
                        if (isVisible) {
                            count++;
                        } else {
                            throw new Exception("ERROR ::::: Problem adding device after 2 attempts");
                        }
                    } else {
                        count++;
                    }

                    if (count >= listOfDeviceData.size()-1 && debugFromThisPosition >= listOfDeviceData.size()-1) {
                        //All done
                        break;
                    }

                    //Try adding another device
                    if (isVisible)
                        addDevices = addDevices.addAnotherDevice();

                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("\nERROR ::::: Problem adding device");
                    listOfDevicesWhichHadProblems.add(dd);
                    count++;
//                    //Try next one
//                    externalHomePage = mainNavigationBar.clickHome();
//                    manufacturerList = externalHomePage.gotoListOfManufacturerPage();
//                    manufacturerDetails = manufacturerList.viewAManufacturer(name);
//
//                    //Add devices: This needs to change to add all the devices
//                    if (registered != null && registered.toLowerCase().equals("registered"))
//                        addDevices = manufacturerDetails.clickAddDeviceBtn();
//                    else
//                        addDevices = new AddDevices(driver);
                }
            }else{
                System.out.println("\n----------------------------------------------------------");
                System.out.println("Line number : " + debugFromThisPosition );
                System.out.println("Device Data Not Validated : \n" + dd.excelFileLineNumber);
                System.out.println("----------------------------------------------------------\n");
            }

            //Only for DEBUGGING
            debugFromThisPosition++;
        }

        System.out.println(listOfDevicesWhichHadProblems);

        //Verify option to add another device is there
        boolean isVisible = addDevices.isOptionToAddAnotherDeviceVisible();
        Assert.assertThat("Expected to see option to : Add another device" , isVisible, Matchers.is(true));

        //Confirm
        addDevices = addDevices.proceedToPayment();
        addDevices = addDevices.submitRegistration();
        externalHomePage = addDevices.finish();
    }

    @Override
    public String toString() {
        return "SmokeTestsAuthorisedRep";
    }
}
