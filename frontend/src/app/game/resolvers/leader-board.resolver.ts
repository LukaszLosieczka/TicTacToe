import { ResolveFn } from '@angular/router';
import {LeaderBoardPos} from "../model/LeaderBoardPos";
import {GameService} from "../services/game.service";
import {inject} from "@angular/core";

export const leaderBoardResolver: ResolveFn<LeaderBoardPos[]> = (route, state) => {
  return inject(GameService).getLeaderBoard();
};
