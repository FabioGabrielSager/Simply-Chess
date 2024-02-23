import { TestBed } from '@angular/core/testing';

import { MatchSessionService } from './match-session.service';

describe('MatchSessionService', () => {
  let service: MatchSessionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MatchSessionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
