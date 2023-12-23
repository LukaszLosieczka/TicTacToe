import {Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {GameService} from "../../services/game.service";
import {Game} from "../../model/Game";
import {Router} from "@angular/router";
import {UserService} from "../../../shared/services/user.service";

@Component({
  selector: 'app-game',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './game.component.html',
  styleUrl: './game.component.css'
})
export class GameComponent implements OnInit{

  constructor(private gameService: GameService, private userService: UserService, private router: Router) {
    if(!this.gameService.isGameActive){
      this.gameService.checkForAGame({
        success: () => this.gameService.connectToServer(),
        fail: () => this.router.navigate(['/queue'])
      });
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
    if(this.getGame().winner){
      if(this.getGame().winner === this.userService.getUserId()){
        return "You Won!";
      }
      return "You Lost!";
    }
    return "Draw!";
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

  quitGame(){
    this.gameService.quitGame();
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
