package com.ai;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.AbstractMap;

public class App {
  public static void main(String[] args) {
    int option = 0;
    parseFlags(args);

    do {
      option = mainMenu();

      switch (option) {
        case 1:
          System.out.println();
          int trainOption = TrainOption();

          switch (trainOption) {
            case 1:
              AbstractMap.SimpleImmutableEntry<String, String> value = onePhrase();
              if (value != null) {
                // TODO: Train the model with a phrase
                System.out.println(value);
              }
              break;

            case 2:
              String path = getPath();
              if (path != null) {
                // TODO: Train the model with a file
                System.out.println(path);
              }
              break;
          }

          System.out.println();
          break;

        case 2:
          System.out.println();
          System.out.println("Infer");
          System.out.println();
          break;
      }

    } while (option != 3);
  }

  private static int mainMenu() {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    int option = 0;

    do {
      System.out.println("What do you want to do?");
      System.out.println("  1) Train");
      System.out.println("  2) Infer");
      System.out.println("  3) Exit");
      System.out.print("Put the number of the option: ");

      try {
        option = Integer.parseInt(br.readLine());
      } catch (IOException | NumberFormatException e) {
        System.out.println("That's not a valid option.\n");
      }
    } while (option != 1 && option != 2 && option != 3);

    return option;
  }

  private static int TrainOption() {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    int option = 0;

    do {
      System.out.println("What kind of training do you want to do?");
      System.out.println("  1) One phrase");
      System.out.println("  2) Bulk file");
      System.out.println("  3) None");
      System.out.print("Put the number of the option: ");

      try {
        option = Integer.parseInt(br.readLine());
      } catch (IOException | NumberFormatException e) {
        System.out.println("That's not a valid option.\n");
      }
    } while (option != 1 && option != 2 && option != 3);

    return option;
  }

  private static AbstractMap.SimpleImmutableEntry<String, String> onePhrase() {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String phrase = "";
    String tag = "";

    System.out.print("Insert the phrase: ");
    try {
      phrase = br.readLine();
    } catch (IOException e) {
      System.out.println("An error occurred reading the phrase");
      return null;
    }

    System.out.print("Insert the tag: ");
    try {
      tag = br.readLine();
    } catch (IOException e) {
      System.out.println("An error occurred reading the tag");
      return null;
    }

    return new AbstractMap.SimpleImmutableEntry<String, String>(phrase, tag);
  }

  private static String getPath() {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String path = "";

    System.out.println("You are in: " + Path.of("").toAbsolutePath().toString());
    System.out.print("Insert the path to the file: ");

    try {
      path = br.readLine();
    } catch (IOException e) {
      System.out.println("An error occurred reading the path");
      return null;
    }

    return path;
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
