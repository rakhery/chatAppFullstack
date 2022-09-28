import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { CanalFormService, CanalFormGroup } from './canal-form.service';
import { ICanal } from '../canal.model';
import { CanalService } from '../service/canal.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

@Component({
  selector: 'jhi-canal-update',
  templateUrl: './canal-update.component.html',
})
export class CanalUpdateComponent implements OnInit {
  isSaving = false;
  canal: ICanal | null = null;

  usersSharedCollection: IUser[] = [];

  editForm: CanalFormGroup = this.canalFormService.createCanalFormGroup();

  constructor(
    protected canalService: CanalService,
    protected canalFormService: CanalFormService,
    protected userService: UserService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ canal }) => {
      this.canal = canal;
      if (canal) {
        this.updateForm(canal);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const canal = this.canalFormService.getCanal(this.editForm);
    if (canal.id !== null) {
      this.subscribeToSaveResponse(this.canalService.update(canal));
    } else {
      this.subscribeToSaveResponse(this.canalService.create(canal));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICanal>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(canal: ICanal): void {
    this.canal = canal;
    this.canalFormService.resetForm(this.editForm, canal);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, ...(canal.users ?? []));
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, ...(this.canal?.users ?? []))))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));
  }
}
