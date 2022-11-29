export class RateRequest {
  public constructor(
    public tournamentDefinition: string,
    public tournamentsToPlay: number
  ) {
  }
}
