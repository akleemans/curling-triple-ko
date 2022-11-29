package ch.kleemans.curlingtripleko.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OptimizeRequestDto {
  String tournamentDefinition;
  int tournamentsToPlay;
  int optimizationRounds;
  int searchDepth;
}
