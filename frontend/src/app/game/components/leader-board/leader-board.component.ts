import {Component, OnInit} from '@angular/core';
import { CommonModule } from '@angular/common';
import {ActivatedRoute} from "@angular/router";
import {LeaderBoardPos} from "../../model/LeaderBoardPos";
import {UserService} from "../../../shared/services/user.service";

@Component({
  selector: 'app-leader-board',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './leader-board.component.html',
  styleUrl: './leader-board.component.css'
})
export class LeaderBoardComponent implements OnInit{

  private leaderBoard: LeaderBoardPos[]

  constructor(private activateRoute: ActivatedRoute, private userService: UserService) {}

  ngOnInit(): void {
    this.activateRoute.data.subscribe(
      ({leaderBoard}) => {
        this.leaderBoard = leaderBoard;
      }
    )
  }

  getLeaderBoard(): LeaderBoardPos[]{
    return this.leaderBoard;
  }

  isUser(userId: string):boolean{
    return this.userService.getUserId() === userId;
  }
}
