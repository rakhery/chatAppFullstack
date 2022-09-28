import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { CanalService } from '../service/canal.service';

import { CanalComponent } from './canal.component';

describe('Canal Management Component', () => {
  let comp: CanalComponent;
  let fixture: ComponentFixture<CanalComponent>;
  let service: CanalService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'canal', component: CanalComponent }]), HttpClientTestingModule],
      declarations: [CanalComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(CanalComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(CanalComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(CanalService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.canals?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to canalService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getCanalIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getCanalIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
