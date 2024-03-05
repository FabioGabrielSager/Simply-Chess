import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PieceSelectorModalComponent } from './piece-selector-modal.component';

describe('PieceSelectorModalComponent', () => {
  let component: PieceSelectorModalComponent;
  let fixture: ComponentFixture<PieceSelectorModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PieceSelectorModalComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PieceSelectorModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
