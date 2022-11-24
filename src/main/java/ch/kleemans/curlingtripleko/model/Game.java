package ch.kleemans.curlingtripleko.model;

import java.util.Random;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class Game {

  Random rand = new Random();

  @NonNull
  String name;

  boolean seededTeam = false;

  Game teamAOrigin;
  Outcome teamAOutcome;

  Game teamBOrigin;
  Outcome teamBOutcome;

  Team teamA;
  Team teamB;

  Team winner;
  Team loser;

  public boolean isReady() {
    if (teamA == null && teamAOrigin != null && teamAOrigin.isDone()) {
      teamA = (teamAOutcome == Outcome.WINNER ? teamAOrigin.getWinner() : teamAOrigin.getLoser());
    }
    if (teamB == null && teamBOrigin != null && teamBOrigin.isDone()) {
      teamB = (teamBOutcome == Outcome.WINNER ? teamBOrigin.getWinner() : teamBOrigin.getLoser());
    }

    return teamA != null && teamB != null;
  }

  public boolean isDone() {
    return winner != null && loser != null;
  }

  public void setOrigin(Game game, Outcome outcome) {
    if (teamA == null && teamAOrigin == null) {
      teamAOrigin = game;
      teamAOutcome = outcome;
    } else if (teamB == null && teamBOrigin == null) {
      teamBOrigin = game;
      teamBOutcome = outcome;
    } else {
      throw new IllegalArgumentException("Both teams already set! game:" + this);
    }
  }

  public void play() {
    teamA.checkSetOpponent(teamB);
    teamB.checkSetOpponent(teamA);

    if (rand.nextBoolean()) {
      winner = teamA;
      loser = teamB;
    } else {
      winner = teamB;
      loser = teamA;
    }
  }

  public void reset() {
    if (!seededTeam) {
      teamA = null;
      teamB = null;
    }
    winner = null;
    loser = null;
  }

  public boolean isInitialized() {
    return (teamA != null && teamB != null) || (teamAOrigin != null && teamBOrigin != null);
  }
}
