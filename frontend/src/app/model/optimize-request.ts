export class OptimizeRequest {
  public constructor(
    public tournamentDefinition: string,
    public tournamentsToPlay: number,
    public optimizationRounds: number,
    public searchDepth: number
  ) {
  }
}
