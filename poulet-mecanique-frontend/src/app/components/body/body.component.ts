import { Component } from '@angular/core';
import { MatTabsModule } from '@angular/material/tabs';
import { RssComponent } from '../rss/rss.component';
import { EventComponent } from '../event/event.component';

@Component({
  selector: 'app-body',
  imports: [MatTabsModule, RssComponent, EventComponent],
  templateUrl: './body.component.html',
  styleUrl: './body.component.scss'
})
export class BodyComponent {

}
