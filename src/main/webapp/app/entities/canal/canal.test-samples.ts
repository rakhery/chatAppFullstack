import dayjs from 'dayjs/esm';

import { ICanal, NewCanal } from './canal.model';

export const sampleWithRequiredData: ICanal = {
  id: 5039,
  name: 'innovate',
  createdAt: dayjs('2022-09-27T18:00'),
};

export const sampleWithPartialData: ICanal = {
  id: 45083,
  name: 'Extended Generic',
  createdAt: dayjs('2022-09-27T17:58'),
};

export const sampleWithFullData: ICanal = {
  id: 11203,
  name: 'synergistic',
  createdAt: dayjs('2022-09-27T18:26'),
  updatedAt: dayjs('2022-09-27T18:12'),
  description: 'e-markets',
};

export const sampleWithNewData: NewCanal = {
  name: 'Cote',
  createdAt: dayjs('2022-09-28T00:24'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
