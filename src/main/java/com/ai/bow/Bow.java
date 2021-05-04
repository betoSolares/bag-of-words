package com.ai.bow;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Bow {
  public void trainFile(String path) {
    try {
      List<String> lines = Files.lines(Path.of(path)).collect(Collectors.toList());

      for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i);
        String[] parts = line.split("\\|");

        if (parts.length >= 2) {
          String tag = parts[parts.length - 1];
          List<String> words = normalizePhrase(getPhrase(parts));
        } else {
          System.out.println("The line " + (i + 1) + " is not in the correct format");
        }
      }
    } catch (IOException e) {
      System.out.println("Can't read file: " + Path.of(path));
    }
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
