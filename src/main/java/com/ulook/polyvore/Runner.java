package com.ulook.polyvore;


import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Runner {

  private static final Logger LOGGER = Logger.getLogger(Runner.class.getCanonicalName());


  private static OutfitProvider outfitProvider = new OutfitProvider();
  private static SeleniumPolyvoreClient polyvoreClient = new SeleniumPolyvoreClient();

  private static String[] CRITERIAS = {"White Graduation Dresses", "Translucent Sunglasses", "Airport Chic", "Colorful Slides", "Gray T-Shirts", "Cropped Jeans", "Star and Stripes", "Floral Tops", "Fuchsia Skirts", "Navy Outfits", "Bold Accessories", "Trench Coats", "Tangerine", "Bold Patterns", "Top Handle Bags", "Grocery Shopping"};

  public static void main(String[] args) throws UnsupportedEncodingException, URISyntaxException, MalformedURLException, InterruptedException {
    List<List<String>> strings = new ArrayList<>();
    for(String criteria : Arrays.asList(CRITERIAS)) {
      strings.add(outfitProvider.retrieveOutfitLink(criteria));

    }

    for(List<String> links : strings) {
      Thread thread = new Thread(() -> polyvoreClient.retrieveOutfits(links));
      LOGGER.info("Starting thread ::" + thread.getName());
      thread.start();
      Thread.sleep(40000);
      LOGGER.info("Stopping thread ::" + thread.getName());
      thread.stop();
    }
    LOGGER.info("Retrive links ::" + strings.size());

  }

}

