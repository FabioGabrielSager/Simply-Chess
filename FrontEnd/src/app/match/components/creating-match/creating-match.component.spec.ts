import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreatingMatchComponent } from './creating-match.component';

describe('CreatingMatchComponent', () => {
  let component: CreatingMatchComponent;
  let fixture: ComponentFixture<CreatingMatchComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreatingMatchComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CreatingMatchComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
