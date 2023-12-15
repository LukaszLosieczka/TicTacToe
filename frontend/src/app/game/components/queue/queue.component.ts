import {Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {WebSocketService} from "../../services/web-socket.service";
import {Game} from "../../model/Game";
import {GameService} from "../../services/game.service";
import {Router} from "@angular/router";
import {UserService} from "../../../shared/services/user.service";
@Component({
  selector: 'app-queue',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './queue.component.html',
  styleUrl: './queue.component.css'
})
export class QueueComponent implements OnInit{

  isConnected = false;
  inQueue = false;
  receivedGame: Game;
  message: string;

  constructor(private webSocketService: WebSocketService, private gameService: GameService, private router: Router,
              private userService: UserService) {}

  ngOnInit(): void {
    this.gameService.checkForAGame({
      success: () => {
        this.router.navigate(['/game']);
      },
      fail: () => {
        this.connectToQueueServer();
      }
    });
  }

  connectToQueueServer():void{
    const topic = `/user/queue/notifications`;
    this.webSocketService.createAndConnect(topic);
    this.webSocketService.getConnectedStatus().subscribe((status) => {
      this.isConnected = status;
    });
    this.webSocketService.getMessage(topic).subscribe((game) => {
      this.receivedGame = <Game> game;
      if(this.receivedGame.id) {
        this.inQueue = false;
        this.message = "Game Found!";
        this.gameService.startGame(this.receivedGame);
      }
    });
  }

  joinQueue(): void {
    this.inQueue = true;
    this.message = "Searching..."
    const at = this.userService.getAccessToken();
    if(at && this.userService.isTokenValid(at)){
      this.webSocketService.send('/app/queue', at);
    }
    else{
      this.userService.refreshTokens()
        .subscribe({
          next: () => {
            this.webSocketService.send('/app/queue', <string> this.userService.getAccessToken());
          },
          error: err => {
            console.log(err);
            this.userService.logout();
          }
        });
    }
  }

  quitQueue(): void {
    this.inQueue = false;
    this.webSocketService.send('/app/queue/disconnect', "disconnect")
  }

  ngOnDestroy(): void {
    if(this.inQueue){
      this.quitQueue();
    }
    if(this.isConnected) {
      this.webSocketService.disconnect();
    }
  }
}
