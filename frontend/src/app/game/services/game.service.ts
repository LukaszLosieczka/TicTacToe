import { Injectable } from '@angular/core';
import {Router} from "@angular/router";
import {Game} from "../model/Game";
import {WebSocketService} from "./web-socket.service";
import {UserService} from "../../shared/services/user.service";
import {Move} from "../model/Move";
import {HttpClient} from "@angular/common/http";
import {environment} from "../../../environments/environment";
import {Player} from "../model/Player";

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
      }
    });
  }

  checkForAGame(resolve?: {success?: () => void, fail?: () => void}): void {
    this.http.get<Game>(environment.apiUrl + "game/current-game").subscribe({
      next: (game) => {
        this.currentGame = game;
        this.isGameActive = true;
        this.gameStarted = true;
        if (resolve && resolve.success) resolve.success();
      },
      error: () => {
        if (resolve && resolve.fail) resolve.fail();
      }
    });
  }

  startGame(game: Game): void{
    this.currentGame = game;
    this.isGameActive = true;
    this.router.navigate([`/game`]);
  }

  makeMove(row: number, column: number): void{
    const playerId = this.userService.getUserId()
    const sign = playerId === this.currentGame.player1.playerId ? this.currentGame.player1.playerSign
      : this.currentGame.player2.playerSign;
    if(this.currentGame.nextPlayerId === playerId && this.currentGame.board[row][column] === "null"){
      this.currentGame.board[row][column] = sign;
      this.currentGame.nextPlayerId = playerId === this.currentGame.player1.playerId ? this.currentGame.player2.playerId
        : this.currentGame.player1.playerId;
      const move: Move = {row: row, column: column};
      this.webSocketService.send(`/app/move/${this.currentGame.id}`, JSON.stringify(move));
    }
  }

  canMove():boolean{
    const playerId = this.userService.getUserId();
    return this.currentGame.nextPlayerId === playerId;
  }

  connectToServer(): void{
    const topic = `/user/queue/game/notifications`;
    this.webSocketService.createAndConnect(topic);
    this.gameStarted = true;
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
    this.currentGame.nextPlayerId = game.nextPlayerId;
    this.currentGame.isFinished = game.isFinished;
    this.currentGame.winner = game.winner;
    for (let i = 0; i < game.board.length; i++) {
      for (let j = 0; j < game.board[i].length; j++) {
        this.currentGame.board[i][j] = game.board[i][j];
      }
    }
  }

  getOpponent(): Player{
    if(this.currentGame.player1.playerId === this.userService.getUserId()){
      return this.currentGame.player2;
    }
    else{
      return this.currentGame.player1;
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
