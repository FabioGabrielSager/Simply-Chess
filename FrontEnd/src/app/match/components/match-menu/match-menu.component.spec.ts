import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MatchMenuComponent } from './match-menu.component';

describe('MatchMenuComponent', () => {
  let component: MatchMenuComponent;
  let fixture: ComponentFixture<MatchMenuComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MatchMenuComponent]
    });
    fixture = TestBed.createComponent(MatchMenuComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
