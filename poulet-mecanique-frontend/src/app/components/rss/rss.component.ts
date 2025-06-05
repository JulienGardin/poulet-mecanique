import { Component, inject, OnInit } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { RssDialog } from './rss.dialog';
import { FeedProperty } from '../../data/feed-property';
import { FeedPropertyService } from '../../services/feed-property.service';
import { DiscordChannel } from '../../data/discord-channel';
import { DiscordDataService } from '../../services/discord-data.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-rss',
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule, MatDialogModule],
  templateUrl: './rss.component.html',
  styleUrl: './rss.component.scss'
})
export class RssComponent implements OnInit{
  
  readonly dialog = inject(MatDialog);
  readonly rssConfigService = inject(FeedPropertyService);
  readonly discordDataService = inject(DiscordDataService);

  data: FeedProperty[] = [];
  channels: DiscordChannel[] = [];

  displayedColumns: string[] = ['icon', 'label', 'channel', 'url', 'action'];

  ngOnInit(): void {
    this.loadList();
  }

  loadList() {
    this.rssConfigService.list().subscribe({
      next: (data) => {
        this.data = data;
      },
      error: (err) => { 
        console.error('Error loading RSS list:', err);
      }
    });
    this.discordDataService.channels().subscribe({
      next: (channels) => {
        this.channels = channels;
      },
      error: (err) => {
        console.error('Error loading Discord channels:', err);
      }
    });
  }

  openDialog(rssConfig: FeedProperty | null = null): void {
    const dialogRef = this.dialog.open(RssDialog, {data: rssConfig});
    dialogRef.afterClosed().subscribe(result => {
      if (!result) return;
      this.data = [];
      this.loadList();
    });
  }

  getChannelLabel(channelId: string): string {
    const channel = this.channels.find(c => c.id === channelId);
    return channel ? channel.label : channelId;
  }

}
