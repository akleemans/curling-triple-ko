import {HttpClient} from '@angular/common/http';
import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {OptimizeRequest} from './model/optimize-request';
import {OptimizeStatusResponse} from './model/optimize-status-response';
import {RateRequest} from './model/rate-request';
import {RateResponse} from './model/rate-response';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  public constructor(private readonly httpClient: HttpClient) {
  }

  public rate(rateRequest: RateRequest): Observable<RateResponse> {
    return this.httpClient.post<RateResponse>('http://localhost:8080/api/rate', rateRequest);
  }

  public optimize(optimizeRequest: OptimizeRequest): Observable<String> {
    return this.httpClient.post<String>('http://localhost:8080/api/optimize', optimizeRequest);
  }

  public getOptimizeState(): Observable<OptimizeStatusResponse> {
    return this.httpClient.get<OptimizeStatusResponse>('http://localhost:8080/api/optimize/status');
  }

  public cancelOptimize(): Observable<String> {
    return this.httpClient.delete<String>('http://localhost:8080/api/optimize');
  }
}
