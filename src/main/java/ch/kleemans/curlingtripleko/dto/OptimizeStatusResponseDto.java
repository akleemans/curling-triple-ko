package ch.kleemans.curlingtripleko.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OptimizeStatusResponseDto {
  float score;
  int round;
  String tournamentDefinition;
}
