package ch.kleemans.curlingtripleko.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@Builder
public class RateResponseDto {
  float score;
  Map<Integer, Float> distribution;
}
