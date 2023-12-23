import { TestBed } from '@angular/core/testing';
import { ResolveFn } from '@angular/router';

import { leaderBoardResolver } from './leader-board.resolver';

describe('leaderBoardResolver', () => {
  const executeResolver: ResolveFn<boolean> = (...resolverParameters) => 
      TestBed.runInInjectionContext(() => leaderBoardResolver(...resolverParameters));

  beforeEach(() => {
    TestBed.configureTestingModule({});
  });

  it('should be created', () => {
    expect(executeResolver).toBeTruthy();
  });
});
