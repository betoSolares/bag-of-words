package com.ai.bow;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Bow {

  private int total;
  private List<String> tags;
  private Map<String, Integer> tagTotals;
  private Map<String, Map<String, Integer>> tagWords;
  private JSONParser jsonParser;
  private Tags supportedTags;

  public Bow() {
    total = 0;
    tags = new ArrayList<String>();
    tagTotals = new HashMap<String, Integer>();
    tagWords = new HashMap<String, Map<String, Integer>>();
    jsonParser = new JSONParser();
    supportedTags = new Tags();
  }

  public void trainFile(String path) {
    try {
      List<String> lines = Files.lines(Path.of(path)).collect(Collectors.toList());
      int initial = total;

      for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i);
        String[] parts = line.split("\\|");

        if (parts.length >= 2) {
          String tag = parts[parts.length - 1].toLowerCase();
          List<String> words = removeStopWords(normalizePhrase(getPhrase(parts)), tag);
          train(tag, words);
        } else {
          System.out.println("The line " + (i + 1) + " is not in the correct format");
        }
      }
      System.out.println((total - initial) + " new words are analyzed");

    } catch (IOException e) {
      System.out.println("Can't read file: " + Path.of(path));
    }
  }

  public void trainPhrase(String phrase, String tag) {
    int initial = total;
    List<String> words = removeStopWords(normalizePhrase(phrase), tag.toLowerCase());
    train(tag.toLowerCase(), words);
    System.out.println((total - initial) + " new words are analyzed");
  }

  public boolean infer(String phrase) {
    List<String> words = normalizePhrase(phrase);

    if (total == 0) {
      System.out.println("\nThere is no data");
      return false;
    }

    if (tags.size() == 1) {
      System.out.println("\nThere must be at least two tags");
      return false;
    }

    Map<String, List<Double>> probability = getProbability(words);
    Map<String, Double> results = naiveBayes(probability);

    System.out.println("\nFeatures Set:");
    System.out.println(words);
    System.out.println("\nProbabilities:");

    for (Map.Entry<String, Double> pair : results.entrySet()) {
      System.out.println(pair.getKey() + " = " + pair.getValue());

      if (pair.getValue() >= 0.65) {
        System.out.println("\nThe tag is: " + pair.getKey());
        train(pair.getKey(), words);
        return true;
      }
    }

    System.out.println("\nCould not determine the tag");
    return false;
  }

  public void showKnowledge() {
    System.out.println("\nTotal numbers of words: " + total);
    System.out.println("Tags: " + String.join(", ", tags));
    System.out.println("Words per tag:");
    for (Map.Entry<String, Integer> pair : tagTotals.entrySet()) {
      System.out.println(pair.getKey() + " = " + pair.getValue());
    }
  }

  private void train(String tag, List<String> words) {
    total += words.size();
    if (!tags.contains(tag)) tags.add(tag);

    try {
      tagTotals.put(tag, tagTotals.get(tag) + words.size());
    } catch (NullPointerException e) {
      tagTotals.put(tag, words.size());
    }

    Map<String, Integer> count;
    try {
      count = new HashMap<String, Integer>(tagWords.get(tag));
    } catch (NullPointerException e) {
      count = new HashMap<String, Integer>();
    }

    for (String word : words) {
      try {
        count.put(word, count.get(word) + 1);
      } catch (NullPointerException e) {
        count.put(word, 1);
      }
    }
    tagWords.put(tag, count);
  }

  private Map<String, List<Double>> getProbability(List<String> words) {
    Map<String, List<Double>> table = new HashMap<String, List<Double>>();

    for (String tag : tagWords.keySet()) {
      int tagTotal = tagTotals.get(tag);
      List<Double> jointProbability = new ArrayList<Double>();
      Map<String, Integer> frequency = tagWords.get(tag);

      for (String word : words) {
        try {
          jointProbability.add((double) (frequency.get(word) + 1) / (tagTotal + words.size()));
        } catch (NullPointerException e) {
          jointProbability.add((double) 1 / (tagTotal + words.size()));
        }
      }

      table.put(tag, jointProbability);
    }

    return table;
  }

  private Map<String, Double> naiveBayes(Map<String, List<Double>> probability) {
    Map<String, Double> result = new HashMap<String, Double>();

    for (String key : probability.keySet()) {
      double numerator = 1;
      double denominator = 1;

      for (Double value : probability.get(key)) {
        numerator *= value;
        denominator *= value;
      }

      double tagProbability =
          (double) (tagTotals.get(key) + probability.get(key).size())
              / (total + probability.get(key).size());
      numerator *= tagProbability;
      denominator *= tagProbability;

      for (String subkey : probability.keySet()) {
        if (!subkey.equals(key)) {
          double newResult = 1;

          for (Double value : probability.get(subkey)) {
            newResult *= value;
          }

          tagProbability =
              (double) (tagTotals.get(subkey) + probability.get(subkey).size())
                  / (total + probability.get(subkey).size());
          newResult *= tagProbability;
          denominator += newResult;
        }
      }

      result.put(key, numerator / denominator);
    }

    return sortResults(result);
  }

  private Map<String, Double> sortResults(Map<String, Double> results) {
    Map<String, Double> cleanResult = new HashMap<String, Double>();

    for (Map.Entry<String, Double> pair : results.entrySet()) {
      if (Double.isNaN(pair.getValue())) cleanResult.put(pair.getKey(), 0.0);
      else cleanResult.put(pair.getKey(), pair.getValue());
    }

    Map<String, Double> newResult =
        cleanResult.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (oldValue, newValue) -> oldValue,
                    LinkedHashMap::new));

    return newResult;
  }

  private String getPhrase(String[] text) {
    String phrase = "";
    for (int i = 0; i < text.length - 1; i++) {
      phrase += text[i] + " ";
    }
    return phrase.trim();
  }

  private List<String> normalizePhrase(String phrase) {
    String[] words =
        phrase
            .replaceAll(
                "[\\|°¬\\!\"#\\$%&\\(\\)\\=\\?\'\\¿¡\\*\\+~\\[\\{\\^\\]\\};,\\.:\\-_\\<\\>`]", " ")
            .replaceAll("[A-Za-z]*\\d[A-Za-z]*", "")
            .toLowerCase()
            .trim()
            .split("\\s+");

    return Arrays.asList(words);
  }

  private List<String> removeStopWords(List<String> words, String tag) {
    List<String> goodWords = new ArrayList<String>();

    if (!supportedTags.existsTag(tag)) return words;

    try {
      Object obj =
          jsonParser.parse(
              new InputStreamReader(Bow.class.getResourceAsStream("/com/ai/stopwords-all.json")));

      JSONObject jsonObject = (JSONObject) obj;
      JSONArray stopwords = (JSONArray) jsonObject.get(supportedTags.getCode(tag));

      for (String word : words) {
        if (!stopwords.contains(word)) goodWords.add(word);
      }

      return goodWords;
    } catch (IOException | ParseException | NullPointerException e) {
      return words;
    }
  }
}
