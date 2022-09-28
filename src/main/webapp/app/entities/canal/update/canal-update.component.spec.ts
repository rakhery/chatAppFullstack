import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { CanalFormService } from './canal-form.service';
import { CanalService } from '../service/canal.service';
import { ICanal } from '../canal.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { CanalUpdateComponent } from './canal-update.component';

describe('Canal Management Update Component', () => {
  let comp: CanalUpdateComponent;
  let fixture: ComponentFixture<CanalUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let canalFormService: CanalFormService;
  let canalService: CanalService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [CanalUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(CanalUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CanalUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    canalFormService = TestBed.inject(CanalFormService);
    canalService = TestBed.inject(CanalService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const canal: ICanal = { id: 456 };
      const users: IUser[] = [{ id: 96726 }];
      canal.users = users;

      const userCollection: IUser[] = [{ id: 84301 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [...users];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ canal });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const canal: ICanal = { id: 456 };
      const user: IUser = { id: 5617 };
      canal.users = [user];

      activatedRoute.data = of({ canal });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.canal).toEqual(canal);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICanal>>();
      const canal = { id: 123 };
      jest.spyOn(canalFormService, 'getCanal').mockReturnValue(canal);
      jest.spyOn(canalService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ canal });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: canal }));
      saveSubject.complete();

      // THEN
      expect(canalFormService.getCanal).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(canalService.update).toHaveBeenCalledWith(expect.objectContaining(canal));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICanal>>();
      const canal = { id: 123 };
      jest.spyOn(canalFormService, 'getCanal').mockReturnValue({ id: null });
      jest.spyOn(canalService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ canal: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: canal }));
      saveSubject.complete();

      // THEN
      expect(canalFormService.getCanal).toHaveBeenCalled();
      expect(canalService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ICanal>>();
      const canal = { id: 123 };
      jest.spyOn(canalService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ canal });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(canalService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
