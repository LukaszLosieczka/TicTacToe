import {Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {WebSocketService} from "../../services/web-socket.service";
import {Game} from "../../model/Game";
import {GameService} from "../../services/game.service";
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

  constructor(private webSocketService: WebSocketService, private gameService: GameService) {}

  ngOnInit(): void {
    const topic = `/user/queue/notifications`;
    this.webSocketService.createAndConnect(topic);
    this.webSocketService.getConnectedStatus().subscribe((status) => {
      this.isConnected = status;
    });
    this.webSocketService.getMessage(topic).subscribe((game) => {
      this.receivedGame = <Game> game;
      if(this.receivedGame.id) {
        this.inQueue = false;
        this.message = "Znaleziono grę!";
        this.gameService.startGame(this.receivedGame);
      }
    });
  }

  joinQueue(): void {
    this.inQueue = true;
    this.message = "Wyszukiwanie..."
    this.webSocketService.send('/app/queue', "connect");
  }

  quitQueue(): void {
    this.inQueue = false;
    this.webSocketService.send('/app/queue/disconnect', "disconnect")
  }

  ngOnDestroy(): void {
    if(this.inQueue){
      this.quitQueue();
    }
    this.webSocketService.disconnect();
  }
}
