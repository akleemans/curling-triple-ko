package ch.kleemans.curlingtripleko.service;

import ch.kleemans.curlingtripleko.dto.OptimizeStatusResponseDto;
import ch.kleemans.curlingtripleko.model.TournamentResult;
import ch.kleemans.curlingtripleko.model.TournamentSimulation;
import ch.kleemans.curlingtripleko.model.Util;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class TournamentService {

  private static int round;
  private static float highScore;
  private static String bestDefinition;

  public TournamentResult rateTournamentDefinition(List<String> list, int gamesToPlay) {
    var tournamentDefinition = list.toArray(new String[0]);
    var tournamentSimulation = new TournamentSimulation(tournamentDefinition, gamesToPlay);
    return tournamentSimulation.play();
  }

  public OptimizeStatusResponseDto getOptimizeStatus() {
    return OptimizeStatusResponseDto.builder()
        .round(round)
        .score(highScore)
        .tournamentDefinition(bestDefinition)
        .build();
  }

  public void cancelOptimize() {
    round = -1;
  }

  @Async
  public CompletableFuture<Void> startOptimizingTournament(
      List<String> list, int tournamentsToSimulate, int searchDepth) {
    System.out.println(
        "Starting optimization for " + tournamentsToSimulate + " tournaments to simulate.");
    highScore = 10.0f;
    bestDefinition = Util.listToString(list);
    round = 0;

    for (int i = 0; i < tournamentsToSimulate; i++) {
      if (round == -1) {
        System.out.println("Cancelling job.");
        break;
      }
      round = i;
      if (round % 100 == 0) {
        System.out.println("Round: " + i);
      }

      if (i % searchDepth == 0) {
        // Reset list
        list = Util.stringToList(bestDefinition);
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
        continue;
      }

      if (score < highScore) {
        highScore = score;
        bestDefinition = Util.listToString(list);
      }
    }
    return null;
  }

  private static boolean mutate(List<String> list) {
    Random rand = new Random();
    int r = rand.nextInt(3);
    if (r == 0) {
      return mutate(list, "B", 1);
    } else if (r == 1) {
      return mutate(list, "B", 2);
    } else {
      return mutate(list, "C", 1);
    }
  }

  private static boolean mutate(List<String> list, String roundLetter, int elementNr) {
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
}
