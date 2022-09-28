import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { ICanal, NewCanal } from '../canal.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ICanal for edit and NewCanalFormGroupInput for create.
 */
type CanalFormGroupInput = ICanal | PartialWithRequiredKeyOf<NewCanal>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends ICanal | NewCanal> = Omit<T, 'createdAt' | 'updatedAt'> & {
  createdAt?: string | null;
  updatedAt?: string | null;
};

type CanalFormRawValue = FormValueOf<ICanal>;

type NewCanalFormRawValue = FormValueOf<NewCanal>;

type CanalFormDefaults = Pick<NewCanal, 'id' | 'createdAt' | 'updatedAt' | 'users'>;

type CanalFormGroupContent = {
  id: FormControl<CanalFormRawValue['id'] | NewCanal['id']>;
  name: FormControl<CanalFormRawValue['name']>;
  createdAt: FormControl<CanalFormRawValue['createdAt']>;
  updatedAt: FormControl<CanalFormRawValue['updatedAt']>;
  description: FormControl<CanalFormRawValue['description']>;
  users: FormControl<CanalFormRawValue['users']>;
};

export type CanalFormGroup = FormGroup<CanalFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class CanalFormService {
  createCanalFormGroup(canal: CanalFormGroupInput = { id: null }): CanalFormGroup {
    const canalRawValue = this.convertCanalToCanalRawValue({
      ...this.getFormDefaults(),
      ...canal,
    });
    return new FormGroup<CanalFormGroupContent>({
      id: new FormControl(
        { value: canalRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(canalRawValue.name, {
        validators: [Validators.required],
      }),
      createdAt: new FormControl(canalRawValue.createdAt, {
        validators: [Validators.required],
      }),
      updatedAt: new FormControl(canalRawValue.updatedAt),
      description: new FormControl(canalRawValue.description),
      users: new FormControl(canalRawValue.users ?? []),
    });
  }

  getCanal(form: CanalFormGroup): ICanal | NewCanal {
    return this.convertCanalRawValueToCanal(form.getRawValue() as CanalFormRawValue | NewCanalFormRawValue);
  }

  resetForm(form: CanalFormGroup, canal: CanalFormGroupInput): void {
    const canalRawValue = this.convertCanalToCanalRawValue({ ...this.getFormDefaults(), ...canal });
    form.reset(
      {
        ...canalRawValue,
        id: { value: canalRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): CanalFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      createdAt: currentTime,
      updatedAt: currentTime,
      users: [],
    };
  }

  private convertCanalRawValueToCanal(rawCanal: CanalFormRawValue | NewCanalFormRawValue): ICanal | NewCanal {
    return {
      ...rawCanal,
      createdAt: dayjs(rawCanal.createdAt, DATE_TIME_FORMAT),
      updatedAt: dayjs(rawCanal.updatedAt, DATE_TIME_FORMAT),
    };
  }

  private convertCanalToCanalRawValue(
    canal: ICanal | (Partial<NewCanal> & CanalFormDefaults)
  ): CanalFormRawValue | PartialWithRequiredKeyOf<NewCanalFormRawValue> {
    return {
      ...canal,
      createdAt: canal.createdAt ? canal.createdAt.format(DATE_TIME_FORMAT) : undefined,
      updatedAt: canal.updatedAt ? canal.updatedAt.format(DATE_TIME_FORMAT) : undefined,
      users: canal.users ?? [],
    };
  }
}
