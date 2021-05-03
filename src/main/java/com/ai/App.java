package com.ai;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class App {
  public static void main(String[] args) {
    parseFlags(args);
    int option = mainMenu();
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

    System.out.println();
    return option;
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
