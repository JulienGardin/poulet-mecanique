import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { FeedProperty } from '../data/feed-property';
import { DiscordChannel } from '../data/discord-channel';

@Injectable({
  providedIn: 'root'
})
export class DiscordDataService {
  private baseUrl = '/api/discord';

  constructor(private http: HttpClient) {}

  channels(): Observable<DiscordChannel[]> {
    return this.http.get<DiscordChannel[]>(this.baseUrl + "/channels");
  }

}
