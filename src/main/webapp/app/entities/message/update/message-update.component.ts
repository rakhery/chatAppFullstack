import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { MessageFormService, MessageFormGroup } from './message-form.service';
import { IMessage } from '../message.model';
import { MessageService } from '../service/message.service';
import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { ICanal } from 'app/entities/canal/canal.model';
import { CanalService } from 'app/entities/canal/service/canal.service';

@Component({
  selector: 'jhi-message-update',
  templateUrl: './message-update.component.html',
})
export class MessageUpdateComponent implements OnInit {
  isSaving = false;
  message: IMessage | null = null;

  usersSharedCollection: IUser[] = [];
  canalsSharedCollection: ICanal[] = [];

  editForm: MessageFormGroup = this.messageFormService.createMessageFormGroup();

  constructor(
    protected messageService: MessageService,
    protected messageFormService: MessageFormService,
    protected userService: UserService,
    protected canalService: CanalService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareUser = (o1: IUser | null, o2: IUser | null): boolean => this.userService.compareUser(o1, o2);

  compareCanal = (o1: ICanal | null, o2: ICanal | null): boolean => this.canalService.compareCanal(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ message }) => {
      this.message = message;
      if (message) {
        this.updateForm(message);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const message = this.messageFormService.getMessage(this.editForm);
    if (message.id !== null) {
      this.subscribeToSaveResponse(this.messageService.update(message));
    } else {
      this.subscribeToSaveResponse(this.messageService.create(message));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMessage>>): void {
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

  protected updateForm(message: IMessage): void {
    this.message = message;
    this.messageFormService.resetForm(this.editForm, message);

    this.usersSharedCollection = this.userService.addUserToCollectionIfMissing<IUser>(this.usersSharedCollection, message.user);
    this.canalsSharedCollection = this.canalService.addCanalToCollectionIfMissing<ICanal>(this.canalsSharedCollection, message.canal);
  }

  protected loadRelationshipsOptions(): void {
    this.userService
      .query()
      .pipe(map((res: HttpResponse<IUser[]>) => res.body ?? []))
      .pipe(map((users: IUser[]) => this.userService.addUserToCollectionIfMissing<IUser>(users, this.message?.user)))
      .subscribe((users: IUser[]) => (this.usersSharedCollection = users));

    this.canalService
      .query()
      .pipe(map((res: HttpResponse<ICanal[]>) => res.body ?? []))
      .pipe(map((canals: ICanal[]) => this.canalService.addCanalToCollectionIfMissing<ICanal>(canals, this.message?.canal)))
      .subscribe((canals: ICanal[]) => (this.canalsSharedCollection = canals));
  }
}
