package ch.kleemans.curlingtripleko.model;

import java.util.Map;
import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class TournamentResult {
  float score;
  Map<Integer, Float> distribution;
}
