import { Component, Inject, inject } from '@angular/core';
import { EventPropertyService } from '../../services/event-property.service';
import { DiscordDataService } from '../../services/discord-data.service';
import { FormControl, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { DiscordChannel } from '../../data/discord-channel';
import { MAT_DIALOG_DATA, MatDialogModule } from '@angular/material/dialog';
import { EventProperty } from '../../data/event-property';
import { GuildiDataService } from '../../services/guildi-data.service';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-event',
  imports: [MatDialogModule, MatButtonModule, FormsModule, ReactiveFormsModule, MatFormFieldModule, MatInputModule, MatSelectModule],
  templateUrl: './event.dialog.html',
  styleUrl: './event.dialog.scss'
})
export class EventDialog {

  readonly rssConfigService = inject(EventPropertyService);
  readonly discordDataService = inject(DiscordDataService);
  readonly guildiDataService = inject(GuildiDataService);

  form: FormGroup = new FormGroup({});
  channels: DiscordChannel[] = [];
  categories : string[] = [];

  constructor(@Inject(MAT_DIALOG_DATA) public data: EventProperty) { 
    this.form = new FormGroup({
      id: new FormControl(data?.id || null),
      label: new FormControl(data?.label || '', Validators.required),
      discordChannel: new FormControl(data?.discordChannel || '', Validators.required),
      category: new FormControl(data?.category || '', [Validators.required])
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
    this.guildiDataService.categories().subscribe({
      next: (categories) => {
        this.categories = categories;
      },
      error: (err) => {
        console.error('Error loading categories:', err);
      }
    });
  }

  submit() {
    this.rssConfigService.save(this.form.value).subscribe({
      next: (result) => {
        console.log('Event configuration saved:', result);
        this.form.reset();
      },
      error: (err) => {
        console.error('Event saving RSS configuration:', err);
      }
    });
  }

}
