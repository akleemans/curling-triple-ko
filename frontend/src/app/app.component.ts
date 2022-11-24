import {Component} from '@angular/core';
import {ChartConfiguration} from 'chart.js';
import {ApiService} from './api.service';

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
    this.apiService.rate(this.tournamentDefinition).subscribe(response => {
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
    this.apiService.optimize(this.tournamentDefinition).subscribe(response => console.log(response))
    setTimeout(() => this.pollOptimize(), 5000);
  }

  public cancelOptimize(): void {
    this.waitingForRequest = false;
    this.apiService.cancelOptimize().subscribe(response => console.log(response))
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
