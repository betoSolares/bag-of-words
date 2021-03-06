package com.ai;

import com.ai.bow.Bow;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.AbstractMap;

public class App {
  public static void main(String[] args) {
    int option = 0;
    Bow bow = new Bow();
    parseFlags(args);

    do {
      option = mainMenu();

      switch (option) {
        case 1:
          int trainOption = trainOption();

          switch (trainOption) {
            case 1:
              AbstractMap.SimpleImmutableEntry<String, String> value = onePhrase();
              if (value != null) bow.trainPhrase(value.getKey(), value.getValue());
              break;

            case 2:
              String path = getPath();
              if (path != null) bow.trainFile(path);
              break;
          }

          break;

        case 2:
          String phrase = getPhrase();
          if (phrase != null) {
            if (!bow.infer(phrase)) {
              String tag = getTag();
              bow.trainPhrase(phrase, tag);
            }
          }
          break;

        case 3:
          bow.showKnowledge();
          break;
      }

    } while (option != 4);
  }

  private static int mainMenu() {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    int option = 0;

    do {
      System.out.println("\nWhat do you want to do?");
      System.out.println("  1) Train");
      System.out.println("  2) Infer");
      System.out.println("  3) Show Knowledge");
      System.out.println("  4) Exit");
      System.out.print("Put the number of the option: ");

      try {
        option = Integer.parseInt(br.readLine());
      } catch (IOException | NumberFormatException e) {
        System.out.println("That's not a valid option.");
      }
    } while (option != 1 && option != 2 && option != 3 && option != 4);

    return option;
  }

  private static int trainOption() {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    int option = 0;

    do {
      System.out.println("\nWhat kind of training do you want to do?");
      System.out.println("  1) One phrase");
      System.out.println("  2) Bulk file");
      System.out.println("  3) None");
      System.out.print("Put the number of the option: ");

      try {
        option = Integer.parseInt(br.readLine());
      } catch (IOException | NumberFormatException e) {
        System.out.println("That's not a valid option.");
      }
    } while (option != 1 && option != 2 && option != 3);

    return option;
  }

  private static AbstractMap.SimpleImmutableEntry<String, String> onePhrase() {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String phrase = "";
    String tag = "";

    do {
      System.out.print("\nInsert the phrase: ");
      try {
        phrase = br.readLine();
      } catch (IOException e) {
        System.out.println("An error occurred reading the phrase");
        return null;
      }
    } while (phrase.isBlank());

    do {
      System.out.print("Insert the tag: ");
      try {
        tag = br.readLine();
      } catch (IOException e) {
        System.out.println("An error occurred reading the tag");
        return null;
      }
    } while (tag.isBlank());

    return new AbstractMap.SimpleImmutableEntry<String, String>(phrase.trim(), tag.trim());
  }

  private static String getPath() {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String path = "";

    do {
      System.out.println("\nYou are in: " + Path.of("").toAbsolutePath().toString());
      System.out.print("Insert the path to the file: ");

      try {
        path = br.readLine();
      } catch (IOException e) {
        System.out.println("An error occurred reading the path");
        return null;
      }
    } while (path.isBlank());

    return path.trim();
  }

  private static String getPhrase() {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String phrase = "";

    do {
      System.out.print("\nInsert the phrase: ");

      try {
        phrase = br.readLine();
      } catch (IOException e) {
        System.out.println("An error occurred reading the phrase");
        return null;
      }
    } while (phrase.isBlank());

    return phrase.trim();
  }

  private static String getTag() {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String tag = "";

    do {
      System.out.print("\nInsert the tag to retrain the model: ");

      try {
        tag = br.readLine();
      } catch (IOException e) {
        System.out.println("An error occurred reading the phrase");
        return null;
      }
    } while (tag.isBlank());

    return tag.trim();
  }

  private static void parseFlags(String[] flags) {
    for (String flag : flags) {
      if (flag.equals("--help") || flag.equals("-h")) showHelp();
      else showHelp("Invalid flag: " + flag);
    }
  }

  private static void showHelp() {
    helpMessage();
    System.exit(0);
  }

  private static void showHelp(String error) {
    System.out.println(error + "\n");
    helpMessage();
    System.exit(0);
  }

  private static void helpMessage() {
    System.out.println("bow 1.0");
    System.out.println("Simple bag of words model for classifying text.\n");
    System.out.println("USAGE:");
    System.out.println("\tbow [FLAGS]\n");
    System.out.println("FLAGS:");
    System.out.println("\t-h, --help        Prints help information");
  }
}
