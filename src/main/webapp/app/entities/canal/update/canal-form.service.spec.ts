import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../canal.test-samples';

import { CanalFormService } from './canal-form.service';

describe('Canal Form Service', () => {
  let service: CanalFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CanalFormService);
  });

  describe('Service methods', () => {
    describe('createCanalFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCanalFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            createdAt: expect.any(Object),
            updatedAt: expect.any(Object),
            description: expect.any(Object),
            users: expect.any(Object),
          })
        );
      });

      it('passing ICanal should create a new form with FormGroup', () => {
        const formGroup = service.createCanalFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            createdAt: expect.any(Object),
            updatedAt: expect.any(Object),
            description: expect.any(Object),
            users: expect.any(Object),
          })
        );
      });
    });

    describe('getCanal', () => {
      it('should return NewCanal for default Canal initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createCanalFormGroup(sampleWithNewData);

        const canal = service.getCanal(formGroup) as any;

        expect(canal).toMatchObject(sampleWithNewData);
      });

      it('should return NewCanal for empty Canal initial value', () => {
        const formGroup = service.createCanalFormGroup();

        const canal = service.getCanal(formGroup) as any;

        expect(canal).toMatchObject({});
      });

      it('should return ICanal', () => {
        const formGroup = service.createCanalFormGroup(sampleWithRequiredData);

        const canal = service.getCanal(formGroup) as any;

        expect(canal).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICanal should not enable id FormControl', () => {
        const formGroup = service.createCanalFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCanal should disable id FormControl', () => {
        const formGroup = service.createCanalFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
