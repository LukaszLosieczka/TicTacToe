import {Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {GameService} from "../../services/game.service";
import {Game} from "../../model/Game";

@Component({
  selector: 'app-game',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './game.component.html',
  styleUrl: './game.component.css'
})
export class GameComponent implements OnInit{

  constructor(private gameService: GameService) {}

  ngOnInit(): void {
  }

  getGame(): Game{
    return this.gameService.getCurrentGame();
  }

  makeMove(row: number, col: number): void{
    this.gameService.makeMove(row, col);
  }

  canMove(): boolean{
    return this.gameService.canMove() && !this.gameService.getCurrentGame().isFinished;
  }

  isCellEmpty(row: number, col: number){
    return this.getGame().board[row][col] === "null";
  }

  ngOnDestroy(): void {
    this.gameService.endGame();
  }

}
