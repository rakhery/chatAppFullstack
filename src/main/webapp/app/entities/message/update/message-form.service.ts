import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IMessage, NewMessage } from '../message.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IMessage for edit and NewMessageFormGroupInput for create.
 */
type MessageFormGroupInput = IMessage | PartialWithRequiredKeyOf<NewMessage>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IMessage | NewMessage> = Omit<T, 'createdAt' | 'updateAt'> & {
  createdAt?: string | null;
  updateAt?: string | null;
};

type MessageFormRawValue = FormValueOf<IMessage>;

type NewMessageFormRawValue = FormValueOf<NewMessage>;

type MessageFormDefaults = Pick<NewMessage, 'id' | 'createdAt' | 'updateAt'>;

type MessageFormGroupContent = {
  id: FormControl<MessageFormRawValue['id'] | NewMessage['id']>;
  content: FormControl<MessageFormRawValue['content']>;
  createdAt: FormControl<MessageFormRawValue['createdAt']>;
  updateAt: FormControl<MessageFormRawValue['updateAt']>;
  user: FormControl<MessageFormRawValue['user']>;
  canal: FormControl<MessageFormRawValue['canal']>;
};

export type MessageFormGroup = FormGroup<MessageFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class MessageFormService {
  createMessageFormGroup(message: MessageFormGroupInput = { id: null }): MessageFormGroup {
    const messageRawValue = this.convertMessageToMessageRawValue({
      ...this.getFormDefaults(),
      ...message,
    });
    return new FormGroup<MessageFormGroupContent>({
      id: new FormControl(
        { value: messageRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      content: new FormControl(messageRawValue.content, {
        validators: [Validators.required],
      }),
      createdAt: new FormControl(messageRawValue.createdAt, {
        validators: [Validators.required],
      }),
      updateAt: new FormControl(messageRawValue.updateAt),
      user: new FormControl(messageRawValue.user),
      canal: new FormControl(messageRawValue.canal),
    });
  }

  getMessage(form: MessageFormGroup): IMessage | NewMessage {
    return this.convertMessageRawValueToMessage(form.getRawValue() as MessageFormRawValue | NewMessageFormRawValue);
  }

  resetForm(form: MessageFormGroup, message: MessageFormGroupInput): void {
    const messageRawValue = this.convertMessageToMessageRawValue({ ...this.getFormDefaults(), ...message });
    form.reset(
      {
        ...messageRawValue,
        id: { value: messageRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): MessageFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
      updateAt: currentTime,
    };
  }

  private convertMessageRawValueToMessage(rawMessage: MessageFormRawValue | NewMessageFormRawValue): IMessage | NewMessage {
    return {
      ...rawMessage,
      createdAt: dayjs(rawMessage.createdAt, DATE_TIME_FORMAT),
      updateAt: dayjs(rawMessage.updateAt, DATE_TIME_FORMAT),
    };
  }

  private convertMessageToMessageRawValue(
    message: IMessage | (Partial<NewMessage> & MessageFormDefaults)
  ): MessageFormRawValue | PartialWithRequiredKeyOf<NewMessageFormRawValue> {
    return {
      ...message,
      createdAt: message.createdAt ? message.createdAt.format(DATE_TIME_FORMAT) : undefined,
      updateAt: message.updateAt ? message.updateAt.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
