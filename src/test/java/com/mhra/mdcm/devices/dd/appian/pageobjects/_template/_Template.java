package com.mhra.mdcm.devices.dd.appian.pageobjects._template;

import com.mhra.mdcm.devices.dd.appian.pageobjects._Page;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by TPD_Auto
 */

public class _Template extends _Page {

    @Autowired
    public _Template(WebDriver driver) {
        super(driver);
    }

}
