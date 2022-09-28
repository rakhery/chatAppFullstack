import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { CanalDetailComponent } from './canal-detail.component';

describe('Canal Management Detail Component', () => {
  let comp: CanalDetailComponent;
  let fixture: ComponentFixture<CanalDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [CanalDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ canal: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(CanalDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(CanalDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load canal on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.canal).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
