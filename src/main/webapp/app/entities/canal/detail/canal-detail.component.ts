import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ICanal } from '../canal.model';

@Component({
  selector: 'jhi-canal-detail',
  templateUrl: './canal-detail.component.html',
})
export class CanalDetailComponent implements OnInit {
  canal: ICanal | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ canal }) => {
      this.canal = canal;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
