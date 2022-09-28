import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EspacesMessagesComponent } from './espaces-messages.component';

describe('EspacesMessagesComponent', () => {
  let component: EspacesMessagesComponent;
  let fixture: ComponentFixture<EspacesMessagesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [EspacesMessagesComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(EspacesMessagesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
