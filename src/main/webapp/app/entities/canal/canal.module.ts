import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { CanalComponent } from './list/canal.component';
import { CanalDetailComponent } from './detail/canal-detail.component';
import { CanalUpdateComponent } from './update/canal-update.component';
import { CanalDeleteDialogComponent } from './delete/canal-delete-dialog.component';
import { CanalRoutingModule } from './route/canal-routing.module';

@NgModule({
  imports: [SharedModule, CanalRoutingModule],
  declarations: [CanalComponent, CanalDetailComponent, CanalUpdateComponent, CanalDeleteDialogComponent],
})
export class CanalModule {}
