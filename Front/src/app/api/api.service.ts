import { Injectable } from '@angular/core';
import { environment } from 'src/environments/environment';
import { HttpClient, HttpHeaders, HttpErrorResponse, HttpParams } from '@angular/common/http';;
import { Observable } from 'rxjs';

const httpOptions = {
  headers: new HttpHeaders({
    'Content-Type': 'application/json',
  })
};

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  constructor(private http: HttpClient) { }


  send(path: string, body?: Object): Observable<any> {
    return this.http.post<any>(`${environment.api_url}${path}`, body? JSON.stringify(body):null, httpOptions)
  }

  get(path: String): Observable<any> {
    return this.http.get<any>(`${environment.api_url}${path}`, httpOptions);
  }
}