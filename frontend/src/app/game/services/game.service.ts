import { Injectable } from '@angular/core';
import {Router} from "@angular/router";
import {Game} from "../model/Game";

@Injectable({
  providedIn: 'root'
})
export class GameService {

  private currentGame: Game;

  constructor(private router: Router) { }

  startGame(game: Game): void{
    this.currentGame = game;
    this.router.navigate([`/game`]);
  }

  getCurrentGame(): Game{
    return this.currentGame;
  }
}
