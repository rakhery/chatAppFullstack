import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { ICanal } from 'app/entities/canal/canal.model';

export interface IMessage {
  id: number;
  content?: string | null;
  createdAt?: dayjs.Dayjs | null;
  updateAt?: dayjs.Dayjs | null;
  user?: Pick<IUser, 'id' | 'login'> | null;
  canal?: Pick<ICanal, 'id'> | null;
}

export type NewMessage = Omit<IMessage, 'id'> & { id: null };
