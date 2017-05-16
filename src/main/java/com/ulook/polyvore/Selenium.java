package com.ulook.polyvore;

import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Created by aliaksei on 5/15/17.
 */
public class Selenium {
  public static void main(String[] args) {
    DesiredCapabilities caps;
    caps = new DesiredCapabilities();
    caps.setJavascriptEnabled(true);
    caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "/Users/aliaksei/Soft/phantomjs-2.1.1-macosx/bin/phantomjs");
    PhantomJSDriver phantomJSDriver = new PhantomJSDriver(caps);
    phantomJSDriver.navigate().to("http://automated-testing.info/t/session-id-is-null-using-webdriver-after-calling-quit/8730/3");
    phantomJSDriver.quit();
    phantomJSDriver.close();
  }
}
