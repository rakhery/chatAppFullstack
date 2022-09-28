import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';

export interface ICanal {
  id: number;
  name?: string | null;
  createdAt?: dayjs.Dayjs | null;
  updatedAt?: dayjs.Dayjs | null;
  description?: string | null;
  users?: Pick<IUser, 'id' | 'login'>[] | null;
}

export type NewCanal = Omit<ICanal, 'id'> & { id: null };
