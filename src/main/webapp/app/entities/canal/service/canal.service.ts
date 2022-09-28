import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { Search } from 'app/core/request/request.model';
import { ICanal, NewCanal } from '../canal.model';

export type PartialUpdateCanal = Partial<ICanal> & Pick<ICanal, 'id'>;

type RestOf<T extends ICanal | NewCanal> = Omit<T, 'createdAt' | 'updatedAt'> & {
  createdAt?: string | null;
  updatedAt?: string | null;
};

export type RestCanal = RestOf<ICanal>;

export type NewRestCanal = RestOf<NewCanal>;

export type PartialUpdateRestCanal = RestOf<PartialUpdateCanal>;

export type EntityResponseType = HttpResponse<ICanal>;
export type EntityArrayResponseType = HttpResponse<ICanal[]>;

@Injectable({ providedIn: 'root' })
export class CanalService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/canals');
  protected resourceSearchUrl = this.applicationConfigService.getEndpointFor('api/_search/canals');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(canal: NewCanal): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(canal);
    return this.http.post<RestCanal>(this.resourceUrl, copy, { observe: 'response' }).pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(canal: ICanal): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(canal);
    return this.http
      .put<RestCanal>(`${this.resourceUrl}/${this.getCanalIdentifier(canal)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(canal: PartialUpdateCanal): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(canal);
    return this.http
      .patch<RestCanal>(`${this.resourceUrl}/${this.getCanalIdentifier(canal)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestCanal>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCanal[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  search(req: Search): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestCanal[]>(this.resourceSearchUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  getCanalIdentifier(canal: Pick<ICanal, 'id'>): number {
    return canal.id;
  }

  compareCanal(o1: Pick<ICanal, 'id'> | null, o2: Pick<ICanal, 'id'> | null): boolean {
    return o1 && o2 ? this.getCanalIdentifier(o1) === this.getCanalIdentifier(o2) : o1 === o2;
  }

  addCanalToCollectionIfMissing<Type extends Pick<ICanal, 'id'>>(
    canalCollection: Type[],
    ...canalsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const canals: Type[] = canalsToCheck.filter(isPresent);
    if (canals.length > 0) {
      const canalCollectionIdentifiers = canalCollection.map(canalItem => this.getCanalIdentifier(canalItem)!);
      const canalsToAdd = canals.filter(canalItem => {
        const canalIdentifier = this.getCanalIdentifier(canalItem);
        if (canalCollectionIdentifiers.includes(canalIdentifier)) {
          return false;
        }
        canalCollectionIdentifiers.push(canalIdentifier);
        return true;
      });
      return [...canalsToAdd, ...canalCollection];
    }
    return canalCollection;
  }

  protected convertDateFromClient<T extends ICanal | NewCanal | PartialUpdateCanal>(canal: T): RestOf<T> {
    return {
      ...canal,
      createdAt: canal.createdAt?.toJSON() ?? null,
      updatedAt: canal.updatedAt?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restCanal: RestCanal): ICanal {
    return {
      ...restCanal,
      createdAt: restCanal.createdAt ? dayjs(restCanal.createdAt) : undefined,
      updatedAt: restCanal.updatedAt ? dayjs(restCanal.updatedAt) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestCanal>): HttpResponse<ICanal> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestCanal[]>): HttpResponse<ICanal[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
