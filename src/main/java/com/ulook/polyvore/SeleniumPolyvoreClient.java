package com.ulook.polyvore;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ulook.polyvore.model.Item;
import com.ulook.polyvore.model.Outfit;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class SeleniumPolyvoreClient {

  private static final Logger LOGGER = Logger.getLogger(SeleniumPolyvoreClient.class.getCanonicalName());

  public void retrieveOutfits(List<String> hrefs) {
    hrefs.parallelStream().filter(StringUtils::isNoneBlank).map(this::retrieveSingleOutfit).filter(Objects::nonNull).forEach(this::persistToFileSystem);
  }

  private Outfit retrieveSingleOutfit(String link) {
    WebDriver driver = SeleniumPool.takeDriver();
    try {
      LOGGER.info("Goes for ::" + link);
      driver.navigate().to(link);
      Outfit outfit = new Outfit();

      List<WebElement> mainImg = driver.findElements(By.className("main_img"));

      outfit.setMainImage(retrieveImage(mainImg.get(1).getAttribute("src")));


      String favCount = driver.findElements(By.className("fav_count")).get(0).getText();
      try {
        outfit.setLikes(Integer.parseInt(favCount.replace(",", "")));
      } catch (Exception e) {
        e.printStackTrace();
      }

      outfit.setOutfitName(driver.getTitle().replaceAll("\\\\", " "));

      ArrayList<Item> items = new ArrayList<>();

      driver.findElements(By.className("img_size_m")).forEach(img -> {
        WebElement parent = img.findElement(By.xpath(".."));
        String itemHref = parent.getAttribute("href");
        Item item = retrieveItem(itemHref);
        if (item != null) {
          items.add(item);
        }
      });

      outfit.setItemList(items);
      LOGGER.info("_______________________________Finished parsing of ::" + outfit.getOutfitName());
      return outfit;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      SeleniumPool.putDriver(driver);
    }
    return null;
  }

  private boolean persistToFileSystem(Outfit outfit) {
    try {
      ObjectMapper om = new ObjectMapper();

      File dir = new File("output/" + outfit.getOutfitName().replaceAll("/", "").trim().replaceAll(" ", ""));
      dir.mkdirs();

      File outputfile = new File(dir, "outfit.png");
      try {

        ImageIO.write(outfit.getMainImage(), "png", outputfile);
        FileUtils.writeStringToFile(new File(dir, "outfit.json"), om.writeValueAsString(outfit));

      } catch (Exception e) {
        e.printStackTrace();
      }

      outfit.getItemList().parallelStream().filter(im -> im.getItemImage() != null && im.getItemTitle() != null).forEach(item -> {
        File itemFile = new File(dir, item.getItemTitle().trim().replaceAll("/", "").trim().replaceAll(" ", "") + ".png");
        try {
          ImageIO.write(item.getItemImage(), "png", itemFile);
          FileUtils.writeStringToFile(new File(dir, item.getItemTitle().replaceAll("/", "").trim().replaceAll(" ", "") + ".json"), om.writeValueAsString(item));
        } catch (Exception e) {
          e.printStackTrace();
        }

      });
      System.out.println(outfit.getOutfitName() + " :: saved ____________________" + outfit.getOutfitName());
    }catch (Exception e){
      LOGGER.info("Exception while persisting ::" + outfit.getOutfitName());
      e.printStackTrace();
    }
    return true;
  }

  private Item retrieveItem(String itemHref) {
    LOGGER.info("Goes for item ::" + itemHref);
    WebDriver driver = SeleniumPool.takeDriver();
    try {
      driver.navigate().to(itemHref);
      Item item = new Item();
      List<WebElement> crumb = driver.findElements(By.className("crumb"));
      if (crumb.size() < 2) {
        return null;
      }
      List<String> category = new ArrayList<>();
      crumb.forEach(cat -> {
        category.add(cat.findElement(By.xpath(".//span")).getText());
      });
      if (category.contains("Home") ||
          category.contains("Home Decor") ||
          category.contains("Jewelry") ||
          category.contains("Beauty Products") ||
          category.contains("Accessories") ||
          category.contains("Eyewear")) {
        return null;
      }
      item.setCategories(category);

      WebElement thingImg = driver.findElement(By.id("thing_img")).findElement(By.xpath(".//a")).findElement(By.xpath(".//img"));

      String imageHref = thingImg.getAttribute("src");
      String title = thingImg.getAttribute("title");

      List<WebElement> db = driver.findElements(By.className("bd"));
      WebElement element = db.get(1).findElement(By.xpath(".//div"));
      String desc = element.findElement(By.xpath(".//div")).getText();

      item.setItemImage(retrieveImage(imageHref));
      item.setItemTitle(title);
      item.setDesc(desc);
      return item;
    } catch (Exception e) {
      LOGGER.info("Exception while processing ::" + itemHref);
      e.printStackTrace();
      return null;
    } finally {
      SeleniumPool.putDriver(driver);
    }
  }

  private BufferedImage retrieveImage(String ref) {
    try {
      return ImageIO.read(new URL(ref));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

}
