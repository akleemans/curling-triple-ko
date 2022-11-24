package ch.kleemans.curlingtripleko.model;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class Team {

  @NonNull
  String name;
  int doubleCount = 0;
  Set<Team> opponents = new HashSet<>();

  public void checkSetOpponent(Team team) {
    if (opponents.contains(team)) {
      doubleCount += 1;
    } else {
      opponents.add(team);
    }
  }

  public void reset() {
    doubleCount = 0;
    opponents = new HashSet<>();
  }
}
