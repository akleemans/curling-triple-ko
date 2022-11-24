package ch.kleemans.curlingtripleko.controller;

import ch.kleemans.curlingtripleko.dto.OptimizeRequestDto;
import ch.kleemans.curlingtripleko.dto.OptimizeStatusResponseDto;
import ch.kleemans.curlingtripleko.dto.RateRequestDto;
import ch.kleemans.curlingtripleko.dto.RateResponseDto;
import ch.kleemans.curlingtripleko.model.Util;
import ch.kleemans.curlingtripleko.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

  @Autowired TournamentService tournamentService;

  @PostMapping("/api/rate")
  public RateResponseDto rate(@RequestBody RateRequestDto rateRequestDto) {
    var list = Util.stringToList(rateRequestDto.getTournamentDefinition());
    var result = tournamentService.rateTournamentDefinition(list, rateRequestDto.getGamesToPlay());
    return RateResponseDto.builder()
        .score(result.getScore())
        .distribution(result.getDistribution())
        .build();
  }

  @PostMapping("/api/optimize")
  public void optimize(@RequestBody OptimizeRequestDto dto) {
    Util.initializeRoadRound(dto.getTournamentDefinition());

    var list = Util.stringToList(dto.getTournamentDefinition());
    tournamentService
        .startOptimizingTournament(list, dto.getTournamentsToSimulate(), dto.getSearchDepth())
        .handle(
            (result, throwable) -> {
              if (throwable != null) {
                System.out.println("Error with future!" + throwable.getMessage());
              }
              return null;
            });
  }

  @GetMapping("/api/optimize/status")
  public OptimizeStatusResponseDto getStatus() {
    return tournamentService.getOptimizeStatus();
  }

  @DeleteMapping("/api/optimize")
  public void cancelOptimize() {
    tournamentService.cancelOptimize();
  }
}
