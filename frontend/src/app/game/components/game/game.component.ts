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

  game: Game;

  constructor(private gameService: GameService) {}

  ngOnInit(): void {
    this.game = this.gameService.getCurrentGame();
  }

}
