import { Component, inject, Inject, OnInit } from '@angular/core';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { FeedProperty } from '../../data/feed-property';
import { FeedPropertyService } from '../../services/feed-property.service';
import { DiscordChannel } from '../../data/discord-channel';
import { DiscordDataService } from '../../services/discord-data.service';

@Component({
  selector: 'app-rss',
  imports: [MatDialogModule, MatButtonModule, FormsModule, ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatSelectModule],
  templateUrl: './rss.dialog.html',
  styleUrl: './rss.dialog.scss'
})
export class RssDialog implements OnInit {

  readonly rssConfigService = inject(FeedPropertyService);
  readonly discordDataService = inject(DiscordDataService);

  form: FormGroup = new FormGroup({});
  channels: DiscordChannel[] = [];

  constructor(@Inject(MAT_DIALOG_DATA) public data: FeedProperty) { 
    this.form = new FormGroup({
      id: new FormControl(data?.id || null),
      label: new FormControl(data?.label || '', Validators.required),
      discordChannel: new FormControl(data?.discordChannel || '', Validators.required),
      url: new FormControl(data?.url || '', [Validators.required, Validators.pattern('https?://.+')]),
      icon: new FormControl(data?.icon || '', [Validators.required, Validators.pattern('https?://.+')]),
      filter: new FormControl(data?.filter || '')
    });
  }

  ngOnInit() {
    this.discordDataService.channels().subscribe({
      next: (channels) => {
        this.channels = channels;
      },
      error: (err) => {
        console.error('Error loading Discord channels:', err);
      }
    });
  }

  submit() {
    this.rssConfigService.save(this.form.value).subscribe({
      next: (result) => {
        console.log('RSS configuration saved:', result);
        this.form.reset();
      },
      error: (err) => {
        console.error('Error saving RSS configuration:', err);
      }
    });
  }

}
