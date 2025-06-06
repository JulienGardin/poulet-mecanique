import { Component, inject, OnInit } from '@angular/core';
import { DiscordChannel } from '../../data/discord-channel';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { DiscordDataService } from '../../services/discord-data.service';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { EventProperty } from '../../data/event-property';
import { EventPropertyService } from '../../services/event-property.service';
import { EventDialog } from './event.dialog';

@Component({
  selector: 'app-event',
  imports: [CommonModule, MatTableModule, MatButtonModule, MatIconModule, MatDialogModule],
  templateUrl: './event.component.html',
  styleUrl: './event.component.scss'
})
export class EventComponent implements OnInit {
  
  readonly dialog = inject(MatDialog);
  readonly eventConfigService = inject(EventPropertyService);
  readonly discordDataService = inject(DiscordDataService);

  data: EventProperty[] = [];
  channels: DiscordChannel[] = [];

  displayedColumns: string[] = ['label', 'channel', 'category', 'action'];

  ngOnInit(): void {
    this.loadList();
  }

  loadList() {
    this.eventConfigService.list().subscribe({
      next: (data) => {
        this.data = data;
      },
      error: (err) => { 
        console.error('Error loading Event list:', err);
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

  openDialog(eventConfig: EventProperty | null = null): void {
      const dialogRef = this.dialog.open(EventDialog, {data: eventConfig});
      dialogRef.afterClosed().subscribe(result => {
        if (!result) return;
        this.data = [];
        this.loadList();
      });
  }

  delete(eventConfig: EventProperty): void {
    if (!eventConfig) return;
    this.eventConfigService.delete(eventConfig.id).subscribe({
      next: () => {
        this.data = this.data.filter(item => item.id !== eventConfig.id);
      },
      error: (err) => {
        console.error('Error deleting Event:', err);
      }
    });
  }
  
  getChannelLabel(channelId: string): string {
    const channel = this.channels.find(c => c.id === channelId);
    return channel ? channel.label : channelId;
  }

}
