import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ICanal } from '../canal.model';
import { CanalService } from '../service/canal.service';

@Injectable({ providedIn: 'root' })
export class CanalRoutingResolveService implements Resolve<ICanal | null> {
  constructor(protected service: CanalService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ICanal | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((canal: HttpResponse<ICanal>) => {
          if (canal.body) {
            return of(canal.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
