package com.ai.bow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Bow {

  private int total;
  private Map<String, Integer> tagTotals;
  private Map<String, Map<String, Integer>> tagWords;

  public Bow() {
    total = 0;
    tagTotals = new HashMap<String, Integer>();
    tagWords = new HashMap<String, Map<String, Integer>>();
  }

  public void trainFile(String path) {
    try {
      List<String> lines = Files.lines(Path.of(path)).collect(Collectors.toList());

      for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i);
        String[] parts = line.split("\\|");

        if (parts.length >= 2) {
          String tag = parts[parts.length - 1];
          List<String> words = normalizePhrase(getPhrase(parts));
          train(tag, words);
        } else {
          System.out.println("The line " + (i + 1) + " is not in the correct format");
        }
      }
    } catch (IOException e) {
      System.out.println("Can't read file: " + Path.of(path));
    }
  }

  public void trainPhrase(String phrase, String tag) {
    List<String> words = normalizePhrase(phrase);
    train(tag, words);
  }

  private void train(String tag, List<String> words) {
    total += words.size();
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
}
