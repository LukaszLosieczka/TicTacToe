import {Player} from "./Player";

export interface Game{
  id: string
  player1: Player
  player2: Player
  board: string [][]
  nextPlayerId: string | null
  creationDate: Date
  isFinished: boolean
  winner: string | null
}
