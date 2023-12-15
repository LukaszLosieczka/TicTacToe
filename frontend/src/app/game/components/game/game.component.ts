import {Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {GameService} from "../../services/game.service";
import {Game} from "../../model/Game";
import {Router} from "@angular/router";

@Component({
  selector: 'app-game',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './game.component.html',
  styleUrl: './game.component.css'
})
export class GameComponent implements OnInit{

  constructor(private gameService: GameService, private router: Router) {
    if(!this.gameService.isGameActive){
      this.gameService.checkForAGame({
        success: () => this.gameService.connectToServer(),
        fail: () => this.router.navigate(['/queue'])
      });
    }
    else{
      this.gameService.connectToServer();
    }
  }

  ngOnInit(): void {}

  getGame(): Game{
    return this.gameService.getCurrentGame();
  }

  isGameActive(): boolean{
    return this.gameService.isGameActive;
  }

  makeMove(row: number, col: number): void{
    this.gameService.makeMove(row, col);
  }

  getWinner(): string{
    let message = "The winner is ";
    if(this.getGame().winner){
      if(this.getGame().winner === this.getGame().player1.playerId){
        message += this.getGame().player1.playerUsername;
      }
      else{
        message += this.getGame().player2.playerUsername;
      }
    }
    return message;
  }

  getOpponent(): string{
    return this.gameService.getOpponent().playerUsername;
  }

  canMove(): boolean{
    return this.gameService.canMove() && !this.gameService.getCurrentGame().isFinished;
  }

  isCellEmpty(row: number, col: number){
    return this.getGame().board[row][col] === "null";
  }

  ngOnDestroy(): void {
    if(this.gameService.getCurrentGame() && !this.gameService.getCurrentGame().isFinished) {
      this.gameService.pauseGame();
    }
    else if(this.gameService.isGameActive){
      this.gameService.endGame()
    }
  }

}
