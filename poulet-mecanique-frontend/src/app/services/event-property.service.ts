import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EventProperty } from '../data/event-property';

@Injectable({
  providedIn: 'root'
})
export class EventPropertyService {
  private baseUrl = '/api/eventproperty';

  constructor(private http: HttpClient) {}

  list(): Observable<EventProperty[]> {
    return this.http.get<EventProperty[]>(this.baseUrl);
  }

  save(rssConfig: any): Observable<EventProperty> {
    return this.http.post<EventProperty>(this.baseUrl, rssConfig);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
