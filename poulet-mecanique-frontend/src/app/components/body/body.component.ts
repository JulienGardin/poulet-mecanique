import { Component } from '@angular/core';
import { MatTabsModule } from '@angular/material/tabs';
import { RssComponent } from '../rss/rss.component';
import { GuildiComponent } from '../guildi/guildi.component';
import { MatSortModule } from '@angular/material/sort';

@Component({
  selector: 'app-body',
  imports: [MatTabsModule, RssComponent, GuildiComponent],
  templateUrl: './body.component.html',
  styleUrl: './body.component.scss'
})
export class BodyComponent {

}
