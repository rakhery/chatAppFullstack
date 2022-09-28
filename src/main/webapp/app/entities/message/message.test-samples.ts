import dayjs from 'dayjs/esm';

import { IMessage, NewMessage } from './message.model';

export const sampleWithRequiredData: IMessage = {
  id: 29027,
  content: 'turn-key SQL Oklahoma',
  createdAt: dayjs('2022-09-28T03:03'),
};

export const sampleWithPartialData: IMessage = {
  id: 76000,
  content: 'invoice',
  createdAt: dayjs('2022-09-27T17:19'),
  updateAt: dayjs('2022-09-27T23:33'),
};

export const sampleWithFullData: IMessage = {
  id: 35076,
  content: 'Florida B2C projection',
  createdAt: dayjs('2022-09-27T16:55'),
  updateAt: dayjs('2022-09-27T20:40'),
};

export const sampleWithNewData: NewMessage = {
  content: 'quantify cyan',
  createdAt: dayjs('2022-09-28T05:26'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
