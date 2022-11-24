package ch.kleemans.curlingtripleko.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RateRequestDto {
  String tournamentDefinition;
  int gamesToPlay;
}
