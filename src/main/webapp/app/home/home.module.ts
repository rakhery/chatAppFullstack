import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { SharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';
import { CanauxComponent } from './components/canaux/canaux.component';
import { MessagesComponent } from './components/messages/messages.component';
import { EspacesMessagesComponent } from './components/espaces-messages/espaces-messages.component';

@NgModule({
  imports: [SharedModule, RouterModule.forChild([HOME_ROUTE])],
  declarations: [HomeComponent, CanauxComponent, MessagesComponent, EspacesMessagesComponent],
})
export class HomeModule {}
