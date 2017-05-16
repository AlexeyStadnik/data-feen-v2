package com.ulook.polyvore;


import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class OutfitProvider {

  private static final String URL = "http://www.polyvore.com/cgi/search.sets?.in={.in}&.out={.out}&request={request}&.locale={.locale}";
  private static final String REQUEST_PARAM = "{\"query\":\"%s\",\".search_src\":\"masthead_search\",\"page\":null,\".passback\":{\"next_token\":{\"limit\":\"30\",\"start\":%s}}}";

  private static final String PREFIX = "http://www.polyvore.com";

  private RestTemplate restTemplate = new RestTemplate();


  public List<String> retrieveOutfitLink(String criteria) {

    ArrayList<String> links = new ArrayList<>();

    IntStream
        .iterate(0, i -> i + 30).limit(30).parallel().
        forEach(i -> {
          Map<String, String> params = new HashMap<>();
          params.put(".in", "json");
          params.put(".out", "jsonx");
          params.put(".locale", "ru");
          params.put("request", String.format(REQUEST_PARAM, criteria, i));

          String response = restTemplate.getForObject(URL, String.class, params);

          JSONObject json = new JSONObject(response);
          String result = ((JSONObject) json.get("result")).getString("html");
          Elements select = Jsoup.parse(StringEscapeUtils.unescapeHtml(result)).select(".type_set");

          select.parallelStream().forEach(element -> links.add(PREFIX + element.child(0).child(0).attr("href").substring(2)));
        });
    return links;
  }

}
