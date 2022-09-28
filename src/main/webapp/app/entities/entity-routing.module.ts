import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'message',
        data: { pageTitle: 'Messages' },
        loadChildren: () => import('./message/message.module').then(m => m.MessageModule),
      },
      {
        path: 'canal',
        data: { pageTitle: 'Canals' },
        loadChildren: () => import('./canal/canal.module').then(m => m.CanalModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
