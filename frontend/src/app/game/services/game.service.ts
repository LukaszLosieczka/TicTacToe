import { Injectable } from '@angular/core';
import {Router} from "@angular/router";
import {Game} from "../model/Game";
import {WebSocketService} from "./web-socket.service";
import {UserService} from "../../shared/services/user.service";
import {Move} from "../model/Move";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class GameService {

  private currentGame: Game;
  isConnected = false;
  gameStarted = false;
  isGameActive = false;

  constructor(private router: Router,  private webSocketService: WebSocketService, private userService: UserService,
              private readonly http: HttpClient) {
    this.webSocketService.getConnectedStatus().subscribe((status) =>{
      if(!status && !this.gameStarted && this.isGameActive){
        this.connectToServer();
        this.gameStarted = true;
      }
    });
  }

  checkForAGame(): void{
    if(!this.isGameActive) {
      this.http.get<Game>(environment.apiUrl + "game/current-game").subscribe({
        next: (game) => {
          this.currentGame = game;
          this.isGameActive = true;
          this.connectToServer();
          this.gameStarted = true;
        },
        error: (_) => {
          this.router.navigate(['/queue'])
        }
      });
    }
  }

  startGame(game: Game): void{
    this.currentGame = game;
    this.isGameActive = true;
    this.router.navigate([`/game`]);
  }

  makeMove(row: number, column: number): void{
    const playerId = this.userService.getUserId()
    const sign = playerId === this.currentGame!.player1 ? this.currentGame.player1Sign : this.currentGame.player2Sign;
    if(this.currentGame.nextPlayer === playerId && this.currentGame.board[row][column] === "null"){
      this.currentGame.board[row][column] = sign;
      this.currentGame.nextPlayer = playerId === this.currentGame.player1 ? this.currentGame.player2 : this.currentGame.player1;
      const move: Move = {row: row, column: column};
      this.webSocketService.send(`/app/move/${this.currentGame.id}`, JSON.stringify(move));
    }
  }

  canMove():boolean{
    const playerId = this.userService.getUserId();
    return this.currentGame.nextPlayer === playerId;
  }

  private connectToServer(): void{
    const topic = `/user/queue/game/notifications`;
    this.webSocketService.createAndConnect(topic);
    this.webSocketService.getConnectedStatus().subscribe((status) => {
      this.isConnected = status;
    });
    this.webSocketService.getMessage(topic).subscribe((game) => {
      const receivedGame = <Game> game;
      console.log(receivedGame);
      if(receivedGame.id === this.currentGame.id){
        this.copyGame(receivedGame);
      }
      if(this.currentGame.isFinished){
        this.endGame();
      }
    });
  }

  private copyGame(game: Game): void {
    this.currentGame.nextPlayer = game.nextPlayer;
    this.currentGame.isFinished = game.isFinished;
    this.currentGame.winner = game.winner;
    for (let i = 0; i < game.board.length; i++) {
      for (let j = 0; j < game.board[i].length; j++) {
        this.currentGame.board[i][j] = game.board[i][j];
      }
    }
  }

  getCurrentGame(): Game{
    return <Game> this.currentGame;
  }

  pauseGame(): void{
    this.gameStarted = false;
  }

  endGame(): void{
    this.gameStarted = false;
    this.isGameActive = false;
    this.webSocketService.disconnect();
  }
}
