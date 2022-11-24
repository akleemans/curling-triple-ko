export interface RateResponse {
  score: number;
  distribution: { [key: number]: number };
}
