package com.ulook.polyvore;


import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;
import java.util.stream.IntStream;

public class SeleniumPool {

  private static final Logger LOGGER = Logger.getLogger(SeleniumPool.class.getCanonicalName());

  private static BlockingQueue<WebDriver> pool;

  static {
    DesiredCapabilities caps;
    caps = new DesiredCapabilities();
    caps.setJavascriptEnabled(true);
    caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "/Users/aliaksei/Soft/phantomjs-2.1.1-macosx/bin/phantomjs");

    pool = new LinkedBlockingQueue<>(10);
    try {
      LOGGER.info("Starting inicializing drivers");
      pool.put(new PhantomJSDriver(caps));
      pool.put(new PhantomJSDriver(caps));
      pool.put(new PhantomJSDriver(caps));
      LOGGER.info("Halfway inicializing drivers");
      pool.put(new PhantomJSDriver(caps));
      pool.put(new PhantomJSDriver(caps));
      pool.put(new PhantomJSDriver(caps));
      pool.put(new PhantomJSDriver(caps));
      pool.put(new PhantomJSDriver(caps));
      pool.put(new PhantomJSDriver(caps));
      pool.put(new PhantomJSDriver(caps));
      LOGGER.info("Finished inicialization");
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    Thread clearCash = new Thread(() -> {
      while (true) {
        try {
          Thread.sleep(2000000);

            WebDriver driver = takeDriver();
            WebDriver driver2 = takeDriver();

            driver.quit();
            driver2.quit();

            putDriver(new PhantomJSDriver(caps));
            putDriver(new PhantomJSDriver(caps));

            LOGGER.info("Drivers cleaned !");

        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    });

    clearCash.setDaemon(true);
    clearCash.start();

  }

  private SeleniumPool() {
  }

  public static WebDriver takeDriver() {
    try {
      return pool.take();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static void putDriver(WebDriver driver) {
    pool.offer(driver);
  }


}

