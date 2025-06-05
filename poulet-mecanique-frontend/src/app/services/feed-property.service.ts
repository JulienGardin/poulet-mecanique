import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FeedProperty } from '../data/feed-property';

@Injectable({
  providedIn: 'root'
})
export class FeedPropertyService {
  private baseUrl = '/api/feedproperty';

  constructor(private http: HttpClient) {}

  list(): Observable<FeedProperty[]> {
    return this.http.get<FeedProperty[]>(this.baseUrl);
  }

  save(rssConfig: any): Observable<FeedProperty> {
    return this.http.post<FeedProperty>(this.baseUrl, rssConfig);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
