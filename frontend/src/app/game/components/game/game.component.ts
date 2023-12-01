import {Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {WebSocketService} from "../../services/web-socket.service";
@Component({
  selector: 'app-game',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './game.component.html',
  styleUrl: './game.component.css'
})
export class GameComponent implements OnInit{

  isConnected = false;
  receivedMessage = '';

  constructor(private webSocketService: WebSocketService) {}

  ngOnInit(): void {
    const topic = `/user/queue/notifications`;
    this.webSocketService.createAndConnect(topic);
    this.webSocketService.getConnectedStatus().subscribe((status) => {
      this.isConnected = status;
    });
    this.webSocketService.getMessage(topic).subscribe((message) => {
      this.receivedMessage = message;
    });
  }

  joinQueue(): void {
    this.webSocketService.send('/app/queue', "");
  }

  ngOnDestroy(): void {
    this.webSocketService.disconnect();
  }
}
