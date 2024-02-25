import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MatchIdModalComponent } from './match-id-modal.component';

describe('MatchIdModalComponent', () => {
  let component: MatchIdModalComponent;
  let fixture: ComponentFixture<MatchIdModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatchIdModalComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(MatchIdModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
