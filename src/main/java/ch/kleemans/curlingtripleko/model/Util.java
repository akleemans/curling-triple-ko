package ch.kleemans.curlingtripleko.model;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Util {

  private static final Map<String, Integer> roadRoundMap = new HashMap<>();
  private static final String DELIMITER = "\n";

  public static void initializeRoadRound(List<String> tournamentDefinition) {
    roadRoundMap.clear();
    int currentRound = 0;
    for (String line : tournamentDefinition) {
      if (line.length() == 0) {
        currentRound++;
        continue;
      }
      String match = line.split(" ")[0];
      roadRoundMap.put(match, currentRound);
    }
    System.out.println("roadRoundMap:" + roadRoundMap);
  }

  public static int roadRound(String name) {
    Integer roadRound = roadRoundMap.get(name);
    if (roadRound == null) {
      throw new IllegalArgumentException("Unknown match");
    }
    return roadRound;
  }

  public static List<String> stringToList(String str) {
    return Arrays.stream(str.split(DELIMITER))
        .filter(s -> s.length() > 1)
        .collect(Collectors.toList());
  }

  public static void initializeRoadRound(String str) {
    var tournamentDefinition = Arrays.stream(str.split(DELIMITER)).toList();
    roadRoundMap.clear();
    int currentRound = 0;
    for (String line : tournamentDefinition) {
      if (line.length() == 0) {
        currentRound++;
        continue;
      }
      String match = line.split(" ")[0];
      roadRoundMap.put(match, currentRound);
    }
  }

  public static String listToString(List<String> list) {
    return String.join(DELIMITER, list);
  }

  public static List<String> readFile(String fileName) {
    List<String> tournamentDefinition = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
      String line;
      while ((line = br.readLine()) != null) {
        tournamentDefinition.add(line);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    initializeRoadRound(tournamentDefinition);
    return tournamentDefinition.stream().filter(line -> line.length() != 0).toList();
  }
}
