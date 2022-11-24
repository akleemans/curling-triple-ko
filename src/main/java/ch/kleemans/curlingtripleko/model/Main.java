package ch.kleemans.curlingtripleko.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Main {

  public static void main(String[] args) {
    // Optimize existing tournament configuration
    //var tournamentLines = Util.readFile("tournament-data/matthias/16_md_v1.txt");
    //optimize(tournamentLines);

    // Rank existing tournament configuration
    rankExisting();
  }

  public static void optimize(List<String> tournamentLines) {
    var highScore = 10.0f;
    var bestDefinition = Util.listToString(tournamentLines);
    var list = Util.stringToList(bestDefinition);

    for (int i = 0; i < 100_000; i++) {
      if (i % 30 == 0) {
        // Reset list
        list = Util.stringToList(bestDefinition);
      }
      if (i < 1000 && i % 100 == 0 || i % 1000 == 0) {
        System.out.println(" i = " + i);
      }

      if (i > 0) {
        // Mutate, if not successful, continue
        if (!mutate(list)) {
          continue;
        }
      }

      var tournamentDefinition = list.toArray(new String[0]);
      TournamentSimulation tournamentSimulation;
      float score;
      try {
        tournamentSimulation = new TournamentSimulation(tournamentDefinition, 100_000);
        score = tournamentSimulation.play().getScore();
      } catch (IllegalArgumentException e) {
        // System.out.println("Not solvable");
        continue;
      }

      if (score < highScore) {
        highScore = score;
        bestDefinition = Util.listToString(list);
        System.out.println(
            "!! New low score: " + score + " - def: " + Arrays.toString(tournamentDefinition));
      }
    }
  }

  public static boolean mutate(List<String> list) {
    Random rand = new Random();
    int r = rand.nextInt(3);
    // Mutating the A-road does not seem necessary
    /*if (r == 0) {
      return mutate(list, "A", 2);
    } else */
    if (r == 0) {
      return mutate(list, "B", 1);
    } else if (r == 1) {
      return mutate(list, "B", 2);
    } else {
      return mutate(list, "C", 1);
    }
  }

  public static boolean mutate(List<String> list, String roundLetter, int elementNr) {
    List<Integer> indices = new ArrayList<>();
    for (int i = 0; i < list.size(); i++) {
      if (list.get(i).replace("*", "").startsWith(roundLetter)) {
        indices.add(i);
      }
    }

    // Pick two randoms & swap last element
    Random rand = new Random();
    int idx1 = indices.get(rand.nextInt(indices.size()));
    int idx2 = indices.get(rand.nextInt(indices.size()));
    String[] elements1 = list.get(idx1).split(" ");
    String[] elements2 = list.get(idx2).split(" ");

    if (elements1[elementNr].equals(elements2[elementNr])) {
      return false;
    }

    // "-" can not be moved
    if (elements1[elementNr].equals("-") || elements2[elementNr].equals("-")) {
      return false;
    }

    // Must be in same road-round
    if (Util.roadRound(elements1[elementNr]) != Util.roadRound(elements2[elementNr])) {
      return false;
    }

    String newElement1 = elements1[0] + " ";
    String newElement2 = elements2[0] + " ";

    if (elementNr == 1) {
      newElement1 += elements2[1] + " " + elements1[2];
      newElement2 += elements1[1] + " " + elements2[2];
    } else if (elementNr == 2) {
      newElement1 += elements1[1] + " " + elements2[2];
      newElement2 += elements2[1] + " " + elements1[2];
    } else {
      throw new IllegalArgumentException("Not an allowed elementNr:" + elementNr);
    }

    list.set(idx1, newElement1);
    list.set(idx2, newElement2);
    return true;
  }

  public static void rankExisting() {
    String[] fileNames = {
        "tournament-data/matthias2/16_md_v6.txt",
        "tournament-data/matthias2/24_ibdc19.txt",
        // "tournament-data/matthias2/24_swisscup_2021.txt",
        /*
        "tournament-data/16_ibdc22.txt",
        "tournament-data/16_ibdc22-optimized.txt",
        "tournament-data/24_ibdc19.txt",
        "tournament-data/24_ibdc19-optimized.txt",
        "tournament-data/32_gpinter21.txt",
        "tournament-data/32_gpinter21-optimized.txt",
        "tournament-data/32_ibdc20.txt",
        "tournament-data/32_ibdc20-optimized.txt",*/
    };
    for (String fileName : fileNames) {
      singleTournament(fileName);
    }
  }

  public static void singleTournament(String fileName) {
    var list = Util.readFile(fileName);

    var tournamentDefinition = list.toArray(new String[0]);
    var tournamentSimulation = new TournamentSimulation(tournamentDefinition, 10_000_000);
    var result = tournamentSimulation.play();
    System.out.println(fileName + " got score: " + result.getScore());
  }
}
