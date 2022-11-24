package ch.kleemans.curlingtripleko.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OptimizeRequestDto {
  String tournamentDefinition;
  int tournamentsToSimulate;
  int searchDepth;
}
