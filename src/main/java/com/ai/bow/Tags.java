package com.ai.bow;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Tags {
  private Map<String, String> tags;

  public Tags() {
    InputStream resource = Tags.class.getResourceAsStream("/com/ai/supported-tags.csv");
    List<String> lines =
        new BufferedReader(new InputStreamReader(resource)).lines().collect(Collectors.toList());
    tags = new HashMap<String, String>();

    for (String line : lines) {
      String[] parts = line.split(",");
      if (!tags.containsKey(parts[0].toLowerCase()))
        tags.put(parts[0].toLowerCase(), parts[1].toLowerCase());
    }
  }

  public Map<String, String> getAll() {
    return tags;
  }

  public boolean existsTag(String tag) {
    return tags.containsKey(tag);
  }

  public boolean existsCode(String code) {
    return tags.containsValue(code);
  }

  public String getCode(String tag) {
    if (tags.containsKey(tag)) return tags.get(tag);
    return null;
  }
}
