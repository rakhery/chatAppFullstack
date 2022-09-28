import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { CanalComponent } from '../list/canal.component';
import { CanalDetailComponent } from '../detail/canal-detail.component';
import { CanalUpdateComponent } from '../update/canal-update.component';
import { CanalRoutingResolveService } from './canal-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const canalRoute: Routes = [
  {
    path: '',
    component: CanalComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: CanalDetailComponent,
    resolve: {
      canal: CanalRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: CanalUpdateComponent,
    resolve: {
      canal: CanalRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: CanalUpdateComponent,
    resolve: {
      canal: CanalRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(canalRoute)],
  exports: [RouterModule],
})
export class CanalRoutingModule {}
