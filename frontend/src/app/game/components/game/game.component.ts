import {Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {WebSocketService} from "../../services/web-socket.service";
import {UserService} from "../../../shared/services/user.service";

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

  constructor(private webSocketService: WebSocketService, private userService: UserService) {}

  ngOnInit(): void {
    this.webSocketService.connect();
    const userId = this.userService.getUserId();
    const topic = `/topic/greetings`;
    // this.webSocketService.subscribe(topic)
    this.webSocketService.getConnectedStatus().subscribe((status) => {
      this.isConnected = status;
    });
    // this.webSocketService.getMessage(topic).subscribe((message) => {
    //   this.receivedMessage = message;
    // });
  }

  sendMessage(message: string): void {
    this.webSocketService.send('/hello', message); // Replace with your destination
  }

  ngOnDestroy(): void {
    this.webSocketService.disconnect();
  }
}
