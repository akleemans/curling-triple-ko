import {Component} from '@angular/core';
import {ChartConfiguration} from 'chart.js';
import {ApiService} from './api.service';
import {OptimizeRequest} from './model/optimize-request';
import {RateRequest} from './model/rate-request';

enum Mode {
  Rate,
  Optimize
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  public tournamentDefinition: string = '';
  public modeEnum = Mode;
  public mode = Mode.Rate;
  public score: number | undefined;
  public waitingForRequest = false;
  public tournamentsToPlayScore = 1000000;
  public tournamentsToPlayOptimize = 100000;
  public optimizationRounds = 100000;
  public searchDepth = 30;

  public bestDefinition = '';
  public round = 0;

  public barChartData: ChartConfiguration<'bar'>['data'] = {
    labels: [],
    datasets: [{data: [], label: ''}]
  };

  // ChartConfiguration<'bar'>['options']
  public barChartOptions = {
    responsive: false,
  };

  public constructor(private readonly apiService: ApiService) {
  }

  public rate() {
    this.score = undefined;
    this.waitingForRequest = true;
    this.mode = Mode.Rate;
    this.apiService.rate(new RateRequest(this.tournamentDefinition, this.tournamentsToPlayScore)).subscribe(response => {
      this.score = response.score;

      this.barChartData.labels = [];
      this.barChartData.datasets[0].data = [];
      // Prepare chart data
      for (const k in response.distribution) {
        const v = response.distribution[k];
        this.barChartData.labels.push(k);
        this.barChartData.datasets[0].data.push(v);
      }
      this.waitingForRequest = false;
    });
  }

  public optimize(): void {
    this.score = undefined;
    this.bestDefinition = '';
    this.round = 0;

    this.waitingForRequest = true;
    this.mode = Mode.Optimize;
    const optimizeRequest = new OptimizeRequest(this.tournamentDefinition, this.tournamentsToPlayOptimize,
      this.optimizationRounds, this.searchDepth);
    this.apiService.optimize(optimizeRequest)
    .subscribe(response => console.log('Optimize request succeeded:', response))
    setTimeout(() => this.pollOptimize(), 5000);
  }

  public cancelOptimize(): void {
    this.waitingForRequest = false;
    this.apiService.cancelOptimize()
    .subscribe(response => console.log('Cancel optimize request succeeded:', response))
  }

  private pollOptimize(): void {
    if (!this.waitingForRequest || this.mode == Mode.Rate) {
      return;
    }
    this.apiService.getOptimizeState().subscribe(response => {
      if (!this.waitingForRequest || this.mode == Mode.Rate) {
        return;
      }
      this.score = response.score;
      this.bestDefinition = response.tournamentDefinition;
      this.round = response.round;
      setTimeout(() => this.pollOptimize(), 5000);
    });
  }
}
