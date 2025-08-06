import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FeedProperty } from '../data/feed-property';
import { DiscordChannel } from '../data/discord-channel';

@Injectable({
  providedIn: 'root'
})
export class GuildiDataService {
  private baseUrl = '/api/guildi';

  constructor(private http: HttpClient) {}

  categories(): Observable<string[]> {
    return this.http.get<string[]>(this.baseUrl + "/categories");
  }

}
