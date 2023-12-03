export interface Game{
  id: string
  player1: string
  player2: string
  board: string[][]
  nextPlayer: string
  creationDate: Date
  isFinished: boolean
  winner: string
}
