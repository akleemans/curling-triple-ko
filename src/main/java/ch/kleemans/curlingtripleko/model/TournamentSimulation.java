package ch.kleemans.curlingtripleko.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class TournamentSimulation {

  private List<Team> teams = new ArrayList<>();
  private List<Game> games = new ArrayList<>();
  private Map<Integer, Integer> results = new HashMap<>();
  int gamesToPlay;

  public TournamentSimulation(String[] tournamentDefinition, int gamesToPlay) {
    this.gamesToPlay = gamesToPlay;
    // System.out.println("Initializing games and teams...");
    createGames(tournamentDefinition);
    // System.out.println("# of games: " + games.size());
    checkConsistency();
    // System.out.println("Consistent game check!");
  }

  public TournamentResult play() {
    // System.out.println("Simulating tournaments...");
    int doubleCount = 0;
    for (int i = 0; i < gamesToPlay; i++) {
      boolean done = false;
      int count = 0;
      while (!done) {
        done = true;
        for (Game game : games) {
          if (!game.isDone()) {
            done = false;
            if (game.isReady()) {
              game.play();
            }
          }
        }

        if (count > 1000) {
          throw new IllegalArgumentException("Tournament not solvable.");
        }
        count++;
      }
      if (count > 100) {
        System.out.println("count:" + count);
      }

      // Evaluate & reset
      for (Team team : teams) {
        doubleCount += team.getDoubleCount();
        team.reset();
      }
      for (Game game : games) {
        game.reset();
      }
      // If two teams meet, this counts as one encounter
      doubleCount = doubleCount / 2;

      int previous = results.getOrDefault(doubleCount, 0);
      results.put(doubleCount, previous + 1);
      doubleCount = 0;
    }

    var total = new AtomicInteger();
    Map<Integer, Float> distribution = new HashMap<>();
    results.forEach(
        (k, v) -> {
          total.addAndGet(k * v);
          var percent = Math.round(v * 1.0f / gamesToPlay * 10000.0f) / 100.0f;
          if (percent > 0.1) {
            distribution.put(k, percent);
          }
        });
    return TournamentResult.builder()
        .distribution(distribution)
        .score(total.get() * 1.0f / gamesToPlay)
        .build();
  }

  private void createGames(String[] tournamentDefinition) {
    for (String line : tournamentDefinition) {
      String[] parts = line.split(" ");
      String gameName = parts[0];
      boolean createTeams = false;

      // Seeded games start with a *, create teams too for those
      if (gameName.startsWith("*")) {
        createTeams = true;
        gameName = gameName.substring(1);
      }
      Game gameA = getOrCreateGame(gameName, createTeams);

      if (!parts[1].equals("-")) {
        Game gameB = getOrCreateGame(parts[1], false);
        gameB.setOrigin(gameA, Outcome.WINNER);
      }
      if (!parts[2].equals("-")) {
        Game gameC = getOrCreateGame(parts[2], false);
        gameC.setOrigin(gameA, Outcome.LOSER);
      }
    }
  }

  private Game getOrCreateGame(String name, boolean createTeams) {
    Optional<Game> game = games.stream().filter(g -> g.getName().equals(name)).findAny();
    if (game.isEmpty()) {
      Game newGame = new Game(name);
      games.add(newGame);
      if (createTeams) {
        newGame.setSeededTeam(true);
        newGame.teamA = new Team("Team " + teams.size());
        teams.add(newGame.teamA);
        newGame.teamB = new Team("Team " + teams.size());
        teams.add(newGame.teamB);
      }
      return newGame;
    }
    return game.get();
  }

  private void checkConsistency() {
    for (Game game : games) {
      if (!game.isInitialized()) {
        throw new IllegalArgumentException("Game not initialized:" + game);
      }
    }
  }
}
